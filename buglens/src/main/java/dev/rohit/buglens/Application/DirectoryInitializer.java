package dev.rohit.buglens.Application;

import java.io.IOException;
import java.nio.file.Files;

import dev.rohit.buglens.Configpaths.BugLensPaths;

public class DirectoryInitializer {

    public static void initialize()
            throws IOException {

        Files.createDirectories(
                BugLensPaths.UPLOADS_DIR);

        Files.createDirectories(
                BugLensPaths.LOGS_DIR);

        Files.createDirectories(
                BugLensPaths.DATABASE_DIR);
    }
}