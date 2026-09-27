import { Search } from "lucide-react";
import { Settings } from "lucide-react";
import { useNavigate } from "react-router-dom";

import "./Header.css";

export function Header({
  collapsed,
  clientId,
  searchQuery,
  setSearchQuery,
  searchResults,
}) {
  const navigate = useNavigate();

  return (
    <header className={`header ${collapsed ? "collapsed" : ""}`}>
      <div className="search-container">
        <div className="search-bar">
          <Search className="search-icon" size={18} />

          <input
            type="text"
            value={searchQuery}
            onChange={(event) => setSearchQuery(event.target.value)}
            placeholder="Search incident, traces, services..."
          />
        </div>

        {searchQuery.trim() && (
          <div className="search-results">
            {searchResults.length === 0 ? (
              <div className="search-no-results">No matching incidents</div>
            ) : (
              searchResults.slice(0, 8).map((incident) => (
                <div
                  className="search-result"
                  key={incident.id}
                  onClick={() => {
                    navigate("/incidents", {
                      state: {
                        incidentId: incident.id,
                      },
                    });

                    setSearchQuery("");
                  }}
                >
                  <div className="search-result-content">
                    <div className="search-result-title">{incident.title}</div>

                    <div className="search-result-meta">
                      <span>{incident.id}</span>

                      <span>·</span>

                      <span>{incident.service}</span>
                    </div>
                  </div>
                </div>
              ))
            )}
          </div>
        )}
      </div>

      <div className="header-settings">
        <Settings className="settings-icon" size={20} />
      </div>

      <div className="separator"></div>

      <div className="client-identity">
        <span>{`ClientId : ${clientId}`}</span>
      </div>
    </header>
  );
}
