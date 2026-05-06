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
  maxLine?: number;
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
  validator?: ValidatorType;
  maxLength?: number;
  onlyPast?: boolean;
  onlyFuture?: boolean;
  birthDate?: boolean;
};
export type formInputType =
  | "text"
  | "radio"
  | "selector"
  | "date"
  | "image"
  | "textArea"
  | "timeRange"
  | "phoneNumber"
  | "checkBox";

export type ValidatorType =
  | "none"
  | "email"
  | "phoneNumber"
  | "hanja"
  | "numbers"
  | "korean"
  | "english"
  | "koreanOrEnglish"
  | "englishOrNumber"
  | "passportNumber"
  | "noSymbol"
  | "emailOrEnglishNumber";

export const VALIDATOR: Record<ValidatorType, RegExp> = {
  none: /^[\s\S]*$/,
  email: /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/,
  phoneNumber: /^(\+?\d{1,3}[-.\s]?)?\(?\d{1,4}\)?[-.\s]?\d{1,4}[-.\s]?\d{1,9}$/,
  hanja: /^[\u4E00-\u9FFF]+$/,
  numbers: /^\d+$/,
  korean: /^[\uAC00-\uD7AF\u1100-\u11FF]+$/,
  english: /^[a-zA-Z]+$/,
  koreanOrEnglish: /^[\uAC00-\uD7AF\u1100-\u11FF\sa-zA-Z]+$/,
  englishOrNumber: /^[a-zA-Z0-9]+$/,
  passportNumber: /^[A-Z0-9]{6,9}$/,
  noSymbol: /^[^\s!@#$%^&*()\-_=+\[\]{};':"\\|,.<>/?`~]+$/,
  emailOrEnglishNumber: /^([a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}|[a-zA-Z0-9]+)$/,
};
