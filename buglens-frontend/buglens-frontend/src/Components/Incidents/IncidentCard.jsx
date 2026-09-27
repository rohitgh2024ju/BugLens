import {
  AlertCircle,
  ArrowRight,
  Clock3,
  Server,
  TriangleAlert,
} from "lucide-react";

import { useNavigate } from "react-router-dom";
import "./IncidentCard.css";

export function IncidentCard({ incident }) {
  const navigate = useNavigate();

  return (
    <div
      className="incident-card"
      onClick={() => navigate(`/incidents/${incident.id}`)}
    >
      <div className="incident-card-header">
        <div className="incident-id">
          <span className={`incident-dot ${incident.severity}`}></span>
          {incident.id}
        </div>
      </div>

      <div className="incident-badges">
        <span className={`severity-badge ${incident.severity}`}>
          <TriangleAlert size={13} />
          {incident.severity}
        </span>

        <span className={`status-badge ${incident.status}`}>
          {incident.status}
        </span>
      </div>

      <div className="incident-main">
        <h2>{incident.title}</h2>
        <p className="incident-description">{incident.description}</p>
      </div>

      <div className="incident-meta">
        <div className="incident-meta-item">
          <Server size={14} />
          <span>{incident.service}</span>
        </div>

        <div className="incident-meta-item">
          <AlertCircle size={14} />
          <span>{incident.exception}</span>
        </div>

        <div className="incident-meta-item">
          <Clock3 size={14} />
          <span>{incident.timestamp}</span>
        </div>

        <div className="incident-meta-item">
          <span>Events: {incident.eventCount}</span>
        </div>

        <div className="incident-meta-item">
          <span>Related: {incident.relatedEventCount}</span>
        </div>
      </div>

      <div className="incident-card-footer">
        <div className="incident-tags">
          {incident.tags.map((tag) => (
            <span className="incident-tag" key={tag}>
              {tag}
            </span>
          ))}
        </div>

        <button className="incident-open-button">
          <ArrowRight size={18} />
        </button>
      </div>
    </div>
  );
}
