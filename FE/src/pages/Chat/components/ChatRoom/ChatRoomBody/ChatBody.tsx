import Tag from "../../../../../components/common/Tag";
import CalcDateSystemMessage from "../../../../../utils/CalcDateSystemMessage";
import { useAuth } from "../../../../../contexts/AuthContextProvider";
import { useChatRoom } from "../../hooks/useChatRoom"; // 커스텀 훅
import ChatMessageGroup from "./ChatMessageGroup"; // 분리된 컴포넌트

type ChatBodyParams = {
  chatRoomId: number;
  onModalAction: (num: number) => void;
  opponentName: string;
  myName: string;
  profileImg: string | null;
};

const ChatBody = ({
  chatRoomId,
  onModalAction,
  opponentName,
  myName,
  profileImg,
}: ChatBodyParams) => {
  const { userType } = useAuth();
  const isAgent = userType === "VALID_AGENT";

  // 모든 로직은 훅 안에 숨김
  const { groupedChats, isLoading, isError, scrollRef, pendingProposalId } =
    useChatRoom(chatRoomId, true);

  if (isLoading) return <div className="p-6">채팅 불러오는 중...</div>;
  if (isError)
    return <div className="p-6 text-red-500">채팅을 불러오지 못했습니다.</div>;

  return (
    <div ref={scrollRef} className="overflow-auto scrollbar-hide h-full">
      {groupedChats.map((group) => {
        const firstMsg = group[0];

        // 날짜 구분선 (SYSTEM 메시지)
        if (firstMsg.type === "SYSTEM") {
          return (
            <div
              key={firstMsg.chatMessageId}
              className="flex flex-row justify-center w-full my-10"
            >
              <Tag type="small_fill_gray">
                {CalcDateSystemMessage(firstMsg.createdAt)}
              </Tag>
            </div>
          );
        }

        // 일반 메시지 그룹
        return (
          <ChatMessageGroup
            key={`group-${firstMsg.chatMessageId}`}
            group={group}
            opponentName={opponentName}
            myName={myName}
            profileImg={profileImg}
            isAgent={isAgent}
            onModalAction={onModalAction}
            pendingProposalId={pendingProposalId}
          />
        );
      })}
    </div>
  );
};

export default ChatBody;
