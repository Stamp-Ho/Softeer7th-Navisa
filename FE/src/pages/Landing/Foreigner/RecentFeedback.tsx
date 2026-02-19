import { useTranslation } from "react-i18next";
import { useRecentAgentFeedbackQuery } from "../../../api/queries/useRecentAgentFeedbackQuery";
import RecentFeedbackCard from "./RecentFeedbackCard";

const RecentFeedback = () => {
  const { t } = useTranslation(["pages"]);
  const { data, isLoading, isError } = useRecentAgentFeedbackQuery();

  const dataToRender =
    isLoading || isError ? (
      <>
        {Array.from({ length: 3 }).map((_, idx) => (
          <RecentFeedbackCard key={idx} />
        ))}
      </>
    ) : (
      <>
        {data?.map((feedback, idx) => (
          <RecentFeedbackCard key={idx} feedback={feedback} />
        ))}
      </>
    );

  return (
    <div className="flex flex-col mt-17">
      <h2 className="headline-s-bold">{t("landing.actualConsultationReviews")}</h2>
      <div className="flex flex-row gap-4 mt-4">{dataToRender}</div>
    </div>
  );
};

export default RecentFeedback;
