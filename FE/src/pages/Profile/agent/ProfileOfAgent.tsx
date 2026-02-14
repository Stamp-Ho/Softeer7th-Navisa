import { useParams } from "react-router-dom";
import BannerBackground from "../../../components/layout/BannerBackground";
import Languages from "../Foreigner/Languages";
import AgentBlog from "./AgentBlog";
import AgentHeader from "./AgentHeader";
import AgentOffice from "./AgentOffice";
import AgentProfilePanel from "./AgentProfilePanel";
import AgentReviewSection from "./AgentReviewSection";
import AgentSpecialties from "./AgentSpecialties";
import { useAgentProfileDetailQuery } from "../../../api/queries/useAgentProfileDetailQuery";

const blog = [
  {
    blogId: 12,
    specialityId: 10, //직무코드
    nationId: 13, //외국인 국적
    languageId: 14, //외국인의 언어
    title: "제목",
    content:
      "내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용",
  },
  {
    blogId: 4,
    specialityId: 10, //직무코드
    nationId: 13, //외국인 국적
    languageId: 14, //외국인의 언어
    title: "제목",
    content: "내용",
  },
  {
    blogId: 123,
    specialityId: 10, //직무코드
    nationId: 13, //외국인 국적
    languageId: 14, //외국인의 언어
    title: "제목",
    content: "내용",
  },
];

const ProfileOfAgent = () => {
  const { agentId } = useParams();
  const { data, isLoading, isError } = useAgentProfileDetailQuery(agentId!);
  if (!agentId) return <div>잘못된 접근입니다.</div>;
  if (isLoading) return <div>로딩 중...</div>;
  const dataToRender = isError ? (
    <>
      <section className="flex flex-col">
        <AgentHeader />

        <div className="flex flex-row gap-5 mt-15">
          <AgentSpecialties />
          <Languages />
        </div>

        <div className="mt-15">
          <div className="headline-m-semibold text-gray-1000">추가 이력</div>
          <div className="mt-13 whitespace-pre-line title-s-medium text-text-base">
            {`주요 약력 및 자격\n현) OO 행정사 사무소 대표 행정사\n대한행정사협회
            정회원`}
          </div>
        </div>

        <div className="mt-40">
          <AgentReviewSection />
        </div>

        <div className="mt-40">
          <AgentBlog blogList={blog} />
        </div>

        <div className="mt-40">
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
          <div className="headline-m-semibold text-gray-1000">추가 이력</div>
          <div className="mt-13 whitespace-pre-line title-s-medium text-text-base">
            {data?.additionalHistory}
          </div>
        </div>

        <div className="mt-40">
          <AgentReviewSection reviewSummary={data?.reviewSummary} />
        </div>

        <div className="mt-40">
          <AgentBlog blogList={blog} agentInfo={data?.agentInfo} />
        </div>

        <div className="mt-40">
          <AgentOffice officeInfo={data?.officeInfo} />
        </div>
      </section>
      <div>
        <AgentProfilePanel
          agentInfo={data?.agentInfo}
          officeName={data?.officeInfo.officeName}
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
