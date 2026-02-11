import { useEffect, useState } from "react";
import Tag from "../../components/common/Tag";
import DocumentCard from "../../components/shared/DocumentCard";
import GrayBackground from "../../components/shared/GrayBackground";
import type { RecentVisaFormsResponse } from "../../api/types/etc";

const data: RecentVisaFormsResponse[] = [
  {
    applicationFormId: "abc",
    title: "주디",
    isDone: false,
    currentStep: 24,
    foreignerProfileImgUrl: "https://placehold.co/76x106",
    lastModifiedAt: "2025. 06. 21",
  },
  {
    applicationFormId: "abc",
    title: "주디",
    isDone: false,
    currentStep: 24,
    foreignerProfileImgUrl: "https://placehold.co/76x106",
    lastModifiedAt: "2025. 06. 21",
  },
  {
    applicationFormId: "abc",
    title: "주디",
    isDone: false,
    currentStep: 24,
    foreignerProfileImgUrl: "https://placehold.co/76x106",
    lastModifiedAt: "2025. 06. 21",
  },
  {
    applicationFormId: "abc",
    title: "주디",
    isDone: false,
    currentStep: 24,
    foreignerProfileImgUrl: "https://placehold.co/76x106",
    lastModifiedAt: "2025. 06. 21",
  },
  {
    applicationFormId: "abc",
    title: "주디",
    isDone: false,
    currentStep: 24,
    foreignerProfileImgUrl: "https://placehold.co/76x106",
    lastModifiedAt: "2025. 06. 21",
  },
  {
    applicationFormId: "abc",
    title: "주디",
    isDone: false,
    currentStep: 24,
    foreignerProfileImgUrl: "https://placehold.co/76x106",
    lastModifiedAt: "2025. 06. 21",
  },
];
const Documents = () => {
  const [statusTab, setStatusTab] = useState<number>(0);
  const [documentToRender, setDocumentToRender] =
    useState<RecentVisaFormsResponse[]>(data);
  const editingDocument = data.filter((document) => !document.isDone);
  const doneDocument = data.filter((document) => document.isDone);
  useEffect(() => {
    if (statusTab === 0) setDocumentToRender(data);
    if (statusTab === 1) setDocumentToRender(editingDocument);
    if (statusTab === 2) setDocumentToRender(doneDocument);
  }, [statusTab]);
  return (
    <>
      <GrayBackground />
      <div className="flex flex-col gap-8">
        <h2 className="headline-m-bold mt-9">문서함</h2>
        <div className="flex flex-row gap-3 w-full">
          <div onClick={() => setStatusTab(0)}>
            <Tag
              type={statusTab === 0 ? "large_violet_off" : "large_white_off"}
              className="w-27 cursor-pointer"
            >
              전체 {statusTab === 0 && data.length}
            </Tag>
          </div>
          <div onClick={() => setStatusTab(1)}>
            <Tag
              type={statusTab === 1 ? "large_violet_off" : "large_white_off"}
              className="w-27 cursor-pointer"
            >
              작성중 {statusTab === 1 && editingDocument.length}
            </Tag>
          </div>
          <div onClick={() => setStatusTab(2)}>
            <Tag
              type={statusTab === 2 ? "large_violet_off" : "large_white_off"}
              className="w-27 cursor-pointer"
            >
              작성완료 {statusTab === 2 && doneDocument.length}
            </Tag>
          </div>
        </div>
        <div
          className="w-full grid-cols-3 grid gap-3 p-4 -m-4 pb-0 mb-0 overflow-y-auto scrollbar-hide "
          style={{ height: "calc(100vh - 250px)" }}
        >
          {documentToRender.map((doc, index) => (
            <DocumentCard key={`doc_${index}`} document={doc} />
          ))}
        </div>
      </div>
    </>
  );
};

export default Documents;
