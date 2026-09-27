import { Handle, Position } from "@xyflow/react";

export function FailureNode({ data }) {
  const component = data.component?.split(".").pop() || "Unknown";

  return (
    <div className="failure-node">
      <Handle type="target" position={Position.Top} />

      <div className="failure-node-circle"></div>

      <div className="failure-node-label">{component}</div>

      <Handle type="source" position={Position.Bottom} />
    </div>
  );
}
