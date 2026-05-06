import { useTranslation } from "react-i18next";
import { AllJobs } from "./jobIcon/AllJobs";
import { Building } from "./jobIcon/Building";
import { Business } from "./jobIcon/Business";
import { Chemistry } from "./jobIcon/Chemistry";
import { Design } from "./jobIcon/Design";
import { Education } from "./jobIcon/Education";
import { Electricity } from "./jobIcon/Electricity";
import { Finance } from "./jobIcon/Finance";
import { It } from "./jobIcon/It";
import { Law } from "./jobIcon/Law";
import { Mechanic } from "./jobIcon/Mechanic";
import { Medical } from "./jobIcon/Medical";
import { Planning } from "./jobIcon/Planning";
import { Research } from "./jobIcon/Research";
import { Sales } from "./jobIcon/Sales";
import { Tour } from "./jobIcon/Tour";

const icons = [
  <Business />,
  <Finance />,
  <Law />,
  <Research />,
  <It />,
  <Electricity />,
  <Mechanic />,
  <Chemistry />,
  <Building />,
  <Education />,
  <Planning />,
  <Sales />,
  <Design />,
  <Medical />,
  <Tour />,
  <AllJobs />,
];

/** 직군 아이콘 순서와 동일한 번역 키 (0~15: jobList, 16: 모든 직군 보기) */
export const JOB_KEYS = [
  "business",
  "finance",
  "law",
  "research",
  "it",
  "electricity",
  "mechanic",
  "chemistry",
  "building",
  "education",
  "planning",
  "sales",
  "design",
  "medical",
  "tour",
  "all",
] as const;

/** JobIcon 16개 + "모든 직군 보기" (17개) 번역 라벨 배열 */
export function useJobLabels(): string[] {
  const { t } = useTranslation(["common"]);
  return JOB_KEYS.map((key) => t(`jobs.${key}`));
}

/** jobList와 동일한 순서의 16개 직군 번역 라벨 (Dropdown, AgentCard 등에서 사용) */
export function useJobListLabels(): string[] {
  const labels = useJobLabels();
  return labels.slice(0, 16);
}

const JobIcon = ({ index = 0 }) => {
  return icons[index];
};

export default JobIcon;
