import { useState } from "react";

export const useResizeImage = () => {
  const [loadingImage, setLoadingImage] = useState(false);
  const [imageSize, setImageSize] = useState({ width: 0, height: 0 });

  const loadImage = (url: string) => {
    return new Promise<HTMLImageElement>((resolve, reject) => {
      const img = new Image();
      img.src = url;
      img.onload = () => resolve(img);
      img.onerror = () => reject(new Error(`Failed to load image: ${url}`));
    });
  };

  const resizeImage = async (
    url: string,
    maxWidth: number,
    maxHeight: number,
  ) => {
    let resultWidth = 0;
    let resultHeight = 0;
    try {
      setLoadingImage(true);
      const img = await loadImage(url);

      let originalWidth = img.naturalWidth;
      let originalHeight = img.naturalHeight;
      if (originalWidth * maxHeight < originalHeight * maxWidth) {
        resultHeight = (originalHeight * maxWidth) / originalWidth;
        resultWidth = maxWidth;
      } else {
        resultWidth = (originalWidth * maxHeight) / originalHeight;
        resultHeight = maxHeight;
      }

      setImageSize({ width: resultWidth, height: resultHeight });
    } catch (error) {
      console.error("이미지 로딩 실패" + error);
      setImageSize({ width: 0, height: 0 });
    } finally {
      setLoadingImage(false);
    }
  };
  return { resizeImage, imageSize, loadingImage };
};
