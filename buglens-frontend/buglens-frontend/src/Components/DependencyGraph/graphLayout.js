import {
    forceSimulation,
    forceLink,
    forceManyBody,
    forceCenter,
    forceCollide,
} from "d3-force";


export function applyForceLayout(nodes, edges) {

    // Create simulation nodes
    const simulationNodes = nodes.map((node) => ({
        ...node,
        x: 0,
        y: 0,
    }));

    // Create simulation links
    const simulationLinks = edges.map((edge) => ({
        ...edge,
        source: edge.source,
        target: edge.target,
    }));

    // Create force simulation
    const simulation = forceSimulation(simulationNodes)

        // Keep connected nodes together
        .force(
            "link",
            forceLink(simulationLinks)
                .id((node) => node.id)
                .distance(120)
                .strength(0.8)
        )

        // Push nodes apart
        .force(
            "charge",
            forceManyBody()
                .strength(-250)
        )

        // Keep graph centered
        .force(
            "center",
            forceCenter(0, 0)
        )

        // Prevent nodes from overlapping
        .force(
            "collision",
            forceCollide(85)
        );

    // Run simulation
    simulation.stop();

    for (let i = 0; i < 300; i++) {
        simulation.tick();
    }

    // Convert positions to React Flow
    const positionedNodes = simulationNodes.map((node) => ({
        ...node,

        position: {
            x: node.x,
            y: node.y,
        },
    }));


    return positionedNodes;
}