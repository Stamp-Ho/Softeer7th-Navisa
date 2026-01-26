import FlagIcon from "../../../assets/FlagIcon";
import TipMessage from "../../../components/common/ToolTipMessage";
import Button from "../../../components/common/Button";
import { useParams } from "react-router-dom";

// 외국인 프로필 상단 헤더
const Header = () => {
  const { clientId } = useParams();
  return (
    <header className="flex flex-row justify-between">
      <div className="flex flex-col gap-spacing-400">
        <ul className="flex flex-row gap-spacing-400">
          <li className="flex flex-row gap-spacing-300">
            <FlagIcon nationIndex={61} className="w-8 h-8" />
            <span className="text-text-base title-l-medium">미국</span>
          </li>
        </ul>
        <div className="text-text-base font-pretendard text-[48px] font-semibold leading-[1.4] tracking-[-1.44px]">
          고라니 {clientId}
        </div>
      </div>
      <div className="flex flex-col items-end gap-1.25">
        <TipMessage message={`최근 ${3 + "일"} 이내에 접속했어요`} />
        <Button className="w-88">상담하기</Button>
      </div>
    </header>
  );
};
export default Header;
