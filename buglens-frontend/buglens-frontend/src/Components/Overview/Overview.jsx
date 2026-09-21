import { BarChart3 } from "lucide-react";
import "./Overview.css";

export function Overview({ eventsOverview }) {
  return (
    <div className="overview">
      <div className="overview-head">
        <div className="overview-title">
          <BarChart3 size={20} />
          <h2>Event Overview</h2>
        </div>

        <span>{eventsOverview.totalCount} total events</span>
      </div>

      <div className="severity-list">
        {eventsOverview.severities.map((item) => (
          <div className="severity-row" key={item.name}>
            <div className="severity-info">
              <span className={`severity-dot ${item.color}`}></span>
              <span>{item.name}</span>

              <span className="severity-percentage">{item.percentage}%</span>

              <span className="severity-count">{item.count}</span>
            </div>

            <div className="severity-bar">
              <div
                className={`severity-fill ${item.color}`}
                style={{ width: `${item.percentage}%` }}
              ></div>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}
