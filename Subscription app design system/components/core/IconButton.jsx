import React from "react";
export function IconButton({ children, label, variant = "ghost", size = 44, disabled = false, onClick, ...rest }) {
  const bg = { ghost: "transparent", surface: "var(--bg-surface)", soft: "var(--accent-soft)" }[variant];
  const fg = variant === "soft" ? "var(--accent-soft-fg)" : "var(--text-secondary)";
  return (
    <button type="button" aria-label={label} title={label} disabled={disabled} onClick={onClick}
      style={{ display: "inline-flex", alignItems: "center", justifyContent: "center",
        width: Math.max(size, 44), height: Math.max(size, 44), minWidth: "var(--tap-min)", minHeight: "var(--tap-min)",
        borderRadius: "var(--radius-md)", border: variant === "surface" ? "1.5px solid var(--border-default)" : "none",
        background: bg, color: fg, cursor: disabled ? "not-allowed" : "pointer", opacity: disabled ? 0.5 : 1,
        transition: "background .15s ease" }} {...rest}>
      {children}
    </button>
  );
}
