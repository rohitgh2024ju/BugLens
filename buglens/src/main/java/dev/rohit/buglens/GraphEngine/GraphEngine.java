package dev.rohit.buglens.GraphEngine;

import java.util.List;

import dev.rohit.buglens.CorrelationEngine.CorrelationEngine;
import dev.rohit.buglens.CorrelationEngine.model.CorrelationResult;
import dev.rohit.buglens.GraphEngine.model.EventGraph;
import dev.rohit.buglens.GraphEngine.service.GraphBuilder;
import dev.rohit.buglens.IngestionEngine.context.ProcessingContext;
import dev.rohit.buglens.IngestionEngine.format.FormatDetector;
import dev.rohit.buglens.IngestionEngine.format.LogFormat;

public class GraphEngine {

        public static void main(String[] args)
                        throws Exception {

                FormatDetector formatDetector = new FormatDetector();
                LogFormat format = formatDetector.detect();

                ProcessingContext processingContext = new ProcessingContext();
                processingContext.setLogFormat(format);

                CorrelationEngine correlationEngine = new CorrelationEngine("000", processingContext);

                List<CorrelationResult> results = correlationEngine.runCorrelate(1);

                EventGraph eventGraph = new EventGraph();

                GraphBuilder graphBuilder = new GraphBuilder(eventGraph);

                graphBuilder.build(results);

                // eventGraph.printVertices();
                System.out.println("--------------------------------");
                System.out.println("Vertices: "
                                + eventGraph.getGraph().vertexSet().size());

                System.out.println("Edges: "
                                + eventGraph.getGraph().edgeSet().size());

                eventGraph.printEdges();
        }
}