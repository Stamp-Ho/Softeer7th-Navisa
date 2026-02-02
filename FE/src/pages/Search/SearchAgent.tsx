import { useEffect, useRef, useState } from "react";
import SearchAgentCard from "./SearchAgentCard";
import SearchAgentFilter from "./SearchAgentFilter";
import { IcArrowUp } from "../../assets/icon/StratisUi";

const dummyData = [
  {
    id: 2,
    img: "https://placehold.co/140x140",
    name: "엄경례1",
    address: "서울특별시 강남구",
    jobs: [1, 4, 6, 2],
    badges: [3, 6],
  },
  {
    id: 2,
    img: "https://placehold.co/140x140",
    name: "엄경례2",
    address: "서울특별시 강남구",
    jobs: [1, 4, 6, 2],
    badges: [3, 6],
  },
  {
    id: 2,
    img: "https://placehold.co/140x140",
    name: "엄경례3",
    address: "서울특별시 강남구",
    jobs: [1, 4, 6, 2],
    badges: [3, 6],
  },
  {
    id: 2,
    img: "https://placehold.co/140x140",
    name: "엄경례4",
    address: "서울특별시 강남구",
    jobs: [1, 4, 6, 2],
    badges: [3, 6],
  },
  {
    id: 2,
    img: "https://placehold.co/140x140",
    name: "엄경례5",
    address: "서울특별시 강남구",
    jobs: [1, 4, 6, 2],
    badges: [3, 6],
  },
  {
    id: 2,
    img: "https://placehold.co/140x140",
    name: "엄경례6",
    address: "서울특별시 강남구",
    jobs: [1, 4, 6, 2],
    badges: [3, 6],
  },
  {
    id: 2,
    img: "https://placehold.co/140x140",
    name: "엄경례",
    address: "서울특별시 강남구",
    jobs: [1, 4, 6, 2],
    badges: [3, 6],
  },
  {
    id: 2,
    img: "https://placehold.co/140x140",
    name: "엄경례",
    address: "서울특별시 강남구",
    jobs: [1, 4, 6, 2],
    badges: [3, 6],
  },
  {
    id: 2,
    img: "https://placehold.co/140x140",
    name: "엄경례",
    address: "서울특별시 강남구",
    jobs: [1, 4, 6, 2],
    badges: [3, 6],
  },
  {
    id: 2,
    img: "https://placehold.co/140x140",
    name: "엄경례",
    address: "서울특별시 강남구",
    jobs: [1, 4, 6, 2],
    badges: [3, 6],
  },
  {
    id: 2,
    img: "https://placehold.co/140x140",
    name: "엄경례",
    address: "서울특별시 강남구",
    jobs: [1, 4, 6, 2],
    badges: [3, 6],
  },
  {
    id: 2,
    img: "https://placehold.co/140x140",
    name: "엄경례",
    address: "서울특별시 강남구",
    jobs: [1, 4, 6, 2],
    badges: [3, 6],
  },
  {
    id: 2,
    img: "https://placehold.co/140x140",
    name: "엄경례",
    address: "서울특별시 강남구",
    jobs: [1, 4, 6, 2],
    badges: [3, 6],
  },
  {
    id: 2,
    img: "https://placehold.co/140x140",
    name: "엄경례",
    address: "서울특별시 강남구",
    jobs: [1, 4, 6, 2],
    badges: [3, 6],
  },
  {
    id: 2,
    img: "https://placehold.co/140x140",
    name: "엄경례",
    address: "서울특별시 강남구",
    jobs: [1, 4, 6, 2],
    badges: [3, 6],
  },
  {
    id: 2,
    img: "https://placehold.co/140x140",
    name: "엄경례",
    address: "서울특별시 강남구",
    jobs: [1, 4, 6, 2],
    badges: [3, 6],
  },
  {
    id: 2,
    img: "https://placehold.co/140x140",
    name: "엄경례",
    address: "서울특별시 강남구",
    jobs: [1, 4, 6, 2],
    badges: [3, 6],
  },
  {
    id: 2,
    img: "https://placehold.co/140x140",
    name: "엄경례",
    address: "서울특별시 강남구",
    jobs: [1, 4, 6, 2],
    badges: [3, 6],
  },
  {
    id: 2,
    img: "https://placehold.co/140x140",
    name: "엄경례",
    address: "서울특별시 강남구",
    jobs: [1, 4, 6, 2],
    badges: [3, 6],
  },
  {
    id: 2,
    img: "https://placehold.co/140x140",
    name: "엄경례",
    address: "서울특별시 강남구",
    jobs: [1, 4, 6, 2],
    badges: [3, 6],
  },
  {
    id: 2,
    img: "https://placehold.co/140x140",
    name: "엄경례",
    address: "서울특별시 강남구",
    jobs: [1, 4, 6, 2],
    badges: [3, 6],
  },
  {
    id: 2,
    img: "https://placehold.co/140x140",
    name: "엄경례",
    address: "서울특별시 강남구",
    jobs: [1, 4, 6, 2],
    badges: [3, 6],
  },
  {
    id: 2,
    img: "https://placehold.co/140x140",
    name: "엄경례",
    address: "서울특별시 강남구",
    jobs: [1, 4, 6, 2],
    badges: [3, 6],
  },
  {
    id: 2,
    img: "https://placehold.co/140x140",
    name: "엄경례",
    address: "서울특별시 강남구",
    jobs: [1, 4, 6, 2],
    badges: [3, 6],
  },
  {
    id: 2,
    img: "https://placehold.co/140x140",
    name: "엄경례",
    address: "서울특별시 강남구",
    jobs: [1, 4, 6, 2],
    badges: [3, 6],
  },
  {
    id: 2,
    img: "https://placehold.co/140x140",
    name: "엄경례",
    address: "서울특별시 강남구",
    jobs: [1, 4, 6, 2],
    badges: [3, 6],
  },
  {
    id: 2,
    img: "https://placehold.co/140x140",
    name: "엄경례",
    address: "서울특별시 강남구",
    jobs: [1, 4, 6, 2],
    badges: [3, 6],
  },
  {
    id: 2,
    img: "https://placehold.co/140x140",
    name: "엄경례",
    address: "서울특별시 강남구",
    jobs: [1, 4, 6, 2],
    badges: [3, 6],
  },
  {
    id: 2,
    img: "https://placehold.co/140x140",
    name: "엄경례",
    address: "서울특별시 강남구",
    jobs: [1, 4, 6, 2],
    badges: [3, 6],
  },
  {
    id: 2,
    img: "https://placehold.co/140x140",
    name: "엄경례",
    address: "서울특별시 강남구",
    jobs: [1, 4, 6, 2],
    badges: [3, 6],
  },
  {
    id: 2,
    img: "https://placehold.co/140x140",
    name: "엄경례",
    address: "서울특별시 강남구",
    jobs: [1, 4, 6, 2],
    badges: [3, 6],
  },
  {
    id: 2,
    img: "https://placehold.co/140x140",
    name: "엄경례",
    address: "서울특별시 강남구",
    jobs: [1, 4, 6, 2],
    badges: [3, 6],
  },
  {
    id: 2,
    img: "https://placehold.co/140x140",
    name: "엄경례",
    address: "서울특별시 강남구",
    jobs: [1, 4, 6, 2],
    badges: [3, 6],
  },
  {
    id: 2,
    img: "https://placehold.co/140x140",
    name: "엄경례",
    address: "서울특별시 강남구",
    jobs: [1, 4, 6, 2],
    badges: [3, 6],
  },
  {
    id: 2,
    img: "https://placehold.co/140x140",
    name: "엄경례",
    address: "서울특별시 강남구",
    jobs: [1, 4, 6, 2],
    badges: [3, 6],
  },
  {
    id: 2,
    img: "https://placehold.co/140x140",
    name: "엄경례",
    address: "서울특별시 강남구",
    jobs: [1, 4, 6, 2],
    badges: [3, 6],
  },
  {
    id: 2,
    img: "https://placehold.co/140x140",
    name: "엄경례",
    address: "서울특별시 강남구",
    jobs: [1, 4, 6, 2],
    badges: [3, 6],
  },
  {
    id: 2,
    img: "https://placehold.co/140x140",
    name: "엄경례",
    address: "서울특별시 강남구",
    jobs: [1, 4, 6, 2],
    badges: [3, 6],
  },
  {
    id: 2,
    img: "https://placehold.co/140x140",
    name: "엄경례",
    address: "서울특별시 강남구",
    jobs: [1, 4, 6, 2],
    badges: [3, 6],
  },
  {
    id: 2,
    img: "https://placehold.co/140x140",
    name: "엄경례",
    address: "서울특별시 강남구",
    jobs: [1, 4, 6, 2],
    badges: [3, 6],
  },
  {
    id: 2,
    img: "https://placehold.co/140x140",
    name: "엄경례",
    address: "서울특별시 강남구",
    jobs: [1, 4, 6, 2],
    badges: [3, 6],
  },
  {
    id: 2,
    img: "https://placehold.co/140x140",
    name: "엄경례",
    address: "서울특별시 강남구",
    jobs: [1, 4, 6, 2],
    badges: [3, 6],
  },
  {
    id: 2,
    img: "https://placehold.co/140x140",
    name: "엄경례",
    address: "서울특별시 강남구",
    jobs: [1, 4, 6, 2],
    badges: [3, 6],
  },
  {
    id: 2,
    img: "https://placehold.co/140x140",
    name: "엄경례",
    address: "서울특별시 강남구",
    jobs: [1, 4, 6, 2],
    badges: [3, 6],
  },
  {
    id: 2,
    img: "https://placehold.co/140x140",
    name: "엄경례",
    address: "서울특별시 강남구",
    jobs: [1, 4, 6, 2],
    badges: [3, 6],
  },
  {
    id: 2,
    img: "https://placehold.co/140x140",
    name: "엄경례",
    address: "서울특별시 강남구",
    jobs: [1, 4, 6, 2],
    badges: [3, 6],
  },
  {
    id: 2,
    img: "https://placehold.co/140x140",
    name: "엄경례",
    address: "서울특별시 강남구",
    jobs: [1, 4, 6, 2],
    badges: [3, 6],
  },
  {
    id: 2,
    img: "https://placehold.co/140x140",
    name: "엄경례",
    address: "서울특별시 강남구",
    jobs: [1, 4, 6, 2],
    badges: [3, 6],
  },
  {
    id: 2,
    img: "https://placehold.co/140x140",
    name: "엄경례",
    address: "서울특별시 강남구",
    jobs: [1, 4, 6, 2],
    badges: [3, 6],
  },
];
const SearchAgent = () => {
  const scrollRef = useRef<HTMLDivElement>(null);
  const endRef = useRef<HTMLDivElement>(null); // 마지막 빈 div를 위한 ref

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

  // 3. 상태에 따른 마스크 스타일 결정
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
      <SearchAgentFilter />
      <div>
        <div
          ref={scrollRef}
          onScroll={handleScroll}
          className={`grid grid-cols-3 mt-9 gap-4 overflow-auto scrollbar-hide ${getMaskStyle()}`}
          style={{ height: "calc(100vh - 340px)" }}
        >
          {dummyData.slice(0, 18 * tempNumber).map((agent, index) => (
            <SearchAgentCard key={index} agent={agent} />
          ))}
          <div ref={endRef} className="w-3 h-3 -mt-10" />
        </div>
      </div>
      <button
        className="absolute -right-20.5 bottom-3 rounded-full cursor-pointer drop-shadow-[0_0_7px_#6860A040] bg-white w-16 h-16 flex items-center justify-center"
        onClick={goTop}
      >
        <IcArrowUp size={20} />
      </button>
    </>
  );
};

export default SearchAgent;
