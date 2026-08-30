package dev.rohit.buglens.CorrelationEngine.model;

import java.util.List;

import dev.rohit.buglens.NormalizerEngine.model.NormalizedEvent;

public class CorrelationResult {
    private final CorrelationType type;
    private final String key;
    private final List<NormalizedEvent> events;

    public CorrelationResult(
        CorrelationType type, String key, List<NormalizedEvent> events
    ) {

        this.type = type;
        this.key = key;
        this.events = events;
    }

    public CorrelationType getType() {
        return type;
    }

    public String getKey() {
        return key;
    }

    public List<NormalizedEvent> getEvents() {
        return events;
    }
}