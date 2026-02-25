import { useNavigate, useParams } from "react-router-dom";
import { useTranslation } from "react-i18next";
import NavisaForm from "../../components/form/NavisaForm";
import { IcDot } from "../../assets/icon/StratisUi";
import { useDocumentScroll } from "./hooks/useDocumentScroll";
import { editDocumentData } from "./constants";
import { FormProvider, useForm } from "react-hook-form";
import EditDocumentWidget from "./Component/EditDocumentWidget";
import { useEffect, useState } from "react";
import { useApplicationFormQuery } from "../../api/queries/useApplicationFormQuery";
import { useApplicationFormMutation } from "../../api/mutations/useApplicationFormMutation";
import { calculateOnlyInputs } from "../../components/form/utils/formUtils";
import { useUploadFormImage } from "../../api/fetchHooks/useUploadFormImage";
import { useForeignerMyFormQuery } from "../../api/queries/useForeignerMyFormQuery";
import { isUUID } from "../../utils/isUuid";
import { alertT } from "../../i18n/alerts";
import Tag from "../../components/common/Tag";

const EditDocument = () => {
  const { t } = useTranslation(["pages"]);
  const navigate = useNavigate();
  const { uploadFormImage } = useUploadFormImage();
  const { documentId } = useParams<{ documentId: string }>();

  const goBack = () => navigate("/", { replace: false });
  const isAgent = !!documentId;

  const agentQuery = useApplicationFormQuery(documentId ?? "", !!documentId);
  const foreignerQuery = useForeignerMyFormQuery(!documentId);

  const data = !!documentId ? agentQuery.data : foreignerQuery.data;
  const isLoading = agentQuery.isLoading || foreignerQuery.isLoading;

  const readOnly = !isAgent && data?.chatRoomId !== null;

  const postForm = useApplicationFormMutation(data?.applicationFormId ?? documentId ?? "", () => {
    alertT("components.form.savingSuccess");
  });
  const methods = useForm();

  const { scrollRef, handleScroll, currentSectionIndex, goToSection, goTop, getMaskStyle } = useDocumentScroll();

  const [loadedFromLocalStorage, setLoadedFromLocalStorage] = useState(false);
  const [imageFile, setImageFile] = useState<File | undefined>(undefined);
  const [imageUrl, setImageUrl] = useState("");
  const [formLayout, setFormLayout] = useState(editDocumentData);
  const [initializing, setInitializing] = useState(true);

  if (documentId && !isUUID(documentId)) {
    goBack();
    return null;
  }
  const compareLocalAndServerData = () => {
    //주소가 잘못되었거나 데이터를 받아오지 못한 경우, 뒤로가기
    if (!data) {
      goBack();
      return;
    }

    const serverHasData = !!data.sections?.[0]?.sectionData;
    // 서버 데이터와 로컬 데이터 중 최근 저장된 데이터로 폼 초기화
    let fresherData = data;
    const localRawData = window.localStorage.getItem(data.applicationFormId);
    if (!serverHasData) {
      //서버에 데이터가 없음
      if (localRawData && !readOnly) {
        try {
          // localStorage에 데이터가 있으면 그걸로 초기화
          fresherData = JSON.parse(localRawData);
          setLoadedFromLocalStorage(true);
        } catch {
          window.localStorage.removeItem(data.applicationFormId);
          setInitializing(false);
          return;
        }
      } else {
        //서버에도, 로컬에도 데이터가 없음 -> 최초 접속이거나 입력한 적이 없음
        setInitializing(false);
        setLoadedFromLocalStorage(false);
        return;
      }
    } else {
      // 서버에서 데이터를 불러왔음. 현재 fresherData 는 서버 데이터
      if (localRawData && !readOnly) {
        try {
          const localData = JSON.parse(localRawData);
          const localTime = new Date(localData.updatedAt).getTime();
          const serverTime = new Date(data.updatedAt).getTime();

          //fresherData = localData.updatedAt > data?.updatedAt ? localData : data;
          if (localTime > serverTime) {
            //로컬 데이터가 더 최신이면 로컬 데이터로 초기화
            fresherData = localData;
            setLoadedFromLocalStorage(true);
          } else {
            //서버 데이터가 더 최신이면 서버 데이터로 초기화
            fresherData = data;
            setLoadedFromLocalStorage(false);
          }
        } catch {
          // localStorage 데이터 손상 시 서버 데이터 사용
          window.localStorage.removeItem(data.applicationFormId);
          setLoadedFromLocalStorage(false);
        }
      }
    }

    // 이미지 URL이 있으면 이미지를 입력한것으로 처리
    //const dataToApply = structuredClone(Object.values(fresherData.sections));
    let dataToApply = { ...fresherData.sections };
    if (data.foreignerProfileImgUrl !== null) dataToApply[0].sectionData[0].values = [true];

    // inputLine(추가 입력)을 적용하여 초기 폼 구조에 line 추가
    const newStruct = structuredClone(editDocumentData);
    Object.values(dataToApply)
      .slice(0, 9)
      .forEach((section, sectionIndex) => {
        //@ts-ignore
        section.sectionData.forEach((field: Record<string, any>, fieldIndex: number) => {
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
    setImageUrl(data.foreignerProfileImgUrl || "");
    methods.reset(dataToApply);
    setInitializing(false);
  };

  useEffect(() => {
    if (isLoading) return;
    compareLocalAndServerData();
  }, [isLoading]);

  // 폼 입력 값 저장 함수
  const onSubmit = (formData: Record<number, any>) => {
    if (readOnly) {
      alertT("documents.noEditPermission");
      return;
    }
    const { totalCount, filledCount } = calculateOnlyInputs(formData);

    if (imageFile !== undefined && data?.applicationFormId) {
      uploadFormImage(data?.applicationFormId ?? documentId, imageFile);
    }
    const formValues = Object.values(formData).slice(0, 9);
    const params = {
      totalCount,
      filledCount,
      sections: formValues.map((section, i) => ({
        sectionId: i + 1,
        sectionData: section.sectionData.map((field: Record<string, any>) => ({
          ...field,
          values: field.values && Object.values(field.values),
        })),
      })),
    };

    postForm.mutate(params);
    setLoadedFromLocalStorage(false);
    setTimeout(() => {
      window.localStorage.removeItem(data?.applicationFormId ?? documentId ?? "");
    }, 100);
  };
  const onError = () => {
    alertT("pages.documents.requiredFieldsError");
  };
  if (isLoading || initializing) return <>loading...</>;
  if (!data) return <>{t("documents.errorOccurred")}</>;
  const informationMessage = [
    t("documents.information1"),
    t("documents.information2"),
    t("documents.information3"),
    t("documents.information4"),
  ];
  const informationMessageWhenDone = [
    t("documents.information1"),
    t("documents.information2"),
    t("documents.information4"),
    t("documents.information5"),
  ];
  const informationWhenReadOnly = [
    t("documents.readOnlyInformation1"),
    t("documents.readOnlyInformation2"),
    t("documents.readOnlyInformation3"),
  ];
  return (
    <FormProvider {...methods}>
      <form className="flex flex-row overflow-y-auto w-fit" onSubmit={methods.handleSubmit(onSubmit, onError)}>
        <div
          ref={scrollRef}
          onScroll={handleScroll}
          className={`w-284 overflow-auto scrollbar-hide ${getMaskStyle()}`}
          style={{ height: "calc(100vh - 100px)" }}
        >
          <div className="flex flex-col pb-15 pt-12">
            <h2 className="headline-m-bold text-text-base mb-3 flex flex-row items-center gap-5">
              {t("documents.documentWrite")}
              {data.isDone ? t("documents.completedSuffix") : ""}
              {loadedFromLocalStorage && (
                <Tag variant="large_violet_off" className="animate-pulse">
                  {t("documents.temporarySave")}
                </Tag>
              )}
            </h2>
            <a className="body-l-medium text-text-base mb-5">{t("documents.description")}</a>
            <ul className="flex flex-col bg-green-bright body-l-medium text-green-vivid gap-1.5 rounded-[20px] py-7 px-5.25">
              {(readOnly ? informationWhenReadOnly : data.isDone ? informationMessageWhenDone : informationMessage).map(
                (text, idx) => (
                  <li className="flex flex-row items-center gap-0.5" key={`inform_${idx}`}>
                    <IcDot size={16} color="var(--green-vivid)" />
                    {text}
                  </li>
                ),
              )}
            </ul>
            <NavisaForm
              formData={formLayout}
              addIndex={true}
              startsWithImage={true}
              imageUrl={imageUrl}
              imageFile={imageFile}
              setImageFile={setImageFile}
              readOnly={readOnly}
            />
          </div>
        </div>
        <EditDocumentWidget
          editDocumentData={editDocumentData}
          currentSectionIndex={currentSectionIndex}
          isAgent={!!documentId}
          isDone={data.isDone}
          imageUrl={imageUrl}
          goToSection={goToSection}
          goTop={goTop}
          documentId={data?.applicationFormId ?? documentId ?? ""}
          chatRoomId={data.chatRoomId}
          readOnly={readOnly}
          loadedFromLocalStorage={loadedFromLocalStorage}
          setLoadedFromLocalStorage={setLoadedFromLocalStorage}
        />
      </form>
    </FormProvider>
  );
};
export default EditDocument;

// const informationMessage = [
//   "본 서비스는 신청서 작성 편의를 위한 보조 수단에 불과하며, 신청서에 기재된 내용의 정확성 및 법적 책임은 전적으로 작성자에게 있습니다.",
//   "신청서의 모든 질문에 대한 답변은 한글 또는 영문으로 작성해야 하며, 누락된 항목은 신청인이 자필로 작성해야 합니다.",
//   "행정사가 ‘내보내기’를 완료한 즉시 비자가 신청된 것으로 간주하며, 2주 후 의뢰의 완료 여부를 문의합니다.",
//   "화면에 보이지 않는 선택지는 내보내기 후 자필로 입력하시길 바랍니다.",
// ];

// const informationMessageWhenDone = [
//   "본 서비스는 신청서 작성 편의를 위한 보조 수단에 불과하며, 신청서에 기재된 내용의 정확성 및 법적 책임은 전적으로 작성자에게 있습니다.",
//   "신청서의 모든 질문에 대한 답변은 한글 또는 영문으로 작성해야 하며, 누락된 항목은 신청인이 자필로 작성해야 합니다.",
//   "화면에 보이지 않는 선택지는 다운로드 후 자필로 입력하시길 바랍니다.",
//   "이미 [내보내기] 완료된 신청서입니다. 수정 및 미리보기, 다운로드가 가능하지만 의뢰 완료 여부 문의 시점은 [내보내기] 2주 후 입니다.",
// ];

// const informationWhenReadOnly = [
//   "본 신청서는 현재 수정이 불가능한 상태입니다.",
//   "행정사에게 문의하여 신청서를 수정하시기 바랍니다.",
//   "수정 및 저장은 불가능하지만, 미리보기와 다운로드는 가능합니다.",
// ];
