import type { ChatRoomHeaderData } from "../../../../types/chatRoomTypes";
import ChatBody from "./ChatRoomBody/ChatBody";
import ChatRoomFooter from "./ChatRoomFooter/ChatRoomFooter";
import ChatRoomHeader from "./ChatRoomHeader/ChatRoomHeader";
import { useAuth } from "../../../../contexts/AuthContextProvider";
import { useChatParticipantsInfoQuery } from "../../../../api/queries/useChatParticipantsInfoQuery";
import { ChatRoomProvider } from "../context/ChatRoomContext";

type ChatRoomParams = {
  pageType?: "CHAT" | "DOCUMENT";
  chatRoomId: number;
  onClose: () => void;
  onModalAction: (num: number) => void;
  profileImg: string | null;
};

const ChatRoom = ({
  pageType = "CHAT",
  chatRoomId,
  onClose,
  onModalAction,
  profileImg,
}: ChatRoomParams) => {
  const { userType } = useAuth();
  const isAgent = userType === "VALID_AGENT";
  const { data: participants } = useChatParticipantsInfoQuery(chatRoomId);

  const headerData: ChatRoomHeaderData = isAgent
    ? { type: "FOREIGNER", data: participants?.foreignerInfo }
    : { type: "AGENT", data: participants?.agentInfo };

  return (
    <ChatRoomProvider chatRoomId={chatRoomId}>
      <ChatRoomHeader
        pageType={pageType}
        headerData={headerData}
        onClose={onClose}
        onModalAction={onModalAction}
      />
      <div className="w-full pt-10" />
      <ChatBody
        pageType={pageType}
        onModalAction={onModalAction}
        profileImg={profileImg}
        opponentName={
          isAgent
            ? participants?.foreignerInfo?.nickname
            : participants?.agentInfo?.name
        }
        myName={
          isAgent
            ? participants?.agentInfo?.name
            : participants?.foreignerInfo?.nickname
        }
      />
      <div className="w-full pt-28" />
      <ChatRoomFooter chatRoomId={chatRoomId} pageType={pageType} />
    </ChatRoomProvider>
  );
};

export default ChatRoom;
