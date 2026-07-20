import * as React from "react";
export interface IconButtonProps {
  children?: React.ReactNode;
  /** REQUIRED accessible name — read by screen readers. */
  label: string;
  variant?: "ghost" | "surface" | "soft";
  size?: number;
  disabled?: boolean;
  onClick?: (e: React.MouseEvent) => void;
}
export function IconButton(props: IconButtonProps): JSX.Element;
