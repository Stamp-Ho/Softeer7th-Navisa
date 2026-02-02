import Header from "./Header";
import Education from "./Education";
import Languages from "./Languages";
import Career from "./Career";
import ExpectedCompany from "./ExpectedCompany";
import BannerBackground from "../../../components/shared/BannerBackground";

const dummy = {
  basicInfo: {
    foreignerId: 99,
    nickname: "고라니 099",
    nationIdList: [1, 24],
    lastAccessDay: "2026-01-26T11:27:02+09:00",
    isChatting: false, // 채팅방 활성화 여부
  },
  academicInfo: {
    degreeLevel: "석사",
    schoolName: "으악대학교",
    majorName: "대박전공",
  },
  languageList: [0, 13, 2, 0, 0, 0, 0],
  careerInfo: {
    totalCareerMonths: 20, // 없어도 됨
    history: [
      {
        companyName: "땡땡회사",
        jobTitle: "머시기 직무",
        period: "2023. 11. 02 ~ 2024. 11. 02",
        durationMonths: 18, // 서버 계산 (int)
      },
      {
        companyName: "땡땡회사",
        jobTitle: "머시기 직무",
        period: "2023. 11. 02 ~ 2024. 11. 02",
        durationMonths: 6,
      },
      {
        companyName: "땡땡회사",
        jobTitle: "머시기 직무",
        period: "2023. 11. 02 ~ 2024. 11. 02",
        durationMonths: 2,
      },
    ],
  },
  expectedInfo: {
    targetJob: "웹 개발자",
    companyName: "대박쩌는 IT회사",
    startDate: "2026. 01. 31",
  },
};

function ProfileOfforeigner() {
  return (
    <>
      <BannerBackground />
      <div className="flex flex-row justify-between mt-20 mb-20">
        <section className="flex flex-col overflow-auto scrollbar-hide">
          <Header
            nationIdList={dummy.basicInfo.nationIdList}
            nickName={dummy.basicInfo.nickname}
          />
          <div className="flex flex-col gap-5 mt-15">
            <div className="flex flex-row gap-4 mb-20">
              <Education
                schoolName={dummy.academicInfo.schoolName}
                degreeLevel={dummy.academicInfo.degreeLevel}
                majorName={dummy.academicInfo.majorName}
              />
              <Languages languageIdList={dummy.languageList} />
            </div>
            <Career foreignerCareerList={dummy.careerInfo.history} />
          </div>
        </section>
        <ExpectedCompany
          companyName={dummy.expectedInfo.companyName}
          jobTitle={dummy.expectedInfo.targetJob}
          startDate={dummy.expectedInfo.startDate}
          lastAccessDay={dummy.basicInfo.lastAccessDay}
          isChatting={dummy.basicInfo.isChatting}
          nickName={dummy.basicInfo.nickname}
        />
      </div>
    </>
  );
}

export default ProfileOfforeigner;
