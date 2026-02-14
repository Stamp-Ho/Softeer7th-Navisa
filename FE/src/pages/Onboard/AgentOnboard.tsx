import { FormProvider, useForm } from "react-hook-form";
import NavisaForm from "../../components/form/NavisaForm";
import { languageList } from "../../constants/language";
import type { FormSection } from "../../types/formType";
import { useNavigate } from "react-router-dom";
import { useAgentProfileMutation } from "../../api/mutations/useAgentProfileMutation";
import { useOnboardScroll } from "./hooks/useOnboardScroll";
import AgentOnboardWidget from "./AgentOnboardWidget";
import { getLeafValues } from "../../components/form/utils/formUtils";
import { jobCodeList, jobCodeList_Codes } from "../../constants/job";
import { useUploadImage } from "../../api/fetchHooks/useUploadImage";
import { useState } from "react";

const AgentOnboard = () => {
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
  const { uploadImage } = useUploadImage();
  const updateProfileMutation = useAgentProfileMutation(() => {
    navigate("/", { replace: true });
  });

  const [imageFile, setImageFile] = useState<File | undefined>(undefined);

  //@ts-ignore
  const onSubmit = async (data) => {
    if (imageFile === undefined) {
      alert("프로필 이미지가 없습니다");
      return;
    }
    const fileMimeType = imageFile.type;

    const imgObjectKey = await uploadImage(
      fileMimeType as "image/jpg" | "image/jpeg" | "image/png",
      "agent-profile",
      imageFile,
    );

    if (!imgObjectKey) {
      alert("저장에 실패했습니다");
      return;
    }

    const param = {
      basicInfo: {
        profileImageUrl: imgObjectKey,
        agentName: data[0][0].values[0].agentName,
        birthDate: data[0][1].values[0].birthDate, // date string
        phoneNumber: data[0][2].values[0].phoneNumber,
        officeName: data[0][3].values[0].officeName,
        officeAddress: data[0][3].values[0].officeAddress,
        officeAddressDetail: data[0][3].values[0].officeAddressDetail,
        businessTime: data[0][3].values[0].businessTime,
      },
      licenseInfo: {
        licenseNo: data[1][1].disabled ? null : data[1][1].values[0].licenseNo,
        licenseIssuedAt: data[1][2].disabled
          ? null
          : data[1][2].values[0].licenseIssuedAt,
        licenseInnerPageNo: data[1][3].disabled
          ? null
          : data[1][3].values[0].licenseInnerPageNo,
        licenseManagementNo: data[1][4].disabled
          ? null
          : data[1][4].values[0].licenseManagementNo,
      },
      detailedInfo: {
        specializedJobCodeIdList: getLeafValues(data[2][0]), //number[]
        availableLanguageIdList: getLeafValues(data[2][1]), //number[]
        agentComment: data[2][2].values[0].agentComment,
        additionalHistory: data[2][3].values[0].additionalHistory,
      },
    };
    updateProfileMutation.mutate(param);
    alert("등록 시도함!");
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
              내 정보 등록하기
            </h2>
            <a className="body-l-medium text-text-base">
              상세 요건을 입력하면 나에게 더 딱 맞는 행정사에게 제의를 받을 수
              있어요.
            </a>
            <NavisaForm
              formData={sections}
              startsWithImage={true}
              imageFile={imageFile}
              setImageFile={setImageFile}
            />
          </div>
        </div>
        <AgentOnboardWidget
          sections={sections}
          currentSectionIndex={currentSectionIndex}
          goToSection={goToSection}
          goTop={goTop}
        />
      </form>
    </FormProvider>
  );
};

export default AgentOnboard;

