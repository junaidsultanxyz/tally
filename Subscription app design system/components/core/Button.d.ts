import * as React from "react";
export interface ButtonProps {
  children?: React.ReactNode;
  /** Visual emphasis */
  variant?: "primary" | "secondary" | "ghost" | "danger";
  size?: "sm" | "md" | "lg";
  fullWidth?: boolean;
  disabled?: boolean;
  iconLeft?: React.ReactNode;
  iconRight?: React.ReactNode;
  onClick?: (e: React.MouseEvent) => void;
}
/**
 * Primary call-to-action button. Pill-shaped, min 44px tall (respects large-tap mode).
 * @startingPoint section="Core" subtitle="Pill button — primary / secondary / ghost / danger" viewport="700x160"
 */
export function Button(props: ButtonProps): JSX.Element;
