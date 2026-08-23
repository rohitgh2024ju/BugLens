package dev.rohit.buglens.ParserEngine;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import dev.rohit.buglens.IngestionEngine.Reader.LogReader;

// coordinates everything
public class ParserEngine {
    String inputPath;

    ParserEngine(String inputPath) throws JSONException, IOException {
        LogReader logReader = new LogReader(inputPath);
        JSONObject logData = logReader.readFile(-1);
    }
}
