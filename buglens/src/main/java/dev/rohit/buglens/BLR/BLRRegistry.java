package dev.rohit.buglens.BLR;

import java.util.HashMap;
import java.util.Map;

import dev.rohit.buglens.BLR.Bundles.SpringBootBundler.SpringBootParser;

public class BLRRegistry {
    private final Map<String, LogParser> parserRegistry = new HashMap<>();

    public BLRRegistry() {
        register("SpringBootParser", new SpringBootParser());
    }

    public void register(String parserId, LogParser parser) {
        parserRegistry.put(parserId, parser);
    }

    public LogParser getParser(String parserId) {
        LogParser parser = parserRegistry.get(parserId);
        if (parser == null) {
            throw new IllegalArgumentException(
                "No BLR parser registered for: " + parserId
            );
        }
        return parserRegistry.get(parserId);
    }
}
