import { Handle, Position } from "@xyflow/react";

export function EventNode({ data }) {
  const component = data.component?.split(".").pop() || "Unknown";

  const severity = data.event?.occurrence?.severity?.toUpperCase() || "UNKNOWN";

  return (
    <div className={`event-node severity-${severity.toLowerCase()}`}>
      <Handle type="target" position={Position.Top} />

      <div className="event-node-circle">
        <div className="event-node-inner"></div>
      </div>

      <div className="event-node-label">{component}</div>

      <Handle type="source" position={Position.Bottom} />
    </div>
  );
}
