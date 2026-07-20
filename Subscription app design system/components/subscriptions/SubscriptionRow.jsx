import React from "react";
import { StatusBadge } from "../core/StatusBadge.jsx";
export function SubscriptionRow({ name, category, amount, cadence = "mo", monogram, tint = "var(--accent-soft)", status, statusLabel, onClick }) {
  return (
    <button type="button" onClick={onClick} style={{ width: "100%", display: "flex", alignItems: "center", gap: "14px",
      background: "transparent", border: "none", cursor: onClick ? "pointer" : "default", textAlign: "left",
      padding: "12px 4px", minHeight: "var(--tap-min)", fontFamily: "var(--font-sans)" }}>
      <span aria-hidden="true" style={{ flexShrink: 0, width: "44px", height: "44px", borderRadius: "var(--radius-md)",
        background: tint, color: "var(--accent-soft-fg)", display: "flex", alignItems: "center", justifyContent: "center",
        fontWeight: "var(--fw-bold)", fontSize: "var(--fs-body-lg)" }}>{monogram || (name ? name[0] : "?")}</span>
      <span style={{ flex: 1, minWidth: 0, display: "flex", flexDirection: "column", gap: "3px" }}>
        <span style={{ display: "flex", alignItems: "center", gap: "8px" }}>
          <span style={{ fontSize: "var(--fs-body)", fontWeight: "var(--fw-semibold)", color: "var(--text-primary)", whiteSpace: "nowrap", overflow: "hidden", textOverflow: "ellipsis" }}>{name}</span>
          {status && <StatusBadge status={status}>{statusLabel}</StatusBadge>}
        </span>
        {category && <span style={{ fontSize: "var(--fs-caption)", color: "var(--text-tertiary)" }}>{category}</span>}
      </span>
      <span style={{ flexShrink: 0, textAlign: "right", fontVariantNumeric: "tabular-nums" }}>
        <span style={{ fontSize: "var(--fs-body)", fontWeight: "var(--fw-semibold)", color: "var(--text-primary)" }}>{amount}</span>
        <span style={{ display: "block", fontSize: "var(--fs-micro)", color: "var(--text-tertiary)" }}>/{cadence}</span>
      </span>
    </button>
  );
}
