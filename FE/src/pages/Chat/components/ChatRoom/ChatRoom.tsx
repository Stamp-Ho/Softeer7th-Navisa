import type { ChatRoomHeaderData } from "../../../../types/chatRoomTypes";
import ChatBody from "./ChatRoomBody/ChatBody";
import ChatRoomFooter from "./ChatRoomFooter/ChatRoomFooter";
import ChatRoomHeader from "./ChatRoomHeader/ChatRoomHeader";
import { useAuth } from "../../../../contexts/AuthContextProvider";
import { useChatRoom } from "../hooks/useChatRoom";
import { useChatParticipantsInfoQuery } from "../../../../api/queries/useChatParticipantsInfoQuery";

type ChatRoomParams = {
  chatRoomId: number;
  onClose: () => void;
  onModalAction: (num: number) => void;
  profileImg: string | null;
};

const ChatRoom = ({
  chatRoomId,
  onClose,
  onModalAction,
  profileImg,
}: ChatRoomParams) => {
  const { userType } = useAuth();
  const isAgent = userType === "VALID_AGENT";
  const { chatStatus } = useChatRoom(chatRoomId, false);
  const { data: participants } = useChatParticipantsInfoQuery(chatRoomId);

  const headerData: ChatRoomHeaderData = isAgent
    ? { type: "FOREIGNER", data: participants?.foreignerInfo }
    : { type: "AGENT", data: participants?.agentInfo };

  return (
    <>
      <ChatRoomHeader
        chatRoomId={chatRoomId}
        headerData={headerData}
        roomStatus={chatStatus}
        onClose={onClose}
        onModalAction={onModalAction}
      />
      <ChatBody
        chatRoomId={chatRoomId}
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
      <ChatRoomFooter roomStatus={chatStatus} chatRoomId={chatRoomId} />
    </>
  );
};

export default ChatRoom;
