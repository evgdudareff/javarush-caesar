package com.dudarev.caesar;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.*;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import com.dudarev.caesar.enums.*;
import com.dudarev.caesar.utils.*;

public class CaesarApp {

    private static Languages currLang = Languages.EN;

    public static void doCaesarEncodeDecode(Operations operation, String filePath, int key) throws IOException, RuntimeException {
        EncoderDecoder encoderDecoder = new EncoderDecoder(currLang);
        String currCharsetName = encoderDecoder.getCurrCharsetName();
        FilesUtils filesUtils = new FilesUtils();

        try (FileChannel inputChannel = FileChannel.open(Paths.get(filePath))) {
            int inputChannelSize = (int) inputChannel.size();

            ByteBuffer inputBuff = ByteBuffer.allocate(inputChannelSize);
            ByteBuffer outputBuff = ByteBuffer.allocate(inputChannelSize);

            inputChannel.read(inputBuff);
            inputBuff.flip();

            Charset cs = Charset.forName(currCharsetName);
            CharBuffer inputCharBuff = cs.decode(inputBuff);

            while (inputCharBuff.hasRemaining()) {
                String currSymbolString = String.valueOf(inputCharBuff.get());
                String decodedEncodedStringSymbol = encoderDecoder.getEncodedDecodedStringSymbol(operation, currSymbolString, key);

                if (decodedEncodedStringSymbol == null) {
                    // if there is no symbol in curr dict, then put it as is
                    outputBuff.put(currSymbolString.getBytes(currCharsetName));
                } else {
                    outputBuff.put(decodedEncodedStringSymbol.getBytes(currCharsetName));
                }
            }

            if (outputBuff.position() != 0) {
                outputBuff.rewind();
                Path outputFileName = filesUtils.getOutputFilePath(filePath, operation);

                FileChannel outputFileChannel = FileChannel.open(outputFileName, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
                outputFileChannel.write(outputBuff);
            }
        }

    }


    public static void main(String[] args) {
        if (args.length < 3) {
            throw new RuntimeException("At least 3 parameters should be provided: operation, filePath, key [,lang]. Check readme");
        }

        String operation = args[0];
        String filePath = args[1];
        String key = args[2];

        String langOptionalParam = null;
        if (args.length == 4) {
            langOptionalParam = args[3];
        }

        try {
            ParamsChecker paramsChecker = new ParamsChecker();
            // if something not ok - throw runtime exception
            paramsChecker.checkParameters(operation, filePath, key, langOptionalParam);

            if (langOptionalParam != null) {
                currLang = Languages.valueOf(langOptionalParam);
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
