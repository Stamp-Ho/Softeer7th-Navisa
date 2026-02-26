import { useNavigate, useParams, useSearchParams } from "react-router-dom";
import { useTranslation } from "react-i18next";
import { useSearchScroll } from "./hooks/useSearchScroll";

import SearchAgentFilter from "./components/SearchAgentFilter";
import SearchAgentCard from "./components/SearchAgentCard";
import SearchforeignerFilter from "./components/SearchForeignerFilter";
import SearchForeignerCard from "./components/SearchForeignerCard";
import GoTopFloating from "../../components/common/GoTopFloating";

import type { SearchAgentCardType, SearchForeignerCardType } from "../../types/Cards";
import { useSearchInfiniteQuery } from "../../api/queries/useSearchInfiniteQuery";
import { regionList } from "../../constants/regions";
import { useAuth } from "../../contexts/AuthContextProvider";
import { useEffect, useState } from "react";
import { jobList } from "../../constants/job";

const Search = () => {
  const { t } = useTranslation(["pages"]);
  const navigate = useNavigate();
  const { userType } = useAuth();
  const { targetType } = useParams();
  const [searchParams] = useSearchParams();
  const [searchResults, setSearchResults] = useState<any[]>([]);

  const isAgent = targetType === "agent";
  const params = isAgent
    ? {
        jobGroupNameList: searchParams
          .getAll("job")
          .map((id) => jobList[Number(id)])
          .filter((v): v is string => v !== undefined),
        regionList: searchParams
          .getAll("region")
          .map((id) => regionList[Number(id)])
          .filter((v): v is string => v !== undefined),
        languageIdList: searchParams.getAll("language").map((id) => Number(id) + 1),
      }
    : {
        jobGroupNameList: searchParams
          .getAll("job")
          .map((id) => jobList[Number(id)])
          .filter((v): v is string => v !== undefined),
        nationIdList: searchParams.getAll("nation").map((id) => Number(id) + 1),
        languageIdList: searchParams.getAll("language").map((id) => Number(id) + 1),
      };

  // 2. 통합 훅 사용 (중복 코드 제거)
  const {
    data,
    fetchNextPage,
    isFetchingNextPage,
    status,
  } = //, hasNextPage
    useSearchInfiniteQuery(targetType || null, params);

  useEffect(() => {
    if (data && status === "success") {
      //@ts-ignore
      setSearchResults(data.pages.flatMap((page) => page.result.content) ?? []);
    }
  }, [data, status]);

  const { scrollRef, handleScroll, searchResultStyle, goTop } = useSearchScroll(() => fetchNextPage());

  useEffect(() => {
    if (userType === "NOT_AUTHED") navigate("/", { replace: true });
    else if (!isAgent && userType !== "VALID_AGENT") navigate("/", { replace: true });
  }, [userType, isAgent]);

  const Filter = isAgent ? SearchAgentFilter : SearchforeignerFilter;

  const renderCards = () => {
    if (data && status === "success" && searchResults.length === 0) return <div>{t("search.noResults")}</div>;

    return searchResults.map((item, index) => {
      // 여기서 item의 타입을 구체화합니다.
      if (isAgent) {
        const agentItem = item as SearchAgentCardType;
        return (
          <SearchAgentCard
            key={`agent_${agentItem.agentId}_${index}`} // ID가 있다면 index보다 ID 권장
            agent={agentItem}
          />
        );
      } else {
        const foreignerItem = item as SearchForeignerCardType;
        return <SearchForeignerCard key={`foreigner_${index}`} foreigner={foreignerItem} />;
      }
    });
  };

  return (
    <>
      <Filter />
      <div
        ref={scrollRef}
        onScroll={handleScroll}
        className={`grid mt-9 p-4 -m-4 pb-10 mb-0 gap-4 h-fit min-h-100 overflow-auto scrollbar-hide
            ${isAgent ? "grid-cols-3" : "grid-cols-4"} ${searchResultStyle()}`}
        style={{ maxHeight: "calc(100vh - 330px)" }}
      >
        {renderCards()}

        {/* 추가 데이터 로딩 표시 */}
        {isFetchingNextPage && <div className="col-span-full text-center">loading...</div>}
      </div>
      {searchResults.length > 12 && <GoTopFloating onClick={goTop} className="absolute -right-21 bottom-3" />}
    </>
  );
};

export default Search;
