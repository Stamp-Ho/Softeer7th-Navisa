import { Link } from "react-router-dom";
import { useTranslation } from "react-i18next";
import { IcArrows } from "../../../assets/icon/StratisUi";
import DocumentCard from "../../../components/domain/DocumentCard";
import { useRecentVisaFormsQuery } from "../../../api/queries/useRecentVisaFormsQuery";

const RecentlyEditedDocuments = () => {
  const { t } = useTranslation(["pages"]);
  const { data, isLoading } = useRecentVisaFormsQuery();
  const dataToRender = isLoading ? (
    Array.from({ length: 1 }).map((_, index) => <DocumentCard key={`doc_${index}`} />)
  ) : data && data.length > 0 ? (
    data.map((doc, index) => <DocumentCard key={`doc_${index}`} document={doc} />)
  ) : (
    <div className="flex flex-row w-124.75 h-34.5 p-4 gap-3 bg-white rounded-[10px] shadow">
      최근 수정한 비자서류가 없습니다.
    </div>
  );
  return (
    <section className="w-full flex flex-col relative gap-5 mt-12">
      <Link className="headline-s-bold flex flex-row items-center gap-1 w-fit" to="/documents">
        {t("landing.recentlyEditedDocuments")}{" "}
        <div className="-rotate-90">
          <IcArrows size={40} />
        </div>
      </Link>
      <div className="grid grid-cols-3 grid-rows-2 whitespace-nowrap gap-3">{dataToRender}</div>
    </section>
  );
};

export default RecentlyEditedDocuments;
