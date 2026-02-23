import React from "react";
import type { FormSection } from "../../../types/formType";
import ProgressStep from "./ProgressStep";
import Tag from "../../common/Tag";
import { calculateOnlyInputs } from "../utils/formUtils";
import { useWatch } from "react-hook-form";

const ProgressStepWidget = ({
  title,
  formData,
  currentSectionId = 0,
  onSectionClick = (_a: number) => {},
  stepBySection = false,
  elementAfterSteps,
}: {
  title: string;
  formData: FormSection[];
  currentSectionId?: number;
  onSectionClick?: (a: number) => void;
  stepBySection?: boolean;
  elementBeforeSteps?: React.ReactNode;
  elementAfterSteps?: React.ReactNode;
}) => {
  const formInputs = useWatch();
  const fieldsPerSections = formData.flatMap((section) => section.fields.length);

  const getAbsoluteIndex = (sectionIndex: number, fieldIndex: number) => {
    let result = fieldIndex;
    for (let i = 0; i < sectionIndex; i++) {
      result += fieldsPerSections[i];
    }
    return result;
  };
  const { totalCount, filledCount } = calculateOnlyInputs(formInputs);
  return (
    <div className="shadow py-7 px-5 rounded-[20px] bg-white flex flex-col gap-5">
      <h3 className="title-s-bold flex flex-row gap-2">
        {title} 항목 현황
        {stepBySection && (
          <Tag variant="small_fill_gray">
            {filledCount}/{totalCount}칸
          </Tag>
        )}
      </h3>
      {stepBySection ? (
        <div className="flex flex-col">
          {formData.map((section, sectionIndex) => (
            <div
              className="flex flex-col"
              onClick={() => onSectionClick(sectionIndex)}
              key={`progress_section_${sectionIndex}`}
            >
              <ProgressStep
                label={section.name}
                sectionIndex={sectionIndex}
                currentIndex={currentSectionId}
                parentLength={formData.length}
              />
            </div>
          ))}
        </div>
      ) : (
        <div className="gap-4 flex flex-col">
          {formData.map((section, sectionIndex) => (
            <div className="flex flex-col" key={`progress_section_without_step_${sectionIndex}`}>
              <h4 className="body-l-semibold mb-3">{section.name}</h4>
              {section.fields.map((field, fieldIndex) => (
                <div
                  key={`progress_${fieldIndex}`}
                  onClick={() => onSectionClick(getAbsoluteIndex(sectionIndex, fieldIndex))}
                >
                  <ProgressStep
                    label={field.label}
                    sectionIndex={sectionIndex}
                    fieldIndex={fieldIndex}
                    currentIndex={currentSectionId - getAbsoluteIndex(sectionIndex, 0)}
                    parentLength={section.fields.length}
                  />
                </div>
              ))}
            </div>
          ))}
        </div>
      )}
      {elementAfterSteps}
    </div>
  );
};

export default ProgressStepWidget;
