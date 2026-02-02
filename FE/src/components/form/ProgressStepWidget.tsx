import React from "react";
import type { FormSection } from "../../types/formType";
import ProgressStep from "./ProgressStep";
import Tag from "../common/Tag";

const ProgressStepWidget = ({
  title,
  formData,
  currentSectionId = 0,
  onSectionClick = (_a: number) => {},
  stepBySection = false,
  elementBeforeSteps,
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
  return (
    <div className="shadow py-7 px-5 rounded-[20px] bg-white flex flex-col gap-5">
      {elementBeforeSteps}
      <h3 className="title-s-bold flex flex-row gap-2">
        {title} 항목 현황
        {stepBySection && <Tag type="small_fill_gray">{105}/138칸</Tag>}
      </h3>
      {stepBySection ? (
        <div className="flex flex-col">
          {formData.map((section, index) => (
            <div
              className="flex flex-col"
              onClick={() => onSectionClick(index)}
            >
              <ProgressStep
                label={section.name}
                index={index}
                currentIndex={currentSectionId}
                parentLength={formData.length}
              />
            </div>
          ))}
        </div>
      ) : (
        <div className="gap-4 flex flex-col">
          {formData.map((section, index) => (
            <div
              className="flex flex-col"
              onClick={() => onSectionClick(index)}
            >
              <h4 className="body-l-semibold mb-3">{section.name}</h4>
              {section.fields.map((field, index) => (
                <React.Fragment key={`progress_${index}`}>
                  <ProgressStep
                    label={field.label}
                    index={index}
                    currentIndex={currentSectionId}
                    parentLength={section.fields.length}
                  />
                </React.Fragment>
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
