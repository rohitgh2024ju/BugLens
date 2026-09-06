package dev.rohit.buglens.Application;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class DirectoryInitializer {

    private static final Path UPLOAD_DIRECTORY =
            Path.of("buglens/uploads");

    private static final Path LOG_DIRECTORY =
            Path.of("buglens/logs");

    private static final Path DATABASE_DIRECTORY =
            Path.of("buglens/Database/Tenants");

    public static void initialize()
            throws IOException {

        Files.createDirectories(
                UPLOAD_DIRECTORY);

        Files.createDirectories(
                LOG_DIRECTORY);

        Files.createDirectories(
                DATABASE_DIRECTORY);
    }
}