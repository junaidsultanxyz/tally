import * as React from "react";
export interface TextFieldProps {
  label?: string;
  value?: string;
  onChange?: (value: string) => void;
  placeholder?: string;
  type?: string;
  /** Leading adornment, e.g. "$" for amount fields. */
  prefix?: React.ReactNode;
  hint?: string;
  error?: string;
  disabled?: boolean;
  id?: string;
}
export function TextField(props: TextFieldProps): JSX.Element;
