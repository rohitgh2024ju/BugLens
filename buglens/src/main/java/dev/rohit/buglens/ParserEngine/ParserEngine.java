package dev.rohit.buglens.ParserEngine;

import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import dev.rohit.buglens.IngestionEngine.Reader.LogReader;
import dev.rohit.buglens.IngestionEngine.format.FormatDetector;
import dev.rohit.buglens.IngestionEngine.format.LogFormat;

// coordinates everything
public class ParserEngine {
    private final String inputPath;
    private final LogParser logParser;

    public ParserEngine(String inputPath, String parserClass) {
        this.inputPath = inputPath;
        this.logParser = new ParserRegistry(parserClass).find();
    }

    public JSONArray runParser() throws JSONException, IOException {

        LogReader logReader = new LogReader(inputPath);
        JSONObject logData = logReader.readFile(-1);
        JSONArray logArray = logData.getJSONArray("logs");

        JSONArray parsedLogs = new JSONArray();

        for (Object logObj : logArray) {
            String logStr = logObj.toString();
            JSONObject parsedData = logParser.parse(logStr);
            parsedLogs.put(parsedData);
        }

        return parsedLogs;
    }

    public static void main(String[] args) {

        try {
            FormatDetector formatDetector = new FormatDetector();
            LogFormat format = formatDetector.detect();
            System.out.println(format.getParser());

            ParserEngine parserEngine = new ParserEngine(
                    "buglens/logs/output.jsonl",
                    format.getParser());

            JSONArray parsedData = parserEngine.runParser();
            System.out.println(parsedData.toString(2));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
