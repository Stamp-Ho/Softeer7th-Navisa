import { FormProvider, useForm } from "react-hook-form";
import { useMemo, useState } from "react";
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
                validator: "koreanOrEnglish",
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
                validator: "koreanOrEnglish",
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
  const updateProfileMutation = useForiengerProfileMutation(() => {
    navigate("/", { replace: true });
  });
  const [isGettingOffer, setIsGettingOffer] = useState(true);
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
            <h2 className="headline-m-bold text-text-base mb-3">{t("onboard.foreignerTitle")}</h2>
            <a className="body-l-medium text-text-base">{t("onboard.description")}</a>
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
