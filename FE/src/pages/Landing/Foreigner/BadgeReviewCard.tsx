import type { AgentBadgeReviewResponse } from "../../../api/types/agent";
import BadgeIcon, { badgeDescription } from "../../../assets/icon/BadgeIcon";
import Tag from "../../../components/common/Tag";

/*reivew = {
    reviewId: 0,
    reviewerInitial: "J",
    reviewContent:
      "상담 남기고 오래 기다려야 할 줄 알았는데 생각보다 빨리 답변이 와서놀랐어요.",
    agentId: "abc",
    agentName: "엄경례",
    agentProfileImgUrl: "https://placehold.co/92x92",
    badgeTop2: [0, 1],
  },
*/
const BadgeReviewCard = ({ review }: { review?: AgentBadgeReviewResponse }) => {
  if (!review) return SeletonUi();
  return (
    <div className="w-92 h-43 border border-violet-50 rounded-[10px] overflow-hidden cursor-pointer">
      <div className="flex flex-row items-center gap-3 px-4 py-2.5 bg-linear-to-r from-[#8D7EED]/10 to-[#54D7D5]/10">
        {review.badgeTop2.map((id) => (
          <div
            key={id}
            className="flex flex-row items-center gap-1 caption-m-medium text-violet-500"
          >
            <BadgeIcon badgeIndex={id} color="var(--primary)" size={12} />
            {badgeDescription[id]}
          </div>
        ))}
      </div>

      <div className="flex flex-row items-center gap-5 px-4 py-4">
        <div className="w-22.5 h-22.5 border border-black rounded-full overflow-hidden">
          <img
            src={review.agentProfileImgUrl}
            alt="행정사 프로필 사진"
            className="22.5 h-22.5"
          />
        </div>
        <div className="flex flex-col w-56">
          <div className="body-l-bold">{review.agentName} 행정사</div>
          <div className="w-56 pt-px my-2 bg-border-normal"></div>
          <div className="caption-l-regular">
            {review.reviewerInitial}****** 님의 후기
          </div>
          <div className="mt-2 body-s-medium line-clamp-2">
            {review.reviewContent}
          </div>
        </div>
      </div>
    </div>
  );
};

export default BadgeReviewCard;

const SeletonUi = () => {
  return (
    <div className="w-92 h-43 border border-gray-100 rounded-[10px] overflow-hidden cursor-pointer">
      <div className="flex flex-row items-center gap-3 px-4 py-2.5 bg-linear-to-r from-[#ffffff] to-[#eeeeee]">
        <div className="flex flex-row items-center gap-1 caption-m-medium text-violet-500">
          <Tag className="w-1" type="tiny_skeleton" />
          <Tag className="w-30" type="tiny_skeleton" />
        </div>
      </div>

      <div className="flex flex-row items-center gap-5 px-4 py-4">
        <div className="w-22.5 h-22.5 bg-gray-100 rounded-full overflow-hidden flex items-center justify-center"></div>
        <div className="flex flex-col w-56">
          <Tag className="w-30" type="small_fill_gray" />

          <div className="w-56 pt-px my-2 bg-gray-100"></div>
          <Tag className="w-30" type="tiny_skeleton" />
          <Tag className="w-50 mt-2" type="tiny_skeleton" />
        </div>
      </div>
    </div>
  );
};
