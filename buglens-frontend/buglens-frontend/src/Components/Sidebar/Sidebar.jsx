import "./Sidebar.css";
import {
  Bug,
  LayoutDashboard,
  UploadCloud,
  CircleAlert,
  Share2,
  PanelLeftClose,
  PanelRightClose
} from "lucide-react";
import { NavLink } from "react-router-dom";

export function Sidebar({ collapsed, setCollapsed }) {
  return (
    <aside className={`sidebar ${collapsed ? "collapsed" : ""}`}>
      <div className="sidebar-logo">
        <Bug size={34} strokeWidth={2.5} />

        <div className="logo-info">
          <div className="logo-title">BugLens</div>

          <div className="logo-version">v1.0.0 · prod</div>
        </div>
      </div>

      <div className="sidebar-content">
        <nav className="sidebar-nav">
          <NavLink
            to="/"
            className={({ isActive }) => `nav-item ${isActive ? "active" : ""}`}
          >
            <LayoutDashboard size={18} />
            <span className="nav-label">Dashboard</span>
          </NavLink>

          <NavLink
            to="/upload"
            className={({ isActive }) => `nav-item ${isActive ? "active" : ""}`}
          >
            <UploadCloud size={18} />
            <span className="nav-label">Upload Logs</span>
          </NavLink>

          <NavLink
            to="/incidents"
            className={({ isActive }) => `nav-item ${isActive ? "active" : ""}`}
          >
            <CircleAlert size={18} />
            <span className="nav-label">Incidents</span>
          </NavLink>

          <NavLink
            to="/dependency-graph"
            className={({ isActive }) => `nav-item ${isActive ? "active" : ""}`}
          >
            <Share2 size={18} />
            <span className="nav-label">Dependency Graph</span>
          </NavLink>
        </nav>
      </div>

      {/* Collapse button */}
      <button
        className="collapse-button"
        onClick={() => setCollapsed((prev) => !prev)}
      >
        {collapsed ? (<PanelRightClose size={18} />) : (<PanelLeftClose size={18} />)}
      </button>

      <div className="system-status">
        <div className="status-title">
          <span className="status-dot"></span>

          <span className="status-label">System Online</span>
        </div>

        <div className="status-row">
          <span>Collector</span>
          <span>healthy</span>
        </div>

        <div className="status-row">
          <span>Analyzer</span>
          <span>healthy</span>
        </div>

        <div className="status-row">
          <span>Graph Engine</span>
          <span>idle</span>
        </div>
      </div>
    </aside>
  );
}
