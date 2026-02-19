import { useTranslation } from "react-i18next";
import { IcClock01, IcLocation, IcPhone } from "../../../assets/icon/StratisUi";

const AgentOffice = ({
  officeInfo = {
    officeName: "엄경례 행정사사무소",
    address: "서울 강남구 테헤란로 116, 10층 1011호",
    businessHours: "평일 AM 10:00~PM 6:00",
    phoneNumber: "010-1234-5678",
  },
}: {
  officeInfo?: {
    officeName: string;
    address: string;
    businessHours: string;
    phoneNumber: string;
  };
}) => {
  const { t } = useTranslation(["components"]);
  return (
    <>
      <div className="headline-m-semibold mb-13">{t("agentProfile.location")}</div>
      <div className="flex flex-row gap-9 items-center">
        <div className="w-121.5 h-70 flex items-center overflow-hidden border border-border-normal rounded-[20px]">
          <img
            className="object-cover"
            src="https://placehold.co/1520x1144"
            alt={t("agentProfile.officeImage")}
          />
        </div>
        <div className="flex flex-col py-14 gap-9 text-text-base">
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
