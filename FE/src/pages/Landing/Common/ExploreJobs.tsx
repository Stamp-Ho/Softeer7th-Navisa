import { Link } from "react-router-dom";
import JobIcon, { jobs } from "../../../assets/JobIcon";

const ExploreJobs = ({ isAgent = false }) => {
  return (
    <div className="flex flex-col gap-5 mt-17">
      <h2 className="headline-s-bold">
        직군별 {isAgent ? "의뢰인" : "행정사"}
        탐색
      </h2>
      <div className="grid grid-rows-2 grid-cols-8 px-5 pb-5 gap-4">
        {Array.from({ length: 16 }).map((_, i) => (
          <Link
            className="flex flex-col items-center body-l-semibold cursor-pointer
              transition-all duration-150 ease-out hover:scale-115"
            to={`/search/${isAgent ? "foreigner" : "agent"}${i === 15 ? "" : `?job=${i}`}`}
          >
            <JobIcon index={i} />
            {jobs[i]}
          </Link>
        ))}
      </div>
    </div>
  );
};

export default ExploreJobs;
