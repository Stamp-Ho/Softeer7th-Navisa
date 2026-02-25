import { useTranslation } from "react-i18next";
import { IcLuggage04 } from "../../../assets/icon/StratisUi";
import Button from "../../../components/common/Button";
import Tag from "../../../components/common/Tag";
import { calcDDay } from "../../../utils/CalcDDay";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../../../contexts/AuthContextProvider";

const MyExpectedCompany = ({
  targetJob = "웹 개발자",
  companyName = "대박쩌는 IT회사",
  startDate = "2026. 01. 31",
  nickname,
}: {
  targetJob?: string;
  companyName?: string;
  startDate?: string;
  nickname?: string;
}) => {
  const { t } = useTranslation(["pages"]);
  const { logOut } = useAuth();
  const navigate = useNavigate();

  const handleEditMyProfile = () => {
    navigate("/onboard/foreigner");
  };

  if (!nickname) {
    return (
      <div>
        <div className="shadow flex flex-col w-92 px-5 py-8 border border-border-normal rounded-radius-400 bg-white">
          <div className="flex flex-col gap-6">
            <span className="text-text-base title-l-medium">{t("profile.noRequirementsRegistered")}</span>
            <div className="py-px w-full bg-border-light" />
            <Button variant="primary" size="large" className="w-full" onClick={handleEditMyProfile}>
              {t("profile.registerMyRequirements")}
            </Button>
            <Button variant="lightGray" size="large" className="w-full " onClick={logOut}>
              {t("profile.logout")}
            </Button>
          </div>
        </div>
      </div>
    );
  }
  return (
    <>
      <div className="">
        <div className="shadow flex flex-col w-92 px-5 py-8 border border-border-normal rounded-radius-400 bg-white">
          <div className="headline-l-bold text-text-base">{nickname}</div>
          <div className="py-px w-full bg-border-light my-7" />
          <div className="flex flex-row gap-2 items-center title-m-semibold text-text-base">
            <IcLuggage04 />
            <span>{t("profile.expectedJoinInfo")}</span>
          </div>
          <ul className="flex flex-col gap-3 mt-7">
            <li className="flex flex-row gap-4 items-center">
              <Tag variant="large_gray_off" className="w-22">
                {t("profile.jobLabel")}
              </Tag>
              <span className="text-text-base title-s-medium">{targetJob}</span>
            </li>
            <li className="flex flex-row gap-4 items-center">
              <Tag variant="large_gray_off" className="w-22">
                {t("profile.companyNameLabel")}
              </Tag>
              <span className="text-text-base title-s-medium">{companyName}</span>
            </li>
            <li className="flex flex-row gap-4 items-center">
              <Tag variant="large_gray_off" className="w-22">
                {t("profile.joinDateLabel")}
              </Tag>
              <span className="text-text-base title-s-medium">{startDate}</span>
              <span className="body-l-medium text-text-sub">{calcDDay(startDate)}</span>
            </li>
          </ul>
          <div className="flex flex-col items-end gap-6 mt-4.25">
            <Button variant="primary" size="large" className="w-full" onClick={handleEditMyProfile}>
              {t("profile.editMyProfile")}
            </Button>
            <Button variant="lightGray" size="large" className="w-full " onClick={logOut}>
              {t("profile.logout")}
            </Button>
          </div>
        </div>
      </div>
    </>
  );
};

export default MyExpectedCompany;
