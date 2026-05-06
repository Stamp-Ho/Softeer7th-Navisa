import FlagIcon from "../../../assets/FlagIcon";
import { useNationLabels } from "../../../hooks/useLocalizationLists";

// 외국인 프로필 상단 헤더
const Header = ({ nationIdList, nickname }: { nationIdList?: number[]; nickname?: string }) => {
  const nationLabels = useNationLabels();

  return (
    <header className="flex flex-row justify-between">
      <div className="flex flex-col gap-spacing-400">
        <ul className="flex flex-row gap-spacing-400">
          {nationIdList?.map((id) => (
            <li key={id - 1} className="flex flex-row gap-spacing-300">
              <FlagIcon nationIndex={id - 1} className="w-8 h-8" />
              <span className="text-text-base title-l-medium">{nationLabels[id - 1]}</span>
            </li>
          ))}
        </ul>
        {nickname && (
          <div className="text-text-base font-pretendard text-[48px] font-semibold leading-[1.4] tracking-[-1.44px]">
            {nickname}
          </div>
        )}
      </div>
    </header>
  );
};
export default Header;
