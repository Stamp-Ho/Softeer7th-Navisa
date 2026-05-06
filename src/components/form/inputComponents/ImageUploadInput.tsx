import { useEffect, useRef, useState } from "react";
import { useTranslation } from "react-i18next";
import { IcDot, IcPlus } from "../../../assets/icon/StratisUi";
import { useFormContext } from "react-hook-form";
import { useResizeImage } from "../../../hooks/useResizeImage";
import { alertT } from "../../../i18n/alerts";

const ImageUploadInput = ({
  placeholder = "",
  imageUrl,
  imageFile,
  setImageFile,
  readOnly = false,
}: {
  placeholder: string;
  isAgent?: boolean;
  imageUrl?: string;
  imageFile: File | undefined;
  setImageFile: React.Dispatch<React.SetStateAction<File | undefined>>;
  readOnly?: boolean;
}) => {
  const { t } = useTranslation(["components"]);
  const { resizeImage, imageSize, loadingImage } = useResizeImage();
  const fileInputRef = useRef<HTMLInputElement>(null);
  const { register, setValue, getValues } = useFormContext();
  const [imagePreview, setImagePreview] = useState("");

  const handleDivClick = () => {
    // 1. div 클릭 시 숨겨진 input을 클릭한 것처럼 동작
    fileInputRef.current?.click();
  };

  const handleFileChange = async (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (!file) return;

    const maxSize = 400 * 1024; // 400KB를 byte 단위로 계산
    if (file.size > maxSize) {
      alertT("components.imageUpload.fileSizeError");
      e.target.value = ""; // input 초기화 (같은 파일 다시 선택 가능하게)
      return;
    }

    const fileMimeType = file.type; // 1. 파일 객체에서 직접 MIME 타입 추출
    const allowedTypes = ["image/jpg", "image/jpeg", "image/png"]; // 2. 허용된 타입인지 확인 (Typescript의 타입 가드 역할)

    if (!allowedTypes.includes(fileMimeType)) {
      alertT("components.imageUpload.fileTypeError");
      return;
    }

    // 이미지 크기 검증 (2500px * 2500px)
    const img = new Image();
    const tempUrl = window.URL.createObjectURL(file);
    img.onload = () => {
      const MAX_WIDTH = 2500;
      const MAX_HEIGHT = 2500;

      if (img.width > MAX_WIDTH || img.height > MAX_HEIGHT) {
        alertT("components.imageUpload.imageSizeError");
        e.target.value = "";
        window.URL.revokeObjectURL(tempUrl);
        return;
      }

      // 검증 통과 시 이미지 설정
      if (imagePreview) URL.revokeObjectURL(imagePreview);
      setValue("0.sectionData.0.values.0", true);
      setImagePreview(tempUrl);
      setImageFile(file);
    };

    img.onerror = () => {
      alertT("components.imageUpload.fileTypeError");
      e.target.value = "";
      window.URL.revokeObjectURL(tempUrl);
    };

    img.src = tempUrl;
  };

  useEffect(() => {
    return () => {
      if (imagePreview) URL.revokeObjectURL(imagePreview);
    };
  }, [imagePreview]);
  useEffect(() => {
    register("0.sectionData.0.values.0");
    const currentValue = getValues("0.sectionData.0.values.0");
    if (currentValue === undefined) {
      setValue("0.sectionData.0.values.0", "");
    }
  }, []);
  useEffect(() => {
    (imageFile !== undefined || imageUrl) && resizeImage(imagePreview || imageUrl || "", 75, 105);
  }, [imagePreview, imageUrl]);
  return (
    <div className="grid-cols-3 flex flex-row gap-5">
      <div
        onClick={handleDivClick}
        className="rounded-xl flex items-center justify-center bg-white w-52.5 h-67.5 cursor-pointer hover:bg-gray-50 transition-colors overflow-hidden"
      >
        {/* 숨겨진 파일 인풋 */}
        <input
          type="file"
          ref={fileInputRef}
          onChange={handleFileChange}
          className="hidden"
          accept="image/png, image/jpeg, image/jpg"
          disabled={readOnly}
        />

        {(!loadingImage && imageFile !== undefined) || imageUrl ? (
          <div>
            <img
              src={imagePreview || imageUrl} // 업로드된 이미지 미리보기
              alt="profile"
              width={imageSize.width}
              height={imageSize.height}
              className="w-full h-full object-cover"
            />
          </div>
        ) : (
          <div>
            <IcPlus />
          </div>
        )}
      </div>
      <div className="flex flex-col justify-end h-67.5 title-s-medium text-gray-400">
        <h4 className="title-l-semibold text-text-base mb-2">{t("imageUpload.uploadTitle")}</h4>
        <h5 className="flex flex-row items-center gap-0.5">
          <IcDot size={24} /> {placeholder}
        </h5>
        <h5 className="flex flex-row items-center gap-0.5">
          <IcDot size={24} /> {t("imageUpload.formatGuide")}
        </h5>
        <h5 className="flex flex-row items-center gap-0.5">
          <IcDot size={24} /> {t("imageUpload.fileSizeGuide")}
        </h5>
      </div>
    </div>
  );
};

export default ImageUploadInput;
