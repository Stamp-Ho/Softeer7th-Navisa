import BannerBackground from "../../../components/shared/BannerBackground";
import Languages from "../Foreigner/Languages";
import AgentBlog from "./AgentBlog";
import AgentHeader from "./AgentHeader";
import AgentOffice from "./AgentOffice";
import AgentProfilePanel from "./AgentProfilePanel";
import AgentReviewSection from "./AgentReviewSection";
import AgentSpecialties from "./AgentSpecialties";

const dummyData = {
  agentInfo: {
    agentId: 10,
    name: "엄경례",
    profileImageUrl: "https://placehold.co/368x452",
    lastAccessDay: "2026-01-28T11:27:02+09:00",
    isChatting: false, // 채팅방 활성화 여부
    slogan: "최고의 결과를 막힘없이 가져다드립니다",
    introduction:
      "E7 비자는 기업의 성장 동력을 확보하는 첫 단추입니다. 그 소중한 시작이 늦어지지 않도록...",
    rating: 4.9,
    reviewCount: 14,
    location: "서울특별시 강남구",
  },
  expertise: {
    specialties: [0, 1, 2, 3],
    languages: [0, 1, 2, 3],
  },
  details:
    "주요 약력 및 자격\n현) OO 행정사 사무소 대표 행정사\n대한행정사협회 정회원",
  totalReviews: 140,
  strengths: [
    { badgeId: 4, reviewCount: 102 },
    { badgeId: 10, reviewCount: 79 },
    { badgeId: 2, reviewCount: 73 },
    { badgeId: 7, reviewCount: 50 },
    { badgeId: 1, reviewCount: 21 },
    { badgeId: 0, reviewCount: 7 },
  ],
  blog: [
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
  ],
  officeInfo: {
    officeName: "엄경례 행정사사무소",
    address: "서울 강남구 테헤란로 116, 10층 1011호",
    businessHours: "평일 AM 10:00~PM 6:00",
    phoneNumber: "010-1234-5678",
  },
};

const ProfileOfAgent = () => {
  return (
    <>
      <BannerBackground />
      <div className="relative flex flex-col mt-21">
        <div className="flex flex-row justify-between">
          <section className="flex flex-col">
            <AgentHeader
              strengths={dummyData.strengths}
              slogan={dummyData.agentInfo.slogan}
            />

            <div className="flex flex-row gap-5 mt-15">
              <AgentSpecialties specialties={dummyData.expertise.specialties} />
              <Languages languageIdList={dummyData.expertise.languages} />
            </div>

            <div className="mt-15">
              <div className="headline-m-semibold text-gray-1000">
                추가 이력
              </div>
              <div className="mt-13 whitespace-pre-line title-s-medium text-text-base">
                {dummyData.details}
              </div>
            </div>

            <div className="mt-40">
              <AgentReviewSection
                totalReviews={dummyData.totalReviews}
                strengths={dummyData.strengths}
              />
            </div>

            <div className="mt-40">
              <AgentBlog
                blogList={dummyData.blog}
                name={dummyData.agentInfo.name}
                profileImageUrl={dummyData.agentInfo.profileImageUrl}
              />
            </div>

            <div className="mt-40">
              <AgentOffice officeInfo={dummyData.officeInfo} />
            </div>
          </section>
          <div>
            <AgentProfilePanel
              profileImageUrl={dummyData.agentInfo.profileImageUrl}
              name={dummyData.agentInfo.name}
              officeName={dummyData.officeInfo.officeName}
              lastAccessDay={dummyData.agentInfo.lastAccessDay}
              isChatting={dummyData.agentInfo.isChatting}
            />
          </div>
        </div>
      </div>
    </>
  );
};

export default ProfileOfAgent;
