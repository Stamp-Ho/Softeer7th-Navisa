import { useState } from "react";
import NavisaLogo from "../../assets/NavisaLogo";
import LanguageSelector from "../common/LanguageSelector";
import { IcFile, IcMessage, IcUserProfile } from "../../assets/icon/StratisUi";
import LoginModal from "./LoginModal";
import SignUpModal from "./SignUpModal";
import { Link, useLocation } from "react-router-dom";

const PathNamesWithBackground = ["/profile"];
const NavigationHeader = () => {
  const location = useLocation();
  const currentPath = location.pathname;
  const isSpecialBackground =
    currentPath === "/" ||
    PathNamesWithBackground.some((path) => currentPath.startsWith(path));

  const isMain = currentPath === "/";

  const [isAgent, _setIsAgent] = useState<boolean>(false);

  const [authMode, setAuthMode] = useState<number>(0); // 0:none, 1:log in, 2:sign in
  const [isAuthed, setIsAuthed] = useState<boolean>(false);

  const homeTabStyle = isSpecialBackground
    ? "text-gray-0"
    : currentPath === "/"
      ? "text-text-base"
      : "text-text-sub";
  const searchTabStyle = isSpecialBackground
    ? "text-gray-0"
    : currentPath.startsWith("/search")
      ? "text-text-base"
      : "text-text-sub";

  return (
    <header
      className={`flex flex-row h-12 justify-between items-center m-4 ml-0 ${isMain && "ml-1 mr-3"}`}
    >
      {authMode === 1 ? (
        <LoginModal onClose={() => setAuthMode(0)} setAuthMode={setAuthMode} />
      ) : authMode === 2 ? (
        <SignUpModal onClose={() => setAuthMode(0)} />
      ) : (
        <></>
      )}
      <div className="flex flex-row items-center gap-32">
        <NavisaLogo whiteMode={isSpecialBackground} />
        <div className={`flex flex-row items-center title-s-bold gap-18`}>
          <Link className={`cursor-pointer ${homeTabStyle}`} to="/">
            홈
          </Link>
          <Link
            className={`cursor-pointer ${searchTabStyle}`}
            to={`/search/${isAgent ? "foreigner" : "agent"}`}
          >
            행정사 탐색
          </Link>
        </div>
      </div>
      <div
        className="flex flex-row gap-6 h-12"
        onClick={() => setIsAuthed((prev) => !prev)}
      >
        {isAuthed ? (
          <div className="flex flex-row items-center">
            <div className="flex flex-row items-center gap-2.25 mr-spacing-700 body-l-semibold text-text-base cursor-pointer">
              <IcMessage />
              상담 메세지
            </div>
            <div className="flex flex-row items-center gap-2.25 mr-spacing-1100 body-l-semibold text-text-base cursor-pointer">
              <IcFile />
              비자서류 작성
            </div>
            <a href="" className="cursor-pointer">
              <IcUserProfile />
            </a>
          </div>
        ) : (
          <div className="flex flex-row items-center body-l-semibold text-gray-800 gap-5 ">
            <a className="cursor-pointer" onClick={() => setAuthMode(1)}>
              로그인
            </a>
            <a className="cursor-pointer" onClick={() => setAuthMode(2)}>
              회원가입
            </a>
          </div>
        )}

        <div className="border-r border-gray-200 h-7 w-px mt-auto mb-auto"></div>
        <LanguageSelector />
      </div>
    </header>
  );
};
export default NavigationHeader;
