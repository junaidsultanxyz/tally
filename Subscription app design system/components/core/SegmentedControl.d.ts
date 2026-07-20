import * as React from "react";
export interface SegmentedControlProps {
  options: (string | { value: string; label: string })[];
  value: string;
  onChange?: (value: string) => void;
}
export function SegmentedControl(props: SegmentedControlProps): JSX.Element;