const sections: FormSection[] = [
  {
    name: "기본 정보",
    fields: [
      {
        label: "사진",
        getMany: false,
        inputLines: [
          {
            inputs: [
              {
                inputType: "image",
                colSpan: 9,
                placeholder:
                  "의뢰인들에게 신뢰를 줄 수 있는 이미지를 선택해주세요",
                isRequired: true,
                requestBodyName: "profileImageUrl",
              },
            ],
          },
        ],
      },
      {
        label: "이름",
        getMany: false,
        inputLines: [
          {
            inputs: [
              {
                placeholder: "이름을 입력 해주세요",
                inputType: "text",
                isRequired: true,
                requestBodyName: "agentName",
              },
            ],
          },
        ],
      },
      {
        label: "생년월일",
        getMany: false,
        inputLines: [
          {
            inputs: [
              {
                inputType: "date",
                isRequired: true,
                requestBodyName: "birthDate",
              },
            ],
          },
        ],
      },
      {
        label: "전화번호",
        getMany: false,
        inputLines: [
          {
            inputs: [
              {
                inputType: "text",
                placeholder: "010-1234-5678",
                isRequired: true,
                requestBodyName: "phoneNumber",
              },
            ],
          },
        ],
      },
      {
        label: "사무소명 및 주소",
        description: "도로명 주소로 검색해 보세요",
        getMany: false,
        inputLines: [
          {
            inputs: [
              {
                inputType: "text",
                inputDescription: "사무소 명",
                placeholder: "사무소 이름을 입력해주세요",
                isRequired: true,
                requestBodyName: "officeName",
              },
              {
                inputType: "timeRange",
                inputDescription: "영업 시간",
                placeholder: "",
                isRequired: true,
                requestBodyName: "businessTime",
              },
              {
                inputType: "text",
                inputDescription: "도로명 주소",
                placeholder: "도로명, 지번, 건물명을 검색하세요",
                changeRow: true,
                isRequired: true,
                requestBodyName: "officeAddress",
              },
              {
                inputType: "text",
                inputDescription: "상세 주소",
                placeholder: "상세 주소를 입력해주세요",
                isRequired: true,
                requestBodyName: "officeAddressDetail",
              },
            ],
          },
        ],
      },
    ],
  },
  {
    name: "자격증 진위 확인",
    fields: [
      {
        label: "자격증 종류",
        getMany: false,
        inputLines: [
          {
            inputs: [
              {
                placeholder: "자격증을 선택하세요",
                inputType: "selector",
                options: ["수첩형 자격증", "상장형 자격증", "모바일형 자격증"],
                isRequired: true,
                requestBodyName: "licenseType",
                disableTargets: { true: [1, 2, 3], false: [4] },
              },
            ],
          },
        ],
      },
      {
        label: "자격증 번호",
        description: "숫자와 알파벳 모두 작성해 주세요",
        getMany: false,
        inputLines: [
          {
            inputs: [
              {
                placeholder: "자격증 번호를 입력해주세요",
                inputType: "text",
                isRequired: true,
                requestBodyName: "licenseNo",
              },
            ],
          },
        ],
      },
      {
        label: "발급연월일",
        description: "최근 발급연월일 또는 등록연월일로 기재해 주세요",
        getMany: false,
        inputLines: [
          {
            inputs: [
              {
                inputType: "date",
                isRequired: true,
                requestBodyName: "licenseIssuedAt",
              },
            ],
          },
        ],
      },
      {
        label: "자격증 내지 번호",
        description:
          "2009년 8월 3일 이후에 발행된 자격증은 반드시 기재해 주세요",
        getMany: false,
        inputLines: [
          {
            inputs: [
              {
                inputType: "text",
                placeholder: "자격증 내지 번호를 입력해주세요",
                isRequired: true,
                requestBodyName: "licenseInnerPageNo",
              },
            ],
          },
        ],
      },
      {
        label: "자격증 관리 번호",
        description: "상장형 자격증이거나 모바일형 자격증일 경우 입력해주세요",
        getMany: false,
        inputLines: [
          {
            inputs: [
              {
                inputType: "text",
                placeholder: "자격증 관리 번호를 입력해주세요",
                isRequired: true,
                requestBodyName: "licenseManagementNo",
              },
            ],
          },
        ],
      },
    ],
  },
  {
    name: "상세 정보",
    fields: [
      {
        label: "전문 분야",
        description: "성공 사례가 가장 많은, 자신있는 직무코드를 선택해 주세요",
        getMany: true,
        inputLines: [
          {
            inputs: [
              {
                placeholder: "직종 코드나 관련 직무명을 입력하세요",
                inputType: "selector",
                options: jobCodeList.map(
                  (j, i) => `${j} (${jobCodeList_Codes[i]})`,
                ),
                isRequired: true,
                requestBodyName: "specializedJobCodeIdList",
              },
            ],
          },
        ],
      },
      {
        label: "사용 가능 언어",
        description: "상담 및 업무 진행이 가능한 언어를 모두 입력해 주세요",
        getMany: true,
        inputLines: [
          {
            inputs: [
              {
                placeholder: "언어를 선택하세요",
                inputType: "selector",
                options: languageList,
                isRequired: true,
                requestBodyName: "availableLanguageIdList",
              },
            ],
          },
        ],
      },
      {
        label: "행정사 한마디",
        description: "의뢰인에게 어필하고 싶은 점을 한마디로 작성해 주세요",
        getMany: false,
        inputLines: [
          {
            inputs: [
              {
                inputType: "text",
                colSpan: 9,
                placeholder: "저는 이런 사람입니다",
                isRequired: true,
                requestBodyName: "agentComment",
              },
            ],
          },
        ],
      },
      {
        label: "추가 이력",
        isOptional: true,
        description: "전문성이나 이력이 있다면 자유롭게 작성해 주세요",
        getMany: false,
        inputLines: [
          {
            inputs: [
              {
                inputType: "textArea",
                colSpan: 9,
                requestBodyName: "additionalHistory",
              },
            ],
          },
        ],
      },
    ],
  },
];
