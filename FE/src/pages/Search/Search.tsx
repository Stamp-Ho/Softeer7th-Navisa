import { useParams, useSearchParams } from "react-router-dom";
import { useSearchScroll } from "./hooks/useSearchScroll";

import SearchAgentFilter from "./components/SearchAgentFilter";
import SearchAgentCard from "./components/SearchAgentCard";
import SearchforeignerFilter from "./components/SearchForeignerFilter";
import SearchForeignerCard from "./components/SearchForeignerCard";
import GoTopFloating from "../../components/common/GoTopFloating";

import type {
  SearchAgentCardType,
  SearchForeignerCardType,
} from "../../types/Cards";
import { useSearchInfiniteQuery } from "../../api/queries/useSearchInfiniteQuery";
import { jobList } from "../../constants/job";
import { regionList } from "../../constants/regions";

const Search = () => {
  const { targetType } = useParams();
  const [searchParams] = useSearchParams();

  const isAgent = targetType === "agent";
  const params = isAgent
    ? {
        jobGroupNameList: searchParams
          .getAll("job")
          .map((id) => jobList[Number(id)]),
        regionList: searchParams
          .getAll("region")
          .map((id) => regionList[Number(id)]),
        languageIdList: searchParams.getAll("language").map((id) => Number(id)),
      }
    : {
        jobGroupNameList: searchParams
          .getAll("job")
          .map((id) => jobList[Number(id)]),
        nationIdList: searchParams.getAll("nation"),
        languageIdList: searchParams.getAll("language").map((id) => Number(id)),
      };

  // 2. 통합 훅 사용 (중복 코드 제거)
  const {
    data,
    fetchNextPage,
    isFetchingNextPage,
    status,
  } = //, hasNextPage
    useSearchInfiniteQuery(targetType || null, params);

  const { scrollRef, handleScroll, searchResultStyle, goTop } = useSearchScroll(
    () => fetchNextPage(),
  );

  const Filter = isAgent ? SearchAgentFilter : SearchforeignerFilter;

  //@ts-ignore
  const allItems = data?.pages.flatMap((page) => page.result.content) ?? [];
  const renderCards = () => {
    if (status === "pending") return <div>로딩 중...</div>;
    if (allItems.length === 0) return <div>검색 결과가 없습니다.</div>;

    return allItems.map((item, index) => {
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
        return (
          <SearchForeignerCard
            key={`foreigner_${index}`}
            foreigner={foreignerItem}
          />
        );
      }
    });
  };

  return (
    <>
      <Filter />
      <div
        ref={scrollRef}
        onScroll={handleScroll}
        className={`grid mt-9 p-4 -m-4 pb-10 gap-4 h-fit min-h-150 overflow-auto scrollbar-hide
            ${isAgent ? "grid-cols-3" : "grid-cols-4"} ${searchResultStyle()}`}
        style={{ maxHeight: "calc(100vh - 350px)" }}
      >
        {renderCards()}

        {/* 추가 데이터 로딩 표시 */}
        {isFetchingNextPage && (
          <div className="col-span-full text-center">추가 로딩 중...</div>
        )}
      </div>
      {allItems.length > 12 && (
        <GoTopFloating
          onClick={goTop}
          className="absolute -right-21 bottom-3"
        />
      )}
    </>
  );
};

export default Search;
