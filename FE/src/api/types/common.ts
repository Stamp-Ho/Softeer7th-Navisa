export interface BaseResponse<T> {
  code: number;
  message: string;
  result: T;
}

export interface PageInfo {
  pageNum: number;
  pageSize: number;
  totalElements: number;
  totalPages: number;
}

export interface PageResponse<T> {
  content: T[];
  existsNext: boolean;
  lastElementId: string;
}

export interface SliceResponse<T> {
  content: T[];
  existsNext: boolean;
  lastElementId: string;
}

export type UserType =
  | "VALID_AGENT"
  | "INVALID_AGENT"
  | "FILLED_FOREIGNER"
  | "UNFILLED_FOREIGNER"
  | "ADMIN";
export type DegreeLevel = "BELOW_BACHELOR" | "BACHELOR" | "ABOVE_MASTER";

export const DegreeLevelList: DegreeLevel[] = [
  "BELOW_BACHELOR",
  "BACHELOR",
  "ABOVE_MASTER",
];

export const DegreeToKorean = {
  BELOW_BACHELOR: "학사 미만",
  BACHELOR: "학사",
  ABOVE_MASTER: "석사 이상",
};
