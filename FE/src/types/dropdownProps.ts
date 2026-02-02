export type DropDownProps = {
  type: string;
  cols?: number;
  category?: { name: string; items: string[] }[];
  dropdownOptions?: string[];
  onInitClicked?: () => void;
  onOptionClicked?: (arg: number) => void;
  onApply: () => void;
};
