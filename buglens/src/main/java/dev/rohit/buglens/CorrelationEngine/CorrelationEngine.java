package dev.rohit.buglens.CorrelationEngine;

import java.util.List;

import dev.rohit.buglens.CorrelationEngine.model.CorrelationGroup;
import dev.rohit.buglens.CorrelationEngine.model.CorrelationResult;
import dev.rohit.buglens.CorrelationEngine.model.CorrelationType;

public class CorrelationEngine {

    private final String clientId;
    private List<CorrelationResult> results;

    public CorrelationEngine(String clientId) {
        this.clientId = clientId;
    }

    public CorrelationEngine() {
        this.clientId = null;
    }

    public List<CorrelationResult> runCorrelate(long time, CorrelationType... types)
            throws IllegalArgumentException, IllegalAccessException {

        String idToUse = (this.clientId != null)
                ? this.clientId
                : "000";

        CorrelationGroup correlationGroup = new CorrelationGroup(idToUse);
        this.results = correlationGroup.correlate(time, types);

        return this.results;
    }

    public void printCorrelation() {
        if (this.results == null) {
            throw new IllegalStateException("No correlation results found. Run correlate() first.");
        }

        this.results.forEach(result -> System.out
                .println(result.getType() + " || " + result.getKey() + " || " + result.getEvents()));
    }

    public static void main(String[] args) throws IllegalArgumentException, IllegalAccessException {
        CorrelationEngine correlationEngine = new CorrelationEngine("000");
        correlationEngine.runCorrelate(
                1,
                CorrelationType.THREAD);

        correlationEngine.printCorrelation();
    }
}