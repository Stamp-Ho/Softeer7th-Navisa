import { useContext, useState } from "react";
import { useTranslation } from "react-i18next";
import NavisaLogo from "../../assets/NavisaLogo";
import LanguageSelector from "../common/LanguageSelector";
import { IcFile, IcMessage, IcUserProfile } from "../../assets/icon/StratisUi";
import LoginModal from "./LoginModal";
import SignUpModal from "./SignUpModal";
import { Link, useLocation } from "react-router-dom";
import { AuthContext } from "../../contexts/AuthContext";

const PathNamesWithBackground = ["/profile"];
const NavigationHeader = () => {
  const { t } = useTranslation(["common"]);
  const location = useLocation();
  const currentPath = location.pathname;
  const isSpecialBackground = currentPath === "/" || PathNamesWithBackground.some((path) => currentPath.startsWith(path));

  const hasScroll = currentPath === "/" || currentPath.startsWith("/profile");

  const [authMode, setAuthMode] = useState<number>(0); // 0:none, 1:log in, 2:sign in

  // Context 전역 상태 호출
  const context = useContext(AuthContext);
  if (!context) return null;

  const { userType } = context; //, setUserType } = context;

  const homeTabStyle = isSpecialBackground ? "text-gray-0" : currentPath === "/" ? "text-text-base" : "text-text-sub";
  const searchTabStyle = isSpecialBackground ? "text-gray-0" : currentPath.startsWith("/search") ? "text-text-base" : "text-text-sub";

  const searchLabel = userType === "VALID_AGENT" ? t("navigation.searchForeigner") : t("navigation.searchAgent");

  const isUserCanAccessDoc = ["VALID_AGENT", "FILLED_FOREIGNER"].includes(userType);
  return (
    <header className={`flex flex-row h-12 justify-between items-center m-4 ml-0 ${hasScroll && "ml-1 mr-3"}`}>
      {authMode === 1 ? <LoginModal onClose={() => setAuthMode(0)} setAuthMode={setAuthMode} /> : authMode === 2 ? <SignUpModal onClose={() => setAuthMode(0)} /> : <></>}
      <div className="flex flex-row items-center gap-32">
        <NavisaLogo whiteMode={isSpecialBackground} />
        <div className={`flex flex-row items-center title-s-bold gap-18`}>
          <Link className={`cursor-pointer ${homeTabStyle}`} to="/">
            {t("navigation.home")}
          </Link>
          <Link className={`cursor-pointer ${searchTabStyle}`} to={`/search/${userType === "VALID_AGENT" ? "foreigner" : "agent"}`}>
            {searchLabel}
          </Link>
        </div>
      </div>

      <div className="flex flex-row gap-6 h-12">
        {userType !== "NOT_AUTHED" ? (
          <div className="flex flex-row items-center">
            <Link to="/chat" className="flex flex-row items-center gap-2.25 mr-spacing-700 body-l-semibold text-text-base cursor-pointer">
              <IcMessage />
              {t("navigation.chatMessage")}
            </Link>
            <Link
              to={userType === "VALID_AGENT" ? "/documents" : "/document"}
              onClick={(e) => {
                if (!isUserCanAccessDoc) e.preventDefault();
              }}
              className={`flex flex-row items-center gap-2.25 mr-spacing-1100 body-l-semibold text-text-base
                ${isUserCanAccessDoc ? " cursor-pointer" : " opacity-50 cursor-not-allowed"}`}
            >
              <IcFile />
              {t("navigation.documentWrite")}
            </Link>
            <Link to={`/profile`} className="cursor-pointer">
              <IcUserProfile />
            </Link>
          </div>
        ) : (
          <div className="flex flex-row items-center body-l-semibold text-gray-800 gap-5 ">
            <a className="cursor-pointer" onClick={() => setAuthMode(1)}>
              {t("button.login")}
            </a>
            <a className="cursor-pointer" onClick={() => setAuthMode(2)}>
              {t("button.signup")}
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
