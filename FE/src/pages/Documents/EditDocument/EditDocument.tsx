import { useNavigate, useParams } from "react-router-dom";
import NavisaForm from "../../../components/form/NavisaForm";
import { IcDot } from "../../../assets/icon/StratisUi";
import { useDocumentScroll } from "./hooks/useDocumentScroll";
import { editDocumentData } from "./constants";
import { FormProvider, useForm } from "react-hook-form";
import EditDocumentWidget from "./EditDocumentWidget";
import { useEffect, useState } from "react";
import { useApplicationFormQuery } from "../../../api/queries/useApplicationFormQuery";
import { useApplicationFormMutation } from "../../../api/mutations/useApplicationFormMutation";
import { calculateOnlyInputs } from "../../../components/form/utils/formUtils";
import { useUploadFormImage } from "../../../api/fetchHooks/useUploadFormImage";
import { useForeignerMyFormQuery } from "../../../api/queries/useForeignerMyFormQuery";

const EditDocument = () => {
  const navigate = useNavigate();
  const { uploadFormImage } = useUploadFormImage();
  const { documentId } = useParams<{ documentId: string }>();

  const agentQuery = useApplicationFormQuery(documentId ?? "", !!documentId);
  const foreignerQuery = useForeignerMyFormQuery(!documentId);

  const data = documentId ? agentQuery.data : foreignerQuery.data;
  const isLoading = agentQuery.isLoading || foreignerQuery.isLoading;
  const isError = agentQuery.isError || foreignerQuery.isError;

  const postForm = useApplicationFormMutation(
    data?.applicationFormId ?? documentId ?? "",
    () => {
      alert("저장되었습니다.");
    },
  );
  const methods = useForm();

  const {
    scrollRef,
    handleScroll,
    currentSectionIndex,
    goToSection,
    goTop,
    getMaskStyle,
  } = useDocumentScroll();

  const [imageFile, setImageFile] = useState<File | undefined>(undefined);
  const [imageUrl, setImageUrl] = useState("");
  const [formLayout, setFormLayout] = useState(editDocumentData);
  const [initializing, setInitializing] = useState(true);

  const goBack = () => {
    navigate("/", { replace: false });
  };

  const compareLocalAndServerData = () => {
    //주소가 잘못되었거나 데이터를 받아오지 못한 경우, 뒤로가기
    if (isError || !data) {
      goBack();
      return;
    }
    // 서버 데이터와 로컬 데이터 중 최근 저장된 데이터로 폼 초기화
    let fresherData = data;
    const localRawData = window.localStorage.getItem(data.applicationFormId);
    if (localRawData && !data.isDone) {
      try {
        const localData = JSON.parse(localRawData);
        const localTime = new Date(localData.updatedAt).getTime();
        const serverTime = new Date(data.updatedAt).getTime();

        //fresherData = localData.updatedAt > data?.updatedAt ? localData : data;
        fresherData = localTime > serverTime ? localData : data;
      } catch {
        // localStorage 데이터 손상 시 서버 데이터 사용
        window.localStorage.removeItem(data.applicationFormId);
      }
    }
    console.log(fresherData.sections);

    // 이미지 URL이 있으면 이미지를 입력한것으로 처리
    //const dataToApply = structuredClone(Object.values(fresherData.sections));
    const dataToApply = { ...fresherData.sections };
    if (data.foreignerProfileImgUrl !== null)
      dataToApply[0].sectionData[0].values = [true];

    // inputLine(추가 입력)을 적용하여 초기 폼 구조에 line 추가
    const newStruct = structuredClone(editDocumentData);
    Object.values(dataToApply).forEach((section, sectionIndex) => {
      //@ts-ignore
      section.sectionData.forEach(
        (field: Record<string, any>, fieldIndex: number) => {
          const targetField = newStruct[sectionIndex].fields[fieldIndex];
          const tempField = field?.values ?? [];
          for (let i = 1; i < tempField.length; i++) {
            const newLine = {
              ...JSON.parse(JSON.stringify(targetField.inputLines[0])),
              rowId: i,
            };
            console.log(
              `${sectionIndex}섹션 ${fieldIndex}에 ${i}번째 inputline추가!`,
            );
            targetField.inputLines.push(newLine);
          }
        },
      );
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

  const onSubmit = (formData: Record<number, any>) => {
    const { totalCount, filledCount } = calculateOnlyInputs(formData);

    if (imageFile !== undefined && data?.applicationFormId) {
      uploadFormImage(data?.applicationFormId ?? documentId, imageFile);
    }
    const formValues = Object.values(formData);
    const params = {
      totalCount,
      filledCount,
      sections: formValues.map((section, i) => ({
        sectionId: i + 1,
        sectionData: section.sectionData.map((field: Record<string, any>) => ({
          ...field,
          values: Object.values(field.values),
        })),
      })),
    };
    console.log(params);
    postForm.mutate(params);
  };
  const onError = (errors: any) => {
    console.log("유효성 검사 실패:", errors);
    alert("필수 입력 항목을 모두 채워주세요.");
  };
  if (isLoading || initializing) return <>loading...</>;
  if (!data || isError) return <>오류가 발생했습니다</>;
  return (
    <FormProvider {...methods}>
      <form
        className="flex flex-row overflow-y-auto w-fit"
        onSubmit={methods.handleSubmit(onSubmit, onError)}
      >
        <div
          ref={scrollRef}
          onScroll={handleScroll}
          className={`w-284 overflow-auto scrollbar-hide ${getMaskStyle()}`}
          style={{ height: "calc(100vh - 100px)" }}
        >
          <div className="flex flex-col pb-15 pt-12">
            <h2 className="headline-m-bold text-text-base mb-3">
              비자 신청서 작성 (사증발급인정신청서)
            </h2>
            <a className="body-l-medium text-text-base mb-5">
              서류에 자필로 작성하는 대신, 직접 빈칸에 입력하여 편하게
              사증발급인정신청서를 작성해보세요
            </a>
            <ul className="flex flex-col bg-green-bright body-l-medium text-green-vivid gap-1.5 rounded-[20px] py-7 px-5.25">
              {informationMessage.map((text, idx) => (
                <li
                  className="flex flex-row items-center gap-0.5"
                  key={`inform_${idx}`}
                >
                  <IcDot size={16} color="var(--green-vivid)" />
                  {text}
                </li>
              ))}
            </ul>
            <NavisaForm
              formData={formLayout}
              addIndex={true}
              startsWithImage={true}
              imageUrl={imageUrl}
              imageFile={imageFile}
              setImageFile={setImageFile}
            />
          </div>
        </div>
        <EditDocumentWidget
          editDocumentData={editDocumentData}
          currentSectionIndex={currentSectionIndex}
          goToSection={goToSection}
          goTop={goTop}
          documentId={data?.applicationFormId ?? documentId ?? ""}
        />
      </form>
    </FormProvider>
  );
};
export default EditDocument;

const informationMessage = [
  "본 서비스는 신청서 작성 편의를 위한 보조 수단에 불과하며, 신청서에 기재된 내용의 정확성 및 법적 책임은 전적으로 작성자에게 있습니다.",
  "신청서의 모든 질문에 대한 답변은 한글 또는 영문으로 작성해야 하며, 누락된 항목은 신청인이 자필로 작성해야 합니다.",
  "행정사가 ‘내보내기’를 완료한 즉시 비자가 신청된 것으로 간주하며, 2주 후 의뢰의 완료 여부를 문의합니다.",
  "화면에 보이지 않는 선택지는 내보내기 후 자필로 입력하시길 바랍니다.",
];
