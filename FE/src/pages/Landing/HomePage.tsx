import { useState } from "react";
import BannerBackground from "../../components/shared/BannerBackground";
import ForeignerBanner from "./ForeignerBanner";
import SuggestedAgents from "./SuggestedAgents";
import SuggestedForeigners from "./SuggestedForeigner";
import RecentlyEditedDocuments from "./RecentlyEditedDocuments";
import ExploreJobs from "./ExploreJobs";
import { getLanguageList } from "../../api/testApi";

const HomePage = () => {
  const { data, isLoading, isError, error } = getLanguageList();
  const [isAgent, setIsAgent] = useState<boolean>(true);

  const langList = () => {
    if (isLoading) return <p>불러오는 중입니다...</p>;
    if (isError) return <p>오류가 발생했습니다: {(error as Error).message}</p>;
    return "성공";
  };
  return (
    <>
      <BannerBackground />
      <button onClick={() => setIsAgent(!isAgent)}>테스트용 버튼</button>
      <div>{langList()}</div>
      {isAgent ? (
        <>
          <RecentlyEditedDocuments />
          <SuggestedForeigners />
        </>
      ) : (
        <>
          <ForeignerBanner />
          <SuggestedAgents />
        </>
      )}
      <ExploreJobs isAgent={isAgent} />
    </>
  );
};
export default HomePage;
