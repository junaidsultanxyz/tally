import React from "react";
export function Switch({ checked = false, onChange, label, description, disabled = false, id }) {
  const sid = id || (label ? label.toLowerCase().replace(/\s+/g, "-") : undefined);
  const toggle = () => !disabled && onChange && onChange(!checked);
  return (
    <div style={{ display: "flex", alignItems: "center", justifyContent: "space-between", gap: "16px",
      minHeight: "var(--tap-min)", opacity: disabled ? 0.5 : 1 }}>
      {(label || description) && (
        <label htmlFor={sid} style={{ display: "flex", flexDirection: "column", gap: "2px", fontFamily: "var(--font-sans)", cursor: disabled ? "default" : "pointer" }}>
          {label && <span style={{ fontSize: "var(--fs-body)", fontWeight: "var(--fw-medium)", color: "var(--text-primary)" }}>{label}</span>}
          {description && <span style={{ fontSize: "var(--fs-caption)", color: "var(--text-tertiary)" }}>{description}</span>}
        </label>
      )}
      <button id={sid} role="switch" aria-checked={checked} aria-label={label} disabled={disabled} onClick={toggle}
        style={{ flexShrink: 0, width: "52px", height: "32px", borderRadius: "var(--radius-pill)", border: "none",
          cursor: disabled ? "not-allowed" : "pointer", padding: "3px", position: "relative",
          background: checked ? "var(--accent)" : "var(--border-strong)", transition: "background .2s ease" }}>
        <span style={{ display: "block", width: "26px", height: "26px", borderRadius: "50%", background: "var(--neutral-0)",
          boxShadow: "var(--shadow-sm)", transform: checked ? "translateX(20px)" : "translateX(0)", transition: "transform .2s ease" }}/>
      </button>
    </div>
  );
}
