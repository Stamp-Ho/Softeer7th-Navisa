import { useState } from "react";
import Button from "../../../../components/common/Button";
import Modal from "../../../../components/common/Modal";
import Radio from "../../../../components/common/Radio";
import Tag from "../../../../components/common/Tag";
import { useTranslation } from "react-i18next";

type ServiceReviewModalParams = {
  reviewHandler: (num: number) => void;
};

const ServiceReviewModal = ({ reviewHandler }: ServiceReviewModalParams) => {
  const { t } = useTranslation(["components"]);
  const [reviewText, setReviewText] = useState<string>("");
  const MAX_LENGTH = 1000;

  return (
    <Modal onClose={() => reviewHandler(0)}>
      <div className="flex flex-col px-5 pt-4">
        <div className="flex flex-row items-center gap-3 mb-6 title-l-semibold text-text-base">
          <Tag variant="small_fill">{t("review.serviceTitle")}</Tag>
          {t("review.visaResult")}
        </div>
        <Radio options={[t("review.visaApproved"), t("review.visaRejected")]} className="mb-9" />
        <div className="title-l-semibold text-text-base">{t("review.serviceDescription")}</div>
        <div className="mt-5 bg-gray-50 rounded-lg p-4 h-86.5 flex flex-col">
          <textarea
            className="w-full flex-1 resize-none outline-none placeholder:text-text-sub"
            placeholder={t("review.servicePlaceholder")}
            value={reviewText}
            onChange={(e) => {
              const value = e.target.value;

              // 1,000자 제한
              if (value.length <= MAX_LENGTH) {
                setReviewText(value);
              }
            }}
            maxLength={MAX_LENGTH}
          />

          {/* 글자 수 카운터 */}
          <div className="text-right body-s-semibold text-text-sub mt-2">
            {reviewText.length}/{MAX_LENGTH}자
          </div>
        </div>
        <Button variant="primary" size="large" className="w-full mt-10 mb-9.75" disabled={false} onClick={() => reviewHandler(0)}>
          {t("review.next")}
        </Button>{" "}
      </div>
    </Modal>
  );
};

export default ServiceReviewModal;
