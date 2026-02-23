import { useTranslation } from "react-i18next";
import { IcClock01, IcLocation, IcPhone } from "../../../assets/icon/StratisUi";

const AgentOffice = ({
  officeInfo,
}: {
  officeInfo?: {
    officeName: string;
    address: string;
    businessHours: string;
    phoneNumber: string;
  };
}) => {
  const { t } = useTranslation(["components"]);
  if (!officeInfo) return <></>;
  return (
    <>
      <div className="headline-m-semibold mb-3">{t("agentProfile.location")}</div>
      <div className="flex flex-row gap-9 items-center">
        <div className="flex flex-col py-5 pl-4 gap-9 text-text-base">
          <div className="headline-m-semibold">{officeInfo.officeName}</div>
          <ul className="flex flex-col gap-5 title-l-medium">
            <li className="flex flex-row gap-1 items-center">
              <IcLocation />
              <span>{officeInfo.address}</span>
            </li>
            <li className="flex flex-row gap-1 items-center">
              <IcClock01 />
              <span>{officeInfo.businessHours}</span>
            </li>
            <li className="flex flex-row gap-1 items-center">
              <IcPhone />
              <span>{officeInfo.businessHours}</span>
            </li>
          </ul>
        </div>
      </div>
    </>
  );
};

export default AgentOffice;
