import { useRecentAgentFeedbackQuery } from "../../../api/hooks/useRecentAgentFeedbackQuery";
import RecentFeedbackCard from "./RecentFeedbackCard";

const RecentFeedback = () => {
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
      <h2 className="headline-s-bold">실제 상담 후기</h2>
      <div className="flex flex-row gap-4 mt-4">{dataToRender}</div>
    </div>
  );
};

export default RecentFeedback;
