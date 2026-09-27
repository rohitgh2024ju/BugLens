export function buildIncidentGraph(incident) {
    const nodes = [];
    const edges = [];

    const nodeMap = new Map();

    incident.failureContexts.forEach((context) => {

        const failureEvent = context.failureEvent;
        const edgeMap = new Map();


        // Failure Event → Failure Node
        if (!nodeMap.has(failureEvent.id)) {

            const failureNode = {
                id: failureEvent.id,

                type: "failure",

                position: {
                    x: 0,
                    y: 0,
                },

                data: {
                    component:
                        failureEvent.source?.component ||
                        "Unknown component",

                    event: failureEvent,
                },
            };

            nodeMap.set(
                failureEvent.id,
                failureNode
            );

            nodes.push(failureNode);
        }

        // Related Events → Event Nodes
        context.relatedEvents.forEach((relatedEvent) => {

            if (!nodeMap.has(relatedEvent.id)) {

                const eventNode = {
                    id: relatedEvent.id,

                    type: "event",

                    position: {
                        x: 0,
                        y: 0,
                    },

                    data: {
                        component:
                            relatedEvent.source?.component ||
                            "Unknown component",

                        event: relatedEvent,
                    },
                };

                nodeMap.set(
                    relatedEvent.id,
                    eventNode
                );

                nodes.push(eventNode);
            }

            // Failure → Related Event
            const edgeId =
                `${failureEvent.id}-${relatedEvent.id}`;

            if (!edgeMap.has(edgeId)) {
                const edge = {
                    id: edgeId,
                    source: failureEvent.id,
                    target: relatedEvent.id
                };

                edgeMap.set(edgeId, edge);
                edges.push(edge)
            }

        });
    });

    return {
        nodes,
        edges,
    };
}