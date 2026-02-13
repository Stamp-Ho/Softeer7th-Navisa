import {
  IcMessageBox,
  IcMessageBoxCross,
} from "../../../../../assets/icon/StratisUi";
import ChatSystemMessageBackground from "../../../../../assets/ChatSystemMessageBackground";

type ChatSystemMessageParams = {
  type: "PROPOSAL" | "ACCEPTED" | "REJECTED" | "CANCELED";
  senderName?: string;
  receiverName?: string;
  date?: string;
  isSentByMe?: boolean;
  onModalAction: (num: number) => void;
  showReplyButton?: boolean;
};

const ChatSystemMessage = ({
  type,
  senderName,
  receiverName,
  isSentByMe,
  onModalAction,
  showReplyButton,
}: ChatSystemMessageParams) => {
  switch (type) {
    case "PROPOSAL":
      return (
        <div className="flex flex-col w-fit rounded-[12px] border-[1.5px] border-violet-50 overflow-hidden">
          <div className="object-fit">
            <ChatSystemMessageBackground />
          </div>

          <div className="flex flex-col gap-5 p-6 bg-gray-0 text-text-base">
            <div className="flex flex-col gap-2">
              <div className="body-l-bold">
                {senderName} 님이 수임 제안을 보냈어요!
              </div>
              <div className="body-s-medium">
                하단 버튼을 통해 답변을 전달해 주세요.
              </div>
            </div>
            {!isSentByMe && showReplyButton && (
              <button
                className="body-s-semibold rounded-[6px] h-[48px] bg-gray-50 text-text-base cursor-pointer"
                onClick={() => onModalAction(3)}
              >
                답변 보내기
              </button>
            )}
          </div>
        </div>
      );

    case "ACCEPTED":
      return (
        <div
          className={`flex flex-col gap-5 p-6 border-[1.5px] border-violet-50 text-violet-500 rounded-[12px] w-92 bg-gray-0`}
        >
          <div className="flex flex-row items-center gap-4">
            <IcMessageBox color="var(--violet-500)" />
            <div className="flex flex-col gap-[1px]">
              <div className="body-l-bold">수임이 확정됐어요.</div>
              <div className="body-s-medium">
                이제 함께 비자 신청서를 작성할 수 있어요.
              </div>
            </div>
          </div>
          <button className="rounded-[6px] h-12 body-l-semibold bg-violet-50-transpar cursor-pointer">
            비자 신청서 보러 가기
          </button>
        </div>
      );

    case "REJECTED":
      return (
        <div
          className={`flex flex-col gap-5 p-6 border-[1.5px] border-green-100 text-green-800 rounded-[12px] w-92 bg-gray-0`}
        >
          <div className="flex flex-row items-center gap-4">
            <IcMessageBoxCross color="var(--green-800)" />
            <div className="flex flex-col gap-[1px]">
              <div className="body-l-bold">아쉽지만 이번 수임 제안은</div>
              <div className="body-l-bold">수락하지 않기로 결정했어요.</div>
            </div>
          </div>
        </div>
      );

    case "CANCELED":
      return (
        <div
          className={`flex flex-col gap-5 p-6 border-[1.5px] border-green-100 text-green-800 rounded-[12px] w-92 bg-gray-0`}
        >
          <div className="flex flex-row items-center gap-4">
            <IcMessageBoxCross color="var(--green-800)" />
            <div className="flex flex-col gap-[1px]">
              <div className="body-l-bold">수임을 취소했어요.</div>
              <div className="body-s-medium">
                {receiverName} 행정사님은 더 이상 의뢰인의 비자 신청서를 작성할
                수 없어요.
              </div>
            </div>
          </div>
        </div>
      );
  }
};

export default ChatSystemMessage;
