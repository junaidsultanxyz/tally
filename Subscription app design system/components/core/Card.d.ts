import * as React from "react";
export interface CardProps {
  children?: React.ReactNode;
  padding?: string;
  elevated?: boolean;
  onClick?: (e: React.MouseEvent) => void;
  style?: React.CSSProperties;
}
export function Card(props: CardProps): JSX.Element;
