import { IcGraduation } from "../../../assets/icon/StratisUi";
import Tag from "../../../components/common/Tag";
import ProfileItemsFrame from "../../../components/common/ProfileItemsFrame";
import { jobCodeList } from "../../../types/job";

type AgentSpecialtiesList = {
  specialties: number[];
};

const AgentSpecialties = ({ specialties }: AgentSpecialtiesList) => {
  return (
    <ProfileItemsFrame>
      <div className="flex flex-row gap-2 title-m-semibold text-text-base">
        <IcGraduation size={24} />
        <span>전문 분야</span>
        <span className="text-violet-500">{specialties.length}</span>
      </div>
      <ul className="flex flex-row gap-2 flex-wrap">
        {specialties.map((specialty, idx) => (
          <li key={idx}>
            <Tag type="large_violet_off">{jobCodeList[specialty]}</Tag>
          </li>
        ))}
      </ul>
    </ProfileItemsFrame>
  );
};

export default AgentSpecialties;
