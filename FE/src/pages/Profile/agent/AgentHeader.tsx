import BadgeIcon, { badgeDescription } from "../../../assets/icon/BadgeIcon";

type AgentHeaderProps = {
  strengths: StrengthsProps[];
  slogan: string;
};

type StrengthsProps = {
  badgeId: number;
  reviewCount: number;
};

const AgentHeader = ({ strengths, slogan }: AgentHeaderProps) => {
  return (
    <>
      <ul className="flex flex-row gap-5">
        {strengths.slice(0, 2).map((obj, idx) => {
          const description = badgeDescription[obj.badgeId];
          if (!description) return null;
          return (
            <li
              key={idx}
              className="flex flex-row gap-1 items-center title-l-medium text-text-base"
            >
              <BadgeIcon badgeIndex={obj.badgeId} />
              {badgeDescription[obj.badgeId]}
            </li>
          );
        })}
      </ul>
      <div className="mt-10 font-pretendard text-[56px] font-semibold leading-[1.4] tracking-[-1.68px] text-text-base">
        {slogan}
      </div>
    </>
  );
};

export default AgentHeader;
