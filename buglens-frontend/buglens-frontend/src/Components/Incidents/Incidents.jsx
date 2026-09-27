import { RefreshCw, Search } from "lucide-react";

import { useEffect, useState } from "react";

import { IncidentCard } from "./IncidentCard";

import "./Incidents.css";

import { useLocation, useNavigate } from "react-router-dom";

export function Incidents({ incidents }) {
  const [searchTerm, setSearchTerm] = useState("");
  const [severityFilter, setSeverityFilter] = useState("all");
  const [statusFilter, setStatusFilter] = useState("all");
  const [isRefreshing, setIsRefreshing] = useState(false);

  const location = useLocation();
  const navigate = useNavigate();

  const filteredIncidents = incidents.filter((incident) => {
    const normalizedSearch = searchTerm.toLowerCase();

    const matchesSearch =
      incident.title.toLowerCase().includes(normalizedSearch) ||
      incident.id.toLowerCase().includes(normalizedSearch);

    const matchesSeverity =
      severityFilter === "all" || incident.severity === severityFilter;

    const matchesStatus =
      statusFilter === "all" || incident.status === statusFilter;

    return matchesSearch && matchesSeverity && matchesStatus;
  });

  useEffect(() => {
    const incidentId = location.state?.incidentId;

    if (!incidentId) {
      return;
    }

    const element = document.getElementById(`incident-${incidentId}`);

    if (!element) {
      return;
    }

    setTimeout(() => {
      element.scrollIntoView({
        behavior: "smooth",
        block: "center",
      });

      element.classList.add("incident-highlight");

      setTimeout(() => {
        element.classList.remove("incident-highlight");
      }, 1500);
    }, 100);

    navigate("/incidents", {
      replace: true,
      state: null,
    });
  }, [location.state, navigate]);

  const handleRefresh = () => {
    setIsRefreshing(true);

    setTimeout(() => {
      setIsRefreshing(false);
    }, 700);
  };

  return (
    <div className="incidents-groups">
      <div className="incidents-header">
        <div>
          <h1>Incidents</h1>

          <p>
            {filteredIncidents.length} of {incidents.length} incidents
            <span> · </span>
            sorted by severity
          </p>
        </div>

        <button
          className="incidents-refresh"
          disabled={isRefreshing}
          onClick={handleRefresh}
        >
          <RefreshCw
            size={16}
            className={isRefreshing ? "refresh-spinning" : ""}
          />

          {isRefreshing ? "Refreshing..." : "Refresh"}
        </button>
      </div>

      <div className="incident-toolbar">
        <div className="incident-search">
          <Search size={17} />

          <input
            type="text"
            value={searchTerm}
            onChange={(event) => setSearchTerm(event.target.value)}
            placeholder="Search by name or description..."
          />
        </div>

        <div className="incident-filters">
          {/* Severity row */}
          <div className="filter-row">
            <span className="filter-label">Severity:</span>

            <button
              className={`filter-button ${
                severityFilter === "all" ? "active" : ""
              }`}
              onClick={() => setSeverityFilter("all")}
            >
              All
            </button>

            <button
              className={`filter-button ${
                severityFilter === "critical" ? "active" : ""
              }`}
              onClick={() => setSeverityFilter("critical")}
            >
              Critical
            </button>

            <button
              className={`filter-button ${
                severityFilter === "high" ? "active" : ""
              }`}
              onClick={() => setSeverityFilter("high")}
            >
              High
            </button>

            <button
              className={`filter-button ${
                severityFilter === "medium" ? "active" : ""
              }`}
              onClick={() => setSeverityFilter("medium")}
            >
              Medium
            </button>

            <button
              className={`filter-button ${
                severityFilter === "low" ? "active" : ""
              }`}
              onClick={() => setSeverityFilter("low")}
            >
              Low
            </button>
          </div>

          {/* Status row */}
          <div className="filter-row">
            <span className="filter-label">Status:</span>

            <button
              className={`filter-button ${
                statusFilter === "all" ? "active" : ""
              }`}
              onClick={() => setStatusFilter("all")}
            >
              All
            </button>

            <button
              className={`filter-button ${
                statusFilter === "open" ? "active" : ""
              }`}
              onClick={() => setStatusFilter("open")}
            >
              Open
            </button>

            <button
              className={`filter-button ${
                statusFilter === "investigating" ? "active" : ""
              }`}
              onClick={() => setStatusFilter("investigating")}
            >
              Investigating
            </button>

            <button
              className={`filter-button ${
                statusFilter === "resolved" ? "active" : ""
              }`}
              onClick={() => setStatusFilter("resolved")}
            >
              Resolved
            </button>

            <button
              className={`filter-button ${
                statusFilter === "closed" ? "active" : ""
              }`}
              onClick={() => setStatusFilter("closed")}
            >
              Closed
            </button>
          </div>
        </div>
      </div>

      <div className="incident-list">
        {filteredIncidents.map((incident) => (
          <div key={incident.id} id={`incident-${incident.id}`}>
            <IncidentCard incident={incident} />
          </div>
        ))}
      </div>
    </div>
  );
}
