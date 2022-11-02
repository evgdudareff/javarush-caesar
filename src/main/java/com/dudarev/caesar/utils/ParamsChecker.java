package com.dudarev.caesar.utils;

import com.dudarev.caesar.enums.Languages;
import com.dudarev.caesar.enums.Operations;

import java.nio.file.Files;
import java.nio.file.Paths;

public class ParamsChecker {

    public void checkParameters(String operation, String filePath, String key, String lang) throws RuntimeException {
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
}
