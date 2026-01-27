import { useEffect, useRef, useState } from "react";
import SearchAgentCard from "./SearchAgentCard";
import SearchAgentFilter from "./SearchAgentFilter";
import { IcArrowUp } from "../../assets/icon/StratisUi";

const dummyData = [
  {
    img: "https://placehold.co/140x140",
    name: "엄경례1",
    address: "서울특별시 강남구",
    jobs: [1, 4, 6, 2],
    badges: [3, 6],
  },
  {
    img: "https://placehold.co/140x140",
    name: "엄경례2",
    address: "서울특별시 강남구",
    jobs: [1, 4, 6, 2],
    badges: [3, 6],
  },
  {
    img: "https://placehold.co/140x140",
    name: "엄경례3",
    address: "서울특별시 강남구",
    jobs: [1, 4, 6, 2],
    badges: [3, 6],
  },
  {
    img: "https://placehold.co/140x140",
    name: "엄경례4",
    address: "서울특별시 강남구",
    jobs: [1, 4, 6, 2],
    badges: [3, 6],
  },
  {
    img: "https://placehold.co/140x140",
    name: "엄경례5",
    address: "서울특별시 강남구",
    jobs: [1, 4, 6, 2],
    badges: [3, 6],
  },
  {
    img: "https://placehold.co/140x140",
    name: "엄경례6",
    address: "서울특별시 강남구",
    jobs: [1, 4, 6, 2],
    badges: [3, 6],
  },
  {
    img: "https://placehold.co/140x140",
    name: "엄경례",
    address: "서울특별시 강남구",
    jobs: [1, 4, 6, 2],
    badges: [3, 6],
  },
  {
    img: "https://placehold.co/140x140",
    name: "엄경례",
    address: "서울특별시 강남구",
    jobs: [1, 4, 6, 2],
    badges: [3, 6],
  },
  {
    img: "https://placehold.co/140x140",
    name: "엄경례",
    address: "서울특별시 강남구",
    jobs: [1, 4, 6, 2],
    badges: [3, 6],
  },
  {
    img: "https://placehold.co/140x140",
    name: "엄경례",
    address: "서울특별시 강남구",
    jobs: [1, 4, 6, 2],
    badges: [3, 6],
  },
  {
    img: "https://placehold.co/140x140",
    name: "엄경례",
    address: "서울특별시 강남구",
    jobs: [1, 4, 6, 2],
    badges: [3, 6],
  },
  {
    img: "https://placehold.co/140x140",
    name: "엄경례",
    address: "서울특별시 강남구",
    jobs: [1, 4, 6, 2],
    badges: [3, 6],
  },
  {
    img: "https://placehold.co/140x140",
    name: "엄경례",
    address: "서울특별시 강남구",
    jobs: [1, 4, 6, 2],
    badges: [3, 6],
  },
  {
    img: "https://placehold.co/140x140",
    name: "엄경례",
    address: "서울특별시 강남구",
    jobs: [1, 4, 6, 2],
    badges: [3, 6],
  },
  {
    img: "https://placehold.co/140x140",
    name: "엄경례",
    address: "서울특별시 강남구",
    jobs: [1, 4, 6, 2],
    badges: [3, 6],
  },
  {
    img: "https://placehold.co/140x140",
    name: "엄경례",
    address: "서울특별시 강남구",
    jobs: [1, 4, 6, 2],
    badges: [3, 6],
  },
  {
    img: "https://placehold.co/140x140",
    name: "엄경례",
    address: "서울특별시 강남구",
    jobs: [1, 4, 6, 2],
    badges: [3, 6],
  },
  {
    img: "https://placehold.co/140x140",
    name: "엄경례",
    address: "서울특별시 강남구",
    jobs: [1, 4, 6, 2],
    badges: [3, 6],
  },
  {
    img: "https://placehold.co/140x140",
    name: "엄경례",
    address: "서울특별시 강남구",
    jobs: [1, 4, 6, 2],
    badges: [3, 6],
  },
  {
    img: "https://placehold.co/140x140",
    name: "엄경례",
    address: "서울특별시 강남구",
    jobs: [1, 4, 6, 2],
    badges: [3, 6],
  },
  {
    img: "https://placehold.co/140x140",
    name: "엄경례",
    address: "서울특별시 강남구",
    jobs: [1, 4, 6, 2],
    badges: [3, 6],
  },
  {
    img: "https://placehold.co/140x140",
    name: "엄경례",
    address: "서울특별시 강남구",
    jobs: [1, 4, 6, 2],
    badges: [3, 6],
  },
  {
    img: "https://placehold.co/140x140",
    name: "엄경례",
    address: "서울특별시 강남구",
    jobs: [1, 4, 6, 2],
    badges: [3, 6],
  },
  {
    img: "https://placehold.co/140x140",
    name: "엄경례",
    address: "서울특별시 강남구",
    jobs: [1, 4, 6, 2],
    badges: [3, 6],
  },
  {
    img: "https://placehold.co/140x140",
    name: "엄경례",
    address: "서울특별시 강남구",
    jobs: [1, 4, 6, 2],
    badges: [3, 6],
  },
  {
    img: "https://placehold.co/140x140",
    name: "엄경례",
    address: "서울특별시 강남구",
    jobs: [1, 4, 6, 2],
    badges: [3, 6],
  },
  {
    img: "https://placehold.co/140x140",
    name: "엄경례",
    address: "서울특별시 강남구",
    jobs: [1, 4, 6, 2],
    badges: [3, 6],
  },
  {
    img: "https://placehold.co/140x140",
    name: "엄경례",
    address: "서울특별시 강남구",
    jobs: [1, 4, 6, 2],
    badges: [3, 6],
  },
  {
    img: "https://placehold.co/140x140",
    name: "엄경례",
    address: "서울특별시 강남구",
    jobs: [1, 4, 6, 2],
    badges: [3, 6],
  },
  {
    img: "https://placehold.co/140x140",
    name: "엄경례",
    address: "서울특별시 강남구",
    jobs: [1, 4, 6, 2],
    badges: [3, 6],
  },
  {
    img: "https://placehold.co/140x140",
    name: "엄경례",
    address: "서울특별시 강남구",
    jobs: [1, 4, 6, 2],
    badges: [3, 6],
  },
  {
    img: "https://placehold.co/140x140",
    name: "엄경례",
    address: "서울특별시 강남구",
    jobs: [1, 4, 6, 2],
    badges: [3, 6],
  },
  {
    img: "https://placehold.co/140x140",
    name: "엄경례",
    address: "서울특별시 강남구",
    jobs: [1, 4, 6, 2],
    badges: [3, 6],
  },
  {
    img: "https://placehold.co/140x140",
    name: "엄경례",
    address: "서울특별시 강남구",
    jobs: [1, 4, 6, 2],
    badges: [3, 6],
  },
  {
    img: "https://placehold.co/140x140",
    name: "엄경례",
    address: "서울특별시 강남구",
    jobs: [1, 4, 6, 2],
    badges: [3, 6],
  },
  {
    img: "https://placehold.co/140x140",
    name: "엄경례",
    address: "서울특별시 강남구",
    jobs: [1, 4, 6, 2],
    badges: [3, 6],
  },
  {
    img: "https://placehold.co/140x140",
    name: "엄경례",
    address: "서울특별시 강남구",
    jobs: [1, 4, 6, 2],
    badges: [3, 6],
  },
  {
    img: "https://placehold.co/140x140",
    name: "엄경례",
    address: "서울특별시 강남구",
    jobs: [1, 4, 6, 2],
    badges: [3, 6],
  },
  {
    img: "https://placehold.co/140x140",
    name: "엄경례",
    address: "서울특별시 강남구",
    jobs: [1, 4, 6, 2],
    badges: [3, 6],
  },
  {
    img: "https://placehold.co/140x140",
    name: "엄경례",
    address: "서울특별시 강남구",
    jobs: [1, 4, 6, 2],
    badges: [3, 6],
  },
  {
    img: "https://placehold.co/140x140",
    name: "엄경례",
    address: "서울특별시 강남구",
    jobs: [1, 4, 6, 2],
    badges: [3, 6],
  },
  {
    img: "https://placehold.co/140x140",
    name: "엄경례",
    address: "서울특별시 강남구",
    jobs: [1, 4, 6, 2],
    badges: [3, 6],
  },
  {
    img: "https://placehold.co/140x140",
    name: "엄경례",
    address: "서울특별시 강남구",
    jobs: [1, 4, 6, 2],
    badges: [3, 6],
  },
  {
    img: "https://placehold.co/140x140",
    name: "엄경례",
    address: "서울특별시 강남구",
    jobs: [1, 4, 6, 2],
    badges: [3, 6],
  },
  {
    img: "https://placehold.co/140x140",
    name: "엄경례",
    address: "서울특별시 강남구",
    jobs: [1, 4, 6, 2],
    badges: [3, 6],
  },
  {
    img: "https://placehold.co/140x140",
    name: "엄경례",
    address: "서울특별시 강남구",
    jobs: [1, 4, 6, 2],
    badges: [3, 6],
  },
  {
    img: "https://placehold.co/140x140",
    name: "엄경례",
    address: "서울특별시 강남구",
    jobs: [1, 4, 6, 2],
    badges: [3, 6],
  },
  {
    img: "https://placehold.co/140x140",
    name: "엄경례",
    address: "서울특별시 강남구",
    jobs: [1, 4, 6, 2],
    badges: [3, 6],
  },
  {
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

  const [tempNumber, setTempNubmer] = useState(1);

  const goTop = () => {
    if (scrollRef.current) {
      scrollRef.current.scrollTo({
        top: 0,
        behavior: "smooth", // 부드럽게 스크롤링
      });
    }
  };
  // 2. Intersection Observer (아래 끝 체크용)
  useEffect(() => {
    const observer = new IntersectionObserver(
      ([entry]) => {
        if (entry.isIntersecting) {
          // 마지막 div가 보이면 다음 페이지 가져오면 됨
          setTempNubmer((prev) => prev + 1);
        }
      },
      { threshold: 0.1 }, // 10%만 보여도 감지
    );

    if (endRef.current) observer.observe(endRef.current);
    return () => observer.disconnect();
  }, []);
  // 3. 상태에 따른 마스크 스타일 결정
  const getMaskStyle = `transition-all duration-500 mask-[linear-gradient(to_bottom,transparent_0%,black_10%,black_80%,transparent_100%)]
                        [-webkit-mask-image:linear-gradient(to_bottom,transparent_0%,black_10%,black_80%,transparent_100%)]`;

  return (
    <>
      <SearchAgentFilter />
      <div>
        <div
          ref={scrollRef}
          className={`grid grid-cols-3 mt-9 gap-4 overflow-auto scrollbar-hide ${getMaskStyle}`}
          style={{ height: "calc(100vh - 340px)" }}
        >
          {dummyData.slice(0, 18 * tempNumber).map((agent, index) => (
            <SearchAgentCard key={index} agent={agent} />
          ))}
          <div ref={endRef} className="w-3 h-1 -mt-10" />
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
