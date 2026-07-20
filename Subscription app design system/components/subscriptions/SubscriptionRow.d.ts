import * as React from "react";
export interface SubscriptionRowProps {
  name: string;
  category?: string;
  amount: string;
  cadence?: string;
  /** Single-letter/emoji monogram shown in the tile. */
  monogram?: string;
  tint?: string;
  status?: "active" | "due" | "overdue" | "paused" | "trial";
  statusLabel?: string;
  onClick?: (e: React.MouseEvent) => void;
}
/**
 * @startingPoint section="Subscriptions" subtitle="List row: monogram, name, status, amount" viewport="700x120"
 */
export function SubscriptionRow(props: SubscriptionRowProps): JSX.Element;
