package dev.rohit.buglens.GraphEngine;

import java.util.List;

import dev.rohit.buglens.CorrelationEngine.CorrelationEngine;
import dev.rohit.buglens.CorrelationEngine.model.CorrelationResult;
import dev.rohit.buglens.CorrelationEngine.model.CorrelationType;
import dev.rohit.buglens.GraphEngine.model.EventGraph;
import dev.rohit.buglens.GraphEngine.service.GraphBuilder;

public class GraphEngine {

    public static void main(String[] args)
            throws Exception {

        CorrelationEngine correlationEngine = new CorrelationEngine("000");

        List<CorrelationResult> results = correlationEngine.runCorrelate(
                1,
                CorrelationType.REQUEST_ID,
                CorrelationType.TIME);

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