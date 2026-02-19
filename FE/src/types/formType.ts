export type FormSection = {
  name: string;
  description?: string;
  fields: inputFieldType[];
};
export type inputFieldType = {
  label: string;
  isOptional?: boolean;
  description?: string;
  disableToggleDescription?: string;
  canInputBlocked?: boolean;
  inputLines: inputLineType[];
  getMany?: boolean;
  addButtonAtFirstLine?: boolean;
  addButtonAtBelowLines?: boolean;
};
export type inputLineType = {
  rowId?: string;
  inputs: input[];
};
export type input = {
  requestBodyName?: string;
  inputType: formInputType;
  colSpan?: number;
  inputDescription?: string;
  englishDescription?: string;
  placeholder?: string;
  disableToggleDescription?: string;
  options?: string[];
  changeRow?: boolean;
  isRequired?: boolean;
  disableTargets?: { true: number[]; false: number[] };
  disableNextField?: boolean;
};
export type formInputType = "text" | "radio" | "selector" | "date" | "image" | "textArea" | "timeRange" | "phoneNumber" | "checkBox";
