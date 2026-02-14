import type { ChatRoomHeaderData } from "../../../../../types/chatRoomTypes";
import { IcX } from "../../../../../assets/icon/StratisUi";
import Button from "../../../../../components/common/Button";
import Chip from "../../../../../components/common/Chip";
import AgentHeaderInfo from "./AgentHeaderInfo";
import ForeignerHeaderInfo from "./ForeignerHeaderInfo";
import type { ChatRoomStatus } from "../../hooks/useChatRoom";

type HeaderParams = {
  headerData: ChatRoomHeaderData;
  roomStatus: ChatRoomStatus;
  onClose: () => void;
  onModalAction: (num: number) => void;
};

const ChatRoomHeader = ({
  headerData,
  roomStatus,
  onClose,
  onModalAction,
}: HeaderParams) => {
  return (
    <>
      <div className="absolute w-full top-0">
        {/* 채팅창 헤더, 외국인인가 행정사인가에 따라 출력 달라짐 */}
        <div className="flex flex-row justify-between items-center p-6 bg-white/80 backdrop-blur-[2px]">
          <div className="w-[500px]">
            {headerData.type === "FOREIGNER" ? (
              <ForeignerHeaderInfo data={headerData.data} />
            ) : (
              <AgentHeaderInfo data={headerData.data} />
            )}
          </div>
          <div className="flex flex-row">
            {roomStatus === "MATCHED" ? (
              <div onClick={() => onModalAction(2)}>
                <Chip type="chips_square_cancel" className="mr-3" />
              </div>
            ) : (
              <div onClick={() => onModalAction(1)}>
                <Chip type="chips_square_request" className="mr-3" />
              </div>
            )}

            <Button
              size="small"
              type="lightGray"
              className="w-[100px] mr-8 body-l-semibold"
              onClick={() => onModalAction(4)}
            >
              차단
            </Button>
            <div className="flex items-center cursor-pointer" onClick={onClose}>
              <IcX />
            </div>
          </div>
        </div>

        <div className="flex flex-row justify-between w-full px-6 py-[2px] bg-gradient-to-r from-violet-50 to-green-50">
          <div className="flex flex-row items-center gap-2 body-s-semibold text-text-base">
            <span>[시스템 알림]</span>
            <span>비자 발급이 완료되었다면 경험을 후기로 남겨주세요.</span>
          </div>
          <Chip type="chips_square_review" />
        </div>
      </div>
    </>
  );
};

export default ChatRoomHeader;
