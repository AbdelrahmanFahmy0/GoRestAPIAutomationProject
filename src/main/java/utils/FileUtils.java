package utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class FileUtils {
    //Calling the method and passing the folder path as file
    public static void deleteFiles(File dirPath) {

        //Checking if the path exists or not if not the method finishes
        if (dirPath == null || !dirPath.exists()) {
            LogsUtil.warn("Directory doesn't exist: " + dirPath);
            return;
        }

        //Checking if the folder contains files or not.if yes it stores them if not the method finishes
        File[] filesList = dirPath.listFiles();
        if (filesList == null) {
            LogsUtil.warn("Failed to list files in: " + dirPath);
            return;
        }

        //Iterate on the files to delete them , if they are folders then calling to the method will occur again
        for (File file : filesList) {
            if (file.isDirectory()) {
                deleteFiles(file);
            } else {
                try {
                    Files.delete(file.toPath());
                } catch (IOException e) {
                    LogsUtil.error("Failed to delete file: " + file);
                }
            }
        }
    }

    //Cleaning the specified directory by deleting it and all its contents quietly
    public static void cleanDirectory(File file) {
        try {
            org.apache.commons.io.FileUtils.deleteQuietly(file);
        } catch (Exception e) {
            LogsUtil.info(e.getMessage());
        }
    }

    //Creating a directory if it does not already exist.
    public static void createDirectory(File path) {
        // Check if the directory already exists
        if (!path.exists()) {
            try {
                Files.createDirectories(path.toPath());
                LogsUtil.info("Directory created: " + path);
            } catch (IOException e) {
                LogsUtil.error("Failed to create directory: " + e.getMessage());
            }
        } else {
            LogsUtil.info("Directory already exists: " + path);
        }
    }

    //Coping a file from the source location to the destination location.
    public static void copyFile(File srcFile, File desFile) {
        // Check if the source file exists
        if (!srcFile.exists()) {
            LogsUtil.warn("Source file does not exist: " + srcFile);
            return;
        }
        // Check if the destination file exists
        if (!desFile.exists()) {
            try {
                Files.copy(srcFile.toPath(), desFile.toPath());
                LogsUtil.info("File copied from " + srcFile + " to " + desFile);
            } catch (IOException e) {
                LogsUtil.error("Failed to copy file: " + e.getMessage());
            }
        } else {
            LogsUtil.info("File already exists: " + desFile);
        }
    }
}
