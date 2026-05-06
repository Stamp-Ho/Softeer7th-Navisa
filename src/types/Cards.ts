import type { DegreeLevel } from "../api/types/common";

export type SearchAgentCardType = {
  agentId: string;
  agentName: string;
  profileImgUrl: string;
  officeAddress: string;
  agentSpecialityTop2: number[];
  badgeTop2: number[];
  specialityJobCount: number;
};
export type SearchForeignerCardType = {
  foreignerId: string;
  nationIdList: number[];
  nickname: string;
  jobTitle: string;
  degreeLevel: DegreeLevel;
  languageIdList: number[];
};
