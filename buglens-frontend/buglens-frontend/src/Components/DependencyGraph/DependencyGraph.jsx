import { ChevronDown, Share2 } from "lucide-react";
import { useMemo, useState } from "react";
import { buildIncidentGraph } from "./graphBuilder";
import { applyForceLayout } from "./graphLayout";
import { ReactFlow, Controls } from "@xyflow/react";
import { FailureNode } from "./FailureNode";
import { EventNode } from "./EventNode";

import "@xyflow/react/dist/style.css";

import "./DependencyGraph.css";

// --------------------------------
// React Flow node types
// --------------------------------

const nodeTypes = {
  failure: FailureNode,
  event: EventNode,
};

export function DependencyGraph({ incidents }) {
  const [selectedIncident, setSelectedIncident] = useState(null);
  const [hoveredNodeId, setHoveredNodeId] = useState(null);
  const [isOpen, setIsOpen] = useState(false);

  // --------------------------------
  // Build graph
  // --------------------------------

  const graph = useMemo(() => {
    if (!selectedIncident) {
      return {
        nodes: [],
        edges: [],
      };
    }

    return buildIncidentGraph(selectedIncident);
  }, [selectedIncident]);

  // --------------------------------
  // Apply force layout
  // --------------------------------

  const positionedNodes = useMemo(() => {
    if (!selectedIncident) {
      return [];
    }

    return applyForceLayout(graph.nodes, graph.edges);
  }, [selectedIncident, graph]);

  // --------------------------------
  // Highlight connected edges
  // --------------------------------

  const highlightedEdges = useMemo(() => {
    return graph.edges.map((edge) => {
      const isConnected =
        hoveredNodeId === edge.source || hoveredNodeId === edge.target;

      return {
        ...edge,

        className: isConnected ? "graph-edge highlighted" : "graph-edge",
      };
    });
  }, [graph.edges, hoveredNodeId]);

  // --------------------------------
  // Debug
  // --------------------------------

  console.log("Selected incident:", selectedIncident);

  console.log("Graph:", graph);

  console.log("Positioned nodes:", positionedNodes);

  return (
    <div className="dependency-graph-page">
      <div className="dependency-graph-header">
        <h1>Dependency Graph Explorer</h1>

        <p>
          Visualize event relationships and failure propagation across services
        </p>
      </div>

      <div className="graph-selector-card">
        <div className="graph-search-bar">
          <span>SELECT INCIDENT TO VISUALIZE</span>

          <button
            className="incident-selector"
            onClick={() => setIsOpen(!isOpen)}
          >
            <span>
              {selectedIncident
                ? selectedIncident.title
                : "Choose an incident to load its dependency graph..."}
            </span>

            <ChevronDown size={18} className={isOpen ? "open" : ""} />
          </button>

          {isOpen && (
            <div className="incident-dropdown">
              {incidents.map((incident) => (
                <div
                  className="incident-option"
                  key={incident.id}
                  onClick={() => {
                    setSelectedIncident(incident);

                    setHoveredNodeId(null);

                    setIsOpen(false);
                  }}
                >
                  <div className={`severity-badge ${incident.severity}`}>
                    <span className="severity-dot"></span>

                    {incident.severity}
                  </div>

                  <div className="incident-option-main">
                    <div className="incident-option-title">
                      {incident.title}
                    </div>

                    <div className="incident-option-meta">
                      {incident.id} · {incident.service}
                    </div>
                  </div>

                  <div className="incident-event-count">
                    {incident.eventCount} events
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>
      </div>

      <div className="dependency-graph-container">
        {!selectedIncident ? (
          <div className="graph-empty-state">
            <div className="graph-empty-icon">
              <Share2 size={34} />
            </div>

            <h2>No graph selected</h2>

            <p>
              Select an incident above to visualize its event dependency graph
              and failure propagation path.
            </p>
          </div>
        ) : (
          <ReactFlow
            nodes={positionedNodes}
            edges={highlightedEdges}
            nodeTypes={nodeTypes}
            fitView
            onNodeMouseEnter={(_, node) => {
              setHoveredNodeId(node.id);
            }}
            onNodeMouseLeave={() => {
              setHoveredNodeId(null);
            }}
          >
            <div className="graph-legend">
              <div className="legend-section">
                <span className="legend-title">EVENT</span>

                <div className="legend-item">
                  <span className="legend-symbol failure"></span>
                  <span>Failure</span>
                </div>

                <div className="legend-item">
                  <span className="legend-symbol related"></span>
                  <span>Related</span>
                </div>
              </div>

              <div className="legend-divider"></div>

              <div className="legend-section">
                <span className="legend-title">SEVERITY</span>

                <div className="legend-item">
                  <span className="legend-symbol severity-critical"></span>
                  <span>Fatal / Error</span>
                </div>

                <div className="legend-item">
                  <span className="legend-symbol severity-warn"></span>
                  <span>Warn</span>
                </div>

                <div className="legend-item">
                  <span className="legend-symbol severity-info"></span>
                  <span>Info</span>
                </div>

                <div className="legend-item">
                  <span className="legend-symbol severity-other"></span>
                  <span>Other</span>
                </div>
              </div>
            </div>
            <Controls />
          </ReactFlow>
        )}
      </div>
    </div>
  );
}
