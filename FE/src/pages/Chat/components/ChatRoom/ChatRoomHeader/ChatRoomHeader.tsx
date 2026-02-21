import { useTranslation } from "react-i18next";
import type { ChatRoomHeaderData } from "../../../../../types/chatRoomTypes";
import { IcX } from "../../../../../assets/icon/StratisUi";
import Button from "../../../../../components/common/Button";
import Chip from "../../../../../components/common/Chip";
import AgentHeaderInfo from "./AgentHeaderInfo";
import ForeignerHeaderInfo from "./ForeignerHeaderInfo";
import { useChatRoomContext } from "../../context/ChatRoomContext";

type HeaderParams = {
  pageType: "CHAT" | "DOCUMENT";
  headerData: ChatRoomHeaderData;
  onClose: () => void;
  onModalAction: (num: number) => void;
  onGoToChat?: () => void;
};

const ChatRoomHeader = ({
  pageType,
  headerData,
  onClose,
  onModalAction,
  onGoToChat,
}: HeaderParams) => {
  const { t } = useTranslation(["components"]);
  const { chatRoomStatus } = useChatRoomContext();
  return (
    <>
      <div className="absolute w-full top-0">
        {/* 채팅창 헤더, 외국인인가 행정사인가에 따라 출력 달라짐 */}
        <div className="flex flex-row justify-between items-center p-6 bg-white/80 backdrop-blur-[2px]">
          <div className="w-fit mr-auto">
            {headerData.type === "FOREIGNER" ? (
              <ForeignerHeaderInfo data={headerData.data} />
            ) : (
              <AgentHeaderInfo data={headerData.data} />
            )}
          </div>
          <div className="flex flex-row">
            {pageType === "CHAT" && chatRoomStatus !== "CHATROOM_BLOCKED" && (
              <>
                {chatRoomStatus === "MATCHED" ? (
                  <button type="button" onClick={() => onModalAction(2)}>
                    <Chip type="chips_square_cancel" className="mr-3" />
                  </button>
                ) : (
                  <button type="button" onClick={() => onModalAction(1)}>
                    <Chip type="chips_square_request" className="mr-3" />
                  </button>
                )}
                <Button
                  size="small"
                  variant="lightGray"
                  className="w-[100px] mr-8 body-l-semibold"
                  onClick={() => onModalAction(4)}
                >
                  {t("chatRoom.block")}
                </Button>
              </>
            )}
            <div className="flex items-center cursor-pointer" onClick={onClose}>
              <IcX />
            </div>
          </div>
        </div>

        {pageType === "DOCUMENT"
          ? onGoToChat && (
              <button
                type="button"
                onClick={onGoToChat}
                className="flex flex-row justify-end items-center w-full px-6 py-2 bg-violet-50 gap-2"
              >
                <Chip type="chips_square_chatroom" />
              </button>
            )
          : chatRoomStatus !== "CHATROOM_BLOCKED" && (
              <div className="flex flex-row justify-between w-full px-6 py-[2px] bg-gradient-to-r from-violet-50 to-green-50">
                <div className="flex flex-row items-center gap-2 body-s-semibold text-text-base">
                  <span>{t("chatRoom.systemNotice")}</span>
                  <span>{t("chatRoom.reviewPrompt")}</span>
                </div>
                <Chip type="chips_square_review" />
              </div>
            )}
      </div>
    </>
  );
};

export default ChatRoomHeader;
