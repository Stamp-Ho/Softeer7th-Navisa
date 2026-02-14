import FlagIcon from "../../assets/FlagIcon";
import { IcLanguage, IcLuggage04 } from "../../assets/icon/StratisUi";
import Tag from "../common/Tag";
import { languageList } from "../../constants/language";
import { Link } from "react-router-dom";
import type { ForeignerCardResponse } from "../../api/types/foreigner";

const RecommendedForeignerCard = ({
  foreigner = {
    foreignerId: "abce46f",
    nickname: "닉 주디 엘리자베스 마야",
    nationIdList: [0, 1, 2, 3, 4, 5],
    languageIdList: [0, 1, 2, 3, 4, 5],
    jobTitle: "웹 개발자",
  },
}: {
  foreigner?: ForeignerCardResponse;
}) => {
  // const authed = true;
  return (
    <div
      className={`flex flex-col py-7 px-6 w-92 h-fit bg-gray-30 rounded-2xl`}
    >
      <ul className="flex flex-row gap-2 items-center">
        {foreigner.nationIdList.slice(0, 5).map((nation) => (
          <FlagIcon
            key={`flag_${nation}`}
            nationIndex={nation}
            className="w-6 h-6"
          />
        ))}
      </ul>
      <Link
        to={`/profile/foreigner/${foreigner.foreignerId}`}
        className="mt-4 title-m-bold text-text-base"
      >
        {foreigner.nickname}
      </Link>
      <div className="flex flex-row gap-4 mt-5 items-end">
        <div className="flex flex-col gap-3">
          <div className="flex flex-col gap-2">
            <div className="flex flex-row gap-1 items-center w-36 caption-m-medium text-text-base">
              <IcLuggage04 size={14} />
              입사 예정 직무
            </div>
            <Tag type="small_fill_gray_2" className="rounded-sm w-fit">
              {foreigner.jobTitle}
            </Tag>
          </div>
        </div>
        <div className="flex flex-col w-36 gap-2">
          <div className="flex flex-row gap-1 items-center caption-m-medium text-text-base">
            <IcLanguage size={14} />
            사용 언어
          </div>
          <div className="flex flex-row gap-1">
            {foreigner.languageIdList.slice(0, 2).map((lang, idx) => (
              <Tag key={idx} type="small_fill_green_max" className="w-fit">
                {languageList[lang]}
              </Tag>
            ))}
            {foreigner.languageIdList.length > 2 && (
              <Tag type="small_fill_gray">
                +{foreigner.languageIdList.length - 2}
              </Tag>
            )}
          </div>
        </div>
      </div>
    </div>
  );
};

export default RecommendedForeignerCard;
