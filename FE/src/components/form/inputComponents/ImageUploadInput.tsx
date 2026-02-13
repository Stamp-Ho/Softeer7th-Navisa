import { useEffect, useRef, useState } from "react";
import { IcDot, IcPlus } from "../../../assets/icon/StratisUi";
import { useFormContext } from "react-hook-form";
import { useResizeImage } from "../../../hooks/useResizeImage";

const ImageUploadInput = ({
  placeholder = "",
  imageUrl,
  imageFile,
  setImageFile,
}: {
  placeholder: string;
  isAgent?: boolean;
  imageUrl?: string;
  imageFile: File | undefined;
  setImageFile: React.Dispatch<React.SetStateAction<File | undefined>>;
}) => {
  const { resizeImage, imageSize, loadingImage } = useResizeImage();
  const fileInputRef = useRef<HTMLInputElement>(null);
  const { register, setValue } = useFormContext();
  const [imagePreview, setImagePreview] = useState("");

  const handleDivClick = () => {
    // 1. div 클릭 시 숨겨진 input을 클릭한 것처럼 동작
    fileInputRef.current?.click();
  };

  const handleFileChange = async (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (!file) return;

    const maxSize = 5 * 1024 * 1024; // 5MB를 byte 단위로 계산
    if (file.size > maxSize) {
      alert("파일 용량이 너무 큽니다. 5MB 이하의 이미지만 업로드 가능합니다.");
      e.target.value = ""; // input 초기화 (같은 파일 다시 선택 가능하게)
      return;
    }

    const fileMimeType = file.type; // 1. 파일 객체에서 직접 MIME 타입 추출
    const allowedTypes = ["image/jpg", "image/jpeg", "image/png"]; // 2. 허용된 타입인지 확인 (Typescript의 타입 가드 역할)

    if (!allowedTypes.includes(fileMimeType)) {
      alert("JPG, JPEG, PNG 형식의 이미지만 업로드 가능합니다.");
      return;
    }
    if (imagePreview) URL.revokeObjectURL(imagePreview);
    let image = window.URL.createObjectURL(file);
    setValue("0.sectionData.0.values.0", true);
    setImagePreview(image);
    setImageFile(file);
  };

  useEffect(() => {
    return () => {
      if (imagePreview) URL.revokeObjectURL(imagePreview);
    };
  }, [imagePreview]);
  useEffect(() => {
    register("0.sectionData.0.values.0");
    setValue("0.sectionData.0.values.0", "");
  }, []);
  useEffect(() => {
    (imageFile !== undefined || imageUrl) &&
      resizeImage(imagePreview || imageUrl || "", 75, 105);
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
        <h4 className="title-l-semibold text-text-base mb-2">
          사진 업로드하기
        </h4>
        <h5 className="flex flex-row items-center gap-0.5">
          <IcDot size={24} /> {placeholder}
        </h5>
        <h5 className="flex flex-row items-center gap-0.5">
          <IcDot size={24} /> 규격 안내
        </h5>
        <h5 className="flex flex-row items-center gap-0.5">
          <IcDot size={24} /> 5MB 이하의 파일만 업로드 가능합니다
        </h5>
      </div>
    </div>
  );
};

export default ImageUploadInput;
