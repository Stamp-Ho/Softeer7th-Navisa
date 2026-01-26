import BannerBackground from "../../components/shared/BannerBackground";
import ForeignerBanner from "./ForeignerBanner";
import SuggestedAgents from "./SuggestedAgents";

const HomePage = () => {
  return (
    <>
      <BannerBackground />
      <ForeignerBanner />
      <SuggestedAgents />
    </>
  );
};
export default HomePage;
