import { useEffect, useRef, useState } from "react";
import { IcArrowUp } from "../../assets/icon/StratisUi";
import SearchForeignerCard from "../../components/shared/SearchForeignerCard";
import SearchForeignerFilter from "./SearchForeignerFilter";

const dummyData = [
  {
    id: 3,
    nations: [0, 1, 2, 3, 4, 5],
    nickName: "닉 주디 엘리자베스 마야",
    targetJob: "웹 개발자",
    major: "컴퓨터공학",
    languages: [0, 1, 2, 3, 4, 5],
  },
  {
    id: 3,
    nations: [0, 1, 2, 3, 4, 5],
    nickName: "닉 주디 엘리자베스 마야",
    targetJob: "웹 개발자",
    major: "컴퓨터공학",
    languages: [0, 1, 2, 3, 4, 5],
  },
  {
    id: 3,
    nations: [0, 1, 2, 3, 4, 5],
    nickName: "닉 주디 엘리자베스 마야",
    targetJob: "웹 개발자",
    major: "컴퓨터공학",
    languages: [0, 1, 2, 3, 4, 5],
  },
  {
    id: 3,
    nations: [0, 1, 2, 3, 4, 5],
    nickName: "닉 주디 엘리자베스 마야",
    targetJob: "웹 개발자",
    major: "컴퓨터공학",
    languages: [0, 1, 2, 3, 4, 5],
  },
  {
    id: 3,
    nations: [0, 1, 2, 3, 4, 5],
    nickName: "닉 주디 엘리자베스 마야",
    targetJob: "웹 개발자",
    major: "컴퓨터공학",
    languages: [0, 1, 2, 3, 4, 5],
  },
  {
    id: 3,
    nations: [0, 1, 2, 3, 4, 5],
    nickName: "닉 주디 엘리자베스 마야",
    targetJob: "웹 개발자",
    major: "컴퓨터공학",
    languages: [0, 1, 2, 3, 4, 5],
  },
  {
    id: 3,
    nations: [0, 1, 2, 3, 4, 5],
    nickName: "닉 주디 엘리자베스 마야",
    targetJob: "웹 개발자",
    major: "컴퓨터공학",
    languages: [0, 1, 2, 3, 4, 5],
  },
  {
    id: 3,
    nations: [0, 1, 2, 3, 4, 5],
    nickName: "닉 주디 엘리자베스 마야",
    targetJob: "웹 개발자",
    major: "컴퓨터공학",
    languages: [0, 1, 2, 3, 4, 5],
  },
  {
    id: 3,
    nations: [0, 1, 2, 3, 4, 5],
    nickName: "닉 주디 엘리자베스 마야",
    targetJob: "웹 개발자",
    major: "컴퓨터공학",
    languages: [0, 1, 2, 3, 4, 5],
  },
  {
    id: 3,
    nations: [0, 1, 2, 3, 4, 5],
    nickName: "닉 주디 엘리자베스 마야",
    targetJob: "웹 개발자",
    major: "컴퓨터공학",
    languages: [0, 1, 2, 3, 4, 5],
  },
  {
    id: 3,
    nations: [0, 1, 2, 3, 4, 5],
    nickName: "닉 주디 엘리자베스 마야",
    targetJob: "웹 개발자",
    major: "컴퓨터공학",
    languages: [0, 1, 2, 3, 4, 5],
  },
  {
    id: 3,
    nations: [0, 1, 2, 3, 4, 5],
    nickName: "닉 주디 엘리자베스 마야",
    targetJob: "웹 개발자",
    major: "컴퓨터공학",
    languages: [0, 1, 2, 3, 4, 5],
  },
  {
    id: 3,
    nations: [0, 1, 2, 3, 4, 5],
    nickName: "닉 주디 엘리자베스 마야",
    targetJob: "웹 개발자",
    major: "컴퓨터공학",
    languages: [0, 1, 2, 3, 4, 5],
  },
  {
    id: 3,
    nations: [0, 1, 2, 3, 4, 5],
    nickName: "닉 주디 엘리자베스 마야",
    targetJob: "웹 개발자",
    major: "컴퓨터공학",
    languages: [0, 1, 2, 3, 4, 5],
  },
  {
    id: 3,
    nations: [0, 1, 2, 3, 4, 5],
    nickName: "닉 주디 엘리자베스 마야",
    targetJob: "웹 개발자",
    major: "컴퓨터공학",
    languages: [0, 1, 2, 3, 4, 5],
  },
  {
    id: 3,
    nations: [0, 1, 2, 3, 4, 5],
    nickName: "닉 주디 엘리자베스 마야",
    targetJob: "웹 개발자",
    major: "컴퓨터공학",
    languages: [0, 1, 2, 3, 4, 5],
  },
];

