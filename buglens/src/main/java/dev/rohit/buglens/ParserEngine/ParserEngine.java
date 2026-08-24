package dev.rohit.buglens.ParserEngine;

import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import dev.rohit.buglens.IngestionEngine.Reader.LogReader;
import dev.rohit.buglens.IngestionEngine.format.FormatDetector;

// coordinates everything
public class ParserEngine {
    private final String inputPath;
    private final LogParser logParser;

    public ParserEngine(String inputPath, String parserClass) {
        this.inputPath = inputPath;
        this.logParser = new ParserRegistry(parserClass).find();
    }

    public void runParser() throws JSONException, IOException {
        LogReader logReader = new LogReader(inputPath);
        JSONObject logData = logReader.readFile(-1);
        JSONArray logArray = logData.getJSONArray("logs");

        for (Object logObj : logArray) {
            String logStr = logObj.toString();
            JSONObject parsedData = logParser.parse(logStr);
            System.out.println(parsedData.toString(2));
        }
    }

    public static void main(String[] args) {

        try {
            FormatDetector formatDetector = new FormatDetector();
            String parser = formatDetector.detect();
            System.out.println(parser);

            ParserEngine parserEngine = new ParserEngine(
                    "buglens/logs/output.jsonl",
                    parser);

            parserEngine.runParser();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
