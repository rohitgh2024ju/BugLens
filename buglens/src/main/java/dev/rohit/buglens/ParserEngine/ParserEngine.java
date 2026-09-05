package dev.rohit.buglens.ParserEngine;

import java.io.IOException;
import java.nio.file.Path;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import dev.rohit.buglens.IngestionEngine.Reader.LogReader;

public class ParserEngine {

    private final Path inputPath;

    private final LogParser logParser;

    public ParserEngine(
            Path inputPath,
            String parserClass) {

        if (inputPath == null) {
            throw new IllegalArgumentException(
                    "Input path is required");
        }

        if (parserClass == null
                || parserClass.isBlank()) {

            throw new IllegalArgumentException(
                    "Parser class is required");
        }

        this.inputPath = inputPath;

        this.logParser =
                new ParserRegistry(parserClass)
                        .find();
    }
    public JSONArray runParser()
            throws JSONException, IOException {

        LogReader logReader =
                new LogReader(inputPath);

        JSONObject logData =
                logReader.readFile(-1);

        JSONArray logArray =
                logData.getJSONArray("logs");

        JSONArray parsedLogs =
                new JSONArray();

        for (Object logObj : logArray) {

            String logStr =
                    logObj.toString();

            JSONObject parsedData =
                    logParser.parse(logStr);

            parsedLogs.put(parsedData);
        }

        return parsedLogs;
    }
}