import { useState } from "react";
import Button from "../../components/common/Button";
import Modal from "../../components/common/Modal";
import BadgeReviewSection from "./BadgeReviewSection";

type BadgeReviewModalParams = {
  reviewHandler: (num: number) => void;
};

const BadgeReviewModal = ({ reviewHandler }: BadgeReviewModalParams) => {
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
        <div className="mb-2 title-l-semibold text-text-base">
          어떤 점이 좋았나요?
        </div>
        <div className="mb-8 body-s-medium text-text-base">
          행정사와의 상담 과정에 어울리는 키워드를 골라주세요. (1~3개)
        </div>
        <div className="flex flex-col gap-7 mb-8">
          <section className="flex flex-col">
            <BadgeReviewSection
              title="전문성/문제해결"
              badgeArray={[6, 1, 2, 3, 4, 5]}
              selectedBadges={selectedBadges}
              onToggleBadges={toggleBadge}
            />
          </section>
          <section className="flex flex-col">
            <BadgeReviewSection
              title="진행 속도/기타"
              badgeArray={[0, 7, 8, 9, 10]}
              selectedBadges={selectedBadges}
              onToggleBadges={toggleBadge}
            />
          </section>
          <section className="flex flex-col">
            <BadgeReviewSection
              title="소통/상담 경험"
              badgeArray={[11, 12, 13, 14]}
              selectedBadges={selectedBadges}
              onToggleBadges={toggleBadge}
            />
          </section>
        </div>
        <Button
          type="primary"
          size="large"
          className="w-full mb-[39px]"
          disabled={selectedBadges.length === 0}
          onClick={() => reviewHandler(0)}
        >
          다음
        </Button>
      </div>
    </Modal>
  );
};

export default BadgeReviewModal;
