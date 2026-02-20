import { useContext } from "react";
import { useTranslation } from "react-i18next";
import { IcArrows } from "../../../../assets/icon/StratisUi";
import { AuthContext } from "../../../../contexts/AuthContext";
import { Link } from "react-router-dom";

type NoChatViewParams = {
  isFileReady: boolean;
};

const NoChatView = ({ isFileReady }: NoChatViewParams) => {
  const { t } = useTranslation(["pages"]);
  const context = useContext(AuthContext);
  if (!context) return null;
  const { userType } = context;
  const isAgent = userType === "VALID_AGENT";

  return (
    <div className="flex flex-col mt-12 h-[904px]">
      <div className="headline-m-bold text-gray-1000 mb-13">{t("chat.title")}</div>
      <div className="flex flex-col gap-4 justify-center items-center h-full">
        <div className="headline-s-medium text-gray-500">{t("chat.noChat")}</div>
        <Link
          to={isFileReady ? `/search/${isAgent ? "foreigner" : "agent"}` : "/onboard"}
          className="flex flex-row items-center pl-5 title-m-semibold text-violet-500 cursor-pointer"
        >
          {isFileReady ? (isAgent ? t("chat.searchForeigner") : t("chat.searchAgent")) : t("chat.registerRequirement")}
          <span className="-rotate-90">
            <IcArrows stroke="var(--violet-500)" size={32} />
          </span>
        </Link>
      </div>
    </div>
  );
};

export default NoChatView;
