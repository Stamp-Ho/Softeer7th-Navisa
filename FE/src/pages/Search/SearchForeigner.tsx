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

  const [tempNumber, setTempNumber] = useState(1);

  const goTop = () => {
    if (scrollRef.current) {
      scrollRef.current.scrollTo({ top: 0, behavior: "smooth" });
    }
  };

  useEffect(() => {
    const observer = new IntersectionObserver(
      ([entry]) => {
        if (entry.isIntersecting) {
          setTempNumber((prev) => prev + 1);
        }
      },
      { threshold: 0.1 },
    );

    if (endRef.current) observer.observe(endRef.current);
    return () => observer.disconnect();
  }, []);

  const getMaskStyle = `transition-all duration-500 mask-[linear-gradient(to_bottom,transparent_0%,black_10%,black_80%,transparent_100%)]
                        [-webkit-mask-image:linear-gradient(to_bottom,transparent_0%,black_10%,black_80%,transparent_100%)]`;

  return (
    <>
      <SearchForeignerFilter />
      <div>
        <div
          ref={scrollRef}
          className={`grid grid-cols-4 mt-9 gap-4 overflow-auto scrollbar-hide ${getMaskStyle}`}
          style={{ height: "calc(100vh - 340px)" }}
        >
          {dummyData.slice(0, 20 * tempNumber).map((foreigner, index) => (
            <SearchForeignerCard key={index} foreigner={foreigner} />
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

export default SearchForeigner;
