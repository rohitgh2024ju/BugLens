package dev.rohit.buglens;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import dev.rohit.buglens.BLR.Bundles.CorrelationBundle;
import dev.rohit.buglens.CollectorEngine.CollectorEngine;
import dev.rohit.buglens.CorrelationEngine.CorrelationEngine;
import dev.rohit.buglens.CorrelationEngine.model.CorrelationResult;
import dev.rohit.buglens.GraphEngine.model.EventGraph;
import dev.rohit.buglens.GraphEngine.service.GraphBuilder;
import dev.rohit.buglens.IncidentEngine.detector.FailureIncidentDetector;
import dev.rohit.buglens.IncidentEngine.model.FailureContext;
import dev.rohit.buglens.IncidentEngine.service.FailureContextService;
import dev.rohit.buglens.IncidentEngine.service.IncidentService;
import dev.rohit.buglens.IngestionEngine.context.ProcessingContext;
import dev.rohit.buglens.IngestionEngine.format.FormatDetector;
import dev.rohit.buglens.IngestionEngine.format.LogFormat;
import dev.rohit.buglens.NormalizerEngine.Normalizer;
import dev.rohit.buglens.NormalizerEngine.model.NormalizedEvent;
import dev.rohit.buglens.QueryLayer.config.NitriteMultiTenantConfig;
import dev.rohit.buglens.QueryLayer.repository.EventRepository;

public class RunBugLens {

    public static void main(String[] args)
            throws IOException, IllegalArgumentException, IllegalAccessException {

        String clientId = "002";

        /*
         * --------------------------------------------------
         * 1. COLLECT RAW LOG DATA
         * --------------------------------------------------
         */

        Path inputPath =
                Paths.get("buglens/src/test.log");

        Path outputDirectory =
                Paths.get("buglens/logs");

        CollectorEngine collectorEngine =
                new CollectorEngine(
                        inputPath,
                        outputDirectory,
                        clientId);

        collectorEngine.collectJson();

        Path outputPath =
                collectorEngine.getOutputPath();

        System.out.println(
                "Log ingestion complete: "
                        + outputPath.toAbsolutePath());

        /*
         * --------------------------------------------------
         * 2. DETECT LOG FORMAT
         * --------------------------------------------------
         */

        FormatDetector formatDetector =
                new FormatDetector();

        LogFormat format =
                formatDetector.detect(clientId);

        ProcessingContext processingContext =
                new ProcessingContext();

        processingContext.setLogFormat(format);

        /*
         * --------------------------------------------------
         * 3. NORMALIZE EVENTS
         * --------------------------------------------------
         */

        Normalizer normalizer =
                new Normalizer(
                        format.getParser(),
                        outputPath);

        List<NormalizedEvent> eventList =
                normalizer.normalize();

        /*
         * --------------------------------------------------
         * 4. STORE NORMALIZED EVENTS
         * --------------------------------------------------
         */

        EventRepository eventRepository =
                new EventRepository();

        eventRepository.deleteAll(clientId);

        eventRepository.saveAll(
                clientId,
                eventList);

        System.out.println(
                "Successfully saved "
                        + eventList.size()
                        + " events.");

        NitriteMultiTenantConfig.closeAll();

        /*
         * --------------------------------------------------
         * 5. CORRELATE EVENTS
         * --------------------------------------------------
         */

        CorrelationEngine correlationEngine =
                new CorrelationEngine(
                        clientId,
                        processingContext);

        List<CorrelationResult> results =
                correlationEngine.runCorrelate(1);

        List<NormalizedEvent> allEvents =
                correlationEngine.getEvents();

        CorrelationBundle bundle =
                correlationEngine.getBundle();

        /*
         * --------------------------------------------------
         * 6. BUILD EVENT GRAPH
         * --------------------------------------------------
         */

        EventGraph eventGraph =
                new EventGraph(clientId);

        GraphBuilder graphBuilder =
                new GraphBuilder(eventGraph);

        graphBuilder.build(
                allEvents,
                results,
                bundle);

        /*
         * --------------------------------------------------
         * 7. DETECT FAILURE EVENTS
         * --------------------------------------------------
         */

        FailureIncidentDetector failureIncidentDetector =
                new FailureIncidentDetector(clientId);

        List<String> failureIds =
                failureIncidentDetector
                        .detectFailureEventIds();

        List<NormalizedEvent> failureEvents =
                failureIncidentDetector
                        .detectFailureEvent(
                                failureIds);

        /*
         * --------------------------------------------------
         * 8. BUILD FAILURE CONTEXTS
         * --------------------------------------------------
         */

        FailureContextService failureContextService =
                new FailureContextService();

        List<FailureContext> contexts =
                failureContextService.buildAll(
                        eventGraph,
                        failureEvents,
                        0.80);

        /*
         * --------------------------------------------------
         * 9. BUILD INCIDENTS
         * --------------------------------------------------
         */

        IncidentService incidentService =
                new IncidentService();

        incidentService.buildIncidents(
                contexts);

        /*
         * --------------------------------------------------
         * 10. DISPLAY INCIDENTS
         * --------------------------------------------------
         */

        incidentService.viewAllIncidents();
    }
}