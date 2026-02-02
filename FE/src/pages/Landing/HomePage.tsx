import { useState } from "react";
import BannerBackground from "../../components/shared/BannerBackground";
import ForeignerBanner from "./ForeignerBanner";
import SuggestedAgents from "./SuggestedAgents";
import SuggestedForeigners from "./SuggestedForeigner";
import RecentlyEditedDocuments from "./RecentlyEditedDocuments";
import ExploreJobs from "./ExploreJobs";

const HomePage = () => {
  const [isAgent, setIsAgent] = useState<boolean>(true);
  return (
    <>
      <BannerBackground />
      <button onClick={() => setIsAgent(!isAgent)}>테스트용 버튼</button>
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
