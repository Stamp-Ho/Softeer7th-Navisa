import { FormProvider, useForm } from "react-hook-form";
import { useEffect, useMemo, useState } from "react";
import { useTranslation } from "react-i18next";
import NavisaForm from "../../components/form/NavisaForm";
import { languageList } from "../../constants/language";
import type { FormSection } from "../../types/formType";
import { useNavigate } from "react-router-dom";
import { getLeafValues } from "../../components/form/utils/formUtils";
import { jobCodeList, jobCodeList_Codes } from "../../constants/job";
import { useUploadImage } from "../../api/fetchHooks/useUploadImage";
import { alertT } from "../../i18n/alerts";
import type { TFunction } from "i18next";
import AgentOnboardWidget from "../Onboard/AgentOnboardWidget";
import { useOnboardScroll } from "../Onboard/hooks/useOnboardScroll";
import { useAgentProfileUpdateMutation } from "../../api/mutations/useAgentProfileUpdateMutation";
import { useMyProfileAgentQuery } from "../../api/queries/useMyProfileAgentQuery";

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
                requestBodyName: "profileImageUrl",
              },
            ],
          },
        ],
      },
      {
        label: t("onboard.agent.phoneLabel"),
        getMany: false,
        inputLines: [
          {
            inputs: [
              {
                inputType: "text",
                placeholder: "010-1234-5678",
                isRequired: true,
                requestBodyName: "phoneNumber",
                validator: "phoneNumber",
              },
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

const EditAgentProfile = () => {
  const { t } = useTranslation(["pages"]);
  const sections = useMemo(() => getAgentSections(t), [t]);
  const { scrollRef, handleScroll, goToSection, goTop, currentSectionIndex, getMaskStyle } = useOnboardScroll();
  const methods = useForm();
  const navigate = useNavigate();
  const { uploadImage } = useUploadImage();
  const { data, isLoading, isError } = useMyProfileAgentQuery("VALID_AGENT");
  const updateProfileMutation = useAgentProfileUpdateMutation(() => {
    navigate("/profile", { replace: true });
  });
  const [formLayout, setFormLayout] = useState(sections);

  const [imageFile, setImageFile] = useState<File | undefined>(undefined);
  const [imageUrl, setImageUrl] = useState<string | undefined>(undefined);
  const loadPrevData = () => {
    if (data && !isError) {
      const { header, agentInfo, expertise, additionalHistory, officeInfo } = data;
      setImageUrl(agentInfo.profileImageUrl);
      methods.setValue("0.sectionData.0.values.0.profileImageUrl", true);
      methods.setValue("0.sectionData.1.values.0.phoneNumber", officeInfo.phoneNumber);
      methods.setValue("0.sectionData.2.values.0.officeName", officeInfo.officeName);
      methods.setValue("0.sectionData.2.values.0.officeAddress", officeInfo.address);
      methods.setValue("0.sectionData.2.values.0.officeAddressDetail", officeInfo.officeAddressDetail);
      methods.setValue(
        "0.sectionData.2.values.0.businessTime",
        officeInfo.businessHours.replace(/^\D+/, "").replace(/\s*~\s*/g, " ~ "),
      );

      methods.setValue(
        "1.sectionData.0.values",
        expertise.jobCodeIds.map((id) => ({ specializedJobCodeIdList: id - 1 })),
      );
      methods.setValue(
        "1.sectionData.1.values",
        expertise.languageIds.map((id) => ({ availableLanguageIdList: id })),
      );
      methods.setValue("1.sectionData.2.values.0.agentComment", header.comment);
      methods.setValue("1.sectionData.3.values.0.additionalHistory", additionalHistory);

      const newStruct = structuredClone(sections);

      // 전문 분야 값 개수만큼 input line 추가
      const jobCodeField = newStruct[1].fields[0]; // 전문 분야 필드
      Array.from({ length: expertise.jobCodeIds.length - 1 }).forEach((_, i) => {
        const newLine = { ...JSON.parse(JSON.stringify(jobCodeField.inputLines[0])), rowId: i };
        jobCodeField.inputLines.push(newLine);
      });

      // 언어 값 개수만큼 input line 추가
      const languageField = newStruct[1].fields[1]; // 언어 필드
      Array.from({ length: expertise.languageIds.length - 1 }).forEach((_, i) => {
        const newLine = { ...JSON.parse(JSON.stringify(languageField.inputLines[0])), rowId: i };
        languageField.inputLines.push(newLine);
      });
      setFormLayout(newStruct);
    }
  };
  useEffect(() => {
    if (isLoading) return;
    loadPrevData();
  }, [data, isLoading, isError]);

  //@ts-ignore
  const onSubmit = async (data) => {
    let imgObjectKey = null;
    if (imageFile) {
      const fileMimeType = imageFile.type;
      imgObjectKey = await uploadImage(
        fileMimeType as "image/jpg" | "image/jpeg" | "image/png",
        "agent-profile",
        imageFile,
      );
    }

    const param = {
      profileObjectKey: imgObjectKey || null,
      phoneNumber: data[0].sectionData[1].values[0].phoneNumber,
      officeName: data[0].sectionData[2].values[0].officeName,
      roadAddress: data[0].sectionData[2].values[0].officeAddress,
      officeAddressDetail: data[0].sectionData[2].values[0].officeAddressDetail,
      businessHours: data[0].sectionData[2].values[0].businessTime,
      specializedJobCodeIdList: getLeafValues(data[1].sectionData[0]), //number[]
      availableLanguageIdList: getLeafValues(data[1].sectionData[1]), //number[]
      introduction: data[1].sectionData[2].values[0].agentComment,
      additionalCareer: data[1].sectionData[3].values[0].additionalHistory,
    };
    updateProfileMutation.mutate(param);
    alertT("onboard.registrationAttempt");
  };
  const onError = (errors: any) => {
    console.error("유효성 검사 실패:", errors);
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
            <h2 className="headline-m-bold text-text-base mb-3">{t("onboard.editMyInfo2")}</h2>
            <a className="body-l-medium text-text-base">{t("onboard.editNotice")}</a>
            <NavisaForm
              formData={formLayout}
              startsWithImage={true}
              imageUrl={imageUrl}
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
          isEditing={true}
        />
      </form>
    </FormProvider>
  );
};

export default EditAgentProfile;
