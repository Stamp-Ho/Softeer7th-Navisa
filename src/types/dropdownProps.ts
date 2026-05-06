export type DropDownProps = {
  paramKey: "job" | "region" | "language" | "nation";
  type: string;
  cols?: number;
  searchAgent: boolean;
  category?: { name: string; items: string[] }[] | null;
  onClose: () => void;
};
