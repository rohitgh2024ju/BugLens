package dev.rohit.buglens.GraphEngine;

import java.util.List;

import dev.rohit.buglens.BLR.Bundles.CorrelationBundle;
import dev.rohit.buglens.CorrelationEngine.CorrelationEngine;
import dev.rohit.buglens.CorrelationEngine.model.CorrelationResult;
import dev.rohit.buglens.GraphEngine.model.EventGraph;
import dev.rohit.buglens.GraphEngine.service.GraphBuilder;
import dev.rohit.buglens.GraphEngine.service.GraphPersistenceService;
import dev.rohit.buglens.IngestionEngine.context.ProcessingContext;
import dev.rohit.buglens.IngestionEngine.format.FormatDetector;
import dev.rohit.buglens.IngestionEngine.format.LogFormat;

public class GraphEngine {

        public static void main(String[] args) throws Exception {

                // Detect log format
                FormatDetector formatDetector = new FormatDetector();

                LogFormat format = formatDetector.detect();

                // Create processing context
                ProcessingContext processingContext = new ProcessingContext();

                processingContext.setLogFormat(format);

                // Run correlation
                CorrelationEngine correlationEngine = new CorrelationEngine(
                                "000",
                                processingContext);

                List<CorrelationResult> results = correlationEngine.runCorrelate(1);

                // Get the bundle used for correlation
                CorrelationBundle bundle = correlationEngine.getBundle();

                // Build graph
                EventGraph eventGraph = new EventGraph("000");

                GraphBuilder graphBuilder = new GraphBuilder(eventGraph);

                graphBuilder.build(
                                results,
                                bundle);

                System.out.println("--------------------------------");

                System.out.println(
                                "Vertices: "
                                                + eventGraph.getGraph()
                                                                .vertexSet()
                                                                .size());

                System.out.println(
                                "Edges: "
                                                + eventGraph.getGraph()
                                                                .edgeSet()
                                                                .size());

                eventGraph.printEdges();

                GraphPersistenceService graphPersistenceService = new GraphPersistenceService("000");
                graphPersistenceService.save(eventGraph);
        }
}