const SearchForeigner = () => {
  const scrollRef = useRef<HTMLDivElement>(null);
  const endRef = useRef<HTMLDivElement>(null);

  const [isAtStart, setIsAtStart] = useState(true);
  const [isAtEnd, setIsAtEnd] = useState(false);
  const [tempNumber, setTempNumber] = useState(1);

  const handleScroll = () => {
    if (scrollRef.current) {
      const { scrollTop, scrollHeight, clientHeight } = scrollRef.current;

      // 상단 도달 체크 (여유값 20px)
      setIsAtStart(scrollTop <= 20);

      // 하단 도달 체크 (바닥에서 20px 이내일 때)
      // scrollHeight(전체높이) - scrollTop(내려온길이) === clientHeight(보이는높이)
      const isBottom = scrollHeight - scrollTop - clientHeight <= 20;
      setIsAtEnd(isBottom);
    }
  };
  const goTop = () => {
    if (scrollRef.current) {
      scrollRef.current.scrollTo({
        top: 0,
        behavior: "smooth", // 부드럽게 스크롤링
      });
    }
  };

  // Intersection Observer는 오직 "데이터 추가 로딩" 역할만 수행
  useEffect(() => {
    const observer = new IntersectionObserver(
      ([entry]) => {
        if (entry.isIntersecting) {
          // 데이터 더 가져오기 로직만 수행
          setTempNumber((prev) => prev + 1);
        }
      },
      { threshold: 0.1 },
    );

    if (endRef.current) observer.observe(endRef.current);
    return () => observer.disconnect();
  }, []);

  const getMaskStyle = () => {
    const base = "transition-all duration-500 ";
    if (isAtStart)
      return (
        base +
        `mask-[linear-gradient(to_bottom,black_85%,transparent_100%)]
        [-webkit-mask-image:linear-gradient(to_bottom,black_85%,transparent_100%)]`
      );
    if (isAtEnd)
      return (
        base +
        `mask-[linear-gradient(to_top,black_85%,transparent_100%)]
        [-webkit-mask-image:linear-gradient(to_top,black_85%,transparent_100%)]`
      );
    return (
      base +
      `mask-[linear-gradient(to_bottom,transparent_0%,black_15%,black_85%,transparent_100%)]
      [-webkit-mask-image:linear-gradient(to_bottom,transparent_0%,black_15%,black_85%,transparent_100%)]`
    );
  };
  return (
    <>
      <SearchForeignerFilter />
      <div>
        <div
          ref={scrollRef}
          onScroll={handleScroll}
          className={`grid grid-cols-4 mt-9 gap-4 overflow-auto scrollbar-hide ${getMaskStyle()}`}
          style={{ height: "calc(100vh - 340px)" }}
        >
          {dummyData.slice(0, 20 * tempNumber).map((foreigner, index) => (
            <SearchForeignerCard key={index} foreigner={foreigner} />
          ))}
          <div ref={endRef} className="w-3 h-1 -mt-10" />
        </div>
      </div>
      <button
        className="absolute -right-20.5 bottom-3 rounded-full cursor-pointer shadow bg-white w-16 h-16 flex items-center justify-center"
        onClick={goTop}
      >
        <IcArrowUp size={20} />
      </button>
    </>
  );
};

export default SearchForeigner;
