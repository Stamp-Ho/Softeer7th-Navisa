import { FormProvider, useForm } from "react-hook-form";
import { useEffect, useMemo, useState } from "react";
import { useTranslation } from "react-i18next";
import NavisaForm from "../../components/form/NavisaForm";
import { languageList } from "../../constants/language";
import type { FormSection } from "../../types/formType";
import ForeignerOnboardWidget from "./ForeignerOnboardWidget";
import { useOnboardScroll } from "./hooks/useOnboardScroll";
import { nationList } from "../../constants/nations";
import { useForiengerProfileMutation } from "../../api/mutations/useForeignerProfileMutation";
import { useNavigate } from "react-router-dom";
import { getLeafValues } from "../../components/form/utils/formUtils";
import { DegreeLevelList } from "../../api/types/common";
import { alertT } from "../../i18n/alerts";
import type { TFunction } from "i18next";
import { useMyProfileQuery } from "../../api/queries/useMyProfileQuery";
import { useAuth } from "../../contexts/AuthContextProvider";

const getForeignerSections = (t: TFunction): FormSection[] => [
  {
    name: t("onboard.basicInfo"),
    fields: [
      {
        label: t("onboard.nationality"),
        description: "Nationality",
        getMany: true,
        maxLine: 3,
        addButtonAtFirstLine: true,
        inputLines: [
          {
            inputs: [
              {
                placeholder: t("onboard.foreigner.selectCountry"),
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
        label: t("onboard.language"),
        description: "Available Languages",
        getMany: true,
        maxLine: 5,
        addButtonAtFirstLine: true,
        inputLines: [
          {
            inputs: [
              {
                placeholder: t("onboard.foreigner.selectLanguage"),
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
        label: t("onboard.education"),
        description: "Education",
        getMany: false,
        inputLines: [
          {
            inputs: [
              {
                placeholder: "",
                inputType: "radio",
                options: [
                  t("onboard.foreigner.degreeBelowBachelor"),
                  t("onboard.foreigner.degreeBachelor"),
                  t("onboard.foreigner.degreeMaster"),
                ],
                isRequired: true,
                requestBodyName: "degreeLevel",
              },
              {
                placeholder: t("onboard.foreigner.schoolPlaceholder"),
                inputDescription: t("onboard.foreigner.schoolLabel"),
                englishDescription: "Name of School",
                inputType: "text",
                changeRow: true,
                isRequired: true,
                requestBodyName: "schoolName",
                validator: "koreanOrEnglish",
              },
              {
                placeholder: t("onboard.foreigner.majorPlaceholder"),
                inputDescription: t("onboard.foreigner.majorLabel"),
                englishDescription: "Name of Major",
                inputType: "text",
                isRequired: true,
                requestBodyName: "majorName",
                validator: "koreanOrEnglish",
              },
            ],
          },
        ],
      },
      {
        label: t("profile.career"),
        description: "Career",
        disableToggleDescription: t("onboard.foreigner.noCareer"),
        getMany: true,
        maxLine: 10,
        addButtonAtFirstLine: true,
        inputLines: [
          {
            inputs: [
              {
                inputDescription: t("onboard.foreigner.jobLabel"),
                englishDescription: "Name of Job",
                placeholder: t("onboard.foreigner.jobPlaceholder"),
                inputType: "text",
                isRequired: true,
                requestBodyName: "jobTitle",
                validator: "koreanOrEnglish",
              },
              {
                inputDescription: t("onboard.foreigner.companyLabel"),
                englishDescription: "Name of Corporate",
                placeholder: t("onboard.foreigner.companyPlaceholder"),
                inputType: "text",
                isRequired: true,
                requestBodyName: "companyName",
              },
              {
                inputDescription: t("onboard.foreigner.startDateLabel"),
                englishDescription: "Date of Start",
                inputType: "date",
                changeRow: true,
                isRequired: true,
                requestBodyName: "startDate",
                onlyPast: true,
              },
              {
                inputDescription: t("onboard.foreigner.endDateLabel"),
                englishDescription: "Date of Retirement",
                inputType: "date",
                changeRow: true,
                disableToggleDescription: t("onboard.foreigner.currentlyEmployed"),
                isRequired: true,
                requestBodyName: "endDate",
                onlyPast: true,
              },
            ],
          },
        ],
      },
    ],
  },
  {
    name: t("onboard.foreigner.expectedSectionName"),
    fields: [
      {
        label: t("onboard.foreigner.expectedJobLabel"),
        description: "Job to join",
        getMany: false,
        inputLines: [
          {
            inputs: [
              {
                placeholder: t("onboard.foreigner.expectedJobPlaceholder"),
                inputType: "text",
                isRequired: true,
                requestBodyName: "jobTitle",
                validator: "koreanOrEnglish",
              },
            ],
          },
        ],
      },
      {
        label: t("onboard.foreigner.expectedCompanyLabel"),
        description: "Company to join",
        getMany: false,
        inputLines: [
          {
            inputs: [
              {
                inputType: "text",
                inputDescription: t("onboard.foreigner.companyLabel"),
                englishDescription: "Name of Corporate",
                placeholder: t("onboard.foreigner.companyNamePlaceholder"),
                options: languageList,
                isRequired: true,
                requestBodyName: "companyName",
              },
              {
                inputType: "date",
                inputDescription: t("onboard.foreigner.expectedDateLabel"),
                englishDescription: "Scheduled date of Employment",
                isRequired: true,
                requestBodyName: "startDate",
                onlyFuture: true,
              },
            ],
          },
        ],
      },
    ],
  },
];

const ForeignerOnboard = () => {
  const { t } = useTranslation(["pages"]);
  const sections = useMemo(() => getForeignerSections(t), [t]);
  const { scrollRef, handleScroll, goToSection, goTop, currentSectionIndex, getMaskStyle } = useOnboardScroll();
  const methods = useForm();
  const navigate = useNavigate();
  const { userType } = useAuth();
  const { data, isLoading, isError } = useMyProfileQuery(userType);
  const updateProfileMutation = useForiengerProfileMutation(() => {
    navigate(!!data ? "/profile" : "/", { replace: true });
  });
  const [formLayout, setFormLayout] = useState(sections);

  const loadPrevData = () => {
    if (data && !isError) {
      const prevData = {
        0: {
          sectionData: {
            0: {
              values: data.nationIdList.map((id) => {
                //@ts-ignore
                const arr = [];
                //@ts-ignore
                arr["nationId"] = id - 1;
                //@ts-ignore
                return arr;
              }),
            },
            1: {
              values: data.languageIdList.map((id) => {
                //@ts-ignore
                const arr = [];
                //@ts-ignore
                arr["languageId"] = id - 1;
                //@ts-ignore
                return arr;
              }),
            },
            2: {
              values: [
                {
                  degreeLevel: DegreeLevelList.indexOf(data.education.degreeLevel),
                  schoolName: data.education.schoolName,
                  majorName: data.education.majorName,
                },
              ],
            },
            3: {
              disabled: data.foreignerCareers.length === 0,
              values: data.foreignerCareers.map((career) => ({
                jobTitle: career.jobTitle,
                companyName: career.companyName,
                startDate: career.startDate,
                endDate: career.endDate,
                endDatedisabled: career.isWork,
              })),
            },
          },
        },
        1: {
          sectionData: {
            0: {
              values: [
                {
                  jobTitle: data.expectedCompany.jobTitle,
                },
              ],
            },
            1: {
              values: [
                {
                  companyName: data.expectedCompany.companyName,
                  startDate: data.expectedCompany.startDate,
                },
              ],
            },
          },
        },
      };
      methods.reset(prevData);

      // inputLine(추가 입력)을 적용하여 초기 폼 구조에 line 추가
      const newStruct = structuredClone(sections);
      Object.values(prevData).forEach((section: any, sectionIndex) => {
        //@ts-ignore
        Object.values(section.sectionData).forEach((field: Record<string, any>, fieldIndex: number) => {
          const targetField = newStruct[sectionIndex].fields[fieldIndex];
          const tempField = field?.values ?? [];
          for (let i = 1; i < tempField.length; i++) {
            const newLine = {
              ...JSON.parse(JSON.stringify(targetField.inputLines[0])),
              rowId: i,
            };
            targetField.inputLines.push(newLine);
          }
        });
      });
      setFormLayout(newStruct);
    }
  };

  const [isGettingOffer, setIsGettingOffer] = useState(true);
  useEffect(() => {
    if (isLoading) return;
    loadPrevData();
  }, [data, isLoading, isError]);

  //@ts-ignore
  const onSubmit = (data) => {
    const param = {
      nationIdList: getLeafValues(data[0].sectionData[0]),
      languageIdList: getLeafValues(data[0].sectionData[1]),
      education: {
        degreeLevel: DegreeLevelList[data[0].sectionData[2].values[0].degreeLevel],
        schoolName: data[0].sectionData[2].values[0].schoolName,
        majorName: data[0].sectionData[2].values[0].majorName,
      },
      foreignerCareers: data[0].sectionData[3].disabled
        ? []
        : (data[0].sectionData[3].values as any[]).map((v) => ({
            companyName: v.companyName,
            jobTitle: v.jobTitle,
            startDate: v.startDate,
            endDate: v.endDate ?? null,
            isWork: !!v.endDatedisabled,
          })),
      expectedCompany: {
        jobTitle: data[1].sectionData[0].values[0].jobTitle,
        companyName: data[1].sectionData[1].values[0].companyName,
        startDate: data[1].sectionData[1].values[0].startDate,
      },
      isRequesting: isGettingOffer,
    };
    updateProfileMutation.mutate(param);
  };
  const onError = (errors: any) => {
    console.log("유효성 검사 실패:", errors);
    alertT("onboard.requiredFieldsError");
  };

  return (
    <FormProvider {...methods}>
      <form className="flex flex-row overflow-y-auto w-fit" onSubmit={methods.handleSubmit(onSubmit, onError)}>
        <div
          className={`w-284 overflow-auto scrollbar-hide ${getMaskStyle()}`}
          style={{ height: "calc(100vh - 100px)" }}
          ref={scrollRef}
          onScroll={handleScroll}
        >
          <div className="flex flex-col pb-10 pt-14">
            <h2 className="headline-m-bold text-text-base mb-3">
              {!!data ? t("onboard.editMyInfo") : t("onboard.foreignerTitle")}
            </h2>
            <a className="body-l-medium text-text-base">{t("onboard.description")}</a>
            <NavisaForm formData={formLayout} />
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
