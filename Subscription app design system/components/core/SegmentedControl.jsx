import React from "react";
export function SegmentedControl({ options = [], value, onChange }) {
  return (
    <div role="tablist" style={{ display: "flex", gap: "2px", padding: "3px",
      background: "var(--bg-sunken)", borderRadius: "var(--radius-md)" }}>
      {options.map((opt) => {
        const val = typeof opt === "string" ? opt : opt.value;
        const label = typeof opt === "string" ? opt : opt.label;
        const active = val === value;
        return (
          <button key={val} role="tab" aria-selected={active} onClick={() => onChange && onChange(val)}
            style={{ flex: 1, border: "none", cursor: "pointer", minHeight: "38px", padding: "0 14px",
              borderRadius: "calc(var(--radius-md) - 3px)", fontFamily: "var(--font-sans)",
              fontSize: "var(--fs-callout)", fontWeight: "var(--fw-semibold)",
              background: active ? "var(--bg-surface)" : "transparent",
              color: active ? "var(--text-primary)" : "var(--text-secondary)",
              boxShadow: active ? "var(--shadow-xs)" : "none", transition: "all .15s ease" }}>
            {label}
          </button>
        );
      })}
    </div>
  );
}
