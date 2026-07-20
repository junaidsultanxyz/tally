import * as React from "react";
export interface SummaryStatProps {
  label: string;
  value: string;
  sublabel?: string;
  tone?: "default" | "accent" | "warning";
}
export function SummaryStat(props: SummaryStatProps): JSX.Element;
