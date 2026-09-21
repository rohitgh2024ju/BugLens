import { useState } from "react";
import { Sidebar } from "./Components/Sidebar/Sidebar";
import { Header } from "./Components/Header/Header";
import "./App.css";
import { Dashboard } from "./Components/Dashboard/Dashboard";

function App({ clientId }) {
  const [collapsed, setCollapsed] = useState(false);
  const results = {
    totalEvents: 184,
    detectedIncidents: 6,
    affectedServices: 24,
    errorEvents: 31,
    criticalEvents: 2,
    correlations: 8,
  };

  const testEventsOverview = {
    totalCount: 184,

    severities: [
      {
        name: "Critical",
        count: 2,
        percentage: 1.1,
        color: "critical",
      },
      {
        name: "High",
        count: 8,
        percentage: 4.3,
        color: "high",
      },
      {
        name: "Medium",
        count: 14,
        percentage: 7.6,
        color: "medium",
      },
      {
        name: "Low",
        count: 27,
        percentage: 14.7,
        color: "low",
      },
      {
        name: "Info",
        count: 133,
        percentage: 72.3,
        color: "info",
      },
    ],
  };

  return (
    <div className="app">
      <Sidebar collapsed={collapsed} setCollapsed={setCollapsed} />

      <div className={`main ${collapsed ? "collapsed" : ""}`}>
        <Header collapsed={collapsed} clientId={clientId} />

        <main className="main-body">
          <Dashboard results={results} eventsOverview={testEventsOverview} />
        </main>
      </div>
    </div>
  );
}

export default App;
