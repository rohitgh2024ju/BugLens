package dev.rohit.buglens.ParserEngine;

import org.json.JSONObject;

// common interface for every parser plugins
public interface LogParser {
    public JSONObject parse(String logLine);
}