import NavisaLogo from "../../../assets/NavisaLogo";

const ForeignerBanner = () => {
  return (
    <div className="flex flex-col gap-9 my-50">
      <NavisaLogo height={12} />
      <div className="flex flex-col gap-3">
        <a className="banner-title">나를 위한 E-7 비자 전문가를 찾아보세요.</a>
        <a className="headline-m-medium text-gray-600">
          학력·경력 등 상세 요건을 입력하면, 해당 분야 경험이 있는 행정사를
          확인할 수 있어요.
        </a>
      </div>
    </div>
  );
};

export default ForeignerBanner;
