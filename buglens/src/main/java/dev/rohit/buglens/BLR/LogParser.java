package dev.rohit.buglens.BLR;

import org.json.JSONObject;

// Common interface for every parser plugin
public interface LogParser {

    JSONObject parse(String logLine);
}