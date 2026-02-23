import { useState } from "react";
import { useTranslation } from "react-i18next";
import NavisaLogo from "../../assets/NavisaLogo";
import LanguageSelector from "../common/LanguageSelector";
import { IcFile, IcMessage, IcUserProfile } from "../../assets/icon/StratisUi";
import LoginModal from "./LoginModal";
import SignUpModal from "./SignUpModal";
import { useChatUnreadCount } from "../../api/queries/useChatUnreadCountQuery";
import { Link, useLocation, useNavigate } from "react-router-dom";
import { useAuth } from "../../contexts/AuthContextProvider";
import { alertT } from "../../i18n/alerts";

const PathNamesWithBackground = ["/profile"];
const NavigationHeader = () => {
  const { t } = useTranslation(["common"]);
  const navigate = useNavigate();
  const location = useLocation();
  const currentPath = location.pathname;
  const isSpecialBackground =
    currentPath === "/" || PathNamesWithBackground.some((path) => currentPath.startsWith(path));

  const hasScroll = currentPath === "/" || currentPath.startsWith("/profile");

  const [authMode, setAuthMode] = useState<number>(0); // 0:none, 1:log in, 2:sign in
  const { data: unreadCountData } = useChatUnreadCount();
  const unreadCount = unreadCountData?.count ?? 0;

  const { userType } = useAuth();

  const homeTabStyle = isSpecialBackground ? "text-gray-0" : currentPath === "/" ? "text-text-base" : "text-text-sub";
  const searchTabStyle = isSpecialBackground
    ? "text-gray-0"
    : currentPath.startsWith("/search")
      ? "text-text-base"
      : "text-text-sub";

  const searchLabel = userType === "VALID_AGENT" ? t("navigation.searchForeigner") : t("navigation.searchAgent");

  const isUserCanAccessDoc = ["VALID_AGENT", "FILLED_FOREIGNER"].includes(userType);

  const handleToSearch = () => {
    if (userType === "NOT_AUTHED") {
      alertT("pages.landing.loginRequired");
    } else {
      navigate(`/search/${userType === "VALID_AGENT" ? "foreigner" : "agent"}`);
    }
  };
  return (
    <header className={`flex flex-row h-12 justify-between items-center m-4 ml-0 ${hasScroll && "ml-1 mr-3"}`}>
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
          <Link
            className={`cursor-pointer ${homeTabStyle}`}
            to="/"
            inert={currentPath === "/" ? true : undefined}
            tabIndex={0}
          >
            {t("navigation.home")}
          </Link>
          <a className={`cursor-pointer ${searchTabStyle}`} onClick={handleToSearch} tabIndex={0}>
            {searchLabel}
          </a>
        </div>
      </div>

      <div className="flex flex-row gap-6 h-12">
        {userType && userType !== "NOT_AUTHED" ? (
          <div className="relative flex flex-row items-center">
            <Link
              to="/chat"
              tabIndex={0}
              className="flex flex-row items-center gap-2.25 mr-spacing-700 body-l-semibold text-text-base cursor-pointer"
            >
              <IcMessage />
              {t("navigation.chatMessage")}
            </Link>
            <Link
              to={userType === "VALID_AGENT" ? "/documents" : "/document"}
              tabIndex={0}
              onClick={(e) => {
                if (!isUserCanAccessDoc) e.preventDefault();
              }}
              className={`flex flex-row items-center gap-2.25 mr-spacing-1100 body-l-semibold text-text-base
                ${isUserCanAccessDoc ? " cursor-pointer" : " opacity-50 cursor-not-allowed"}`}
            >
              <IcFile />
              {t("navigation.documentWrite")}
            </Link>
            <Link to={`/profile`} className="cursor-pointer" tabIndex={0} inert={currentPath === "/profile"}>
              <IcUserProfile />
            </Link>
            {unreadCount > 0 && (
              <div
                className="absolute w-3 h-3 bg-red-600 rounded-full left-6 top-4 pointer-events-none"
                aria-label={`읽지 않은 메시지 ${unreadCount}개`}
                role="status"
              />
            )}
          </div>
        ) : (
          <div className="flex flex-row items-center body-l-semibold text-gray-800 gap-5 ">
            <a className="cursor-pointer" onClick={() => setAuthMode(1)} tabIndex={0}>
              {t("button.login")}
            </a>
            <a className="cursor-pointer" onClick={() => setAuthMode(2)} tabIndex={0}>
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
