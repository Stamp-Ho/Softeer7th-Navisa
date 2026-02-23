import { useTranslation } from "react-i18next";

export type MyForeignerCareerItem = {
  companyName: string;
  jobTitle: string;
  startDate: string;
  endDate: string;
  isWork: boolean;
};

const MyCareerFrame = ({ companyName, jobTitle, startDate, endDate, isWork }: MyForeignerCareerItem) => {
  const period = `${startDate} ~ ${isWork ? "재직중" : endDate}`;
  return (
    <>
      <div className="flex flex-col gap-3 w-56 pl-3 py-3">
        <div className="title-s-medium text-text-base">{period}</div>
      </div>
      <div className="h-13 pl-0.25 mx-9 bg-border-normal"></div>
      <div className="flex flex-col gap-3">
        <div className="title-s-semibold text-text-base">{companyName}</div>
        <div className="body-l-medium text-violet-500">{jobTitle}</div>
      </div>
    </>
  );
};

const MyCareer = ({ foreignerCareerList }: { foreignerCareerList?: MyForeignerCareerItem[] }) => {
  const { t } = useTranslation(["pages"]);
  if (!foreignerCareerList) return <SkeletonMyCareer />;
  return (
    <div className="flex flex-col mx-3">
      <span className="headline-m-semibold text-gray-1000 mb-10">{t("profile.career")}</span>
      <ul className="flex flex-col gap-4 p-1">
        {foreignerCareerList.length === 0 && (
          <li className="body-l-medium text-gray-600 p-5 pb-20 bg-gray-0 border border-border-light rounded-[12px] shadow">
            경력이 없습니다
          </li>
        )}
        {foreignerCareerList.map((career, idx) => (
          <li
            key={idx}
            className="flex flex-row items-center p-5 bg-gray-0 border border-border-light rounded-[12px] drop-shadow-[0_3px_3px_#6860A040]"
          >
            <MyCareerFrame
              companyName={career.companyName}
              jobTitle={career.jobTitle}
              startDate={career.startDate}
              endDate={career.endDate}
              isWork={career.isWork}
            />
          </li>
        ))}
      </ul>
    </div>
  );
};

export default MyCareer;

const SkeletonMyCareer = () => {
  return (
    <div className="flex flex-col mx-3">
      <span className="headline-m-semibold text-gray-1000 mb-10">{`경력`}</span>
      <ul className="flex flex-col gap-4 p-1">
        <li className="body-l-medium text-gray-600 p-5 pb-20 bg-gray-0 border border-border-light rounded-[12px] shadow">
          경력이 없습니다
        </li>
      </ul>
    </div>
  );
};
