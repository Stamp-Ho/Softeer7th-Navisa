import { IcClock01, IcLocation, IcPhone } from "../../../assets/icon/StratisUi";

type AgentOfficeProps = {
  officeInfo: AgentOfficeData;
};

type AgentOfficeData = {
  officeName: string;
  address: string;
  businessHours: string;
  phoneNumber: string;
};

const AgentOffice = ({ officeInfo }: AgentOfficeProps) => {
  return (
    <>
      <div className="headline-m-semibold mb-13">행정사 위치</div>
      <div className="flex flex-row gap-9 items-center">
        <div className="w-121.5 h-70 flex items-center overflow-hidden border border-border-normal rounded-[20px]">
          <img
            className="object-cover"
            src="https://placehold.co/1520x1144"
            alt="행정사 사무소 위치 이미지"
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
