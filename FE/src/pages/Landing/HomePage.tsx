import { useContext } from "react";
import BannerBackground from "../../components/shared/BannerBackground";
import ForeignerBanner from "./ForeignerBanner";
import SuggestedAgents from "./SuggestedAgents";
import SuggestedForeigners from "./SuggestedForeigner";
import RecentlyEditedDocuments from "./RecentlyEditedDocuments";
import ExploreJobs from "./ExploreJobs";
import { AuthContext } from "../../contexts/AuthContext";

const HomePage = () => {
  const context = useContext(AuthContext);
  if (!context) return null;
  const { userType, setUserType } = context;
  const isAgent = userType === "VALID_AGENT";

  return (
    <>
      <BannerBackground />
      {/* 유저상태 테스트용 */}
      <div className="flex flex-row gap-2 cursor-pointer">
        <div onClick={() => setUserType("VALID_AGENT")}>인증행정사</div>
        <div onClick={() => setUserType("UNVALID_AGENT")}>비인증행정사</div>
        <div onClick={() => setUserType("FILLED_FOREIGNER")}>등록외국인</div>
        <div onClick={() => setUserType("UNFILLED_FOREIGNER")}>
          미등록외국인
        </div>
        <div onClick={() => setUserType("NOT_AUTHED")}>미로그인</div>
      </div>
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
