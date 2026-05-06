import { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import Tag from "../../components/common/Tag";
import DocumentCard from "../../components/domain/DocumentCard";
import GrayBackground from "../../components/layout/GrayBackground";
import type { RecentVisaFormsResponse } from "../../api/types/etc";
import { useRecentApplicationsQuery } from "../../api/queries/useRecentApplicationsQuery";

const Documents = () => {
  const { t } = useTranslation(["pages"]);
  const { data, isLoading, isError } = useRecentApplicationsQuery();
  const [statusTab, setStatusTab] = useState<number>(0);

  const [documentToRender, setDocumentToRender] = useState<RecentVisaFormsResponse[]>([]);

  useEffect(() => {
    if (data) {
      const editingDocument = data.filter((document) => !document.isDone);
      const doneDocument = data.filter((document) => document.isDone);
      if (statusTab === 0) setDocumentToRender(data);
      if (statusTab === 1) setDocumentToRender(editingDocument);
      if (statusTab === 2) setDocumentToRender(doneDocument);
    }
  }, [data, statusTab]);

  const renderTabs = () => {
    const tabLabels = [t("documents.allTab"), t("documents.inProgressTab"), t("documents.completedTab")];
    return tabLabels.map((opt, index) => {
      const isActive = statusTab === index;
      return (
        <div key={`tab_${index}`} onClick={() => setStatusTab(index)}>
          <Tag variant={isActive ? "large_violet_off_bold" : "large_white_off"} className="w-27 cursor-pointer">
            {opt}
            {isActive && <span className="ml-2">{documentToRender.length}</span>}
          </Tag>
        </div>
      );
    });
  };

  const renderDocuments = () => {
    if (isLoading) return Array.from({ length: 4 }).map((_, i) => <DocumentCard key={`skel_${i}`} />);
    if (isError || !data) return <div className="title-m-medium text-gray-600">{t("documents.error")}</div>;
    return documentToRender.map((doc, index) => <DocumentCard key={`doc_${index}`} document={doc} />);
  };
  return (
    <>
      <GrayBackground />
      <div className="flex flex-col gap-8">
        <h2 className="headline-m-bold mt-9">{t("documents.title")}</h2>
        <div className="flex flex-row gap-3 w-full">{renderTabs()}</div>
        <div
          className="w-full grid-cols-3 grid gap-3 p-4 -m-4 pb-4 mb-0 overflow-y-auto scrollbar-hide "
          style={{ maxHeight: "calc(100vh - 250px)" }}
        >
          {renderDocuments()}
        </div>
      </div>
    </>
  );
};

export default Documents;
