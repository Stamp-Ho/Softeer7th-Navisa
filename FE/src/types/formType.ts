export type FormSection = {
  name: string;
  description?: string;
  fields: Field[];
};
type Field = {
  label: string;
  isOptional?: boolean;
  description?: string;
  disableToggleDescription?: string;
  inputLines: inputLine[];
};
type inputLine = {
  getMany?: boolean;
  addButtonAtFirstLine?: boolean;
  addButtonAtBelowLines?: boolean;
  inputs: input[];
};
export type input = {
  inputType: formInputType;
  colSpan?: number;
  inputDescription?: string;
  englishDescription?: string;
  placeholder?: string;
  disableToggleDescription?: string;
  options?: string[];
  changeRow?: boolean;
};
type formInputType =
  | "text"
  | "radio"
  | "selector"
  | "date"
  | "image"
  | "textArea"
  | "timeRange"
  | "phoneNumber";
