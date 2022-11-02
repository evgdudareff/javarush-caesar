package com.dudarev.caesar.utils;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import com.dudarev.caesar.enums.Languages;
import com.dudarev.caesar.enums.Operations;

public class EncoderDecoder {
    private final String englishDict = "A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q,R,S,T,U,V,W,X,Y,Z,a,b,c,d,e," +
            "f,g,h,i,j,k,l,m,n,o,p,q,r,s,t,u,v,w,x,y,z";
    private final String russianDict = "А,Б,В,Г,Д,Е,Ё,Ж,З,И,Й,К,Л,М,Н,О,П,Р,С,Т,У,Ф,Х,Ц,Ч,Ш,Щ,Ъ,Ы,Ь,Э,Ю" +
            "а,б,в,г,д,е,ё,ж,з,и,й,к,л,м,н,о,п,р,с,т,у,ф,х,ц,ч,ш,щ,ъ,ы,ь,э,ю";

    private Languages currLang;
    private HashMap<Languages, String> langToDictMap = new HashMap<>() {{
        put(Languages.EN, englishDict);
        put(Languages.RU, russianDict);
    }};

    public EncoderDecoder(Languages lang) {
        currLang = lang;
    }

    private ArrayList<String> getCurrDictionary() {
        return new ArrayList<>(Arrays.asList(langToDictMap.get(currLang).split(",")));
    }

    public String getCurrCharsetName() {
        return StandardCharsets.UTF_8.toString();
    }

    public int getShiftedEncodedDecodedCharIndex(Operations operation, String currStringSymbol, int key) {
        int currCharIndex = getCurrDictionary().indexOf(currStringSymbol);
        if (currCharIndex == -1) {
            return currCharIndex;
        }

        int newCharIndex = operation == Operations.encode ? currCharIndex + key : currCharIndex - key;
        int currDictLength = getCurrDictionary().size();

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

    public String getEncodedDecodedStringSymbol(Operations operation, String currStringSymbol, int key) {
        int newCharIndex = getShiftedEncodedDecodedCharIndex(operation, currStringSymbol, key);

        if (newCharIndex == -1) {
            // if there is no symbol in curr dict, return null
            return null;
        }

        String newStringSymbol = getCurrDictionary().get(newCharIndex);
        return newStringSymbol;
    }
}
