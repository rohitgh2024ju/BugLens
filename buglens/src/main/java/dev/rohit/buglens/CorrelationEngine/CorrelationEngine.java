package dev.rohit.buglens.CorrelationEngine;

import java.util.List;

import dev.rohit.buglens.BLR.Bundles.CorrelationBundle;
import dev.rohit.buglens.CorrelationEngine.model.CorrelationGroup;
import dev.rohit.buglens.NormalizerEngine.model.NormalizedEvent;
import dev.rohit.buglens.CorrelationEngine.model.CorrelationResult;
import dev.rohit.buglens.IngestionEngine.context.ProcessingContext;
import dev.rohit.buglens.IngestionEngine.format.FormatDetector;
import dev.rohit.buglens.IngestionEngine.format.LogFormat;

public class CorrelationEngine {

        private final String clientId;
        private final ProcessingContext context;

        private List<CorrelationResult> results;
        private CorrelationGroup correlationGroup;

        public CorrelationEngine(
                        String clientId,
                        ProcessingContext context) {

                this.clientId = clientId;
                this.context = context;
        }

        public List<CorrelationResult> runCorrelate(
                        long time)
                        throws IllegalArgumentException, IllegalAccessException {

                this.correlationGroup = new CorrelationGroup(
                                this.clientId,
                                this.context);

                this.results = correlationGroup.correlateAll(time);

                return this.results;
        }

        public List<NormalizedEvent> getEvents() {
                return this.correlationGroup.getEvents();
        }

        public CorrelationBundle getBundle() {
                return this.correlationGroup.getBundle();
        }

        public void printCorrelation() {

                if (this.results == null) {
                        throw new IllegalStateException(
                                        "No correlation results found. Run correlate() first.");
                }

                this.results.forEach(result -> System.out.println(
                                result.getType()
                                                + " || "
                                                + result.getKey()
                                                + " || "
                                                + result.getEvents()));
        }

        public static void main(String[] args)
                        throws IllegalArgumentException, IllegalAccessException {

                FormatDetector formatDetector = new FormatDetector();
                LogFormat format = formatDetector.detect();

                ProcessingContext processingContext = new ProcessingContext();
                processingContext.setLogFormat(format);

                CorrelationEngine correlationEngine = new CorrelationEngine(
                                "000",
                                processingContext);

                correlationEngine.runCorrelate(
                                1);

                correlationEngine.printCorrelation();
        }
}