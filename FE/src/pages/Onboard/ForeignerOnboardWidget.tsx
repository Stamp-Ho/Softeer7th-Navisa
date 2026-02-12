import { IcMessageBox } from "../../assets/icon/StratisUi";
import Button from "../../components/common/Button";
import TogglePill from "../../components/common/TogglePill";
import ProgressStepWidget from "../../components/form/ProgressStepWidget";
import type { FormSection } from "../../types/formType";
import GoTopFloating from "../../components/common/GoTopFloating";

const ForeignerOnboardWidget = ({
  sections,
  currentSectionIndex,
  goToSection,
  goTop,
  isGettingOffer,
  setIsGettingOffer,
}: {
  sections: FormSection[];
  currentSectionIndex: number;
  goToSection: (i: number) => void;
  goTop: () => void;

  isGettingOffer: boolean;
  setIsGettingOffer: React.Dispatch<React.SetStateAction<boolean>>;
}) => {
  return (
    <div className="w-fit ml-4 left-0 mt-19.75 flex flex-row">
      <div className="flex flex-col w-92 gap-5 ">
        <Button type="primary" className="shadow">
          저장
        </Button>
        <div className="flex flex-col bg-green-bright shadow gap-7 rounded-[20px] py-7.75 px-5.25">
          <div className="flex flex-row text-green-vivid title-s-semibold items-center gap-2">
            <IcMessageBox />
            행정사의 제안을 받고싶어요
            <TogglePill
              className="ml-auto"
              isActive={isGettingOffer}
              setIsActive={setIsGettingOffer}
              activeColor={"bg-green-vivid"}
            />
          </div>
          <div className="text-text-700 break-keep text-gray-700">
            해당 스위치를 on할 시 회원님이 작성한 프로필이{" "}
            <strong>서비스에 공개</strong>되며, 행정사가 회원님의 프로필을 보고
            수임 제안을 받을 수 있어요. 민감한 개인정보는 유출될 위험이 있으므로
            작성하지 않는게 좋아요.
          </div>
        </div>
        <ProgressStepWidget
          title="요건 등록하기"
          formData={sections}
          currentSectionId={currentSectionIndex}
          onSectionClick={goToSection}
          stepBySection={false}
        />
      </div>
      <GoTopFloating onClick={goTop} className="m-4 mt-auto" />
    </div>
  );
};
export default ForeignerOnboardWidget;
