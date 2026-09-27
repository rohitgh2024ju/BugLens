import { useState } from "react";
import { Routes, Route } from "react-router-dom";
import { AnimatePresence, motion } from "framer-motion";
import { useLocation } from "react-router-dom";

import { Sidebar } from "./Components/Sidebar/Sidebar";
import { Header } from "./Components/Header/Header";

import { Dashboard } from "./Components/Dashboard/Dashboard";
import { Upload } from "./Components/Upload/Upload";
import { Incidents } from "./Components/Incidents/Incidents";

import { results, eventsOverview } from "./data/dashboardData";
import { incidentsData } from "./data/IncidentData";
import { enrichIncidents } from "./utils/incidentEnricher";

import "./App.css";
import { DependencyGraph } from "./Components/DependencyGraph/DependencyGraph";
import { searchIncidents } from "./utils/searchIncidents";
import { IncidentDetails } from "./Components/Incidents/IncidentDetails";

function PageTransition({ children }) {
  return (
    <motion.div
      initial={{
        opacity: 0,
        y: 8,
      }}
      animate={{
        opacity: 1,
        y: 0,
      }}
      exit={{
        opacity: 0,
        y: -8,
      }}
      transition={{
        duration: 0.2,
        ease: "easeOut",
      }}
    >
      {children}
    </motion.div>
  );
}

function App({ clientId }) {
  const [collapsed, setCollapsed] = useState(false);
  const initialIncidents = enrichIncidents(incidentsData);
  const [incidentState, setIncidentState] = useState(initialIncidents);
  const [searchQuery, setSearchQuery] = useState("");
  const location = useLocation();

  const searchResults = searchIncidents(incidentsData, searchQuery);

  const updateIncidentStatus = (incidentId, newStatus) => {
    setIncidentState((currentIncidents) =>
      currentIncidents.map((incident) =>
        incident.id === incidentId
          ? {
              ...incident,
              status: newStatus,
            }
          : incident,
      ),
    );
  };

  return (
    <div className="app">
      <Sidebar collapsed={collapsed} setCollapsed={setCollapsed} />

      <div className={`main ${collapsed ? "collapsed" : ""}`}>
        <Header
          collapsed={collapsed}
          clientId={clientId}
          searchQuery={searchQuery}
          setSearchQuery={setSearchQuery}
          searchResults={searchResults}
        />
        <main className="main-body">
          <AnimatePresence mode="wait">
            <Routes location={location} key={location.pathname}>
              <Route
                path="/"
                element={
                  <PageTransition>
                    <Dashboard
                      results={results}
                      eventsOverview={eventsOverview}
                      incidents={incidentState}
                    />
                  </PageTransition>
                }
              />

              <Route
                path="/incidents"
                element={
                  <PageTransition>
                    <Incidents incidents={incidentState} />
                  </PageTransition>
                }
              />

              <Route
                path="/incidents/:incidentId"
                element={
                  <PageTransition>
                    <IncidentDetails incidents={incidentState} updateIncidentStatus={updateIncidentStatus} />
                  </PageTransition>
                }
              />

              <Route
                path="/upload"
                element={
                  <PageTransition>
                    <Upload />
                  </PageTransition>
                }
              />

              <Route
                path="/dependency-graph"
                element={
                  <PageTransition>
                    <DependencyGraph incidents={incidentState} />
                  </PageTransition>
                }
              />
            </Routes>
          </AnimatePresence>
        </main>
      </div>
    </div>
  );
}

export default App;
