import { useState } from "react";
import {
  IcSendActive,
  IcSendDefault,
} from "../../../../../assets/icon/StratisUi";
import ChatArea from "./ChatArea";
import Chip from "../../../../../components/common/Chip";
import { useChatSender } from "../../../../../api/websocket/useChatSender";
import { useChatRoomContext } from "../../context/ChatRoomContext";
import { Link } from "react-router-dom";

type ChatRoomFooterParams = {
  chatRoomId: number;
  pageType: "CHAT" | "DOCUMENT";
  applicationFormId?: string | null;
};

const ChatRoomFooter = ({
  chatRoomId,
  pageType,
  applicationFormId,
}: ChatRoomFooterParams) => {
  const [message, setMessage] = useState<string>("");
  const { sendChat } = useChatSender();
  const { chatRoomStatus, documentId } = useChatRoomContext();

  const formRoute = documentId
    ? `/document/${documentId}`
    : applicationFormId
      ? `/document/${applicationFormId}`
      : null;
  return (
    <div className="absolute bottom-8 w-full flex flex-col px-6">
      {chatRoomStatus === "MATCHED" && pageType === "CHAT" && formRoute && (
        <Link to={formRoute} className="w-fit mb-3">
          <Chip type="chips_square_form_view" />
        </Link>
      )}
      <div className="flex justify-center w-full relative">
        <ChatArea
          value={message}
          setValue={setMessage}
          roomId={chatRoomId}
          roomStatus={chatRoomStatus}
        />
        {chatRoomStatus !== "CHATROOM_BLOCKED" && (
          <div className="absolute right-2 top-1/2 -translate-y-1/2">
            {message.trim().length > 0 ? (
              <div
                className="cursor-pointer"
                onClick={() => {
                  sendChat(chatRoomId, "TEXT", message);
                  setMessage("");
                }}
              >
                <IcSendActive size="32" />
              </div>
            ) : (
              <div className="cursor-not-allowed">
                <IcSendDefault size="32" />
              </div>
            )}
          </div>
        )}
      </div>
    </div>
  );
};

export default ChatRoomFooter;
