import * as React from "react";
export interface SwitchProps {
  checked?: boolean;
  onChange?: (checked: boolean) => void;
  label?: string;
  description?: string;
  disabled?: boolean;
  id?: string;
}
/**
 * @startingPoint section="Forms" subtitle="Accessible toggle with label + description" viewport="700x120"
 */
export function Switch(props: SwitchProps): JSX.Element;
