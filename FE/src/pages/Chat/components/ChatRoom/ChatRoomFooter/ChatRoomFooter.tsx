import { useState } from "react";
import {
  IcSendActive,
  IcSendDefault,
} from "../../../../../assets/icon/StratisUi";
import ChatArea from "./ChatArea";
import Chip from "../../../../../components/common/Chip";
import { useChatSender } from "../../../../../api/hooks/useChatSender";

type ChatRoomFooterParams = {
  isMatched: boolean;
  chatRoomId: number;
};

const ChatRoomFooter = ({ isMatched, chatRoomId }: ChatRoomFooterParams) => {
  const [message, setMessage] = useState<string>("");
  const { sendChat } = useChatSender();

  return (
    <div className="absolute bottom-8 w-full flex flex-col px-6">
      {isMatched && (
        <div className="w-fit mb-3">
          <Chip type="chips_square_form_view" />
        </div>
      )}
      <div className="flex justify-center w-full  relative">
        <ChatArea value={message} setValue={setMessage} roomId={chatRoomId} />
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
      </div>
    </div>
  );
};

export default ChatRoomFooter;
