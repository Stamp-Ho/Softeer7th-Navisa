import { useTranslation } from "react-i18next";
import Header from "./Header";
import Education from "./Education";
import Languages from "./Languages";
import Career from "./Career";
import ExpectedCompany from "./ExpectedCompany";
import BannerBackground from "../../../components/layout/BannerBackground";
import { useParams } from "react-router-dom";
import { useForeignerProfileDetailQuery } from "../../../api/queries/useForeignerProfileDetailQuery";

function ProfileOfForeigner() {
  const { foreignerId } = useParams();
  const { t } = useTranslation(["pages"]);
  const { data, isLoading, isError } = useForeignerProfileDetailQuery(foreignerId!);
  if (!foreignerId) return <div>{t("profile.invalidAccess")}</div>;
  if (isLoading) return <div>{t("landing.loading")}</div>;
  const dataToRender = isError ? (
    <>
      <section className="flex flex-col overflow-auto scrollbar-hide h-full">
        <Header />
        <div className="flex flex-col gap-5 mt-15 overflow-visible">
          <div className="flex flex-row gap-4 mb-20 px-3.5">
            <Education />
            <Languages />
          </div>
          <Career />
        </div>
      </section>
      <ExpectedCompany />
    </>
  ) : (
    <>
      <section className="flex flex-col overflow-auto scrollbar-hide h-full">
        <Header nationIdList={data?.basicInfo.nationIdList} nickname={data?.basicInfo.nickname} />
        <div className="flex flex-col gap-5 mt-15 overflow-visible">
          <div className="flex flex-row gap-4 mb-20 px-3.5">
            <Education
              school={data?.educationInfo.school}
              degreeLevel={data?.educationInfo.degreeLevel}
              major={data?.educationInfo.major}
            />
            <Languages languageIds={data?.languageList} />
          </div>
          <Career foreignerCareerList={data?.careerInfo.history} />
        </div>
      </section>
      <ExpectedCompany
        companyName={data?.expectedCompanyInfo.companyName}
        targetJob={data?.expectedCompanyInfo.targetJob}
        startDate={data?.expectedCompanyInfo.startDate}
        lastAccessDay={data?.basicInfo.lastAccessDay}
        hasChatRoomBetween={data?.basicInfo.hasChatRoomBetween}
        nickname={data?.basicInfo.nickname}
        opponentProfileId={data?.basicInfo.foreignerId}
      />
    </>
  );

  return (
    <>
      <BannerBackground />
      <div className="flex flex-row justify-between mt-20 mb-20 min-h-200">{dataToRender}</div>
    </>
  );
}

export default ProfileOfForeigner;
