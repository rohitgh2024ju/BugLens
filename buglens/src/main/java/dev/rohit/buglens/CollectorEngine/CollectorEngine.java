package dev.rohit.buglens.CollectorEngine;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import org.json.JSONObject;

public class CollectorEngine {

    private final Path inputPath;
    private final Path outputPath;

    public CollectorEngine(Path inputPath, Path outputPath) {
        this.inputPath = inputPath;
        this.outputPath = outputPath;
    }

    public void collectJson() throws IOException {
        try (BufferedReader reader = Files.newBufferedReader(inputPath, StandardCharsets.UTF_8);
                BufferedWriter writer = Files.newBufferedWriter(outputPath, StandardCharsets.UTF_8)) {

            String line;

            int random4DInt = ThreadLocalRandom.current().nextInt(1000, 10000);
            String fileId = "file-" + random4DInt;

            while ((line = reader.readLine()) != null) {
                JSONObject log = new JSONObject();
                log.put("file_id", fileId);
                log.put("ingestion_id", ShortIdGenerator.generateId(8));
                log.put("received_at", Instant.now().truncatedTo(ChronoUnit.SECONDS).toString());
                log.put("file_name", inputPath.getFileName().toString());
                log.put("file_type", ".log");
                log.put("raw_data", line);

                // Write as JSON Lines (.jsonl) format
                writer.write(log.toString());
                writer.newLine();
            }
        }
    }
}

class ShortIdGenerator {
    // Pure static helper method
    public static String generateId(int length) {
        String randomPart = UUID.randomUUID().toString().replace("-", "");
        return "igs-" + randomPart.substring(0, Math.min(length, 32));
    }
}

