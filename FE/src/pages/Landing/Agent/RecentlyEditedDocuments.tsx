import { Link } from "react-router-dom";
import { IcArrows } from "../../../assets/icon/StratisUi";
import DocumentCard from "../../../components/shared/DocumentCard";
import { useRecentVisaFormsQuery } from "../../../api/hooks/useRecentVisaFormsQuery";

const RecentlyEditedDocuments = () => {
  const { data, isLoading, isError } = useRecentVisaFormsQuery();
  if (isLoading) return <div>로딩중...</div>;
  const dataToRender = isError
    ? Array.from({ length: 6 }).map((_, index) => (
        <DocumentCard key={`doc_${index}`} />
      ))
    : data?.map((doc, index) => (
        <DocumentCard key={`doc_${index}`} document={doc} />
      ));

  return (
    <section className="w-full flex flex-col relative gap-5 mt-12">
      <Link
        className="headline-s-bold flex flex-row items-center gap-1 w-fit"
        to="/documents"
      >
        최근 수정한 비자서류{" "}
        <div className="-rotate-90">
          <IcArrows size={40} />
        </div>
      </Link>
      <div className="grid grid-cols-3 grid-rows-2 whitespace-nowrap gap-3">
        {dataToRender}
      </div>
    </section>
  );
};

export default RecentlyEditedDocuments;
