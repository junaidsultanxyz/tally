import React from "react";
export function SummaryStat({ label, value, sublabel, tone = "default" }) {
  const valueColor = { default: "var(--text-primary)", accent: "var(--accent)", warning: "var(--warning-fg)" }[tone];
  return (
    <div style={{ display: "flex", flexDirection: "column", gap: "4px", fontFamily: "var(--font-sans)" }}>
      <span style={{ fontSize: "var(--fs-caption)", fontWeight: "var(--fw-semibold)", color: "var(--text-secondary)",
        textTransform: "uppercase", letterSpacing: "var(--tracking-wide)" }}>{label}</span>
      <span style={{ fontSize: "var(--fs-title)", fontWeight: "var(--fw-bold)", color: valueColor,
        fontVariantNumeric: "tabular-nums", letterSpacing: "-0.01em", lineHeight: 1.1 }}>{value}</span>
      {sublabel && <span style={{ fontSize: "var(--fs-caption)", color: "var(--text-tertiary)" }}>{sublabel}</span>}
    </div>
  );
}
