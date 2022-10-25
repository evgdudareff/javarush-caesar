package caesar;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class CaesarCrypto {
    enum Operations {
        decode, encode
    }

    enum Languages {
        EN, RU
    }

    private static final String englishDict = "A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q,R,S,T,U,V,W,X,Y,Z,a,b,c,d,e," +
            "f,g,h,i,j,k,l,m,n,o,p,q,r,s,t,u,v,w,x,y,z";
    private static final String russianDict = "А,Б,В,Г,Д,Е,Ё,Ж,З,И,Й,К,Л,М,Н,О,П,Р,С,Т,У,Ф,Х,Ц,Ч,Ш,Щ,Ъ,Ы,Ь,Э,Ю" +
            "а,б,в,г,д,е,ё,ж,з,и,й,к,л,м,н,о,п,р,с,т,у,ф,х,ц,ч,ш,щ,ъ,ы,ь,э,ю";
    private static Languages currLang = Languages.EN;
    private static HashMap<Languages, String> langToDictMap = new HashMap<>() {{
        put(Languages.EN, englishDict);
        put(Languages.RU, russianDict);
    }};

    private static String getCurrDict(Languages lang) {
        return langToDictMap.get(lang);
    }

    private static String getCurrCharsetName() {
        return StandardCharsets.UTF_8.toString();
    }

    private static Path getOutputFilePath(String inputFile, Operations operation) {
        String fileName = Paths.get(inputFile).getFileName().toString();
        String[] splitedFileName = fileName.split("\\.");
        String newFileName = String.format("%s(%sd).%s", splitedFileName[0], operation, splitedFileName[1]);
        return Paths.get(inputFile).getParent().resolve(Paths.get(newFileName));
    }

    private static ArrayList<String> getCurrDictionary() {
        return new ArrayList<>(Arrays.asList(getCurrDict(currLang).split(",")));
    }

    private static int getShiftedEncodedDecodedCharIndex(Operations operation, int currCharIndex, int currDictLength, int key) {
        int newCharIndex = operation == Operations.encode ? currCharIndex + key : currCharIndex - key;
        if (newCharIndex >= currDictLength) {
            do {
                newCharIndex = newCharIndex - currDictLength;
            } while (newCharIndex >= currDictLength);
        } else if (newCharIndex < 0) {
            do {
                newCharIndex = currDictLength - Math.abs(newCharIndex);
            } while (newCharIndex < 0);

        }
        return newCharIndex;


    }

    private static void checkParameters(String operation, String filePath, String key, String lang) throws RuntimeException {
        try {
            Operations.valueOf(operation);
        } catch (Exception e) {
            throw new RuntimeException(String.format("Operation \"%s\" not implemented yet", operation));
        }

        if (!Files.exists(Paths.get(filePath))) {
            throw new RuntimeException(String.format("Path \"%s\" is not exist", filePath));
        }

        int intKey = 0;
        try {
            intKey = Integer.parseInt(key);
        } catch (Exception e) {
            throw new RuntimeException("Key could not be parsed as integer");
        }

        if (intKey <= 0) {
            // there could be more checks for positive integers
            throw new RuntimeException("Crypto key must be positive integer number");
        }

        if (lang != null) {
            try {
                Languages.valueOf(lang);
            } catch (Exception e) {
                throw new RuntimeException(String.format("Language \"%s\" not implemented yet", lang));
            }
        }
    }

    public static void doCaesarEncodeDecode(Operations operation, String filePath, int key) throws IOException, RuntimeException {
        ArrayList<String> currDictionary = getCurrDictionary();
        int currDictLength = currDictionary.size();

        try (FileChannel inputChannel = FileChannel.open(Paths.get(filePath))) {
            int inputChannelSize = (int) inputChannel.size();

            ByteBuffer inputBuff = ByteBuffer.allocate(inputChannelSize);
            ByteBuffer outputBuff = ByteBuffer.allocate(inputChannelSize);

            inputChannel.read(inputBuff);
            inputBuff.flip();

            Charset cs = Charset.forName(getCurrCharsetName());
            CharBuffer inputCharBuff = cs.decode(inputBuff);

            while (inputCharBuff.hasRemaining()) {
                char currSymbolChar = inputCharBuff.get();
                String currSymbolString = String.valueOf(currSymbolChar);

                int currCharIndex = currDictionary.indexOf(currSymbolString);

                if (currCharIndex == -1) {
                    // if there is no symbol in curr dict, then put it as is
                    outputBuff.put(currSymbolString.getBytes(getCurrCharsetName()));
                    continue;
                }

                int newCharIndex = getShiftedEncodedDecodedCharIndex(operation, currCharIndex, currDictLength, key);
                String newChar = currDictionary.get(newCharIndex);

                outputBuff.put(String.valueOf(newChar).getBytes(getCurrCharsetName()));
            }

            if (outputBuff.position() != 0) {
                outputBuff.rewind();
                Path outputFileName = getOutputFilePath(filePath, operation);

                FileChannel outputFileChannel = FileChannel.open(outputFileName, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
                outputFileChannel.write(outputBuff);
            }
        }

    }


    public static void main(String[] args) {
        String operation = args[0];
        String filePath = args[1];
        String key = args[2];

        String lang = null;
        if (args.length == 4) {
            lang = args[3];
        }

        try {
            checkParameters(operation, filePath, key, lang); // if something not ok - throw runtime exception

            if (args.length == 4) {
                currLang = Languages.valueOf(args[3]);
            }
            doCaesarEncodeDecode(Operations.valueOf(operation), filePath, Integer.parseInt(key));

            System.out.println(String.format("Operation \"%s\" is succeeded", operation));
        } catch (IOException e) {
            System.out.println("An error occurred while working with files");
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
        } catch (Exception e) {
            System.out.println("Something went wrong...see stacktrace\n" + e.getStackTrace());
        }

    }
}
