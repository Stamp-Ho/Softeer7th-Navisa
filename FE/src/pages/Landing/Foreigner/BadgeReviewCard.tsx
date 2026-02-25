import { useTranslation } from "react-i18next";
import type { AgentBadgeReviewResponse } from "../../../api/types/agent";
import BadgeIcon, { badgeDescription } from "../../../assets/icon/BadgeIcon";
import Tag from "../../../components/common/Tag";
import { useResizeImage } from "../../../hooks/useResizeImage";
import { useEffect } from "react";
import { Link } from "react-router-dom";
import { useAuth } from "../../../contexts/AuthContextProvider";
import { alertT } from "../../../i18n/alerts";

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
  const { t } = useTranslation(["components"]);
  const { resizeImage, imageSize, loadingImage } = useResizeImage();
  const { userType } = useAuth();

  const authed = userType === "VALID_AGENT" || userType === "FILLED_FOREIGNER";
  useEffect(() => {
    if (review?.agentProfileImgUrl) resizeImage(review.agentProfileImgUrl, 240, 192);
  }, [review]);
  if (!review || loadingImage) return <SkeletonUi />;
  return (
    <Link
      className={`w-92 h-43 border border-violet-50 rounded-[10px] overflow-hidden cursor-pointer
      transition-all hover:scale-105 hover:mx-2 duration-150`}
      to={authed ? `/profile/agent/${review.agentId}` : "#"}
      tabIndex={0}
      onClick={() => authed || alertT("components.agentCard.loginRequired")}
    >
      <div className="flex flex-row items-center gap-3 px-4 py-2.5 bg-linear-to-r from-[#8D7EED]/10 to-[#54D7D5]/10">
        {review.badgeTop2.map((id) => (
          <div key={id} className="flex flex-row items-center gap-1 caption-m-medium text-violet-500">
            <BadgeIcon badgeIndex={id - 1} color="var(--primary)" size={12} />
            {badgeDescription[id - 1]}
          </div>
        ))}
      </div>

      <div className="flex flex-row items-center gap-5 px-4 py-4">
        {imageSize.width && imageSize.height ? (
          <div className="w-22.5 h-22.5 border border-black rounded-full overflow-hidden flex items-center justify-center">
            <div>
              <img
                src={review.agentProfileImgUrl}
                alt={t("chatRoom.attorneyProfileImage")}
                width={imageSize.width}
                height={imageSize.height}
              />
            </div>
          </div>
        ) : (
          <div className="w-22.5 h-22.5 bg-gray-100 rounded-full overflow-hidden flex items-center justify-center"></div>
        )}
        <div className="flex flex-col w-56">
          <div className="body-l-bold">
            {review.agentName} {t("agentProfile.title")}
          </div>
          <div className="w-56 pt-px my-2 bg-border-normal"></div>
          <div className="caption-l-regular">
            {t("agentProfile.reviewBy", { initial: review.reviewerInitial.slice(0, 1) })}
          </div>
          <div className="mt-2 body-s-medium line-clamp-2">{review.reviewContent}</div>
        </div>
      </div>
    </Link>
  );
};

export default BadgeReviewCard;

const SkeletonUi = () => {
  return (
    <div className="w-92 h-43 border border-gray-100 rounded-[10px] overflow-hidden cursor-pointer" tabIndex={-1}>
      <div className="flex flex-row items-center gap-3 px-4 py-2.5 bg-linear-to-r from-[#ffffff] to-[#eeeeee]">
        <div className="flex flex-row items-center gap-1 caption-m-medium text-violet-500">
          <Tag className="w-1" variant="tiny_skeleton" />
          <Tag className="w-30" variant="tiny_skeleton" />
        </div>
      </div>

      <div className="flex flex-row items-center gap-5 px-4 py-4">
        <div className="w-22.5 h-22.5 bg-gray-100 rounded-full overflow-hidden flex items-center justify-center"></div>
        <div className="flex flex-col w-56">
          <Tag className="w-30" variant="small_fill_gray" />

          <div className="w-56 pt-px my-2 bg-gray-100"></div>
          <Tag className="w-30" variant="tiny_skeleton" />
          <Tag className="w-50 mt-2" variant="tiny_skeleton" />
        </div>
      </div>
    </div>
  );
};
