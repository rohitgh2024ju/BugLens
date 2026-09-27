import {
  ArrowLeft,
  AlertCircle,
  Clock3,
  Server,
  TriangleAlert,
} from "lucide-react";

import { useNavigate, useParams } from "react-router-dom";

import "./IncidentDetails.css";
import { useState } from "react";

export function IncidentDetails({ incidents, updateIncidentStatus }) {
  const [aiResponse, setAiResponse] = useState(null);
  const [isResolving, setIsResolving] = useState(false);
  const [resolveError, setResolveError] = useState(null);

  const { incidentId } = useParams();
  const navigate = useNavigate();

  const handleResolveWithAI = async () => {
    setIsResolving(true);
    setResolveError(null);

    try {
      const response = await fetch(
        `/api/buglens/incidents/${incident.id}/resolve`,
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
          },
          credentials: "include",
          body: JSON.stringify({
            incident,
          }),
        },
      );

      if (!response.ok) {
        throw new Error("Failed to generate AI resolution.");
      }

      const data = await response.json();

      setAiResponse(data);
    } catch (error) {
      setResolveError(error.message);
    } finally {
      setIsResolving(false);
    }
  };

  const incident = incidents.find((item) => item.id === incidentId);

  if (!incident) {
    return (
      <div className="incident-details-page">
        <h1>Incident Not Found</h1>

        <button onClick={() => navigate("/incidents")}>
          <ArrowLeft size={16} />
          Back to Incidents
        </button>
      </div>
    );
  }

  return (
    <div className="incident-details-page">
      {/* Header */}
      <div className="incident-details-header">
        <button
          className="incident-back-button"
          onClick={() => navigate("/incidents")}
        >
          <ArrowLeft size={16} />
          Back to Incidents
        </button>

        <div className="incident-details-id">{incident.id}</div>
      </div>

      {/* Main incident information */}
      <div className="incident-details-main">
        <div className="incident-details-badges">
          <div className="incident-status-info">
            <span className={`severity-badge ${incident.severity}`}>
              <TriangleAlert size={14} />
              {incident.severity}
            </span>

            <span className={`status-badge ${incident.status}`}>
              {incident.status}
            </span>
          </div>

          <select
            className={`status-selector ${incident.status}`}
            value={incident.status}
            onChange={(event) =>
              updateIncidentStatus(incident.id, event.target.value)
            }
          >
            <option value="open">Open</option>
            <option value="investigating">Investigating</option>
            <option value="resolved">Resolved</option>
            <option value="closed">Closed</option>
          </select>
        </div>

        <h1>{incident.title}</h1>

        <p className="incident-details-description">{incident.description}</p>

        {/* Metadata */}
        <div className="incident-details-meta">
          <div className="incident-details-meta-item">
            <Server size={15} />
            <span>{incident.service}</span>
          </div>

          <div className="incident-details-meta-item">
            <AlertCircle size={15} />
            <span>{incident.exception}</span>
          </div>

          <div className="incident-details-meta-item">
            <Clock3 size={15} />
            <span>{incident.timestamp}</span>
          </div>
        </div>

        {/* Statistics */}
        <div className="incident-details-stats">
          <div>
            <span>EVENTS</span>
            <strong>{incident.eventCount}</strong>
          </div>

          <div>
            <span>RELATED EVENTS</span>
            <strong>{incident.relatedEventCount}</strong>
          </div>
        </div>

        {/* Tags */}
        <div className="incident-details-tags">
          <span className="incident-details-section-title">TAGS</span>

          <div className="incident-tags">
            {incident.tags.map((tag) => (
              <span className="incident-tag" key={tag}>
                {tag}
              </span>
            ))}
          </div>
        </div>
      </div>

      {/* Failure contexts */}
      <div className="failure-contexts">
        <div className="incident-details-section-title">FAILURE CONTEXTS</div>

        {incident.failureContexts.map((context, index) => (
          <div className="failure-context" key={context.failureEvent.id}>
            <div className="failure-context-header">
              <span>Context {index + 1}</span>

              <span>{context.failureEvent.id}</span>
            </div>

            {/* Failure event */}
            <div className="failure-event">
              <div className="failure-event-title">FAILURE EVENT</div>

              <div className="failure-event-message">
                {context.failureEvent.occurrence?.message}
              </div>

              <div className="failure-event-meta">
                <span>{context.failureEvent.source?.component}</span>

                <span>{context.failureEvent.occurrence?.severity}</span>

                <span>{context.failureEvent.timestamp}</span>
              </div>
            </div>

            {/* Related events */}
            <div className="related-events">
              <div className="failure-event-title">RELATED EVENTS</div>

              {context.relatedEvents.map((event) => (
                <div className="related-event" key={event.id}>
                  <div>{event.occurrence?.message}</div>

                  <span>{event.source?.component}</span>
                </div>
              ))}
            </div>
          </div>
        ))}
      </div>

      <button
        className="resolve-button"
        onClick={handleResolveWithAI}
        disabled={isResolving}
      >
        {isResolving ? "Analyzing Incident..." : "Resolve with AI"}
      </button>

      {isResolving && (
        <div className="ai-resolution-loading">
          Analyzing incident and generating resolution...
        </div>
      )}

      {resolveError && (
        <div className="ai-resolution-error">{resolveError}</div>
      )}

      {aiResponse && (
        <div className="ai-resolution">
          <div className="ai-resolution-header">
            <h2>AI Resolution</h2>

            <span>AI Analysis</span>
          </div>

          <div className="ai-resolution-section">
            <h3>SUMMARY</h3>
            <p>{aiResponse.summary}</p>
          </div>

          <div className="ai-resolution-section">
            <h3>ROOT CAUSE</h3>
            <p>{aiResponse.rootCause}</p>
          </div>

          <div className="ai-resolution-section">
            <h3>IMPACT</h3>
            <p>{aiResponse.impact}</p>
          </div>

          <div className="ai-resolution-section">
            <h3>RECOMMENDATIONS</h3>

            <ul>
              {aiResponse.recommendation.map((item, index) => (
                <li key={index}>{item}</li>
              ))}
            </ul>
          </div>

          <div className="ai-confidence">
            Confidence: {Math.round(aiResponse.confidence * 100)}%
          </div>
        </div>
      )}
    </div>
  );
}
