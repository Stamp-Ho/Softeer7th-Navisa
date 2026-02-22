import { FormProvider, useForm } from "react-hook-form";
import { useMemo, useState } from "react";
import { useTranslation } from "react-i18next";
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
import { alertT } from "../../i18n/alerts";
import type { TFunction } from "i18next";

const getAgentSections = (t: TFunction): FormSection[] => [
  {
    name: t("onboard.basicInfo"),
    fields: [
      {
        label: t("onboard.agent.photoLabel"),
        getMany: false,
        inputLines: [
          {
            inputs: [
              {
                inputType: "image",
                colSpan: 9,
                placeholder: t("onboard.agent.profilePlaceholder"),
                isRequired: true,
                requestBodyName: "profileImageUrl",
              },
            ],
          },
        ],
      },
      {
        label: t("onboard.agent.nameLabel"),
        getMany: false,
        inputLines: [
          {
            inputs: [
              {
                placeholder: t("onboard.agent.namePlaceholder"),
                inputType: "text",
                isRequired: true,
                requestBodyName: "agentName",
              },
            ],
          },
        ],
      },
      {
        label: t("onboard.agent.birthLabel"),
        getMany: false,
        inputLines: [{ inputs: [{ inputType: "date", isRequired: true, requestBodyName: "birthDate" }] }],
      },
      {
        label: t("onboard.agent.phoneLabel"),
        getMany: false,
        inputLines: [
          {
            inputs: [
              { inputType: "text", placeholder: "010-1234-5678", isRequired: true, requestBodyName: "phoneNumber" },
            ],
          },
        ],
      },
      {
        label: t("onboard.agent.officeLabel"),
        description: t("onboard.agent.officeSearchHint"),
        getMany: false,
        inputLines: [
          {
            inputs: [
              {
                inputType: "text",
                inputDescription: t("onboard.agent.officeNameLabel"),
                placeholder: t("onboard.agent.officeNamePlaceholder"),
                isRequired: true,
                requestBodyName: "officeName",
              },
              {
                inputType: "timeRange",
                inputDescription: t("onboard.agent.businessHoursLabel"),
                placeholder: "",
                isRequired: true,
                requestBodyName: "businessTime",
              },
              {
                inputType: "text",
                inputDescription: t("onboard.agent.addressLabel"),
                placeholder: t("onboard.agent.addressPlaceholder"),
                changeRow: true,
                isRequired: true,
                requestBodyName: "officeAddress",
              },
              {
                inputType: "text",
                inputDescription: t("onboard.agent.detailAddressLabel"),
                placeholder: t("onboard.agent.detailAddressPlaceholder"),
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
    name: t("onboard.agent.certSectionName"),
    fields: [
      {
        label: t("onboard.agent.certTypeLabel"),
        getMany: false,
        inputLines: [
          {
            inputs: [
              {
                placeholder: t("onboard.agent.certTypePlaceholder"),
                inputType: "selector",
                options: [
                  t("onboard.agent.certTypeBook"),
                  t("onboard.agent.certTypePlate"),
                  t("onboard.agent.certTypeMobile"),
                ],
                isRequired: true,
                requestBodyName: "licenseType",
                disableTargets: { true: [1, 2, 3], false: [4] },
              },
            ],
          },
        ],
      },
      {
        label: t("onboard.agent.certNumberLabel"),
        description: t("onboard.agent.certNumberDesc"),
        getMany: false,
        inputLines: [
          {
            inputs: [
              {
                placeholder: t("onboard.agent.certNumberPlaceholder"),
                inputType: "text",
                isRequired: true,
                requestBodyName: "licenseNo",
              },
            ],
          },
        ],
      },
      {
        label: t("onboard.agent.certDateLabel"),
        description: t("onboard.agent.certDateDesc"),
        getMany: false,
        inputLines: [{ inputs: [{ inputType: "date", isRequired: true, requestBodyName: "licenseIssuedAt" }] }],
      },
      {
        label: t("onboard.agent.certPageLabel"),
        getMany: false,
        inputLines: [
          {
            inputs: [
              {
                inputType: "text",
                placeholder: t("onboard.agent.certPagePlaceholder"),
                isRequired: true,
                requestBodyName: "licenseInnerPageNo",
              },
            ],
          },
        ],
      },
      {
        label: t("onboard.agent.certManageLabel"),
        description: t("onboard.agent.certManageDesc"),
        getMany: false,
        inputLines: [
          {
            inputs: [
              {
                inputType: "text",
                placeholder: t("onboard.agent.certManagePlaceholder"),
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
    name: t("onboard.agent.detailSectionName"),
    fields: [
      {
        label: t("onboard.agent.specialtyLabel"),
        description: t("onboard.agent.specialtyDesc"),
        getMany: true,
        inputLines: [
          {
            inputs: [
              {
                placeholder: t("onboard.agent.specialtyPlaceholder"),
                inputType: "selector",
                options: jobCodeList.map((j, i) => `${j} (${jobCodeList_Codes[i]})`),
                isRequired: true,
                requestBodyName: "specializedJobCodeIdList",
              },
            ],
          },
        ],
      },
      {
        label: t("onboard.agent.languageLabel"),
        description: t("onboard.agent.languageDesc"),
        getMany: true,
        inputLines: [
          {
            inputs: [
              {
                placeholder: t("onboard.agent.languagePlaceholder"),
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
        label: t("onboard.agent.oneLinerLabel"),
        description: t("onboard.agent.oneLinerDesc"),
        getMany: false,
        inputLines: [
          {
            inputs: [
              {
                inputType: "text",
                colSpan: 9,
                placeholder: t("onboard.agent.oneLinerPlaceholder"),
                isRequired: true,
                requestBodyName: "agentComment",
              },
            ],
          },
        ],
      },
      {
        label: t("onboard.agent.additionalHistoryLabel"),
        isOptional: true,
        description: t("onboard.agent.additionalHistoryDesc"),
        getMany: false,
        inputLines: [{ inputs: [{ inputType: "textArea", colSpan: 9, requestBodyName: "additionalHistory" }] }],
      },
    ],
  },
];

const AgentOnboard = () => {
  const { t } = useTranslation(["pages"]);
  const sections = useMemo(() => getAgentSections(t), [t]);
  const { scrollRef, handleScroll, goToSection, goTop, currentSectionIndex, getMaskStyle } = useOnboardScroll();
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
      alertT("onboard.profileImageError");
      return;
    }
    const fileMimeType = imageFile.type;

    const imgObjectKey = await uploadImage(
      fileMimeType as "image/jpg" | "image/jpeg" | "image/png",
      "agent-profile",
      imageFile,
    );

    if (!imgObjectKey) {
      alertT("onboard.savingFailed");
      return;
    }

    const param = {
      basicInfo: {
        profileImageUrl: imgObjectKey,
        agentName: data[0].sectionData[0].values[0].agentName,
        birthDate: data[0].sectionData[1].values[0].birthDate, // date string
        phoneNumber: data[0].sectionData[2].values[0].phoneNumber,
        officeName: data[0].sectionData[3].values[0].officeName,
        officeAddress: data[0].sectionData[3].values[0].officeAddress,
        officeAddressDetail: data[0].sectionData[3].values[0].officeAddressDetail,
        businessTime: data[0].sectionData[3].values[0].businessTime,
      },
      licenseInfo: {
        licenseNo: data[1].sectionData[1].disabled ? null : data[1].sectionData[1].values[0].licenseNo,
        licenseIssuedAt: data[1].sectionData[2].disabled ? null : data[1].sectionData[2].values[0].licenseIssuedAt,
        licenseInnerPageNo: data[1].sectionData[3].disabled
          ? null
          : data[1].sectionData[3].values[0].licenseInnerPageNo,
        licenseManagementNo: data[1].sectionData[4].disabled
          ? null
          : data[1].sectionData[4].values[0].licenseManagementNo,
      },
      detailedInfo: {
        specializedJobCodeIdList: getLeafValues(data[2].sectionData[0]), //number[]
        availableLanguageIdList: getLeafValues(data[2].sectionData[1]), //number[]
        agentComment: data[2].sectionData[2].values[0].agentComment,
        additionalHistory: data[2].sectionData[3].values[0].additionalHistory,
      },
    };
    updateProfileMutation.mutate(param);
    alertT("onboard.registrationAttempt");
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
            <h2 className="headline-m-bold text-text-base mb-3">{t("onboard.agentTitle")}</h2>
            <a className="body-l-medium text-text-base">{t("onboard.description")}</a>
            <NavisaForm formData={sections} startsWithImage={true} imageFile={imageFile} setImageFile={setImageFile} />
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
