package dev.rohit.buglens.Configpaths;

import java.nio.file.Path;
import java.nio.file.Paths;

public final class BugLensPaths {
        private static final Path BASE_DIR = Paths.get("").toAbsolutePath();

        public static final Path LOGS_DIR = BASE_DIR.resolve("logs");

        public static final Path UPLOADS_DIR = BASE_DIR.resolve("uploads");

        public static final Path DATABASE_DIR = BASE_DIR.resolve("Database").resolve("tenants");

        public static final Path RESOURCES_DIR = BASE_DIR.resolve("Resources");

        public static final Path LOG_FORMATS_FILE = RESOURCES_DIR.resolve("log_formats.jsonl");

        public static final Path NORMALIZATION_MAPPING_FILE = BASE_DIR.resolve("config")
                        .resolve("normalization-mapping.xml");

        public static void printPaths() {
                System.out.println("BUGLENS BASE DIR: " + BASE_DIR);
                System.out.println("DATABASE DIR: " + DATABASE_DIR);
                System.out.println("LOGS DIR: " + LOGS_DIR);
                System.out.println("RESOURCES DIR: " + RESOURCES_DIR);
        }

        private BugLensPaths() {
                // Prevent object creation
        }
}
