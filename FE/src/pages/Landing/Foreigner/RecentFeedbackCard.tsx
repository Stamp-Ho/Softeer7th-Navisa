import type { AgentRecentFeedbackResponse } from "../../../api/types/agent";
import { IcQuotes } from "../../../assets/icon/StratisUi";

const RecentFeedbackCard = ({
  feedback = {
    feedbackId: 0,
    feedbackContent:
      "어쩌구저쩌구 텍스트어쩌구저쩌구 텍스트어쩌구저쩌구 텍스트어쩌구저쩌구 텍스트어쩌구저쩌구 텍스트어쩌구저쩌구 텍스트어쩌구저쩌구 텍스트어쩌구저쩌구 텍스트어쩌구저쩌구 텍스트어쩌구저쩌구 텍스트어쩌구저쩌구 텍스트어쩌구저쩌구 텍스트어쩌구저쩌구 텍스트어쩌구저쩌구 텍스트어쩌구저쩌구 텍스트어쩌구저쩌구 텍스트어쩌구저쩌구 텍스트어쩌구저쩌구 텍스트어쩌구저쩌구 텍스트어쩌구저쩌구 텍스트어쩌구저쩌구 텍스트어쩌구저쩌구 텍스트어쩌구저쩌구 텍스트어쩌구저쩌구 텍스트어쩌구저쩌구 텍스트어쩌구저쩌구 텍스트어쩌구저쩌구 텍스트어쩌구저쩌구 텍스트어쩌구저쩌구 텍스트어쩌구저쩌구 텍스트어쩌구저쩌구 텍스트어쩌구저쩌구 텍스트어쩌구저쩌구 텍스트어쩌구저쩌구 텍스트",
    writerId: "",
    writerName: "Jackson",
    writerProfileImgUrl: "https://placehold.co/26x26",
  },
}: {
  feedback?: AgentRecentFeedbackResponse;
}) => {
  return (
    <div className="flex flex-col w-124 h-[278px] px-6 pt-6 pb-8 rounded-[16px] bg-gray-50">
      <div className="flex flex-row justify-end items-center gap-2 body-s-medium text-text-sub">
        name
        <img
          src={feedback.writerProfileImgUrl}
          alt={feedback.writerProfileImgUrl}
          className="w-[26px] h-[26px] rounded-full object-fit"
        />
      </div>

      <div className="flex flex-col gap-5">
        <IcQuotes />
        <div className="h-[132px] line-clamp-6 body-l-medium">
          {feedback.feedbackContent}
        </div>
        <div className="caption-l-regular">
          {feedback.writerName.slice(0, 1)}****** 님의 후기
        </div>
      </div>
    </div>
  );
};

export default RecentFeedbackCard;
