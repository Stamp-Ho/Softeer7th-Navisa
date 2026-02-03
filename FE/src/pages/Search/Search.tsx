import { useParams } from "react-router-dom";
import { useSearchScroll } from "./hooks/useSearchScroll";
import { useSearchQuery } from "./hooks/useSearchQuery";

import SearchAgentFilter from "./components/SearchAgentFilter";
import SearchAgentCard from "./components/SearchAgentCard";
import SearchforeignerFilter from "./components/SearchForeignerFilter";
import SearchForeignerCard from "./components/SearchForeignerCard";
import GoTopFloating from "../../components/common/GoTopFloating";

import type {
  SearchAgentCardType,
  SearchForeignerCardType,
} from "../../types/Cards";

const Search = () => {
  const { targetType } = useParams();
  const { data, fetchNextPage, dummyAgentData, dummyForeignerData } =
    useSearchQuery<SearchAgentCardType | SearchForeignerCardType>();
  const { scrollRef, handleScroll, searchResultStyle, goTop } =
    useSearchScroll(fetchNextPage);

  const isAgent = targetType === "agent";

  const Filter = isAgent ? SearchAgentFilter : SearchforeignerFilter;

  const renderCards = () => {
    if (!data) {
      //Api 붙이면 여기 삭제
      if (isAgent) {
        return (dummyAgentData as SearchAgentCardType[]).map((item, index) => (
          <SearchAgentCard key={`search_agent_${1000 + index}`} agent={item} />
        ));
      }
      return (dummyForeignerData as SearchForeignerCardType[]).map(
        (item, index) => (
          <SearchForeignerCard
            key={`search_foreigner_${1000 + index}`}
            foreigner={item}
          />
        ),
      );
      //여기까지 삭제
      return <></>;
    }
    if (isAgent)
      return (data as SearchAgentCardType[]).map((item, index) => (
        <SearchAgentCard key={`search_agent_${index}`} agent={item} />
      ));
    return (data as SearchForeignerCardType[]).map((item, index) => (
      <SearchForeignerCard key={`search_foreigner_${index}`} foreigner={item} />
    ));
  };

  return (
    <>
      <Filter />
      <div
        ref={scrollRef}
        onScroll={handleScroll}
        className={`grid  mt-9 gap-4 overflow-auto scrollbar-hide
            ${isAgent ? "grid-cols-3" : "grid-cols-4"} ${searchResultStyle()}`}
        style={{ height: "calc(100vh - 340px)" }}
      >
        {renderCards()}
      </div>
      <GoTopFloating onClick={goTop} className="absolute -right-21 bottom-3" />
    </>
  );
};

export default Search;
