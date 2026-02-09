import { useNavigate } from "react-router-dom";

import FilterWithDropdown from "../../../components/common/FilterWithDropdown";
import { IcRotate } from "../../../assets/icon/StratisUi";
import React, { useState } from "react";

const SearchAgentFilter = () => {
  const navigate = useNavigate();
  const [openedFilter, setOpenedFilter] = useState(-1);

  const initFilter = () => {
    navigate("/search/agent", { replace: true });
  };

  const filterInfoList: {
    paramKey: "job" | "region" | "language";
    cols: number;
    style: string;
  }[] = [
    { paramKey: "job", cols: 5, style: "flex-5" },
    { paramKey: "region", cols: 4, style: "flex-2" },
    { paramKey: "language", cols: 4, style: "flex-2" },
  ];

  return (
    <div className="flex flex-col items-center w-full">
      <h2 className="headline-m-bold mr-auto text-gray-1000 mb-13 mt-12">
        행정사 탐색
      </h2>
      <div className="flex flex-row items-center w-full gap-3">
        {filterInfoList.map((pk, index) => (
          <React.Fragment key={`filter_${pk.paramKey}`}>
            <FilterWithDropdown
              paramKey={pk.paramKey}
              cols={pk.cols}
              className={pk.style}
              searchAgent={true}
              isOpen={openedFilter === index}
              onClick={() => setOpenedFilter(index)}
              onClose={() => setOpenedFilter(-1)}
            />
          </React.Fragment>
        ))}
        <div
          className="flex-1 flex-row flex gap-2 items-center justify-center body-l-semibold text-text-base cursor-pointer"
          onClick={initFilter}
        >
          <IcRotate /> 초기화
        </div>
      </div>
    </div>
  );
};

export default SearchAgentFilter;
