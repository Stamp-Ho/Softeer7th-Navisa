import Header from "./Header";
import Education from "./Education";
import Languages from "./Languages";
import Career from "./Career";
import ExpectedCompany from "./ExpectedCompany";
import BannerBackground from "../../../components/shared/BannerBackground";
import { useParams } from "react-router-dom";
import { useForeignerProfileDetailQuery } from "../../../api/hooks/useForeignerProfileDetailQuery";

function ProfileOfForeigner() {
  const { foreignerId } = useParams();
  const { data, isLoading, isError } = useForeignerProfileDetailQuery(
    foreignerId!,
  );
  if (!foreignerId) return <div>잘못된 접근입니다.</div>;
  if (isLoading) return <div>로딩 중...</div>;
  const dataToRender = isError ? (
    <>
      <section className="flex flex-col overflow-auto scrollbar-hide">
        <Header />
        <div className="flex flex-col gap-5 mt-15">
          <div className="flex flex-row gap-4 mb-20">
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
      <section className="flex flex-col overflow-auto scrollbar-hide">
        <Header
          nationIdList={data?.basicInfo.nationIdList}
          nickname={data?.basicInfo.nickname}
        />
        <div className="flex flex-col gap-5 mt-15">
          <div className="flex flex-row gap-4 mb-20">
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
        chatRoomId={data?.basicInfo.chatRoomId}
      />
    </>
  );

  return (
    <>
      <BannerBackground />
      <div className="flex flex-row justify-between mt-20 mb-20">
        {dataToRender}
      </div>
    </>
  );
}

export default ProfileOfForeigner;
