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
  id: number;
  nations: number[];
  nickName: string;
  targetJob: string;
  major: string;
  languages: number[];
};
