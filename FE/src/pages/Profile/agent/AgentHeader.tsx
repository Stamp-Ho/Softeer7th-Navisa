import BadgeIcon, { badgeDescription } from "../../../assets/icon/BadgeIcon";

const AgentHeader = ({
  header = {
    top2badgeIds: [0, 1],
    comment:
      "E7 비자는 기업의 성장 동력을 확보하는 첫 단추입니다. 그 소중한 시작이 늦어지지 않도록...",
  },
}: {
  header?: { top2badgeIds: number[]; comment: string };
}) => {
  return (
    <>
      <ul className="flex flex-row gap-5">
        {header.top2badgeIds.map((data, idx) => {
          const description = badgeDescription[data];
          if (!description) return null;
          return (
            <li
              key={idx}
              className="flex flex-row gap-1 items-center title-l-medium text-text-base"
            >
              <BadgeIcon badgeIndex={data} />
              {badgeDescription[data]}
            </li>
          );
        })}
      </ul>
      <div className="mt-10 font-pretendard text-[56px] font-semibold leading-[1.4] tracking-[-1.68px] text-text-base">
        {header.comment}
      </div>
    </>
  );
};

export default AgentHeader;
