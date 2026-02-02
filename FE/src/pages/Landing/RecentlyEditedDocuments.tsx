import { Link } from "react-router-dom";
import { IcArrows } from "../../assets/icon/StratisUi";
import DocumentCard from "../../components/shared/DocumentCard";
import type { documentType } from "../Documents/Documents";

const RecentlyEditedDocuments = () => {
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
        {data.map((doc) => (
          <DocumentCard document={doc} />
        ))}
      </div>
    </section>
  );
};

export default RecentlyEditedDocuments;
const data: documentType[] = [
  {
    name: "주디",
    editing: true,
    filledFields: 24,
    lastEdittedAt: "2025. 06. 21",
  },
  {
    name: "주디",
    editing: true,
    filledFields: 24,
    lastEdittedAt: "2025. 06. 21",
  },
  {
    name: "주디",
    editing: true,
    filledFields: 24,
    lastEdittedAt: "2025. 06. 21",
  },
  {
    name: "주디",
    editing: true,
    filledFields: 24,
    lastEdittedAt: "2025. 06. 21",
  },
  {
    name: "주디",
    editing: false,
    filledFields: 24,
    lastEdittedAt: "2025. 06. 21",
  },
  {
    name: "주디",
    editing: false,
    filledFields: 24,
    lastEdittedAt: "2025. 06. 21",
  },
];
