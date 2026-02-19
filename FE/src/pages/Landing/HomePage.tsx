import { useTranslation } from "react-i18next";
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

const HomePage = () => {
  const { t } = useTranslation(["pages"]);
  const { userType } = useAuth();
  const isAgent = userType === "VALID_AGENT";

  return (
    <>
      <BannerBackground />
      {isAgent ? (
        <>
          <RecentlyEditedDocuments />
          <SuggestedForeigners />
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
