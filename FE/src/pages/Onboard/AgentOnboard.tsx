import { IcArrowUp } from "../../assets/icon/StratisUi";
import Button from "../../components/common/Button";
import NavisaForm from "../../components/form/NavisaForm";
import ProgressStepWidget from "../../components/form/ProgressStepWidget";
import { languageList } from "../../types/language";
import type { FormSection } from "../../types/formType";

const AgentOnboard = () => {
  return (
    <div className="flex flex-row overflow-y-auto w-fit">
      <div
        className=" w-284 overflow-auto scrollbar-hide "
        style={{ height: "calc(100vh - 100px)" }}
      >
        <div className="flex flex-col pb-10 pt-14">
          <h2 className="headline-m-bold text-text-base mb-3">
            내 정보 등록하기
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
          <Button
            type="primary"
            size="medium"
            className="drop-shadow-[0_0_7px_#6860A040]"
          >
            저장
          </Button>
          <ProgressStepWidget title={"정보 등록하기"} formData={sections} />
        </div>
        <button
          className="m-4 mt-auto rounded-full cursor-pointer drop-shadow-[0_0_7px_#6860A040] bg-white w-16 h-16 flex items-center justify-center"
          onClick={() => {}}
        >
          <IcArrowUp size={20} />
        </button>
      </div>
    </div>
  );
};

export default AgentOnboard;

const sections: FormSection[] = [
  {
    name: "기본 정보",
    fields: [
      {
        label: "사진",
        inputLines: [
          {
            getMany: false,
            inputs: [
              {
                inputType: "image",
                colSpan: 9,
                placeholder:
                  "의뢰인들에게 신뢰를 줄 수 있는 이미지를 선택해주세요",
              },
            ],
          },
        ],
      },
      {
        label: "이름",
        inputLines: [
          {
            getMany: false,
            inputs: [
              {
                placeholder: "이름을 입력 해주세요",
                inputType: "text",
              },
            ],
          },
        ],
      },
      {
        label: "생년월일",
        inputLines: [
          {
            getMany: false,
            inputs: [
              {
                inputType: "date",
              },
            ],
          },
        ],
      },
      {
        label: "사무소명 및 주소",
        description: "도로명 주소로 검색해 보세요",
        inputLines: [
          {
            getMany: false,
            inputs: [
              {
                inputType: "text",
                inputDescription: "사무소 명",
                placeholder: "사무소 이름을 입력해주세요",
              },
              {
                inputType: "timeRange",
                inputDescription: "영업 시간",
                placeholder: "",
              },
              {
                inputType: "text",
                inputDescription: "도로명 주소",
                placeholder: "도로명, 지번, 건물명을 검색하세요",
                changeRow: true,
              },
              {
                inputType: "text",
                inputDescription: "상세 주소",
                placeholder: "상세 주소를 입력해주세요",
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
        inputLines: [
          {
            getMany: false,
            inputs: [
              {
                placeholder: "자격증을 선택하세요",
                inputType: "selector",
              },
            ],
          },
        ],
      },
      {
        label: "자격증 번호",
        description: "숫자와 알파벳 모두 작성해 주세요",
        inputLines: [
          {
            getMany: false,
            inputs: [
              {
                placeholder: "자격증 번호를 입력해주세요",
                inputType: "text",
              },
            ],
          },
        ],
      },
      {
        label: "발급연월일",
        description: "최근 발급연월일 또는 등록연월일로 기재해 주세요",
        inputLines: [
          {
            getMany: false,
            inputs: [
              {
                inputType: "date",
              },
            ],
          },
        ],
      },
      {
        label: "자격증 내지 번호",
        description:
          "2009년 8월 3일 이후에 발행된 자격증은 반드시 기재해 주세요",
        inputLines: [
          {
            getMany: false,
            inputs: [
              {
                inputType: "text",
                placeholder: "자격증 내지 번호를 입력해주세요",
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
        inputLines: [
          {
            getMany: false,
            inputs: [
              {
                placeholder: "직종 코드나 관련 직무명을 입력하세요",
                inputType: "selector",
                options: [],
              },
            ],
          },
        ],
      },
      {
        label: "사용 가능 언어",
        description: "상담 및 업무 진행이 가능한 언어를 모두 입력해 주세요",
        inputLines: [
          {
            getMany: true,
            inputs: [
              {
                placeholder: "언어를 선택하세요",
                inputType: "selector",
                options: languageList,
              },
            ],
          },
        ],
      },
      {
        label: "행정사 한마디",
        description: "의뢰인에게 어필하고 싶은 점을 한마디로 작성해 주세요",
        inputLines: [
          {
            getMany: false,
            inputs: [
              {
                inputType: "text",
                colSpan: 9,
                placeholder: "저는 이런 사람입니다",
              },
            ],
          },
        ],
      },
      {
        label: "추가 이력",
        isOptional: true,
        description: "전문성이나 이력이 있다면 자유롭게 작성해 주세요",
        inputLines: [
          {
            getMany: false,
            inputs: [
              {
                inputType: "textArea",
                colSpan: 9,
              },
            ],
          },
        ],
      },
    ],
  },
];
