package dev.rohit.buglens.IncidentEngine;

import java.util.List;

import dev.rohit.buglens.BLR.Bundles.CorrelationBundle;
import dev.rohit.buglens.CorrelationEngine.CorrelationEngine;
import dev.rohit.buglens.CorrelationEngine.model.CorrelationResult;
import dev.rohit.buglens.GraphEngine.model.EventGraph;
import dev.rohit.buglens.GraphEngine.service.GraphBuilder;
import dev.rohit.buglens.IncidentEngine.detector.FailureIncidentDetector;
import dev.rohit.buglens.IngestionEngine.context.ProcessingContext;
import dev.rohit.buglens.IngestionEngine.format.FormatDetector;
import dev.rohit.buglens.IngestionEngine.format.LogFormat;
import dev.rohit.buglens.NormalizerEngine.model.NormalizedEvent;

public class IncidentEngine {
    public static void main(String[] args) throws IllegalArgumentException, IllegalAccessException {
        FailureIncidentDetector failureIncidentDetector = new FailureIncidentDetector("000");

        FormatDetector formatDetector = new FormatDetector();
        LogFormat format = formatDetector.detect();

        ProcessingContext processingContext = new ProcessingContext();
        processingContext.setLogFormat(format);

        CorrelationEngine correlationEngine = new CorrelationEngine(
                "000",
                processingContext);

        List<CorrelationResult> results = correlationEngine.runCorrelate(1);
        List<NormalizedEvent> allEvents = correlationEngine.getEvents();

        CorrelationBundle bundle = correlationEngine.getBundle();

        EventGraph eventGraph = new EventGraph("000");

        GraphBuilder graphBuilder = new GraphBuilder(eventGraph);

        graphBuilder.build(
                allEvents,
                results,
                bundle);

        List<String> failureId = failureIncidentDetector.detectFailureEventIds();
        List<NormalizedEvent> eventList = failureIncidentDetector.detectFailureEvent(failureId);

        eventList.forEach(event -> {
            System.out.println(event);
        });

    }
}
