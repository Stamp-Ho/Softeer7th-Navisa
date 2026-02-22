import type { FormSection } from "../../types/formType";
import { nationList } from "../../constants/nations";

export const editDocumentData: FormSection[] = [
  {
    name: "인적사항",
    description: "Personal Detail",
    fields: [
      {
        label: "여권용 사진 (35mm*45mm)",
        getMany: false,
        inputLines: [
          {
            inputs: [
              {
                placeholder: "흰색 바탕에 모자를 쓰지 않은 정면 사진으로 촬영일로부터 6개월이 경과하지 않아야 함",
                inputType: "image",
                colSpan: 9,
                requestBodyName: "profileImageUrl",
              },
            ],
          },
        ],
      },
      {
        label: "여권에 기재된 영문 성명",
        description: "Full name is English (as shown in passport)",
        getMany: false,
        inputLines: [
          {
            inputs: [
              {
                placeholder: "성",
                inputDescription: "성",
                englishDescription: "Family Name",
                inputType: "text",
                validator: "koreanOrEnglish",
                maxLength: 20,
              },
              {
                placeholder: "이름",
                inputDescription: "명",
                englishDescription: "Given Name",
                inputType: "text",
                validator: "koreanOrEnglish",
                maxLength: 20,
              },
            ],
          },
        ],
      },
      {
        label: "한자성명",
        description: "Chinese character's name",
        getMany: false,
        inputLines: [
          {
            inputs: [
              {
                placeholder: "한자 이름",
                inputType: "text",
                validator: "hanja",
                maxLength: 10,
              },
            ],
          },
        ],
      },
      {
        label: "성별 ",
        description: "Sex",
        getMany: false,
        inputLines: [
          {
            inputs: [
              {
                inputType: "radio",
                colSpan: 2,
                options: ["남성", "여성"],
              },
            ],
          },
        ],
      },
      {
        label: "생년월일",
        description: "Date of birth",
        getMany: false,
        inputLines: [
          {
            inputs: [
              {
                inputType: "date",
                onlyPast: true,
                birthDate: true,
              },
            ],
          },
        ],
      },
      {
        label: "국적",
        description: "Nationality",
        getMany: true,
        maxLine: 3,
        inputLines: [
          {
            inputs: [
              {
                inputType: "selector",
                options: nationList,
                placeholder: "국가를 선택해 주세요",
              },
            ],
          },
        ],
      },
      {
        label: "출생 국가",
        description: "Country of Birth",
        getMany: false,
        inputLines: [
          {
            inputs: [
              {
                inputType: "selector",
                options: nationList,
                placeholder: "국가를 선택해 주세요",
              },
            ],
          },
        ],
      },
      {
        label: "국가신분증번호",
        description: "National Identy No.",
        getMany: false,
        inputLines: [
          {
            inputs: [
              {
                inputType: "text",
                placeholder: "신분증 번호를 입력해 주세요",
                validator: "noSymbol",
                maxLength: 20,
              },
            ],
          },
        ],
      },
      {
        label: "이전에 한국에 출입국하였을 때 다른 성명을 사용했는지 여부",
        description: "Has the applicant ever used any other names to enter or depart Korea?",
        getMany: false,
        canInputBlocked: true,
        inputLines: [
          {
            inputs: [
              {
                placeholder: "성",
                inputDescription: "성",
                englishDescription: "Family Name",
                inputType: "text",
                validator: "koreanOrEnglish",
              },
              {
                placeholder: "이름",
                inputDescription: "명",
                englishDescription: "Given Name",
                inputType: "text",
                validator: "koreanOrEnglish",
              },
            ],
          },
        ],
      },
      {
        label: "복수 국적 여부",
        description: "Is the applicant  a citizen of more than one country??",
        getMany: false,
        canInputBlocked: true,
        inputLines: [
          {
            inputs: [
              {
                inputType: "text",
                inputDescription: "세부사항",
                englishDescription: "If ‘Yes’, please provide details",
                placeholder: "세부사항을 입력해주세요.",
                maxLength: 18,
                validator: "noSymbol",
              },
            ],
          },
        ],
      },
    ],
  },
  {
    name: "여권정보",
    description: "Passport Information",
    fields: [
      {
        label: "여권 종류",
        description: "Passport Type",
        getMany: false,
        inputLines: [
          {
            inputs: [
              {
                inputType: "radio",
                colSpan: 4,
                options: ["외교관", "관용", "일반", "기타"],
              },
            ],
          },
        ],
      },
      {
        label: "여권번호",
        description: "Passport No.",
        getMany: false,
        inputLines: [
          {
            inputs: [
              {
                inputType: "text",
                placeholder: "여권번호를 입력해주세요",
                maxLength: 18,
                validator: "englishOrNumber",
              },
            ],
          },
        ],
      },
      {
        label: "발급국가",
        description: "Country of Passport",
        getMany: false,
        inputLines: [
          {
            inputs: [
              {
                inputType: "selector",
                options: nationList,
                placeholder: "국가를 선택해 주세요",
              },
            ],
          },
        ],
      },
      {
        label: "발급지",
        description: "Place of Issue",
        getMany: false,
        inputLines: [
          {
            inputs: [
              {
                inputType: "text",
                placeholder: "여권 발급지를 입력해주세요",
                maxLength: 15,
                validator: "koreanOrEnglish",
              },
            ],
          },
        ],
      },
      {
        label: "발급일자",
        description: "Date of Issue",
        getMany: false,
        inputLines: [
          {
            inputs: [
              {
                inputType: "date",
                onlyPast: true,
              },
            ],
          },
        ],
      },
      {
        label: "기간 만료일",
        description: "Date of Expiry",
        getMany: false,
        inputLines: [
          {
            inputs: [
              {
                inputType: "date",
                onlyFuture: true,
              },
            ],
          },
        ],
      },
      {
        label: "다른 여권 소지 여부",
        description: "Does the applicant have any other valid passport?",
        getMany: false,
        inputLines: [
          {
            inputs: [
              {
                inputType: "radio",
                colSpan: 2,
                options: ["예", "아니오"],
              },
            ],
          },
        ],
      },
    ],
  },
  {
    name: "연락처",
    description: "Contact Information",
    fields: [
      {
        label: "본국주소",
        description: "Home Country Adress of the Applicant",
        getMany: false,
        inputLines: [
          {
            inputs: [
              {
                inputType: "text",
                colSpan: 9,
                inputDescription: "상세주소",
                englishDescription: "Street adrdess, APT, building, etc",
                maxLength: 50,
              },
              {
                inputType: "text",
                inputDescription: "시",
                englishDescription: "city",
                maxLength: 20,
                changeRow: true,
              },
              {
                inputType: "selector",
                inputDescription: "국가",
                englishDescription: "Country",
                options: nationList,
              },
            ],
          },
        ],
      },
      {
        label: "현 거주지",
        disableToggleDescription: "본국 주소와 동일해요",
        description: "Current Residential Adress / Please write the current address if different from above.",
        getMany: false,
        inputLines: [
          {
            inputs: [
              {
                inputType: "text",
                colSpan: 9,
                inputDescription: "상세주소",
                englishDescription: "Street adrdess, APT, building, etc",
                maxLength: 50,
              },
              {
                inputType: "text",
                inputDescription: "시",
                englishDescription: "city",
                changeRow: true,
                maxLength: 20,
              },
              {
                inputType: "selector",
                inputDescription: "국가",
                englishDescription: "Country",
                options: nationList,
              },
            ],
          },
        ],
      },
      {
        label: "휴대전화",
        description: "Cell Phone No.",
        getMany: false,
        inputLines: [
          {
            inputs: [
              {
                inputType: "text",
                validator: "phoneNumber",
                maxLength: 15,
              },
            ],
          },
        ],
      },
      {
        label: "일반전화",
        description: "Telephone No.",
        getMany: false,
        inputLines: [
          {
            inputs: [
              {
                inputType: "text",
                validator: "phoneNumber",
                maxLength: 15,
              },
            ],
          },
        ],
      },
      {
        label: "이메일",
        description: "E-mail",
        getMany: false,
        inputLines: [
          {
            inputs: [
              {
                inputType: "text",
                placeholder: "exampleEmail",
                validator: "emailOrEnglishNumber",
                maxLength: 30,
              },
              {
                inputType: "selector",
                placeholder: "@domain.com",
                options: ["@gmail.com", "@naver.com", "@hotmail.com", "직접 입력"],
              },
            ],
          },
        ],
      },
      {
        label: "비상시 연락처",
        description: "Emergency Contact Information",
        getMany: false,
        inputLines: [
          {
            inputs: [
              {
                inputType: "text",
                inputDescription: "성명",
                englishDescription: "Full name in English",
                validator: "koreanOrEnglish",
                placeholder: "이름을 입력해 주세요",
                maxLength: 20,
              },
              {
                inputType: "text",
                inputDescription: "관계",
                englishDescription: "Relationship to the applicant",
                validator: "koreanOrEnglish",
                placeholder: "지원자와 관계를 입력해 주세요",
                maxLength: 20,
              },
              {
                inputType: "selector",
                inputDescription: "거주국가",
                englishDescription: "Country of Residence",
                placeholder: "국가를 선택해주세요",
                options: nationList,
                changeRow: true,
              },
              {
                inputType: "text",
                validator: "phoneNumber",
                inputDescription: "전화번호",
                englishDescription: "Telephone No.",
                placeholder: "전화번호를 입력해주세요",
                maxLength: 15,
              },
            ],
          },
        ],
      },
    ],
  },
  {
    name: "혼인 및 가족사항",
    description: "Marital Status and Family Details",
    fields: [
      {
        label: "현재 혼인사항",
        description: "Current Martial Status",
        getMany: false,
        inputLines: [
          {
            inputs: [
              {
                inputType: "radio",
                options: ["기혼", "이혼", "미혼"],
                disableNextField: true,
              },
            ],
          },
        ],
      },
      {
        label: "배우자 인적사항",
        description: "Personal Information of applicant’s Spouse",
        getMany: false,
        inputLines: [
          {
            inputs: [
              {
                inputType: "text",
                inputDescription: "성",
                englishDescription: "Family name (in English)",
                validator: "koreanOrEnglish",
                maxLength: 20,
              },
              {
                inputType: "text",
                inputDescription: "명",
                englishDescription: "Given Names (in English)",
                validator: "koreanOrEnglish",
                maxLength: 20,
              },
              {
                inputType: "date",
                inputDescription: "생년월일",
                englishDescription: "Date of Birth",
                changeRow: true,
                onlyPast: true,
                birthDate: true,
              },
              {
                inputType: "selector",
                inputDescription: "국적",
                englishDescription: "Nationality",
                options: nationList,
                changeRow: true,
              },
              {
                inputType: "text",
                colSpan: 6,
                inputDescription: "거주지",
                englishDescription: "Residential Address",
                maxLength: 20,
              },
              {
                inputType: "text",
                validator: "phoneNumber",
                inputDescription: "연락처",
                englishDescription: "Contact No.",
                maxLength: 25,
                changeRow: true,
              },
            ],
          },
        ],
      },
      {
        label: "자녀 유무",
        description: "Does the applicant have children?",
        getMany: false,
        canInputBlocked: true,
        inputLines: [
          {
            inputs: [
              {
                inputType: "text",
                inputDescription: "자녀 수",
                englishDescription: "Number of Children",
                colSpan: 3,
                maxLength: 2,
                validator: "numbers",
                changeRow: true,
              },
            ],
          },
        ],
      },
    ],
  },
  {
    name: "학력",
    description: "Education",
    fields: [
      {
        label: "최종학력",
        description: "What is the highest degree or level of education the applicant has completed?",
        getMany: false,
        inputLines: [
          {
            inputs: [
              {
                inputType: "radio",
                colSpan: 4,
                options: ["석사/박사", "대졸", "고졸", "기타"],
              },
            ],
          },
        ],
      },
      {
        label: "학교명",
        description: "Name of School",
        getMany: false,
        inputLines: [
          {
            inputs: [
              {
                inputType: "text",
                placeholder: "학교명을 입력해 주세요",
                maxLength: 20,
              },
            ],
          },
        ],
      },
      {
        label: "학교 소재지",
        description: "Location of School (city/province/country)",
        getMany: false,
        inputLines: [
          {
            inputs: [
              {
                inputType: "text",
                inputDescription: "도시",
                englishDescription: "City",
                maxLength: 25,
              },
              {
                inputType: "selector",
                inputDescription: "국가",
                englishDescription: "Country",
                options: nationList,
              },
            ],
          },
        ],
      },
    ],
  },
  {
    name: "직업",
    description: "Employment",
    fields: [
      {
        label: "직업",
        description: "Current personal circumstances",
        getMany: false,
        inputLines: [
          {
            inputs: [
              {
                inputType: "radio",
                colSpan: 9,
                options: ["사업가", "자영업자", "직장인", "공무원", "학생", "퇴직자", "무직", "기타"],
              },
              {
                inputType: "text",
                inputDescription: "상세내용을 기재하세요",
                englishDescription: "Please provide details",
                colSpan: 6,
                maxLength: 18,
              },
            ],
          },
        ],
      },
      {
        label: "직업 상세정보",
        description: "Employment Details",
        getMany: false,
        inputLines: [
          {
            inputs: [
              {
                inputType: "text",
                inputDescription: "회사, 기관, 학교명",
                englishDescription: "Name of Company, Institute, School",
                maxLength: 30,
              },
              {
                inputType: "text",
                inputDescription: "직위, 과정",
                englishDescription: "Position, Course",
                maxLength: 15,
              },
              {
                inputType: "selector",
                inputDescription: "국가",
                englishDescription: "Country",
                options: nationList,
                changeRow: true,
              },
              {
                inputType: "text",
                colSpan: 6,
                inputDescription: "회사, 기관, 학교 주소",
                englishDescription: "Address of Company, Institute, School",
                maxLength: 30,
              },
              {
                inputType: "text",
                validator: "phoneNumber",
                inputDescription: "전화번호",
                englishDescription: "Telephone No.",
                changeRow: true,
                maxLength: 15,
              },
            ],
          },
        ],
      },
    ],
  },
  {
    name: "방문정보",
    description: "Details of Visit",
    fields: [
      {
        label: "입국목적",
        description: "Purpose of Visit to Korea",
        getMany: false,
        inputLines: [
          {
            inputs: [
              {
                inputType: "radio",
                colSpan: 9,
                options: [
                  "관광/통과",
                  "행사참석",
                  "의료관광",
                  "단기상용",
                  "유학/연수",
                  "취업활동",
                  "무역/투자/주재",
                  "가족/친지 방문",
                  "결혼이민",
                  "외교/공무",
                  "기타",
                ],
              },
              {
                inputType: "text",
                inputDescription: "상세내용을 기재하세요",
                englishDescription: "Please provide details",
                colSpan: 6,
                maxLength: 16,
              },
            ],
          },
        ],
      },
      {
        label: "체류예정기간",
        description: "Period of Stay",
        getMany: false,
        inputLines: [
          {
            inputs: [
              {
                inputType: "text",
                placeholder: "예: 30일",
                maxLength: 10,
              },
            ],
          },
        ],
      },
      {
        label: "입국예정일",
        description: "Intended Date of Entry",
        getMany: false,
        inputLines: [
          {
            inputs: [{ inputType: "date" }],
          },
        ],
      },
      {
        label: "체류예정지 (호텔포함)",
        description: "Address in Korea (Including hotels)",
        getMany: false,
        inputLines: [
          {
            inputs: [
              {
                inputType: "text",
                inputDescription: "도시",
                englishDescription: "City",
                maxLength: 15,
              },
              {
                inputType: "text",
                colSpan: 6,
                inputDescription: "상세주소",
                englishDescription: "Street address, APT, building, etc",
                maxLength: 30,
              },
            ],
          },
        ],
      },
      {
        label: "한국 내 연락처",
        description: "Contact No. in Korea",
        getMany: false,
        inputLines: [
          {
            inputs: [
              {
                inputType: "text",
                validator: "phoneNumber",
                placeholder: "전화번호를 입력해 주세요",
                maxLength: 15,
              },
            ],
          },
        ],
      },
      {
        label: "과거 5년간 한국을 방문한 경력",
        description: "Has the applicant travelled to Korea in the last 5 years?",
        getMany: false,
        canInputBlocked: true,
        inputLines: [
          {
            inputs: [
              {
                inputType: "selector",
                colSpan: 1,
                options: ["1", "2", "3", "4", "5", "5+"],
                placeholder: "횟수",
              },
              {
                inputType: "text",
                inputDescription: "최근 방문 목적",
                englishDescription: "Purpose of Recent Visit",
                placeholder: "방문 목적을 입력해주세요",
                maxLength: 18,
                colSpan: 6,
                changeRow: true,
              },
            ],
          },
        ],
      },
      {
        label: "한국 외에 과거 5년간 여행한 국가",
        description:
          "Has the applicant travelled outside his/her country of residence, excluding to Korea, in the last 5 years?",
        getMany: true,
        maxLine: 5,
        addButtonAtBelowLines: true,
        canInputBlocked: true,
        inputLines: [
          {
            inputs: [
              {
                inputType: "selector",
                inputDescription: "국가명",
                englishDescription: "Name of Country",
                options: nationList,
              },
              {
                inputType: "text",
                inputDescription: "방문 목적",
                colSpan: 5,
                englishDescription: "Purpose of Visit",
                maxLength: 15,
              },
              {
                inputType: "date",
                inputDescription: "방문 시작 기간",
                englishDescription: "Date of Beggining Staying",
                changeRow: true,
                onlyPast: true,
              },
              {
                inputType: "date",
                inputDescription: "방문 종료 기간",
                englishDescription: "Date of End Staying",
                onlyPast: true,
              },
            ],
          },
        ],
      },
      {
        label: "국내 체류 가족 유무",
        description: "Does the applicant have any family member(s) staying in Korea?",
        getMany: true,
        maxLine: 4,
        addButtonAtBelowLines: true,
        canInputBlocked: true,
        inputLines: [
          {
            inputs: [
              {
                inputType: "text",
                inputDescription: "성명",
                englishDescription: "Full name inEnglish",
                validator: "koreanOrEnglish",
                maxLength: 15,
              },
              {
                inputType: "date",
                inputDescription: "생년월일",
                englishDescription: "Date of Birth",
                onlyPast: true,
                birthDate: true,
              },
              {
                inputType: "selector",
                inputDescription: "국적",
                englishDescription: "Nationality",
                options: nationList,
                changeRow: true,
              },
              {
                inputType: "selector",
                colSpan: 1,
                inputDescription: "관계",
                englishDescription: "Relationship",
                options: ["부", "모", "형제", "자식", "조부모", "친척"],
              },
            ],
          },
        ],
      },
      {
        label: "동반입국 가족 유무",
        description: "Is the applicant traveling to Korea with any family member(s)?",
        getMany: true,
        maxLine: 5,
        addButtonAtBelowLines: true,
        canInputBlocked: true,
        inputLines: [
          {
            inputs: [
              {
                inputType: "text",
                inputDescription: "성명",
                englishDescription: "Full name inEnglish",
                validator: "koreanOrEnglish",
                maxLength: 15,
              },
              {
                inputType: "date",
                inputDescription: "생년월일",
                englishDescription: "Date of Birth",
                onlyPast: true,
                birthDate: true,
              },
              {
                inputType: "selector",
                inputDescription: "국적",
                englishDescription: "Nationality",
                options: nationList,
                changeRow: true,
              },
              {
                inputType: "selector",
                colSpan: 1,
                inputDescription: "관계",
                englishDescription: "Relationship",
                options: ["부", "모", "형제", "자식", "조부모", "친척"],
              },
            ],
          },
        ],
      },
    ],
  },
  {
    name: "서류 작성 시 도움 여부",
    description: "Assistance with this form",
    fields: [
      {
        label: "이 신청서를 작성하는데 다른 사람의 도움을 받았습니까?",
        description: "Did the applicant receive assistance in completing this form?",
        getMany: false,
        canInputBlocked: true,
        inputLines: [
          {
            inputs: [
              {
                inputType: "text",
                inputDescription: "성명",
                englishDescription: "Full name",
                validator: "koreanOrEnglish",
                changeRow: true,
                maxLength: 15,
              },
              {
                inputType: "selector",
                colSpan: 2,
                inputDescription: "관계",
                englishDescription: "Relationship",
                options: ["행정사", "가족", "친구/지인"],
              },
              {
                inputType: "text",
                validator: "phoneNumber",
                inputDescription: "연락처",
                englishDescription: "Phone No.",
                changeRow: true,
                maxLength: 13,
              },
              {
                inputType: "date",
                inputDescription: "생년월일",
                englishDescription: "Date of Birth",
                changeRow: true,
                onlyPast: true,
                birthDate: true,
              },
            ],
          },
        ],
      },
    ],
  },
  {
    name: "초청 정보",
    description: "Details of Invitation",
    fields: [
      {
        label: "초청인/초청회사",
        description: "Is there anyone inviting the applicant for the visa?",
        getMany: false,
        canInputBlocked: true,
        inputLines: [
          {
            inputs: [
              {
                inputType: "text",
                colSpan: 3,
                inputDescription: "초청인/초청회사명",
                englishDescription: "Name of inviting person/organization",
                changeRow: true,
                maxLength: 30,
              },
              {
                inputType: "text",
                colSpan: 4,
                inputDescription: "생년월일/사업자등록번호",
                validator: "phoneNumber",
                englishDescription: "Date of Birth / Business Registration No.",
                maxLength: 25,
              },
              {
                inputType: "text",
                colSpan: 2,
                inputDescription: "관계",
                englishDescription: "Relationship to applicant",
                validator: "koreanOrEnglish",
                maxLength: 12,
              },
              {
                inputType: "text",
                colSpan: 9,
                inputDescription: "상세주소",
                englishDescription: "Street address, APT, building, etc",
                changeRow: true,
                maxLength: 30,
              },
              {
                inputType: "text",
                validator: "phoneNumber",
                inputDescription: "연락처",
                englishDescription: "Phone No.",
                changeRow: true,
                maxLength: 15,
              },
            ],
          },
        ],
      },
    ],
  },
];
