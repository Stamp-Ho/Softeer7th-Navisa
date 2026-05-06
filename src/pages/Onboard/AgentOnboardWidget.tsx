import { useTranslation } from "react-i18next";
import Button from "../../components/common/Button";
import GoTopFloating from "../../components/common/GoTopFloating";
import ProgressStepWidget from "../../components/form/widgetComponents/ProgressStepWidget";
import type { FormSection } from "../../types/formType";

const AgentOnboardWidget = ({
  sections,
  currentSectionIndex,
  goToSection,
  goTop,
  isEditing = false,
}: {
  sections: FormSection[];
  currentSectionIndex: number;
  goToSection: (i: number) => void;
  goTop: () => void;
  isEditing?: boolean;
}) => {
  const { t } = useTranslation(["pages"]);
  return (
    <div className="w-fit ml-4 left-0 mt-19.75 flex flex-row">
      <div className="flex flex-col w-92 gap-5 ">
        <Button variant="primary" size="medium" className="shadow" type="submit">
          {isEditing ? "정보 수정하기" : t("onboard.save")}
        </Button>
        <ProgressStepWidget
          title={isEditing ? "정보 수정하기" : t("onboard.registerInfo")}
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

export default AgentOnboardWidget;
