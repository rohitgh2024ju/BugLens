import "./Sidebar.css";

export function Sidebar({ collapsed, setCollapsed }) {
  return (
    <aside className={`sidebar ${collapsed ? "collapsed" : ""}`}>
      <div className="sidebar-logo">
        <div className="sidebar-icon">🐞</div>

        <div className="logo-info">
          <div className="logo-title">BugLens</div>

          <div className="logo-version">v1.0.0 · prod</div>
        </div>
      </div>

      <div className="sidebar-content">
        <nav className="sidebar-nav">
          <div className="nav-item active">
            <span>▦</span>
            <span className="nav-label">Dashboard</span>
          </div>

          <div className="nav-item">
            <span>☁</span>
            <span className="nav-label">Upload Logs</span>
          </div>

          <div className="nav-item">
            <span>⬡</span>
            <span className="nav-label">Incidents</span>
          </div>

          <div className="nav-item">
            <span>♧</span>
            <span className="nav-label">Dependency Graph</span>
          </div>
        </nav>
      </div>

      {/* Collapse button */}
      <button
        className="collapse-button"
        onClick={() => setCollapsed((prev) => !prev)}
      >
        {collapsed ? "=>" : "<="}
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
