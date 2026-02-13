import FlagIcon from "../../assets/FlagIcon";
import NavisaLogo from "../../assets/NavisaLogo";

const Footer = () => {
  const currentYear = new Date().getFullYear();

  return (
    <footer className="w-480 -ml-50 mt-25 bg-slate-800 text-slate-300 pt-12 pb-8 px-6">
      <div className="max-w-7xl mx-auto">
        {/* 상단: 서비스 로고 및 국가 퀵 링크 */}
        <div className="flex flex-col lg:flex-row justify-between mb-6 border-b border-gray-800 pb-10 gap-10">
          <div className="max-w-sm">
            <NavisaLogo whiteMode={true} />
            <h2 className="text-2xl font-bold text-white mt-1 mb-4 tracking-tight">
              내비자
            </h2>
            <p className="text-sm leading-relaxed">
              복잡한 E-7 비자 발급, <br />
              <strong>Navisa</strong>가 당신에게 꼭 맞는 행정사를 추천해줍니다
            </p>
          </div>

          <div className="grid grid-cols-2 sm:grid-cols-3 gap-8 text-sm">
            <div>
              <h4 className="text-white font-bold mb-4">비자 서비스</h4>
              <ul className="space-y-2">
                <li className="hover:text-white cursor-pointer transition">
                  E-7비자란?
                </li>
                <li className="hover:text-white cursor-pointer transition">
                  비자 발급 절차
                </li>
                <li className="hover:text-white cursor-pointer transition">
                  전자 비자(e-Visa)
                </li>
              </ul>
            </div>
            <div>
              <h4 className="text-white font-bold mb-4">행정사</h4>
              <ul className="space-y-2">
                <li className="hover:text-white cursor-pointer transition">
                  행정사 약관
                </li>
                <li className="hover:text-white cursor-pointer transition">
                  행정사 인증 문의
                </li>
                <li className="hover:text-white cursor-pointer transition">
                  우리의 행정사
                </li>
              </ul>
            </div>
            <div>
              <h4 className="text-white font-bold mb-4">고객지원</h4>
              <ul className="space-y-2">
                <li className="hover:text-white cursor-pointer transition">
                  자주 묻는 질문
                </li>
                <li className="hover:text-white cursor-pointer transition">
                  대사관 안내
                </li>
                <li className="hover:text-white cursor-pointer transition">
                  1:1 문의
                </li>
              </ul>
            </div>
          </div>
        </div>

        {/* 하단: 저작권 및 글로벌 네트워크 (FlagIcon 활용) */}
        <div className="flex flex-col md:flex-row justify-between items-center gap-6">
          <div className="text-xs space-y-1">
            <p>© {currentYear} Navisa Global Inc. All rights reserved.</p>
            <div className="flex gap-4">
              <span className="hover:underline cursor-pointer">이용약관</span>
              <span className="hover:underline cursor-pointer text-white font-medium">
                개인정보처리방침
              </span>
            </div>
          </div>

          {/* 주요 서비스 국가 아이콘 노출 */}
          <div className="flex items-center gap-2 bg-gray-800/50 px-4 py-2 rounded-full border border-gray-700">
            <span className="text-[10px] uppercase font-bold tracking-widest mr-2">
              Top Destinations
            </span>
            <div className="flex -space-x-2">
              {[25, 7, 61, 22].map((idx) => (
                <div
                  key={idx}
                  className="w-6 h-6 rounded-full border-2 border-[#111827] overflow-hidden"
                >
                  <FlagIcon
                    nationIndex={idx}
                    className="w-full h-full object-cover shadow-sm"
                  />
                </div>
              ))}
            </div>
            <span className="text-xs ml-2 text-gray-300">+60 Countries</span>
          </div>
        </div>
      </div>
    </footer>
  );
};

export default Footer;
