import "./StatCard.css";

export function StatCard({ icon, value, label, variant = "default" }) {
  return (
    <div className={`stat-card ${variant}`}>
      <div className="card-icon">{icon}</div>

      <div className="card-value">{value}</div>

      <div className="card-label">{label}</div>
    </div>
  );
}
