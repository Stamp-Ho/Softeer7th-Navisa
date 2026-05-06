import BannerBackground from "../../components/layout/BannerBackground";
import ForeignerBanner from "./Foreigner/ForeignerBanner";
import SuggestedAgents from "./Foreigner/SuggestedAgents";
import SuggestedForeigners from "./Agent/SuggestedForeigner";
import RecentlyEditedDocuments from "./Agent/RecentlyEditedDocuments";
import ExploreJobs from "./Common/ExploreJobs";
import RecommendPanel from "./Foreigner/RecommendPanel";
import RecentFeedback from "./Foreigner/RecentFeedback";
import BadgeReview from "./Foreigner/BadgeReview";
import { useAuth } from "../../contexts/AuthContextProvider";
import Footer from "./Footer";
import AgentBanner from "./Agent/AgentBanner";

const HomePage = () => {
  const { userType } = useAuth();
  const isAgent = userType === "VALID_AGENT";
  const isInvalidAgent = userType === "INVALID_AGENT";

  return (
    <>
      <BannerBackground />
      {isAgent ? (
        <>
          <RecentlyEditedDocuments />
          <SuggestedForeigners />
        </>
      ) : isInvalidAgent ? (
        <>
          <AgentBanner />
        </>
      ) : (
        <>
          <ForeignerBanner />
          <SuggestedAgents />
          {userType === "FILLED_FOREIGNER" || <RecommendPanel />}
        </>
      )}
      <ExploreJobs isAgent={isAgent} />
      <BadgeReview />
      <RecentFeedback />
      <Footer />
    </>
  );
};
export default HomePage;
