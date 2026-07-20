import React from "react";

const sizes = {
  sm: { fontSize: "var(--fs-callout)", padding: "0 14px", minHeight: "36px" },
  md: { fontSize: "var(--fs-body)", padding: "0 20px", minHeight: "var(--tap-min)" },
  lg: { fontSize: "var(--fs-body-lg)", padding: "0 26px", minHeight: "56px" },
};

const variants = {
  primary:   { background: "var(--accent)", color: "var(--text-on-accent)", border: "1.5px solid transparent" },
  secondary: { background: "var(--bg-surface)", color: "var(--text-primary)", border: "1.5px solid var(--border-default)" },
  ghost:     { background: "transparent", color: "var(--accent)", border: "1.5px solid transparent" },
  danger:    { background: "var(--danger-bg)", color: "var(--danger-fg)", border: "1.5px solid transparent" },
};

export function Button({
  children, variant = "primary", size = "md", fullWidth = false,
  disabled = false, iconLeft = null, iconRight = null, onClick, ...rest
}) {
  const style = {
    display: "inline-flex", alignItems: "center", justifyContent: "center", gap: "8px",
    fontFamily: "var(--font-sans)", fontWeight: "var(--fw-semibold)",
    borderRadius: "var(--radius-pill)", cursor: disabled ? "not-allowed" : "pointer",
    width: fullWidth ? "100%" : "auto", transition: "background .15s ease, transform .1s ease",
    opacity: disabled ? 0.5 : 1, whiteSpace: "nowrap",
    ...sizes[size], ...variants[variant],
  };
  return (
    <button type="button" style={style} disabled={disabled} onClick={onClick} aria-disabled={disabled} {...rest}>
      {iconLeft}{children}{iconRight}
    </button>
  );
}
