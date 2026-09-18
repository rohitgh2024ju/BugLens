package dev.rohit.buglens.BLR;

import org.json.JSONObject;

import dev.rohit.buglens.IngestionEngine.format.LogFormat;

public class BLRDispatcher {
    private final BLRRegistry registry;

    public BLRDispatcher() {
        this.registry = new BLRRegistry();
    }

    public JSONObject parse(LogFormat logFormat, String logLine) {
        if (logFormat == null) {
            throw new IllegalArgumentException("log format cannot be null");
        }

        LogParser parser = registry.getParser(logFormat.getParser());

        return parser.parse(logLine);
    }
}
