import React from "react";
export function TextField({ label, value, onChange, placeholder, type = "text", prefix, hint, error, disabled = false, id }) {
  const fieldId = id || (label ? label.toLowerCase().replace(/\s+/g, "-") : undefined);
  return (
    <label htmlFor={fieldId} style={{ display: "flex", flexDirection: "column", gap: "6px", fontFamily: "var(--font-sans)" }}>
      {label && <span style={{ fontSize: "var(--fs-callout)", fontWeight: "var(--fw-semibold)", color: "var(--text-secondary)" }}>{label}</span>}
      <span style={{ display: "flex", alignItems: "center", gap: "8px",
        background: "var(--bg-surface)", border: `1.5px solid ${error ? "var(--danger-fg)" : "var(--border-default)"}`,
        borderRadius: "var(--radius-md)", padding: "0 14px", minHeight: "var(--tap-min)", opacity: disabled ? 0.5 : 1 }}>
        {prefix && <span style={{ color: "var(--text-tertiary)", fontWeight: "var(--fw-semibold)" }}>{prefix}</span>}
        <input id={fieldId} type={type} value={value} placeholder={placeholder} disabled={disabled}
          onChange={(e) => onChange && onChange(e.target.value)}
          aria-invalid={!!error}
          style={{ flex: 1, border: "none", outline: "none", background: "transparent",
            fontFamily: "inherit", fontSize: "var(--fs-body)", color: "var(--text-primary)",
            minHeight: "calc(var(--tap-min) - 4px)" }}/>
      </span>
      {error ? <span style={{ fontSize: "var(--fs-caption)", color: "var(--danger-fg)" }}>{error}</span>
             : hint ? <span style={{ fontSize: "var(--fs-caption)", color: "var(--text-tertiary)" }}>{hint}</span> : null}
    </label>
  );
}
