import useApiClient from "../../hooks/useApiClient";
import { visaService } from "../services/etc";
import { useUploadImage } from "./useUploadImage";

export const useUploadFormImage = () => {
  const { apiClient } = useApiClient();
  const { uploadImage } = useUploadImage();

  const uploadFormImage = async (formId: string, imageFile: File) => {
    if (imageFile === undefined) {
      alert("프로필 이미지가 없습니다");
      return;
    }
    const allowedTypes = ["image/jpg", "image/jpeg", "image/png"] as const;
    type AllowedMime = (typeof allowedTypes)[number];
    const fileMimeType = imageFile.type;
    if (!allowedTypes.includes(fileMimeType as AllowedMime)) {
      alert("jpg, jpeg, png 형식의 이미지만 업로드 가능합니다");
      return;
    }

    const imgObjectKey = await uploadImage(
      fileMimeType as AllowedMime,
      "agent-profile",
      imageFile,
    );

    if (!imgObjectKey) {
      alert("저장에 실패했습니다");
      return;
    }
    return visaService
      .postApplicationFormImage(apiClient, formId, imgObjectKey)
      .then(() => {
        return true;
      })
      .catch((e) => {
        throw new Error(e);
      });
  };

  return { uploadFormImage };
};
