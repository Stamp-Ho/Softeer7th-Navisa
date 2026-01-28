type ForeignerCareerList = {
  foreignerCareerList: ForeignerCareerItem[];
};

type ForeignerCareerItem = {
  companyName: string;
  jobTitle: string;
  period: string;
  durationMonths: number;
};

// 경력 계산
const calcMonthDiff = (months: number) => {
  if (months <= 12) return `${months}개월`;
  const years = Math.floor(months / 12);
  return `${years}년 ${months % 12}개월`;
};

const CareerFrame = ({
  companyName,
  jobTitle,
  period,
  durationMonths,
}: ForeignerCareerItem) => {
  return (
    <>
      <div className="flex flex-col gap-3 w-56 pl-3 py-3">
        <div className="title-s-medium text-text-base">{period}</div>
        <div className="body-l-medium text-gray-400">
          {calcMonthDiff(durationMonths)}
        </div>
      </div>
      <div className="h-13 pl-0.25 mx-9 bg-border-normal"></div>
      <div className="flex flex-col gap-3">
        <div className="title-s-semibold text-text-base">{companyName}</div>
        <div className="body-l-medium text-violet-500">{jobTitle}</div>
      </div>
    </>
  );
};

const Career = ({ foreignerCareerList }: ForeignerCareerList) => {
  return (
    <div className="flex flex-col">
      <span className="headline-m-semibold text-gray-1000 mb-10">경력</span>
      <ul className="flex flex-col gap-4 p-1">
        {foreignerCareerList.map((career, idx) => (
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
