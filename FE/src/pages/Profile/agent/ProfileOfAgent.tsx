import { useParams } from "react-router-dom";
import { useTranslation } from "react-i18next";
import BannerBackground from "../../../components/layout/BannerBackground";
import Languages from "../Foreigner/Languages";
import AgentHeader from "./AgentHeader";
import AgentOffice from "./AgentOffice";
import AgentProfilePanel from "./AgentProfilePanel";
import AgentReviewSection from "./AgentReviewSection";
import AgentSpecialties from "./AgentSpecialties";
import { useAgentProfileDetailQuery } from "../../../api/queries/useAgentProfileDetailQuery";

const ProfileOfAgent = () => {
  const { t } = useTranslation(["pages"]);
  const { agentId } = useParams();
  const { data, isLoading, isError } = useAgentProfileDetailQuery(agentId!);
  if (!agentId) return <div>{t("profile.invalidAccess")}</div>;
  if (isLoading) return <div>{t("search.loading")}</div>;
  const dataToRender = isError ? (
    <>
      <section className="flex flex-col">
        <AgentHeader />

        <div className="flex flex-row gap-5 mt-15">
          <AgentSpecialties />
          <Languages />
        </div>

        <div className="mt-15">
          <div className="headline-m-semibold text-gray-1000">{t("profile.additionalHistory")}</div>
          <div className="mt-13 whitespace-pre-line title-s-medium text-text-base">
            {`주요 약력 및 자격\n현) OO 행정사 사무소 대표 행정사\n대한행정사협회
            정회원`}
          </div>
        </div>

        <div className="mt-30">
          <AgentReviewSection />
        </div>

        <div className="mt-30">
          <AgentOffice />
        </div>
      </section>
      <div>
        <AgentProfilePanel />
      </div>
    </>
  ) : (
    <>
      <section className="flex flex-col">
        <AgentHeader header={data?.header} />

        <div className="flex flex-row gap-5 mt-15">
          <AgentSpecialties jobCodeIds={data?.expertise.jobCodeIds} />
          <Languages languageIds={data?.expertise.languageIds} />
        </div>

        <div className="mt-15">
          <div className="headline-m-semibold text-gray-1000 mb-10">{t("profile.additionalHistory")}</div>
          {data?.additionalHistory &&
            data?.additionalHistory.split("\n").length > 1 &&
            data.additionalHistory.split("\n").map((line, index) => (
              <div key={index} className="mt-4 ml-4 whitespace-pre-line title-s-medium text-text-base">
                {line}
              </div>
            ))}
        </div>

        <div className="mt-30">
          <AgentReviewSection reviewSummary={data?.reviewSummary} />
        </div>
        {/* 
        <div className="mt-30">
          <AgentBlog blogList={blog} agentInfo={data?.agentInfo} />
        </div> */}

        <div className="mt-30">
          <AgentOffice officeInfo={data?.officeInfo} />
        </div>
      </section>
      <div>
        <AgentProfilePanel
          agentInfo={data?.agentInfo}
          officeName={data?.officeInfo.officeName}
          opponentProfileId={data?.agentInfo.agentId}
        />
      </div>
    </>
  );

  return (
    <>
      <BannerBackground />
      <div className="relative flex flex-col mt-21">
        <div className="flex flex-row justify-between">{dataToRender}</div>
      </div>
    </>
  );
};

export default ProfileOfAgent;
