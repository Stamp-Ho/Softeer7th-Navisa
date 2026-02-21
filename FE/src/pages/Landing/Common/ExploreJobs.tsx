import { Link } from "react-router-dom";
import { useTranslation } from "react-i18next";
import JobIcon, { useJobLabels } from "../../../assets/JobIcon";

const ExploreJobs = ({ isAgent = false }) => {
  const { t } = useTranslation(["pages"]);
  const jobLabels = useJobLabels();
  return (
    <div className="flex flex-col gap-5 mt-17">
      <h2 className="headline-s-bold">{isAgent ? "직군별 의뢰인 탐색" : t("landing.exploreJobs")}</h2>
      <div className="grid grid-rows-2 grid-cols-8 px-5 pb-5 gap-4">
        {Array.from({ length: 16 }).map((_, i) => (
          <Link
            key={`job_${i}`}
            className="flex flex-col items-center body-l-semibold cursor-pointer
              transition-all duration-150 ease-out hover:scale-115"
            to={`/search/${isAgent ? "foreigner" : "agent"}${i === 15 ? "" : `?job=${i}`}`}
          >
            <JobIcon index={i} />
            {jobLabels[i]}
          </Link>
        ))}
      </div>
    </div>
  );
};

export default ExploreJobs;
