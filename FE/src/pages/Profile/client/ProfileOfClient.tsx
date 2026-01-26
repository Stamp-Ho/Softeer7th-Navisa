import Header from "./Header";
import Education from "./Education";
import Languages from "./Languages";
import Career from "./Career";
import ExpectedCompany from "./ExpectedCompany";
import BannerBackground from "../../../components/shared/BannerBackground";

function ProfileOfClient() {
  return (
    <>
      <BannerBackground />
      <section className="flex flex-col gap-15 mt-21 mx-50 pb-21">
        <Header />
        <div className="flex flex-col gap-5">
          <div className="flex flex-row gap-4">
            <Education />
            <Languages />
          </div>
          <div className="flex flex-col gap-5">
            <div className="flex flex-row gap-4">
              <Career />
              <ExpectedCompany />
            </div>
          </div>
        </div>
      </section>
    </>
  );
}

export default ProfileOfClient;
