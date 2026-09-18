package dev.rohit.buglens.ParserEngine;

import java.io.IOException;
import java.nio.file.Path;

import org.json.JSONArray;
import org.json.JSONObject;

import dev.rohit.buglens.BLR.BLRRegistry;
import dev.rohit.buglens.BLR.LogParser;
import dev.rohit.buglens.IngestionEngine.Reader.LogReader;
import dev.rohit.buglens.IngestionEngine.format.LogFormat;

public class ParserEngine {

    private final Path inputPath;
    private final LogReader logReader;
    private final BLRRegistry registry;

    public ParserEngine(Path inputPath) {

        if (inputPath == null) {
            throw new IllegalArgumentException(
                    "Input path is required"
            );
        }

        this.inputPath = inputPath;
        this.logReader = new LogReader(inputPath);
        this.registry = new BLRRegistry();
    }

    public JSONArray runParser(
            LogFormat logFormat) throws IOException {

        if (logFormat == null) {
            throw new IllegalArgumentException(
                    "Log format is required"
            );
        }

        JSONObject logData =
                logReader.readFile(0);

        JSONArray rawLogs =
                logData.getJSONArray("logs");


        String parserId =
                logFormat.getParser();

        LogParser parser =
                registry.getParser(parserId);

        JSONArray parsedLogs =
                new JSONArray();

        for (int i = 0; i < rawLogs.length(); i++) {

            String rawLog =
                    rawLogs.getString(i);

            if (rawLog == null || rawLog.isBlank()) {
                continue;
            }

            try {

                JSONObject parsedLog =
                        parser.parse(rawLog);

                if (parsedLog != null
                        && !parsedLog.isEmpty()) {

                    parsedLogs.put(parsedLog);

                } else {

                    System.out.println(
                            "Parser returned empty result for log index: "
                                    + i
                    );
                }

            } catch (Exception e) {

                System.err.println(
                        "Failed to parse log at index "
                                + i
                                + ": "
                                + e.getMessage()
                );
            }
        }

        System.out.println(
                "RAW LOG COUNT: "
                        + rawLogs.length()
        );

        System.out.println(
                "PARSED LOG COUNT: "
                        + parsedLogs.length()
        );

        return parsedLogs;
    }

    public Path getInputPath() {
        return inputPath;
    }

    public LogReader getLogReader() {
        return logReader;
    }

    public BLRRegistry getRegistry() {
        return registry;
    }
}