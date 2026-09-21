import { Search } from "lucide-react";
import { Settings } from "lucide-react";
import "./Header.css";

export function Header({ collapsed, clientId }) {
  return (
    <header className={`header ${collapsed ? "collapsed" : ""}`}>
      <div className="search-bar">
        <Search className="search-icon" size={18} />
        <input type="text" placeholder="Search incident, traces, services..." />
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
