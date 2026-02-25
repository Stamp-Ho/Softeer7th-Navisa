import { useTranslation } from "react-i18next";
import { calcMonthDiff } from "../../../utils/CalcMonthDiff";

type ForeignerCareerItem = {
  companyName: string;
  jobTitle: string;
  period: string;
  durationMonths: number;
};

const CareerFrame = ({ companyName, jobTitle, period, durationMonths }: ForeignerCareerItem) => {
  return (
    <>
      <div className="flex flex-col gap-3 w-56 pl-3 py-3">
        <div className="title-s-medium text-text-base">{period}</div>
        <div className="body-l-medium text-gray-400">{calcMonthDiff(durationMonths)}</div>
      </div>
      <div className="h-13 pl-0.25 mx-9 bg-border-normal"></div>
      <div className="flex flex-col gap-3">
        <div className="title-s-semibold text-text-base">{companyName}</div>
        <div className="body-l-medium text-violet-500">{jobTitle}</div>
      </div>
    </>
  );
};

const Career = ({ foreignerCareerList }: { foreignerCareerList?: ForeignerCareerItem[] }) => {
  const { t } = useTranslation(["pages"]);
  return (
    <div className="flex flex-col mx-3">
      <span className="headline-m-semibold text-gray-1000 mb-10">{t("profile.career")}</span>
      <ul className="flex flex-col gap-4 p-1">
        {foreignerCareerList && foreignerCareerList.length === 0 && (
          <li className="body-l-medium text-gray-600 p-5 pb-20 bg-gray-0 border border-border-light rounded-[12px] shadow">
            {t("profile.noCareer")}
          </li>
        )}
        {foreignerCareerList &&
          foreignerCareerList.map((career, idx) => (
            <li
              key={idx}
              className="flex flex-row items-center p-5 bg-gray-0 border border-border-light rounded-[12px] drop-shadow-[0_3px_3px_#6860A040]"
            >
              <CareerFrame
                companyName={career.companyName}
                jobTitle={career.jobTitle}
                period={career.period}
                durationMonths={career.durationMonths}
              />
            </li>
          ))}
      </ul>
    </div>
  );
};

export default Career;
