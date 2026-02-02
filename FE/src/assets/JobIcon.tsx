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
const JobIcon = ({ index = 0 }) => {
  return icons[index];
};

export default JobIcon;

export const jobs = [
  "경영/운영",
  "금융/경영 컨설팅",
  "법/행정",
  "연구/과학",
  "IT/개발/데이터",
  "전기/통신기술",
  "기계/제조기술",
  "화학/환경기술",
  "건축/토목",
  "교육/강의",
  "기획/마케팅",
  "영업/무역",
  "디자인/미디어",
  "의료/헬스케어",
  "관광/서비스",
  "모든 직군 보기",
];
