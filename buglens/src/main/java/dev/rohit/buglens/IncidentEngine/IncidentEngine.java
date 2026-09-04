package dev.rohit.buglens.IncidentEngine;

import java.util.List;

import dev.rohit.buglens.BLR.Bundles.CorrelationBundle;
import dev.rohit.buglens.CorrelationEngine.CorrelationEngine;
import dev.rohit.buglens.CorrelationEngine.model.CorrelationResult;
import dev.rohit.buglens.GraphEngine.model.EventGraph;
import dev.rohit.buglens.GraphEngine.service.GraphBuilder;
import dev.rohit.buglens.IncidentEngine.detector.FailureIncidentDetector;
import dev.rohit.buglens.IncidentEngine.model.FailureContext;
import dev.rohit.buglens.IncidentEngine.service.FailureContextService;
import dev.rohit.buglens.IngestionEngine.context.ProcessingContext;
import dev.rohit.buglens.IngestionEngine.format.FormatDetector;
import dev.rohit.buglens.IngestionEngine.format.LogFormat;
import dev.rohit.buglens.NormalizerEngine.model.NormalizedEvent;

public class IncidentEngine {

        public static void main(String[] args)
                        throws IllegalArgumentException, IllegalAccessException {

                /*
                 * Detect log format
                 */
                FormatDetector formatDetector = new FormatDetector();

                LogFormat format = formatDetector.detect();

                ProcessingContext processingContext = new ProcessingContext();

                processingContext.setLogFormat(format);

                /*
                 * Run correlation engine
                 */
                CorrelationEngine correlationEngine = new CorrelationEngine(
                                "000",
                                processingContext);

                List<CorrelationResult> results = correlationEngine.runCorrelate(1);

                List<NormalizedEvent> allEvents = correlationEngine.getEvents();

                CorrelationBundle bundle = correlationEngine.getBundle();

                /*
                 * Build event graph
                 */
                EventGraph eventGraph = new EventGraph("000");

                GraphBuilder graphBuilder = new GraphBuilder(eventGraph);

                graphBuilder.build(
                                allEvents,
                                results,
                                bundle);

                /*
                 * Detect failure events
                 */
                FailureIncidentDetector failureIncidentDetector = new FailureIncidentDetector("000");

                List<String> failureIds = failureIncidentDetector.detectFailureEventIds();

                List<NormalizedEvent> failureEvents = failureIncidentDetector.detectFailureEvent(
                                failureIds);

                /*
                 * Build failure contexts
                 */
                FailureContextService failureContextService = new FailureContextService();

                List<FailureContext> contexts = failureContextService.buildAll(
                                eventGraph,
                                failureEvents,
                                0.80);

                /*
                 * View generated contexts
                 */
                failureContextService.viewAllContexts();
        }
}