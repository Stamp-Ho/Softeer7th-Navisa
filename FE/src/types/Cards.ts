export type SearchAgentCardType = {
  id: number;
  img: string;
  name: string;
  address: string;
  jobs: number[];
  badges: number[];
};
export type SearchForeignerCardType = {
  id: number;
  nations: number[];
  nickName: string;
  targetJob: string;
  major: string;
  languages: number[];
};
