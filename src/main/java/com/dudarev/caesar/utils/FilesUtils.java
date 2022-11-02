package com.dudarev.caesar.utils;

import java.nio.file.Path;
import java.nio.file.Paths;

import com.dudarev.caesar.enums.Operations;

public class FilesUtils {
    public Path getOutputFilePath(String inputFile, Operations operation) {
        String fileName = Paths.get(inputFile).getFileName().toString();
        String[] splitedFileName = fileName.split("\\.");
        String newFileName = String.format("%s(%sd).%s", splitedFileName[0], operation, splitedFileName[1]);
        return Paths.get(inputFile).getParent().resolve(Paths.get(newFileName));
    }
}
