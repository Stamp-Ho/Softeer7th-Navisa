import { Link } from "react-router-dom";
import {
  RPDocument,
  RPMessage,
  RPPeople,
} from "../../../assets/icon/RecommendPanelIcon";
import { IcPencilLine } from "../../../assets/icon/StratisUi";
import Button from "../../../components/common/Button";

const RecommendPanel = () => {
  return (
    <section className="flex flex-row mt-7 p-10 border-[1.5px] border-violet-200 rounded-2xl">
      {recommendations.map((rec) => (
        <div className="flex flex-col w-85 gap-4">
          <rec.icon />
          {rec.message}
        </div>
      ))}
      <Link to="onboard/foreigner" className="ml-auto mt-auto">
        <Button type="primary" size="large" className="w-58 gap-2">
          내 요건 등록하러가기
          <IcPencilLine color="white" />
        </Button>
      </Link>
    </section>
  );
};

export default RecommendPanel;
const recommendations = [
  {
    icon: RPMessage,
    message: (
      <>
        언제든 부담없이
        <br />
        행정사에게 상담 메시지로 질문해요.
      </>
    ),
  },
  {
    icon: RPPeople,
    message: (
      <>
        혼자 고민하지 말고
        <br />
        행정사와 함께 비자 신청서를 작성해요.
      </>
    ),
  },
  {
    icon: RPDocument,
    message: (
      <>
        제출 전까지 꼼꼼하게
        <br />
        행정사의 피드백으로 서류 완성도를 높여요.
      </>
    ),
  },
];
