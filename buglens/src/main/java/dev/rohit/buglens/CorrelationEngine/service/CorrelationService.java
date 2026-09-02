package dev.rohit.buglens.CorrelationEngine.service;

import dev.rohit.buglens.BLR.Bundles.CorrelationBundle;
import dev.rohit.buglens.BLR.Bundles.SpringBootBundle;
import dev.rohit.buglens.IngestionEngine.context.ProcessingContext;
import dev.rohit.buglens.IngestionEngine.format.LogFormat;

public class CorrelationService {

    private final ProcessingContext context;

    public CorrelationService(ProcessingContext context) {
        this.context = context;
    }

    public CorrelationBundle getBundle() {

        LogFormat format = context.getLogFormat();

        return switch (format.getName()) {
            case "Spring Boot / Java Standard Log" -> new SpringBootBundle();
            default -> throw new IllegalArgumentException(
                    "Unsupported log format: " + format);
        };
    }
}