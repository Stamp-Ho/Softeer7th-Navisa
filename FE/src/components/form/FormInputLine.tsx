import { Controller, useFormContext, useWatch } from "react-hook-form";
import AddButton from "../common/AddButton";
import SubtractButton from "../common/SubtractButton";
import InputRenderer from "./InputRenderer";
import CheckBox from "../common/CheckBox";
import React from "react";
import type {
  FormSection,
  inputFieldType,
  inputLineType,
} from "../../types/formType";

const FormInputLine = ({
  inputField,
  sectionIdx = 0,
  fieldIdx = 0,
  inputLine,
  inputLineIdx,
  setFormStruct,
}: {
  inputField: inputFieldType;
  sectionIdx: number;
  fieldIdx: number;
  inputLine: inputLineType;
  inputLineIdx: number;
  setFormStruct: React.Dispatch<React.SetStateAction<FormSection[]>>;
}) => {
  const { control, getValues, setValue } = useFormContext();

  const isFieldDisabled = useWatch({
    control,
    name: `${sectionIdx}.sectionData.${fieldIdx}.disabled`,
  });

  // 줄 추가
  const handleAddField = () => {
    setFormStruct((prev) => {
      // 1. 전체 구조 깊은 복사 (중첩 구조이므로 중요!)
      const newStruct = JSON.parse(JSON.stringify(prev));
      const targetField = newStruct[sectionIdx].fields[fieldIdx];

      const newLine = {
        ...JSON.parse(JSON.stringify(targetField.inputLines[0])),
        rowId: Date.now(), // 고유 키 추가
      };
      targetField.inputLines.push(newLine);
      return newStruct;
    });
  };

  // 줄 삭제
  const handleSubtractField = () => {
    const path = `${sectionIdx}.sectionData.${fieldIdx}.values`; // 감시 중인 배열 경로
    const currentValues = getValues(path);

    // 1. 데이터 배열에서 해당 인덱스 삭제
    const nextValues = [...currentValues];
    nextValues.splice(inputLineIdx, 1);
    // 2. setValue로 배열 전체를 '교체'
    // { shouldDirty: true } 등을 주면 watch가 "아, 값이 변했구나!" 하고 즉시 반응합니다.
    setValue(path, nextValues, {
      shouldDirty: true,
    });
    setFormStruct((prev) => {
      const newStruct = JSON.parse(JSON.stringify(prev));
      const targetLines = newStruct[sectionIdx].fields[fieldIdx].inputLines;

      if (targetLines.length <= 1) return prev; // 최소 한 줄은 남기기
      targetLines.splice(inputLineIdx, 1); // 해당 인덱스 삭제

      return newStruct;
    });
  };

  const AddBtn = (
    <AddButton onClick={handleAddField} disabled={isFieldDisabled} />
  );
  const SubsBtn = <SubtractButton onClick={handleSubtractField} />;

  let addBtnAlreadyRendered = !inputField.getMany;

  const addAddBtnAtFirstLine = (inputIdx: number) => {
    return (
      inputField.getMany &&
      !inputField.addButtonAtBelowLines &&
      inputLineIdx === 0 &&
      inputIdx === inputLine.inputs.length - 1
    );
  };
  const addAddBtnBeforeThisLine = (changeRow: boolean | undefined) => {
    return changeRow && inputField.addButtonAtFirstLine && inputLineIdx === 0;
  };

  const addAddBtnBelowLine =
    inputField.getMany &&
    !inputField.addButtonAtFirstLine &&
    inputField.addButtonAtBelowLines &&
    inputLineIdx === inputField.inputLines.length - 1;
  //Array.from({ length: 9 }).map((_, i) => `col-span-${i + 1}`);
  return (
    <div
      className="grid grid-cols-9 items-end gap-x-3 gap-y-6"
      key={inputLine.rowId || `field_${inputLineIdx}`}
    >
      {inputLineIdx > 0 && inputLine.inputs.length > 1 && (
        <div className="col-span-9 mr-71.5 bg-gray-200 h-0.5 -mb-1.5" />
      )}
      {inputLine.inputs.map((input, inputIdx) => {
        let btnBeforeThisInput = <></>;
        let btnAfterThisInput = <></>;
        if (!addBtnAlreadyRendered) {
          if (addAddBtnAtFirstLine(inputIdx)) {
            btnAfterThisInput = AddBtn;
            addBtnAlreadyRendered = true;
          } else if (addAddBtnBeforeThisLine(input.changeRow)) {
            btnBeforeThisInput = AddBtn;
            addBtnAlreadyRendered = true;
          }
          if (inputLineIdx !== 0 && input.changeRow) {
            btnBeforeThisInput = SubsBtn;
            addBtnAlreadyRendered = true;
          } else if (inputLineIdx !== 0 && inputLine.inputs.length === 1) {
            btnAfterThisInput = SubsBtn;
          }
        }
        const inputLabel = `${sectionIdx}.sectionData.${fieldIdx}.values.${inputLine.rowId ?? inputLineIdx}.${input.requestBodyName ?? inputIdx}`;

        return (
          <React.Fragment key={`field_${inputIdx}`}>
            {btnBeforeThisInput}
            <div
              className={`flex flex-col ${input.colSpan ? colSpans[input.colSpan - 1] : "col-span-3"}
                                  ${input.changeRow ? "col-start-1" : ""}`}
            >
              {input.inputDescription && (
                <a className="body-m-regular text-gray-400 mb-1">
                  {input.inputDescription}
                  {input.englishDescription && `/${input.englishDescription}`}
                </a>
              )}
              <InputRenderer input={input} inputLabel={inputLabel} />
            </div>
            {input.disableToggleDescription && (
              <Controller
                name={`${inputLabel}disabled`}
                control={control}
                render={({ field }) => (
                  <CheckBox
                    value={field.value} // 불리언 값
                    setValue={field.onChange} // 클릭 시 실행
                    label={input.disableToggleDescription}
                    className="col-span-2"
                    disabled={isFieldDisabled}
                  />
                )}
              />
            )}
            {btnAfterThisInput}
          </React.Fragment>
        );
      })}
      {addAddBtnBelowLine && <div className="col-start-1">{AddBtn}</div>}
    </div>
  );
};
export default FormInputLine;

const colSpans = [
  "col-span-1",
  "col-span-2",
  "col-span-3",
  "col-span-4",
  "col-span-5",
  "col-span-6",
  "col-span-7",
  "col-span-8",
  "col-span-9",
];
