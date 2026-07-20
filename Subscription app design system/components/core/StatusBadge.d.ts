import * as React from "react";
export interface StatusBadgeProps {
  status?: "active" | "due" | "overdue" | "paused" | "trial";
  children?: React.ReactNode;
  /** Keep true — status must never be color-only (colorblind safety). */
  showIcon?: boolean;
}
export function StatusBadge(props: StatusBadgeProps): JSX.Element;
