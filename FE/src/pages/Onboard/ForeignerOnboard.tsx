import { useState } from "react";
import { IcArrowUp, IcMessageBox } from "../../assets/icon/StratisUi";
import Button from "../../components/common/Button";
import TogglePill from "../../components/common/TogglePill";
import NavisaForm from "../../components/form/NavisaForm";
import ProgressStepWidget from "../../components/form/ProgressStepWidget";
import { languageList } from "../../constants/language";
import { regionList } from "../../constants/regions";
import type { FormSection } from "../../types/formType";

const ForeignerOnboard = () => {
  const [isGettingOffer, setIsGettingOffer] = useState<boolean>(true);
  return (
    <div className="flex flex-row overflow-y-auto w-fit">
      <div
        className=" w-284 overflow-auto scrollbar-hide "
        style={{ height: "calc(100vh - 100px)" }}
      >
        <div className="flex flex-col pb-10 pt-14">
          <h2 className="headline-m-bold text-text-base mb-3">
            내 요건 등록하기
          </h2>
          <a className="body-l-medium text-text-base">
            상세 요건을 입력하면 나에게 더 딱 맞는 행정사에게 제의를 받을 수
            있어요.
          </a>
          <NavisaForm formData={sections} />
        </div>
      </div>
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
              <strong>서비스에 공개</strong>되며, 행정사가 회원님의 프로필을
              보고 수임 제안을 받을 수 있어요. 민감한 개인정보는 유출될 위험이
              있으므로 작성하지 않는게 좋아요.
            </div>
          </div>
          <ProgressStepWidget title="요건 등록하기" formData={sections} />
        </div>
        <button
          className="m-4 mt-auto rounded-full cursor-pointer shadow bg-white w-16 h-16 flex items-center justify-center"
          onClick={() => {}}
        >
          <IcArrowUp size={20} />
        </button>
      </div>
    </div>
  );
};

export default ForeignerOnboard;

const sections: FormSection[] = [
  {
    name: "기본 정보",
    fields: [
      {
        label: "국적",
        description: "Nationality",
        inputLines: [
          {
            getMany: true,
            addButtonAtFirstLine: true,
            inputs: [
              {
                placeholder: "국가를 선택해 주세요",
                inputType: "selector",
                options: regionList,
              },
            ],
          },
        ],
      },
      {
        label: "사용 가능 언어",
        description: "Available Languages",
        inputLines: [
          {
            getMany: true,
            addButtonAtFirstLine: true,
            inputs: [
              {
                placeholder: "언어를 선택해 주세요",
                inputType: "selector",
                options: languageList,
              },
            ],
          },
        ],
      },
      {
        label: "학력",
        description: "Education",
        inputLines: [
          {
            getMany: true,
            inputs: [
              {
                placeholder: "",
                inputType: "radio",
                options: ["학사미만", "학사", "석사 이상"],
              },
              {
                placeholder: "최종으로 졸업한 학력을 입력해주세요",
                inputDescription: "학교 명",
                englishDescription: "Name of School",
                inputType: "text",
                changeRow: true,
              },
              {
                placeholder: "전공 이름을 입력해주세요",
                inputDescription: "전공 명",
                englishDescription: "Name of Major",
                inputType: "text",
              },
            ],
          },
        ],
      },
      {
        label: "경력",
        description: "Career",
        disableToggleDescription: "경력이 없어요",
        inputLines: [
          {
            getMany: true,
            addButtonAtFirstLine: true,
            inputs: [
              {
                inputDescription: "직무 명",
                englishDescription: "Name of Job",
                placeholder: "직무 이름을 입력해주세요",
                inputType: "text",
              },
              {
                inputDescription: "회사 명",
                englishDescription: "Name of Corporate",
                placeholder: "회사 이름을 입력해주세요",
                inputType: "text",
              },
              {
                inputDescription: "입사 일자",
                englishDescription: "Date of Retirement",
                inputType: "date",
                changeRow: true,
              },
              {
                inputDescription: "퇴사 일자",
                englishDescription: "Date of Retirement",
                inputType: "date",
                changeRow: true,
                disableToggleDescription: "재직 중이에요",
              },
            ],
          },
        ],
      },
    ],
  },
  {
    name: "입사 예정 정보",
    fields: [
      {
        label: "입사 예정 직무",
        description: "Job to join",
        inputLines: [
          {
            getMany: false,
            inputs: [
              {
                placeholder: "직무 명을 원본 그대로 입력해주세요",
                inputType: "text",
              },
            ],
          },
        ],
      },
      {
        label: "입사 예정 회사",
        description: "Company to join",
        inputLines: [
          {
            getMany: false,
            inputs: [
              {
                inputType: "text",
                inputDescription: "회사 명",
                englishDescription: "Name of Corprorate",
                placeholder: "회사 명을 입력해주세요.",
                options: languageList,
              },
              {
                inputType: "date",
                inputDescription: "입사 예정 일자",
                englishDescription: "Scheduled date of Employment",
              },
            ],
          },
        ],
      },
    ],
  },
];
