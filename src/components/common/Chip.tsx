import { useTranslation } from "react-i18next";
import { IcArrows, IcPencilCross, IcPencilLine } from "../../assets/icon/StratisUi";

interface ChipProps {
  as?: React.ElementType; // 사용할 HTML 태그 (기본값: div)
  type?: string;
  className?: string;
}
const Chip = ({ as: Component = "div", type = "default", className = "" }: ChipProps) => {
  const { t } = useTranslation(["components"]);
  let style = "flex flex-row justify-center items-center cursor-pointer ";
  switch (type) {
    case "chips_square_request":
      style += "gap-1 px-6 py-3 rounded-[6px] bg-violet-25 outline outline-1 outline-violet-50 text-violet-500";
      return (
        <Component className={`${style}  ${className}`}>
          <IcPencilLine color="var(--violet-500)" size="18" />
          {t("chip.retainerProposal")}
        </Component>
      );

    case "chips_square_cancel":
      style += "gap-1 px-6 py-3 rounded-[6px] bg-background-sub text-text-base";
      return (
        <Component className={`${style}  ${className}`}>
          <IcPencilCross color="var(--text-base)" size="18" />
          {t("chip.cancelRetainer")}
        </Component>
      );

    case "chips_square_form_view":
      style += "gap-[2px] pl-4 pr-2 py-2 rounded-[20px] bg-violet-25 border border-violet-50 text-violet-500";
      return (
        <Component className={`${style}  ${className}`}>
          {t("chip.visaApplicationLink")}
          <div className="flex items-center -rotate-90">
            <IcArrows stroke="var(--violet-500)" size={20} />
          </div>
        </Component>
      );

    case "chips_square_review":
      style += "gap-[2px] pl-4 pr-2 py-2 text-violet-500";
      return (
        <Component className={`${style}  ${className}`}>
          {t("chip.writeReview")}
          <div className="flex items-center -rotate-90">
            <IcArrows stroke="var(--violet-500)" size={20} />
          </div>
        </Component>
      );

    case "chips_square_chatroom":
      style += "gap-[2px] pl-4 pr-2 py-2 text-text-base caption-l-semibold";
      return (
        <Component className={`${style}  ${className}`}>
          {t("chip.messageLink")}
          <div className="flex items-center -rotate-90">
            <IcArrows stroke="var(--text-base)" size={20} />
          </div>
        </Component>
      );
  }
};

export default Chip;
