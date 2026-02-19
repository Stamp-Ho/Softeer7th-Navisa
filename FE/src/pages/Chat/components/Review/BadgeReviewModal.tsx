import { useState } from "react";
import Button from "../../../../components/common/Button";
import Modal from "../../../../components/common/Modal";
import { useTranslation } from "react-i18next";
import BadgeReviewSection from "./BadgeReviewSection";

type BadgeReviewModalParams = {
  reviewHandler: (num: number) => void;
};

const BadgeReviewModal = ({ reviewHandler }: BadgeReviewModalParams) => {
  const { t } = useTranslation(["components"]);
  const [selectedBadges, setSelectedBadges] = useState<number[]>([]);
  const MAX_SELECT = 3;

  const toggleBadge = (id: number) => {
    setSelectedBadges((prev) => {
      if (prev.includes(id)) {
        return prev.filter((badgeId) => badgeId !== id);
      }

      if (prev.length < MAX_SELECT) {
        return [...prev, id];
      }

      return prev;
    });
  };

  return (
    <Modal onClose={() => reviewHandler(0)}>
      <div className="px-5 pt-1">
        <div className="mb-2 title-l-semibold text-text-base">{t("review.badgeTitle")}</div>
        <div className="mb-8 body-s-medium text-text-base">{t("review.badgeDescription")}</div>
        <div className="flex flex-col gap-7 mb-8">
          <section className="flex flex-col">
            <BadgeReviewSection title={t("review.professionalism")} badgeArray={[6, 1, 2, 3, 4, 5]} selectedBadges={selectedBadges} onToggleBadges={toggleBadge} />
          </section>
          <section className="flex flex-col">
            <BadgeReviewSection title={t("review.speed")} badgeArray={[0, 7, 8, 9, 10]} selectedBadges={selectedBadges} onToggleBadges={toggleBadge} />
          </section>
          <section className="flex flex-col">
            <BadgeReviewSection title={t("review.communication")} badgeArray={[11, 12, 13, 14]} selectedBadges={selectedBadges} onToggleBadges={toggleBadge} />
          </section>
        </div>
        <Button variant="primary" size="large" className="w-full mb-[39px]" disabled={selectedBadges.length === 0} onClick={() => reviewHandler(0)}>
          {t("review.next")}
        </Button>
      </div>
    </Modal>
  );
};

export default BadgeReviewModal;
