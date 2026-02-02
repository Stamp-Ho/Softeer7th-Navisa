import type React from "react";

export type FilterWithDropdownProps = {
  isActive?: boolean;
  className?: string;
  children?: React.ReactNode;
  category?: { name: string; items: string[] }[];
  cols?: number;
  dropdownOptions?: string[];
  dropdownAlign?: string;
  onOptionClicked?: (a: number) => void;
};
