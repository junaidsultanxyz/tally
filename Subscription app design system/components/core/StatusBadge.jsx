import React from "react";
const map = {
  active:   { fg: "var(--success-fg)", bg: "var(--success-bg)", glyph: "\u2713" },
  due:      { fg: "var(--warning-fg)", bg: "var(--warning-bg)", glyph: "\u25CF" },
  overdue:  { fg: "var(--danger-fg)",  bg: "var(--danger-bg)",  glyph: "!" },
  paused:   { fg: "var(--info-fg)",    bg: "var(--info-bg)",    glyph: "\u2016" },
  trial:    { fg: "var(--accent-soft-fg)", bg: "var(--accent-soft)", glyph: "\u2605" },
};
export function StatusBadge({ status = "active", children, showIcon = true }) {
  const s = map[status] || map.active;
  return (
    <span style={{ display: "inline-flex", alignItems: "center", gap: "5px",
      background: s.bg, color: s.fg, borderRadius: "var(--radius-pill)",
      padding: "3px 10px", fontSize: "var(--fs-caption)", fontWeight: "var(--fw-semibold)",
      lineHeight: 1.2 }}>
      {showIcon && <span aria-hidden="true" style={{ fontSize: "0.9em" }}>{s.glyph}</span>}
      {children || status}
    </span>
  );
}
