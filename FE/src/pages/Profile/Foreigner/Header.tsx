import FlagIcon from "../../../assets/FlagIcon";
import { nationList } from "../../../constants/nations";

// 외국인 프로필 상단 헤더
const Header = ({ nationIdList, nickname }: { nationIdList?: number[]; nickname?: string }) => {
  return (
    <header className="flex flex-row justify-between">
      <div className="flex flex-col gap-spacing-400">
        <ul className="flex flex-row gap-spacing-400">
          {nationIdList?.map((id) => (
            <li key={id} className="flex flex-row gap-spacing-300">
              <FlagIcon nationIndex={id} className="w-8 h-8" />
              <span className="text-text-base title-l-medium">{nationList[id]}</span>
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
