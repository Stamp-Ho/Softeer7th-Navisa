import { Link } from "react-router-dom";
import { IcGraduation, IcLocation } from "../../assets/icon/StratisUi";
import Tag from "../common/Tag";
import { useContext } from "react";
import { AuthContext } from "../../contexts/AuthContext";

const AgentCard = ({ hasAnimation = true, className = "" }) => {
  const agent = {
    id: 0,
    name: "엄경례",
    address: "서울특별시 강남구",
  };

  const context = useContext(AuthContext);
  if (!context) return null;
  const { userType } = context;

  const animationStyle = hasAnimation
    ? "transition-all duration-150 ease-out hover:scale-107 hover:m-2"
    : "";
  return (
    <li
      className={`${animationStyle} ${className}
        flex flex-col bg-white w-60 rounded-[10px] overflow-hidden shadow-[0px_0px_7px_0px_rgba(104,96,160,0.25)]`}
    >
      <img
        className="w-60 h-48"
        src="https://placehold.co/240x192"
        alt="https://placehold.co/240x192"
      />
      <div className="flex flex-col gap-3 pb-5 px-4">
        <Link to={`/profile/agent/${agent.id}`} className="title-m-bold pt-5">
          {agent.name} 행정사
        </Link>
        <div className="flex-col flex gap-1">
          <a className="flex flex-row items-center gap-1.5 caption-m-medium">
            <IcGraduation size={14} /> 전문 분야
          </a>
          {userType !== "NOT_AUTHED" ? (
            <ol></ol>
          ) : (
            <Tag type={"small_fill_gray"}>로그인 후 확인 가능합니다.</Tag>
          )}
        </div>
        <div className="flex-col flex gap-1">
          <a className="flex flex-row items-center gap-1.5 caption-m-medium">
            <IcLocation size={14} /> 사무실 위치
          </a>
          <a className="text-text-base body-m-medium">{agent.address}</a>
        </div>
      </div>
    </li>
  );
};

export default AgentCard;
