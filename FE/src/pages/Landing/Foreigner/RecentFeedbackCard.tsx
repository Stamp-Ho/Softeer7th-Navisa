import type { AgentRecentFeedbackResponse } from "../../../api/types/agent";
import { IcQuotes } from "../../../assets/icon/StratisUi";
import Tag from "../../../components/common/Tag";

/**feedback = {
    feedbackId: 0,
    feedbackContent:
      "어쩌구저쩌구 텍스트어쩌구저쩌구 텍스트어쩌구저쩌구 텍스트어쩌구저쩌구 텍스트어쩌구저쩌구 텍스트어쩌구저쩌구 텍스트어쩌구저쩌구 텍스트어쩌구저쩌구 텍스트어쩌구저쩌구 텍스트어쩌구저쩌구 텍스트어쩌구저쩌구 텍스트어쩌구저쩌구 텍스트어쩌구저쩌구 텍스트어쩌구저쩌구 텍스트어쩌구저쩌구 텍스트어쩌구저쩌구 텍스트어쩌구저쩌구 텍스트어쩌구저쩌구 텍스트어쩌구저쩌구 텍스트어쩌구저쩌구 텍스트어쩌구저쩌구 텍스트어쩌구저쩌구 텍스트어쩌구저쩌구 텍스트어쩌구저쩌구 텍스트어쩌구저쩌구 텍스트어쩌구저쩌구 텍스트어쩌구저쩌구 텍스트어쩌구저쩌구 텍스트어쩌구저쩌구 텍스트어쩌구저쩌구 텍스트어쩌구저쩌구 텍스트어쩌구저쩌구 텍스트어쩌구저쩌구 텍스트어쩌구저쩌구 텍스트",
    writerId: "",
    writerName: "Jackson",
    writerProfileImgUrl: "https://placehold.co/26x26",
  }, */
const RecentFeedbackCard = ({
  feedback,
}: {
  feedback?: AgentRecentFeedbackResponse;
}) => {
  if (!feedback) return SkeletonUI();
  return (
    <div className="flex flex-col w-124 h-69.5 px-6 pt-6 pb-8 rounded-2xl bg-gray-50">
      <div className="flex flex-row justify-end items-center gap-2 body-s-medium text-text-sub">
        name
        <img
          src={feedback.writerProfileImgUrl}
          alt={feedback.writerProfileImgUrl}
          className="w-6.5 h-6.5 rounded-full object-fit"
        />
      </div>

      <div className="flex flex-col gap-5">
        <IcQuotes />
        <div className="h-33 line-clamp-6 body-l-medium">
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

const SkeletonUI = () => {
  return (
    <div className="flex flex-col w-124 h-69.5 px-6 pt-6 pb-8 rounded-2xl bg-gray-50">
      <div className="flex flex-row justify-end items-center gap-2 body-s-medium text-text-sub">
        <Tag type="tiny_skeleton" className="bg-gray-150 w-15" />
        <div className="w-6.5 h-6.5 bg-gray-150 rounded-[20px]" />
      </div>

      <div className="flex flex-col gap-5">
        <IcQuotes />
        <div className="h-33 line-clamp-6 body-l-medium flex flex-col gap-3">
          <Tag type="small_fill_gray_dark" />
          <Tag type="small_fill_gray_dark" />
          <Tag type="small_fill_gray_dark" className="bg-gray-150 w-40" />
        </div>
        <Tag type="tiny_skeleton" className="bg-gray-150 w-25" />
      </div>
    </div>
  );
};
