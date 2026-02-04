import { useState } from "react";
import Button from "../../components/common/Button";
import Modal from "../../components/common/Modal";
import Radio from "../../components/common/Radio";
import Tag from "../../components/common/Tag";

type ServiceReviewModalParams = {
  reviewHandler: (num: number) => void;
};

const ServiceReviewModal = ({ reviewHandler }: ServiceReviewModalParams) => {
  const [reviewText, setReviewText] = useState<string>("");
  const MAX_LENGTH = 1000;

  return (
    <Modal onClose={() => reviewHandler(0)}>
      <div className="flex flex-col px-5 pt-4">
        <div className="flex flex-row items-center gap-3 mb-6 title-l-semibold text-text-base">
          <Tag type="small_fill">필수</Tag>
          비자 발급 결과를 선택해주세요.
        </div>
        <Radio options={["발급됨", "발급되지 않음"]} className="mb-9" />
        <div className="title-l-semibold text-text-base">
          비자 발급 과정에서 느낀 점을 남겨주세요.
        </div>
        <div className="mt-5 bg-gray-50 rounded-lg p-4 h-[346px] flex flex-col">
          <textarea
            className="w-full flex-1 resize-none outline-none placeholder:text-text-sub"
            placeholder="예) 설명이 이해하기 쉬웠어요. 진행 상황을 계속 안내해줘서 안심됐어요."
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
        <Button
          type="primary"
          size="large"
          className="w-full mt-10 mb-[39px]"
          disabled={false}
          onClick={() => reviewHandler(0)}
        >
          다음
        </Button>{" "}
      </div>
    </Modal>
  );
};

export default ServiceReviewModal;
