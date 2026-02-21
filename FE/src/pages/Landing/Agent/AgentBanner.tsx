import { Link } from "react-router-dom";
import { IcPencilLine } from "../../../assets/icon/StratisUi";
import NavisaLogo from "../../../assets/NavisaLogo";
import Button from "../../../components/common/Button";

const AgentBanner = () => {
  return (
    <div className="flex flex-col gap-9 my-50">
      <NavisaLogo height={12} />
      <div className="flex flex-col gap-3">
        <h2 className="banner-title">E-7비자를 위한 최적의 솔루션, Navisa와 함께해주세요.</h2>
        <p className="headline-m-medium text-gray-600">행정사님을 기다리는 외국인을 위해 내 요건을 등록해주세요.</p>

        <Link to="/onboard/agent" className="mt-15 mr-15">
          <Button variant="primary" size="large" className="px-5 gap-2 ml-auto mt-auto">
            내 요건 등록하러 가기
            <IcPencilLine color="white" />
          </Button>
        </Link>
      </div>
    </div>
  );
};

export default AgentBanner;
