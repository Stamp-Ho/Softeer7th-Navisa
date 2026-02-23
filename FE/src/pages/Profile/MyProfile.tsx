import { Navigate } from "react-router-dom";
import { useMyProfileQuery } from "../../api/queries/useMyProfileQuery";
import BannerBackground from "../../components/layout/BannerBackground";
import { useAuth } from "../../contexts/AuthContextProvider";
import Education from "./Foreigner/Education";
import Header from "./Foreigner/Header";
import Languages from "./Foreigner/Languages";
import MyCareer from "./Foreigner/MyCareer";
import MyExpectedCompany from "./Foreigner/MyExpectedCompany";
import { useMyProfileAgentQuery } from "../../api/queries/useMyProfileAgentQuery";
import AgentHeader from "./agent/AgentHeader";
import AgentSpecialties from "./agent/AgentSpecialties";
import AgentReviewSection from "./agent/AgentReviewSection";
import AgentOffice from "./agent/AgentOffice";
import MyAgentProfilePanel from "./agent/MyAgentProfilePanel";

const MyProfile = () => {
  const { userType } = useAuth();
  const isAgent = userType.includes("AGENT");

  const foreignerMyProfile = useMyProfileQuery(userType);
  const agentMyProfile = useMyProfileAgentQuery(userType);

  const foreignerData = foreignerMyProfile.data;
  const agentData = agentMyProfile.data;

  // 권한 체크 - 렌더링 전에 처리
  if (userType === "NOT_AUTHED") {
    return <Navigate to="/" replace />;
  }

  const dataToRender = isAgent ? (
    !agentData?.agentInfo?.name || agentMyProfile.isLoading || agentMyProfile.isError ? (
      SkeletonAgentProfile()
    ) : (
      <>
        <section className="flex flex-col">
          <h1 className="text-text-base font-pretendard text-[40px] font-semibold leading-[1.4] tracking-[-1.44px] mb-5">
            내 프로필
          </h1>
          <AgentHeader header={agentData?.header} />

          <div className="flex flex-row gap-5 mt-15">
            <AgentSpecialties jobCodeIds={agentData?.expertise.jobCodeIds} />
            <Languages languageIds={agentData?.expertise.languageIds} />
          </div>

          <div className="mt-15">
            <div className="headline-m-semibold text-gray-1000 mb-10">추가 이력</div>
            {agentData?.additionalHistory &&
              agentData?.additionalHistory.split("\n").length > 1 &&
              agentData.additionalHistory.split("\n").map((line, index) => (
                <div key={index} className="mt-4 ml-4 whitespace-pre-line title-s-medium text-text-base">
                  {line}
                </div>
              ))}
          </div>

          <div className="mt-30">
            <AgentReviewSection reviewSummary={agentData?.reviewSummary} />
          </div>

          <div className="mt-30">
            <AgentOffice officeInfo={agentData?.officeInfo} />
          </div>
        </section>
        <div>
          <MyAgentProfilePanel
            agentInfo={agentData?.agentInfo}
            officeName={agentData?.officeInfo.officeName}
            opponentProfileId={agentData?.agentInfo.agentId}
          />
        </div>
      </>
    )
  ) : !foreignerData?.name || foreignerMyProfile.isLoading || foreignerMyProfile.isError ? (
    // 외국인, 프로필 데이터 없거나 로딩중
    SkeletonForeignerProfile()
  ) : (
    <>
      <section className="flex flex-col overflow-auto scrollbar-hide h-full">
        <h1 className="text-text-base font-pretendard text-[40px] font-semibold leading-[1.4] tracking-[-1.44px] mb-5">
          내 프로필
        </h1>
        <Header nationIdList={foreignerData?.nationIdList} />
        <div className="flex flex-col gap-5 mt-15 overflow-visible">
          <div className="flex flex-row gap-4 mb-20 px-3.5">
            <Education
              school={foreignerData?.education.schoolName}
              degreeLevel={foreignerData?.education.degreeLevel}
              major={foreignerData?.education.majorName}
            />
            <Languages languageIds={foreignerData?.languageIdList} />
          </div>
          <MyCareer foreignerCareerList={foreignerData?.foreignerCareers ?? []} />
        </div>
      </section>
      <MyExpectedCompany
        companyName={foreignerData?.expectedCompany.companyName}
        targetJob={foreignerData?.expectedCompany.jobTitle}
        startDate={foreignerData?.expectedCompany.startDate}
        nickname={foreignerData?.name}
      />
    </>
  );
  return (
    <>
      <BannerBackground />
      <div className="flex flex-row justify-between mt-20 mb-20 min-h-200">{dataToRender}</div>
    </>
  );
};

export default MyProfile;

const SkeletonForeignerProfile = () => {
  return (
    <>
      <section className="flex flex-col overflow-auto scrollbar-hide h-full">
        <h1 className="text-text-base font-pretendard text-[40px] font-semibold leading-[1.4] tracking-[-1.44px] mb-5">
          내 프로필
        </h1>
        <Header />
        <div className="flex flex-col gap-5 mt-15 overflow-visible">
          <div className="flex flex-row gap-4 mb-20 px-3.5">
            <Education />
            <Languages />
          </div>
          <MyCareer />
        </div>
      </section>
      <MyExpectedCompany />
    </>
  );
};

const SkeletonAgentProfile = () => {
  return (
    <>
      <section className="flex flex-col">
        <h1 className="text-text-base font-pretendard text-[40px] font-semibold leading-[1.4] tracking-[-1.44px] mb-5">
          내 프로필
        </h1>
        <div className="flex flex-row gap-5 mt-15">
          <AgentSpecialties />
          <Languages />
        </div>
        <div className="mt-15">
          <div className="headline-m-semibold text-gray-1000">추가 이력</div>
          <div className="mt-13 whitespace-pre-line title-s-medium text-text-base">추가 이력</div>
        </div>
        <div className="mt-30">
          <AgentReviewSection />
        </div>
        <div className="mt-30">
          <AgentOffice />
        </div>
      </section>
      <div>
        <MyAgentProfilePanel />
      </div>
    </>
  );
};
