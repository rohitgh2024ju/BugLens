package dev.rohit.buglens.Application;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.springframework.stereotype.Service;

import dev.rohit.buglens.BLR.Bundles.CorrelationBundle;
import dev.rohit.buglens.CollectorEngine.CollectorEngine;
import dev.rohit.buglens.Configpaths.BugLensPaths;
import dev.rohit.buglens.CorrelationEngine.CorrelationEngine;
import dev.rohit.buglens.CorrelationEngine.model.CorrelationResult;
import dev.rohit.buglens.GraphEngine.model.EventGraph;
import dev.rohit.buglens.GraphEngine.service.GraphBuilder;
import dev.rohit.buglens.IncidentEngine.detector.FailureIncidentDetector;
import dev.rohit.buglens.IncidentEngine.model.FailureContext;
import dev.rohit.buglens.IncidentEngine.model.Incident;
import dev.rohit.buglens.IncidentEngine.service.FailureContextService;
import dev.rohit.buglens.IncidentEngine.service.IncidentService;
import dev.rohit.buglens.IncidentGroupingEngine.model.IncidentGroup;
import dev.rohit.buglens.IncidentGroupingEngine.service.IncidentGroupingService;
import dev.rohit.buglens.IngestionEngine.context.ProcessingContext;
import dev.rohit.buglens.IngestionEngine.format.FormatDetector;
import dev.rohit.buglens.IngestionEngine.format.LogFormat;
import dev.rohit.buglens.NormalizerEngine.Normalizer;
import dev.rohit.buglens.NormalizerEngine.model.NormalizedEvent;
import dev.rohit.buglens.QueryLayer.config.NitriteMultiTenantConfig;
import dev.rohit.buglens.QueryLayer.repository.EventRepository;

@Service
public class InitBugLens {
        public List<IncidentGroup> run(
                        String clientId,
                        Path inputPath,
                        long seconds,
                        double threshold,
                        boolean print)
                        throws IOException, IllegalArgumentException, IllegalAccessException {

                BugLensPaths.printPaths();

                try {

                        /*
                         * --------------------------------------------------
                         * 1. COLLECT RAW LOG DATA
                         * --------------------------------------------------
                         */

                        Path outputDirectory = BugLensPaths.LOGS_DIR;

                        CollectorEngine collectorEngine = new CollectorEngine(
                                        inputPath,
                                        outputDirectory,
                                        clientId);

                        collectorEngine.collectJson();

                        Path outputPath = collectorEngine.getOutputPath();

                        System.out.println(
                                        "Log ingestion complete: "
                                                        + outputPath.toAbsolutePath());

                        /*
                         * --------------------------------------------------
                         * 2. DETECT LOG FORMAT
                         * --------------------------------------------------
                         */

                        FormatDetector formatDetector = new FormatDetector();

                        LogFormat format = formatDetector.detect(clientId);

                        ProcessingContext processingContext = new ProcessingContext();

                        processingContext.setLogFormat(format);

                        /*
                         * --------------------------------------------------
                         * 3. NORMALIZE EVENTS
                         * --------------------------------------------------
                         */

                        Normalizer normalizer = new Normalizer(
                                        format.getParser(),
                                        outputPath);

                        List<NormalizedEvent> eventList = normalizer.normalize();
                        System.out.println("NORMALIZED EVENTS: " + eventList.size());

                        eventList.stream()
                                        .limit(3)
                                        .forEach(event -> {
                                                System.out.println("================================");
                                                System.out.println("ID: " + event.getId());
                                                System.out.println("TIMESTAMP: " + event.getTimestamp());
                                                System.out.println("SOURCE: " + event.getSource());
                                                System.out.println("OCCURRENCE: " + event.getOccurrence());
                                                System.out.println("CONTEXT: " + event.getContext());
                                                System.out.println("METADATA: " + event.getMetadata());
                                        });

                        /*
                         * --------------------------------------------------
                         * 4. STORE NORMALIZED EVENTS
                         * --------------------------------------------------
                         */

                        EventRepository eventRepository = new EventRepository();

                        eventRepository.deleteAll(clientId);

                        eventRepository.saveAll(
                                        clientId,
                                        eventList);

                        System.out.println(
                                        "Successfully saved "
                                                        + eventList.size()
                                                        + " events.");

                        /*
                         * --------------------------------------------------
                         * 5. CORRELATE EVENTS
                         * --------------------------------------------------
                         */

                        CorrelationEngine correlationEngine = new CorrelationEngine(
                                        clientId,
                                        processingContext);

                        List<CorrelationResult> results = correlationEngine.runCorrelate(seconds);

                        List<NormalizedEvent> allEvents = correlationEngine.getEvents();

                        CorrelationBundle bundle = correlationEngine.getBundle();
                        System.out.println("CORRELATION RESULTS: " + results.size());
                        System.out.println("ALL EVENTS: " + allEvents.size());

                        /*
                         * --------------------------------------------------
                         * 6. BUILD EVENT GRAPH
                         * --------------------------------------------------
                         */

                        EventGraph eventGraph = new EventGraph(clientId);

                        GraphBuilder graphBuilder = new GraphBuilder(eventGraph);

                        graphBuilder.build(
                                        allEvents,
                                        results,
                                        bundle);

                        /*
                         * --------------------------------------------------
                         * 7. DETECT FAILURE EVENTS
                         * --------------------------------------------------
                         */

                        FailureIncidentDetector failureIncidentDetector = new FailureIncidentDetector(clientId);

                        List<String> failureIds = failureIncidentDetector
                                        .detectFailureEventIds();

                        List<NormalizedEvent> failureEvents = failureIncidentDetector
                                        .detectFailureEvent(
                                                        failureIds);
                        System.out.println("FAILURE IDS: " + failureIds.size());

                        /*
                         * --------------------------------------------------
                         * 8. BUILD FAILURE CONTEXTS
                         * --------------------------------------------------
                         */

                        FailureContextService failureContextService = new FailureContextService();

                        List<FailureContext> contexts = failureContextService.buildAll(
                                        eventGraph,
                                        failureEvents,
                                        threshold);
                        System.out.println("FAILURE EVENTS: " + failureEvents.size());
                        System.out.println("FAILURE CONTEXTS: " + contexts.size());

                        /*
                         * --------------------------------------------------
                         * 9. BUILD INCIDENTS
                         * --------------------------------------------------
                         */

                        IncidentService incidentService = new IncidentService();

                        List<Incident> incidents = incidentService.buildIncidents(
                                        contexts);
                        System.out.println("INCIDENTS: " + incidents.size());

                        /*
                         * --------------------------------------------------
                         * 10. DISPLAY INCIDENTS
                         * --------------------------------------------------
                         */

                        // incidentService.viewAllIncidents();

                        IncidentGroupingService incidentGroupingService = new IncidentGroupingService();
                        List<IncidentGroup> incidentGroups = incidentGroupingService.buildGroups(incidents);
                        System.out.println("INCIDENT GROUPS: " + incidentGroups.size());

                        if (print) {
                                incidentGroupingService.viewAllGroups();
                        }
                        return incidentGroups;
                } finally {

                        /*
                         * --------------------------------------------------
                         * CLEANUP CLIENT RESOURCES
                         * --------------------------------------------------
                         */

                        NitriteMultiTenantConfig.closeClient(clientId);
                }
        }

        public static void main(String[] args)
                        throws IOException,
                        IllegalArgumentException,
                        IllegalAccessException {

                InitBugLens initBugLens = new InitBugLens();

                initBugLens.run(
                                "003",
                                Paths.get("buglens/src/test.log"),
                                10,
                                0.80, true);
        }
}