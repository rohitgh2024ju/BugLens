import {
  Activity,
  CircleAlert,
  Server,
  TriangleAlert,
  Flame,
  Link,
} from "lucide-react";

import "./Dashboard.css";
import { StatCard } from "./StatCard";
import { Overview } from "../Overview/Overview";

export function Dashboard({ results, eventsOverview }) {
  return (
    <main className="dashboard">
      <div className="dashboard-head">
        <div className="dashboard-text">
          <h1>BugLens Dashboard</h1>
          <p>Log Analysis and Incident Investigation Overview</p>
        </div>

        <div className="analysis-signal">
          <div className="signal dot"></div>
          <span>Analysis ready</span>
        </div>
      </div>

      <div className="stat-cards">
        <StatCard
          icon={<Activity size={22} />}
          value={results.totalEvents}
          label="EVENTS INGESTED"
          variant="events"
        />

        <StatCard
          icon={<CircleAlert size={22} />}
          value={results.detectedIncidents}
          label="INCIDENTS DETECTED"
          variant="incidents"
        />

        <StatCard
          icon={<Server size={22} />}
          value={results.affectedServices}
          label="SERVICES INVOLVED"
          variant="services"
        />

        <StatCard
          icon={<TriangleAlert size={22} />}
          value={results.errorEvents}
          label="ERROR EVENTS"
          variant="errors"
        />

        <StatCard
          icon={<Flame size={22} />}
          value={results.criticalEvents}
          label="CRITICAL"
          variant="critical"
        />

        <StatCard
          icon={<Link size={22} />}
          value={results.correlations}
          label="CORRELATIONS FOUND"
          variant="correlations"
        />
      </div>

      <Overview eventsOverview={eventsOverview}/>
    </main>
  );
}
