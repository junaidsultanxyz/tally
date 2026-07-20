import React from "react";
export function Card({ children, padding = "var(--space-5)", elevated = false, onClick, style = {}, ...rest }) {
  return (
    <div onClick={onClick} style={{
      background: "var(--bg-surface)", borderRadius: "var(--radius-xl)",
      border: "1px solid var(--border-subtle)", boxShadow: elevated ? "var(--shadow-md)" : "var(--shadow-xs)",
      padding, cursor: onClick ? "pointer" : "default", ...style }} {...rest}>
      {children}
    </div>
  );
}
