import { Link } from "react-router-dom";
import { useTranslation } from "react-i18next";
import { IcArrows } from "../../../assets/icon/StratisUi";
import DocumentCard from "../../../components/domain/DocumentCard";
import { useRecentVisaFormsQuery } from "../../../api/queries/useRecentVisaFormsQuery";

const RecentlyEditedDocuments = () => {
  const { t } = useTranslation(["pages"]);
  const { data, isLoading, isError } = useRecentVisaFormsQuery();
  if (isLoading) return <div>{t("landing.loading")}</div>;
  const dataToRender = isError ? (
    Array.from({ length: 6 }).map((_, index) => <DocumentCard key={`doc_${index}`} />)
  ) : (
    <>
      {data?.map((doc, index) => (
        <DocumentCard key={`doc_${index}`} document={doc} />
      ))}
      {Array.from({ length: 5 - (data?.length || 0) }).map((_, index) => (
        <DocumentCard key={`doc_${index}`} />
      ))}
    </>
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
