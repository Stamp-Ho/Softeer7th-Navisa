export type FilterWithDropdownProps = {
  className?: string;
  category?: { name: string; items: string[] }[] | null;
  searchAgent: boolean;
  cols?: number;
  isOpen: boolean;
  paramKey: "job" | "region" | "language" | "nation";
  onClick?: () => void;
  onClose: () => void;
};
