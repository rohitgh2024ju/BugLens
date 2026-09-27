import { useNavigate } from "react-router-dom";
import "./DetectedIncidents.css";

export function DetectedIncidents({ incidents }) {
  const navigate = useNavigate();
  return (
    <div className="incident-list-card">
      <div className="incident-list-head">
        <h2>Detected Incidents</h2>
        <span className="view-all" onClick={() => navigate("/incidents")}>
          View all →
        </span>
      </div>

      <div className="incident-list">
        {incidents.map((incident) => (
          <div
            className="incident-row"
            key={incident.id}
            onClick={() => {
              navigate("/incidents", {
                state: {
                  incidentId: incident.id,
                },
              });
            }}
          >
            {/* Severity dot */}
            <span className={`incident-dot ${incident.severity}`}></span>

            {/* Incident information */}
            <div className="incident-info">
              <span className="incident-title">{incident.title}</span>

              <span className="incident-details">
                {incident.service} · {incident.exception}
              </span>
            </div>

            {/* Right side */}
            <div className="incident-action">
              <span className={`severity-badge ${incident.severity}`}>
                <span className="badge-dot"></span>

                {incident.severity.toUpperCase()}
              </span>

              <span className="incident-arrow">→</span>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}
