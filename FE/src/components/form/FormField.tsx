import { Controller, useFormContext, useWatch } from "react-hook-form";
import FormRadio from "./inputComponents/FormRadio";
import Tag from "../common/Tag";
import CheckBox from "../common/CheckBox";
import type { FormSection, inputFieldType } from "../../types/formType";
import React, { useEffect } from "react";
import FormInputLine from "./FormInputLine";

const FormField = ({
  inputField,
  sectionIdx = 0,
  fieldIdx = 0,
  addIndex = false,
  startsWithImage = false,
  setFormStruct,
}: {
  inputField: inputFieldType;
  sectionIdx: number;
  fieldIdx: number;
  addIndex: boolean;
  startsWithImage?: boolean;
  setFormStruct: React.Dispatch<React.SetStateAction<FormSection[]>>;
}) => {
  const methods = useFormContext();

  const isFieldDisabled = useWatch({
    control: methods.control,
    name: `${sectionIdx}.sectionData.${fieldIdx}.disabled`,
  });
  useEffect(() => {
    if (isFieldDisabled) {
      const targetFieldIndex =
        startsWithImage && sectionIdx === 0 ? fieldIdx + 1 : fieldIdx;

      setFormStruct((prev) => {
        return prev.map((section, sIdx) => {
          if (sIdx !== sectionIdx) return section;

          return {
            ...section,
            fields: section.fields.map((field, fIdx) => {
              if (fIdx !== targetFieldIndex) return field;

              return {
                ...field,
                inputLines: [field.inputLines[0]],
              };
            }),
          };
        });
      });

      methods.unregister(`${sectionIdx}.sectionData.${fieldIdx}.values`);
    }
  }, [isFieldDisabled]);
  return (
    <div className="flex flex-col" key={`formSection_${fieldIdx}`}>
      <div className="flex flex-row items-center">
        <div className="mb-4">
          {addIndex && (
            <Tag
              type={"small_fill_gray_dark"} //false ? "small_fill_violet_max" :"small_fill_gray_dark"
              className="w-fit mb-2"
            >
              {sectionIdx + 1}-
              {startsWithImage && sectionIdx === 0 ? fieldIdx : fieldIdx + 1}
            </Tag>
          )}
          <h4 className="title-l-semibold flex flex-row gap-1 items-center">
            {inputField.isOptional && <Tag type="small_fill">선택</Tag>}
            {inputField.label}
          </h4>
          {inputField.description && (
            <h5 className="title-s-medium text-text-sub">
              {inputField.description}
            </h5>
          )}
          {inputField.canInputBlocked && (
            <Controller
              name={`${sectionIdx}.sectionData.${fieldIdx}.disabled`}
              control={methods.control}
              render={({ field }) => (
                <FormRadio
                  value={field.value} // 불리언 값
                  setValue={field.onChange} // 클릭 시 실행
                  options={[false, true]}
                  className=" mt-3 w-56.5"
                />
              )}
            />
          )}
        </div>
        {inputField.disableToggleDescription && (
          <div className="ml-auto mr-3">
            <Controller
              name={`${sectionIdx}.sectionData.${fieldIdx}.disabled`}
              control={methods.control}
              render={({ field }) => (
                <CheckBox
                  value={field.value} // 불리언 값
                  setValue={field.onChange} // 클릭 시 실행
                  label={inputField.disableToggleDescription}
                />
              )}
            />
          </div>
        )}
      </div>
      <div className="flex flex-col gap-4">
        {inputField.inputLines.map((inputLine, inputLineIdx) => (
          <FormInputLine
            inputField={inputField}
            sectionIdx={sectionIdx}
            fieldIdx={fieldIdx}
            inputLine={inputLine}
            inputLineIdx={inputLineIdx}
            setFormStruct={setFormStruct}
            key={`inputLine_${sectionIdx}_${inputLineIdx}`}
          />
        ))}
      </div>
    </div>
  );
};

export default FormField;
