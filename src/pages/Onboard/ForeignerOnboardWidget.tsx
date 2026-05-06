import { useTranslation } from "react-i18next";
import { IcMessageBox } from "../../assets/icon/StratisUi";
import Button from "../../components/common/Button";
import TogglePill from "../../components/form/formComponents/TogglePill";
import ProgressStepWidget from "../../components/form/widgetComponents/ProgressStepWidget";
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
  const { t } = useTranslation(["pages"]);
  return (
    <div className="w-fit ml-4 left-0 mt-19.75 flex flex-row">
      <div className="flex flex-col w-92 gap-5 ">
        <Button variant="primary" className="shadow" type="submit">
          {t("onboard.save")}
        </Button>
        <div className="flex flex-col bg-green-bright shadow gap-7 rounded-[20px] py-7.75 px-5.25">
          <div className="flex flex-row text-green-vivid title-s-semibold items-center gap-2">
            <IcMessageBox />
            {t("onboard.wantOffers")}
            <TogglePill
              className="ml-auto"
              isActive={isGettingOffer}
              setIsActive={setIsGettingOffer}
              activeColor={"bg-green-vivid"}
            />
          </div>
          <div className="text-text-700 break-keep text-gray-700">
            {t("onboard.publicNoticeBefore")}
            <strong>{t("onboard.publicNoticeHighlight")}</strong>
            {t("onboard.publicNoticeAfter")}
          </div>
        </div>
        <ProgressStepWidget
          title={t("onboard.registerRequirement")}
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
