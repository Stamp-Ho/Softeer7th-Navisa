import { FormProvider, useForm } from "react-hook-form";
import NavisaForm from "../../components/form/NavisaForm";
import { languageList } from "../../constants/language";
import type { FormSection } from "../../types/formType";
import ForeignerOnboardWidget from "./ForeignerOnboardWidget";
import { useOnboardScroll } from "./hooks/useOnboardScroll";
import { nationList } from "../../constants/nations";
import { useForiengerProfileMutation } from "../../api/hooks/useForeignerProfileMutation";
import { useNavigate } from "react-router-dom";
import { getLeafValues } from "../../components/form/utils/formUtils";
import { DegreeLevelList } from "../../api/types/common";
import { useState } from "react";

const ForeignerOnboard = () => {
  const {
    scrollRef,
    handleScroll,
    goToSection,
    goTop,
    currentSectionIndex,
    getMaskStyle,
  } = useOnboardScroll();
  const methods = useForm();
  const navigate = useNavigate();
  const updateProfileMutation = useForiengerProfileMutation(() => {
    navigate("/", { replace: true });
  });
  const [isGettingOffer, setIsGettingOffer] = useState(true);
  //@ts-ignore
  const onSubmit = (data) => {
    console.log(data);
    const param = {
      nationIdList: getLeafValues(data[0][0]),
      languageIdList: getLeafValues(data[0][1]),
      education: {
        degreeLevel: DegreeLevelList[data[0][2].values[0].degreeLevel],
        schoolName: data[0][2].values[0].schoolName,
        majorName: data[0][2].values[0].majorName,
      },
      foreignerCareers: data[0][3].disabled
        ? []
        : [
            {
              companyName: data[0][3].values[0].companyName,
              jobTitle: data[0][3].values[0].jobTitle,
              startDate: data[0][3].values[0].startDate,
              endDate: data[0][3]?.values?.[0]?.endDate ?? null,
              isWork: !!data[0][3]?.values?.[0]?.endDatedisabled,
            },
          ],
      expectedCompany: {
        jobTitle: data[1][0].values[0].jobTitle,
        companyName: data[1][1].values[0].companyName,
        startDate: data[1][1].values[0].startDate,
      },
      isRequesting: isGettingOffer,
    };
    updateProfileMutation.mutate(param);
  };
  const onError = (errors: any) => {
    console.log("유효성 검사 실패:", errors);
    alert("필수 입력 항목을 모두 채워주세요.");
  };
  return (
    <FormProvider {...methods}>
      <form
        className="flex flex-row overflow-y-auto w-fit"
        onSubmit={methods.handleSubmit(onSubmit, onError)}
      >
        <div
          className={`w-284 overflow-auto scrollbar-hide ${getMaskStyle()}`}
          style={{ height: "calc(100vh - 100px)" }}
          ref={scrollRef}
          onScroll={handleScroll}
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

        <ForeignerOnboardWidget
          sections={sections}
          currentSectionIndex={currentSectionIndex}
          goToSection={goToSection}
          goTop={goTop}
          isGettingOffer={isGettingOffer}
          setIsGettingOffer={setIsGettingOffer}
        />
      </form>
    </FormProvider>
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
        getMany: true,
        addButtonAtFirstLine: true,
        inputLines: [
          {
            inputs: [
              {
                placeholder: "국가를 선택해 주세요",
                inputType: "selector",
                options: nationList,
                isRequired: true,
                requestBodyName: "nationId",
              },
            ],
          },
        ],
      },
      {
        label: "사용 가능 언어",
        description: "Available Languages",
        getMany: true,
        addButtonAtFirstLine: true,
        inputLines: [
          {
            inputs: [
              {
                placeholder: "언어를 선택해 주세요",
                inputType: "selector",
                options: languageList,
                isRequired: true,
                requestBodyName: "languageId",
              },
            ],
          },
        ],
      },
      {
        label: "학력",
        description: "Education",
        getMany: false,
        inputLines: [
          {
            inputs: [
              {
                placeholder: "",
                inputType: "radio",
                options: ["학사미만", "학사", "석사 이상"],
                isRequired: true,
                requestBodyName: "degreeLevel",
              },
              {
                placeholder: "최종으로 졸업한 학력을 입력해주세요",
                inputDescription: "학교 명",
                englishDescription: "Name of School",
                inputType: "text",
                changeRow: true,
                isRequired: true,
                requestBodyName: "schoolName",
              },
              {
                placeholder: "전공 이름을 입력해주세요",
                inputDescription: "전공 명",
                englishDescription: "Name of Major",
                inputType: "text",
                isRequired: true,
                requestBodyName: "majorName",
              },
            ],
          },
        ],
      },
      {
        label: "경력",
        description: "Career",
        disableToggleDescription: "경력이 없어요",
        getMany: true,
        addButtonAtFirstLine: true,
        inputLines: [
          {
            inputs: [
              {
                inputDescription: "직무 명",
                englishDescription: "Name of Job",
                placeholder: "직무 이름을 입력해주세요",
                inputType: "text",
                isRequired: true,

                requestBodyName: "jobTitle",
              },
              {
                inputDescription: "회사 명",
                englishDescription: "Name of Corporate",
                placeholder: "회사 이름을 입력해주세요",
                inputType: "text",
                isRequired: true,
                requestBodyName: "companyName",
              },
              {
                inputDescription: "입사 일자",
                englishDescription: "Date of Retirement",
                inputType: "date",
                changeRow: true,
                isRequired: true,

                requestBodyName: "startDate",
              },
              {
                inputDescription: "퇴사 일자",
                englishDescription: "Date of Retirement",
                inputType: "date",
                changeRow: true,
                disableToggleDescription: "재직 중이에요",
                isRequired: true,
                requestBodyName: "endDate",
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
        getMany: false,
        inputLines: [
          {
            inputs: [
              {
                placeholder: "직무 명을 원본 그대로 입력해주세요",
                inputType: "text",
                isRequired: true,
                requestBodyName: "jobTitle",
              },
            ],
          },
        ],
      },
      {
        label: "입사 예정 회사",
        description: "Company to join",
        getMany: false,
        inputLines: [
          {
            inputs: [
              {
                inputType: "text",
                inputDescription: "회사 명",
                englishDescription: "Name of Corprorate",
                placeholder: "회사 명을 입력해주세요.",
                options: languageList,
                isRequired: true,

                requestBodyName: "companyName",
              },
              {
                inputType: "date",
                inputDescription: "입사 예정 일자",
                englishDescription: "Scheduled date of Employment",
                isRequired: true,

                requestBodyName: "startDate",
              },
            ],
          },
        ],
      },
    ],
  },
];
