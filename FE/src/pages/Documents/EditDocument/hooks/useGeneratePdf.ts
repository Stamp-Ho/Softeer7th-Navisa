import { PDFDocument, PDFPage, rgb } from "pdf-lib";
import fontkit from "@pdf-lib/fontkit"; // 한글 폰트 임베딩을 위해 필요
import { nationList } from "../../../../constants/nations";
import type { formInputType } from "../../../../types/formType";
import { useEffect } from "react";

export const useGeneratePdf = () => {
  const prepareToGenerate = async () => {};
  useEffect(() => {
    prepareToGenerate();
  }, []);

  const convertWebpToJpg = async (url: string): Promise<ArrayBuffer> => {
    return new Promise((resolve, reject) => {
      const img = new Image();
      img.crossOrigin = "anonymous"; // CORS 이슈 방지
      img.onload = () => {
        const canvas = document.createElement("canvas");
        canvas.width = img.width;
        canvas.height = img.height;
        const ctx = canvas.getContext("2d");
        if (ctx) {
          ctx.drawImage(img, 0, 0);

          // canvas 내용을 jpeg 바이너리로 변환
          canvas.toBlob(
            (blob) => {
              if (blob) {
                blob.arrayBuffer().then(resolve);
              } else {
                reject(new Error("Failed to convert canvas to blob"));
              }
            },
            "image/jpeg",
            0.9,
          ); // 0.9는 품질(Quality)
        } else {
          reject(new Error("Failed to get canvas 2d context"));
        }
      };
      img.onerror = reject;
      img.src = url;
    });
  };

  const generatePdf = async (
    filledFormData: Record<string, any>,
    imageUrl: string,
  ): Promise<string> => {
    // 1. 공식 서식 PDF 가져오기 (원본 파일)
    const formUrl = "/APPLICATION_FORM.pdf";
    const formPdfBytes = await fetch(formUrl).then((res) => res.arrayBuffer());

    // 2. 한글 폰트(Pretendard) 가져오기
    const fontUrl = "/PretendardVariable.ttf"; // 1. TTF 폰트 로드
    const fontBytes = await fetch(fontUrl).then((res) => {
      if (!res.ok) throw new Error("폰트를 불러오지 못했습니다.");
      return res.arrayBuffer();
    });
    const hanjaUrl = "/KPA_CJK_KR-Medium.ttf"; // 1.1. 한자폰트로드
    const hanjaBytes = await fetch(hanjaUrl).then((res) => {
      if (!res.ok) throw new Error("폰트를 불러오지 못했습니다.");
      return res.arrayBuffer();
    });
    // 3. PDF 문서 로드 및 폰트 등록
    const pdfDoc = await PDFDocument.load(formPdfBytes);
    pdfDoc.registerFontkit(fontkit);
    const pretendard = await pdfDoc.embedFont(fontBytes);
    const kpa = await pdfDoc.embedFont(hanjaBytes);
    // 4. 첫 번째 페이지 가져오기
    if (!pdfDoc) return "";
    const pages = pdfDoc.getPages();

    // 2. 실제 PDF 적용 부분
    if (imageUrl) {
      try {
        // fetch 대신 위에서 만든 변환 함수 사용
        const jpgImageBytes = await convertWebpToJpg(imageUrl);
        const jpgImage = await pdfDoc.embedJpg(jpgImageBytes);
        const jpgDims = jpgImage.scaleToFit(100, 125);
        pages[0].drawImage(jpgImage, {
          ...jpgDims,
          x: 111 - jpgDims.width / 2,
          y: 518 - jpgDims.height / 2,
        });
      } catch (error) {
        console.error("WebP 변환 또는 이미지 삽입 실패:", error);
      }
    }

    const fillPdfData = (filledFormData: any, pages: PDFPage[]) => {
      const formValues = Object.values(filledFormData).slice(0, 9) as any[];
      const sections = formValues.map((section, i) => ({
        sectionId: i + 1,
        sectionData: section.sectionData.map((field: Record<string, any>) => ({
          ...field,
          values: Object.values(field.values),
        })),
      }));

      Object.entries(PDF_LAYOUT).forEach(([path, config]) => {
        const [sIdx, fIdx, iIdx] = path.split("-").map(Number);
        const section = sections[sIdx];
        const fieldData = section?.sectionData[fIdx];

        if (!fieldData || !fieldData.values) return;

        // 해당 필드가 작성될 타겟 페이지 설정
        const pageTarget = pages[config.pageIdx];
        if (!pageTarget) return;

        // 공통 텍스트 그리기 함수 (클로저 내부 정의)
        const draw = (
          text: string,
          x: number,
          y: number,
          isToRight: boolean = false,
          isToCenter: boolean = false,
        ) => {
          if (!text || text === "undefined" || text === "null") return;

          const currentFont = config.isHanja ? kpa : pretendard;
          const currentSize = config.size || 10;
          const textWidth = currentFont.widthOfTextAtSize(text, currentSize);

          pageTarget.drawText(text, {
            x: x - (isToRight ? textWidth : isToCenter ? textWidth / 2 : 0),
            y: y,
            size: currentSize,
            font: currentFont,
            color: rgb(0, 0, 0),
          });
        };

        // 국적과 같이 여러 입력을 한 필드에 넣는 경우
        if (config.manyInOneField) {
          const content = fieldData.values
            .map((v: string[] | number[]) => {
              const val: string | number = v[0];
              if (val === undefined || val === null || val === "") return;
              return config.format ? config.format(val) : String(val);
            })
            .join(", ");
          draw(content, config.x || 0, config.y, config.toRight);
        } else if (path.includes("disabled")) {
          // 필드에 disabled 속성이 있어 체크 표시가 필요할 때
          const selectedIdx = fieldData.disabled ? 0 : 1;
          const targetPos = config.options?.[selectedIdx];
          const targetX = targetPos?.x || 0;
          const targetY = targetPos?.y || 0;
          // 라디오 버튼은 보통 중앙 정렬이므로 toRight를 무시하고 √를 그림
          if (targetX !== undefined) draw("√", targetX, targetY, false);
        } else if (config.isGetMany) {
          // --- CASE 1: getMany (다중 행 데이터) ---
          // 예: 여행 국가 리스트처럼 한 필드 내에 여러 줄(row)이 있는 경우
          fieldData.values.forEach((row: any[], rowIdx: number) => {
            const val: string | number = row[iIdx]; // path의 iIdx(컬럼)에 해당하는 값을 가져옴
            if (val === undefined || val === null || val === "") return;

            const text = config.format ? config.format(val) : String(val);
            const dynamicY = config.y - rowIdx * (config.spacing || 15);

            draw(
              text,
              config.x || 0,
              dynamicY,
              config.toRight,
              config.toCenter,
            );
          });
          return;
        } else {
          // --- CASE 2: Single Field (단일 데이터) ---
          const rawValue: string | number = fieldData.values[0]?.[iIdx];
          if (rawValue === undefined || rawValue === null || rawValue === "")
            return;

          // 라디오 버튼/체크박스 처리
          if (config.type === "radio") {
            const selectedIdx = Number(rawValue);
            const targetPos = config.options?.[selectedIdx];
            const targetX = targetPos?.x || 0;
            const targetY = targetPos?.y || 0;
            // 라디오 버튼은 보통 중앙 정렬이므로 toRight를 무시하고 √를 그림
            if (targetX !== undefined) draw("√", targetX, targetY, false);
          }
          // 일반 텍스트 필드 처리
          else {
            const formattedValue =
              config.continued && iIdx > 0
                ? fieldData.values[0]?.[iIdx - 1]
                : "" +
                  (config.format ? config.format(rawValue) : String(rawValue));
            draw(
              formattedValue,
              config.x || 100,
              config.y,
              config.toRight || false,
              config.toCenter || false,
            );
          }
        }
      });
    };
    fillPdfData(filledFormData, pages);

    const pdfBytes = await pdfDoc.save(); // 결과 파일 저장

    //@ts-ignore
    const blob = new Blob([pdfBytes], { type: "application/pdf" }); // Blob 객체 생성 (MIME 타입 지정)

    const url = URL.createObjectURL(blob); // 브라우저에서 접근 가능한 임시 URL 생성

    return url;
  };

  const previewPdf = async (
    filledFormData: Record<string, any>,
    imageUrl: string,
  ) => {
    const url = await generatePdf(filledFormData, imageUrl);
    if (url) {
      window.open(url);
    }
  };
  const downloadPdf = async (
    filledFormData: Record<string, any>,
    imageUrl: string,
  ) => {
    const url = await generatePdf(filledFormData, imageUrl);
    if (url) {
      const link = document.createElement("a"); //가상의 <a> 태그를 만들어 클릭 이벤트 발생
      link.href = url;
      link.download = "사증발급인정신청서.pdf"; // 다운로드될 파일명
      document.body.appendChild(link);
      link.click();

      document.body.removeChild(link); //사용 후 메모리 해제 및 태그 제거
      URL.revokeObjectURL(url);
    }
  };
  return { previewPdf, downloadPdf };
};

/**
 * [섹션-필드-인덱스]: { x, y, type?, format?, isGetMany?, spacing? }
 */
type tempType = {
  x?: number;
  y: number;
  type?: formInputType;
  size?: number;
  toRight?: boolean;
  toCenter?: boolean;
  isHanja?: boolean;
  format?: (a: string | number) => string;
  options?: { x: number; y: number }[];
  isGetMany?: true;
  continued?: true;
  manyInOneField?: true;
  spacing?: number;
};

const PDF_LAYOUT: Record<string, tempType & { pageIdx: number }> = {
  // --- PAGE 1 (pageIdx: 0) ---
  "0-1-0": { pageIdx: 0, x: 350, y: 542, toRight: true }, // 성 (Family Name)
  "0-1-1": { pageIdx: 0, x: 538, y: 542, toRight: true }, // 이름 (Given Name)
  "0-2-0": { pageIdx: 0, x: 350, y: 512, toRight: true, isHanja: true }, // 한자성명
  "0-3-0": {
    pageIdx: 0,
    y: 0,
    type: "radio",
    options: [
      { x: 423, y: 513 },
      { x: 505, y: 513 },
    ],
  }, // 성별
  "0-4-0": {
    pageIdx: 0,
    x: 350,
    y: 484,
    toRight: true,
    format: (v: string | number) => String(v).replaceAll("-", "."),
  }, // 생년월일
  "0-5-0": {
    pageIdx: 0,
    x: 538,
    y: 484,
    toRight: true,
    manyInOneField: true,
    format: (v: string | number) => nationList[Number(v)],
  }, // 국적
  "0-6-0": {
    pageIdx: 0,
    x: 350,
    y: 458,
    toRight: true,
    format: (v: string | number) => nationList[Number(v)],
  }, // 출생국가
  "0-7-0": { pageIdx: 0, x: 538, y: 458, toRight: true }, // 국가신분증번호
  "0-8-disabled": {
    pageIdx: 0,
    y: 0,
    type: "radio",
    options: [
      { x: 196, y: 414 },
      { x: 125, y: 414 },
    ],
  }, // 이전 한국 출입시 다른 이름 사용 여부
  "0-8-0": { pageIdx: 0, x: 277, y: 398.5, toRight: true }, // 다른 성명 성
  "0-8-1": { pageIdx: 0, x: 485, y: 398.5, toRight: true }, // 다른 성명 이름
  "0-9-disabled": {
    pageIdx: 0,
    y: 0,
    type: "radio",
    options: [
      { x: 430, y: 383 },
      { x: 498, y: 383 },
    ],
  }, // 복수국적
  "0-9-0": { pageIdx: 0, x: 490, y: 367, toRight: true }, // 복수국적 세부사항

  "1-0-0": {
    pageIdx: 0,
    y: 340,
    type: "radio",
    options: [
      { x: 213, y: 313 },
      { x: 448, y: 313 },
      { x: 205, y: 300 },
      { x: 442, y: 300 },
    ],
  }, // 여권 종류
  "1-1-0": { pageIdx: 0, x: 214, y: 243, toRight: true }, // 여권번호
  "1-2-0": {
    pageIdx: 0,
    x: 377,
    y: 243,
    toRight: true,
    format: (v: string | number) => nationList[Number(v)],
  }, // 발급국가
  "1-3-0": { pageIdx: 0, x: 535, y: 243, toRight: true }, // 발급지
  "1-4-0": {
    pageIdx: 0,
    x: 214,
    y: 207,
    toRight: true,
    format: (v: string | number) => String(v).replaceAll("-", "."),
  }, // 발급일자
  "1-5-0": {
    pageIdx: 0,
    x: 377,
    y: 207,
    toRight: true,
    format: (v: string | number) => String(v).replaceAll("-", "."),
  }, // 만료일자
  "1-6-0": {
    pageIdx: 0,
    y: 0,
    type: "radio",
    options: [
      { x: 450, y: 192 },
      { x: 515, y: 192 },
    ],
  }, // 다른 여권 소지 여부

  // --- PAGE 2 (pageIdx: 1) ---
  "2-0-0": { pageIdx: 1, x: 525, y: 740, toRight: true }, // 본국주소 상세 ["베이커스트릿 221B", "런던", 55(나라 id)]
  "2-0-2": {
    pageIdx: 1,
    x: 525,
    y: 754,
    toRight: true,
    continued: true,
    format: (v: string | number) => nationList[Number(v)],
  },
  "2-1-0": { pageIdx: 1, x: 525, y: 686, toRight: true }, // 현 거주지 상세
  "2-1-2": {
    pageIdx: 1,
    x: 525,
    y: 698,
    toRight: true,
    continued: true,
    format: (v: string | number) => nationList[Number(v)],
  }, // 국가, 도시
  "2-2-0": { pageIdx: 1, x: 205, y: 654, toRight: true }, // 휴대전화
  "2-3-0": { pageIdx: 1, x: 358, y: 654, toRight: true }, // 일반 전화
  "2-4-0": { pageIdx: 1, x: 525, y: 654, toRight: true }, // 이메일
  // ["John Watson", "Friend", 55, "1121414"],
  "2-5-0": { pageIdx: 1, x: 287, y: 608, toRight: true }, // 비상연락 성명
  "2-5-1": { pageIdx: 1, x: 525, y: 580, toRight: true }, // 관계
  "2-5-2": {
    pageIdx: 1,
    x: 525,
    y: 608,
    toRight: true,
    format: (v: string | number) => nationList[Number(v)],
  }, // 국적
  "2-5-3": { pageIdx: 1, x: 287, y: 580, toRight: true }, // 전화번호

  "3-0-0": {
    pageIdx: 1,
    y: 0,
    type: "radio",
    options: [
      { x: 160, y: 524 },
      { x: 322, y: 524 },
      { x: 474, y: 524 },
    ],
  }, // 혼인사항 ["아", "내", "2023-05-02", 2, "내 마음속", "01010341"]
  "3-1-0": { pageIdx: 1, x: 288, y: 463, toRight: true }, // 배우자 성
  "3-1-1": { pageIdx: 1, x: 525, y: 463, toRight: true }, // 배우자 명
  "3-1-2": {
    pageIdx: 1,
    x: 288,
    y: 433,
    toRight: true,
    format: (v: string | number) => String(v).replaceAll("-", "."),
  }, // 생년월일
  "3-1-3": { pageIdx: 1, x: 525, y: 433, toRight: true }, // 국적
  "3-1-4": { pageIdx: 1, x: 288, y: 402, toRight: true }, // 거주지
  "3-1-5": { pageIdx: 1, x: 525, y: 402, toRight: true }, // 연락처
  "3-2-disabled": {
    pageIdx: 1,
    y: 0,
    type: "radio",
    options: [
      { x: 149, y: 369 },
      { x: 307, y: 369 },
    ],
  }, // 자녀유무
  "3-2-0": {
    pageIdx: 1,
    x: 511,
    y: 369,
    toRight: true,
    format: (v: string | number) => String(Number(v) % 100),
  }, // 자녀 수 [cite: 81]

  "4-0-0": {
    pageIdx: 1,
    y: 440,
    type: "radio",
    options: [
      { x: 242, y: 315 },
      { x: 463, y: 315 },
      { x: 228, y: 300 },
      { x: 432, y: 300 },
    ],
  }, // 최종학력 [cite: 83, 84, 85, 86]
  "4-1-0": { pageIdx: 1, x: 270, y: 245, toRight: true }, // 학교명
  "4-2-1": { pageIdx: 1, x: 525, y: 245, toRight: true, continued: true }, // 학교 소재지

  //직업
  "5-0-0": {
    pageIdx: 1,
    x: 270,
    y: 245,
    type: "radio",
    options: [
      { x: 199, y: 191 },
      { x: 349, y: 191 },
      { x: 514, y: 191 },
      { x: 199, y: 176 },
      { x: 349, y: 176 },
      { x: 514, y: 176 },
      { x: 199, y: 161 },
      { x: 349, y: 161 },
    ],
  }, // 직업
  "5-0-1": { pageIdx: 1, x: 501, y: 278, toRight: true }, //기타 시 상세정보
  "5-1-0": { pageIdx: 1, x: 361, y: 97, toRight: true }, // 직장명 ["내회사", "사장", 2, "우주어딘가", "0201042"]
  "5-1-1": { pageIdx: 1, x: 525, y: 97, toRight: true }, //직위
  "5-1-2": {
    pageIdx: 1,
    x: 361,
    y: 75,
    toRight: true,
    format: (v: string | number) => nationList[Number(v)],
  }, //국가
  "5-1-3": { pageIdx: 1, x: 361, y: 62, toRight: true }, //주소
  "5-1-4": { pageIdx: 1, x: 525, y: 62, toRight: true }, //전화번호

  // --- PAGE 3 (pageIdx: 2) ---
  "6-0-0": {
    pageIdx: 2,
    x: 0,
    y: 0,
    type: "radio",
    options: [
      { x: 203, y: 749 },
      { x: 365, y: 749 },
      { x: 525, y: 749 },
      { x: 203, y: 731 },
      { x: 365, y: 731 },
      { x: 525, y: 731 },
      { x: 203, y: 699 },
      { x: 365, y: 699 },
      { x: 525, y: 704 },
      { x: 203, y: 678 },
      { x: 365, y: 678 },
    ],
  }, // 입국 목적
  "6-0-1": { pageIdx: 2, x: 491, y: 660, toRight: true }, //기타 시 상세정보
  "6-1-0": { pageIdx: 2, x: 297, y: 580.5, toRight: true }, // 체류예정기간 ["100일"]
  "6-2-0": {
    pageIdx: 2,
    x: 535,
    y: 580.5,
    toRight: true,
    format: (v: string | number) => String(v).replaceAll("-", "."),
  }, // 입국예정일 ["2026-03-03"]
  "6-3-1": { pageIdx: 2, x: 377, y: 548, toRight: true, continued: true }, // 체류예정지["서울", "길거리 어디든 비바람 막히는 곳"]
  "6-4-0": { pageIdx: 2, x: 535, y: 548, toRight: true }, // 한국 내 연락처
  "6-5-disabled": {
    pageIdx: 2,
    y: 0,
    type: "radio",
    options: [
      { x: 134, y: 513 },
      { x: 200, y: 513 },
    ],
  }, //5년 내 한국 방문 경험
  "6-5-0": { pageIdx: 2, x: 101, y: 500, toRight: true }, // 방문 횟수
  "6-5-1": { pageIdx: 2, x: 528, y: 500, toRight: true }, // 방문 목적

  // 여행 국가 리스트 (getMany - 복수 컬럼 처리) [3, "그냥", "2024-03-05", "2024-03-06"]
  "6-6-disabled": {
    pageIdx: 2,
    y: 0,
    type: "radio",
    options: [
      { x: 133, y: 460 },
      { x: 199, y: 460 },
    ],
  }, //5년 내 여행 국가
  "6-6-0": {
    pageIdx: 2,
    x: 147,
    y: 418.5,
    spacing: 15.2,
    isGetMany: true,
    toCenter: true,
    format: (v: string | number) => nationList[Number(v)],
  }, // 국가명
  "6-6-1": {
    pageIdx: 2,
    x: 315,
    y: 418.5,
    spacing: 15.2,
    isGetMany: true,
    toCenter: true,
  }, // 방문목적
  "6-6-2": {
    pageIdx: 2,
    x: 470,
    y: 418.5,
    spacing: 15.2,
    isGetMany: true,
    toRight: true,
    format: (v: string | number) => String(v).replaceAll("-", ".") + " ~ ",
  }, // 시작일
  "6-6-3": {
    pageIdx: 2,
    x: 470,
    y: 418.5,
    spacing: 15.2,
    isGetMany: true,
    format: (v: string | number) => String(v).replaceAll("-", "."),
  }, // 종료일

  // 국내 체류 가족 [cite: 137, 139]
  // { values: [["650295", 4, 1, "2023-05-02", "2020-04-04"]], disabled: false },
  "6-7-disabled": {
    pageIdx: 2,
    y: 0,
    type: "radio",
    options: [
      { x: 131, y: 329 },
      { x: 191, y: 329 },
    ],
  }, //국내 체류 가족
  "6-7-0": {
    pageIdx: 2,
    x: 147,
    y: 291,
    spacing: 15.2,
    isGetMany: true,
    toCenter: true,
  }, // 이름
  "6-7-1": {
    pageIdx: 2,
    x: 287,
    y: 291,
    spacing: 15.2,
    isGetMany: true,
    toCenter: true,
    format: (v: string | number) => String(v).replaceAll("-", "."),
  }, // 생년월일
  "6-7-2": {
    pageIdx: 2,
    x: 385,
    y: 291,
    spacing: 15.2,
    isGetMany: true,
    toCenter: true,
    format: (v: string | number) => nationList[Number(v)],
  }, // 국적
  "6-7-3": {
    pageIdx: 2,
    x: 488,
    y: 291,
    spacing: 15.2,
    isGetMany: true,
    toCenter: true,
    format: (v: string | number) =>
      ["부", "모", "형제", "자식", "조부모", "친척"][Number(v)],
  }, // 관계

  "6-8-disabled": {
    pageIdx: 2,
    y: 0,
    type: "radio",
    options: [
      { x: 130, y: 191 },
      { x: 190, y: 191 },
    ],
  }, //동반입국 가족 유무
  "6-8-0": {
    pageIdx: 2,
    x: 147,
    y: 140,
    spacing: 15.2,
    isGetMany: true,
    toCenter: true,
  }, // 이름
  "6-8-1": {
    pageIdx: 2,
    x: 287,
    y: 140,
    spacing: 15.2,
    isGetMany: true,
    toCenter: true,
    format: (v: string | number) => String(v).replaceAll("-", "."),
  }, // 생년월일
  "6-8-2": {
    pageIdx: 2,
    x: 385,
    y: 140,
    spacing: 15.2,
    isGetMany: true,
    toCenter: true,
    format: (v: string | number) => nationList[Number(v)],
  }, // 국적
  "6-8-3": {
    pageIdx: 2,
    x: 488,
    y: 140,
    spacing: 15.2,
    isGetMany: true,
    toCenter: true,
    format: (v: string | number) =>
      ["부", "모", "형제", "자식", "조부모", "친척"][Number(v)],
  }, // 관계

  // --- PAGE 4 (pageIdx: 3) ---
  "7-0-disabled": {
    pageIdx: 3,
    y: 0,
    type: "radio",
    options: [
      { x: 118, y: 752 },
      { x: 169, y: 752 },
    ],
  }, //작성 도움 여부 [["챗지피티", 2, "샘 알트만네 번호", "2022-10-30"]]
  "7-0-0": { pageIdx: 3, x: 177, y: 696, toCenter: true }, // 작성 도움 성명
  "7-0-1": {
    pageIdx: 3,
    x: 515,
    y: 696,
    toCenter: true,
    format: (v: string | number) => ["행정사", "가족", "친구/지인"][Number(v)],
  }, // 관계
  "7-0-2": { pageIdx: 3, x: 416, y: 696, toCenter: true }, // 연락처
  "7-0-3": {
    pageIdx: 3,
    x: 314,
    y: 696,
    toCenter: true,
    format: (v: string | number) => String(v).replaceAll("-", "."),
  }, // 생년월일

  "8-0-disabled": {
    pageIdx: 3,
    y: 0,
    type: "radio",
    options: [
      { x: 125, y: 623 },
      { x: 193, y: 623 },
    ],
  }, //초청인/회사 여부 ["한국 사설탐정사무소", "4414", "여기적여기", "001482"]
  "8-0-0": { pageIdx: 3, x: 310, y: 591.5, toCenter: true }, // 초청인/회사명
  "8-0-1": { pageIdx: 3, x: 420, y: 550, size: 18, toRight: true }, // 생년월일/사업자등록번호
  "8-0-2": { pageIdx: 3, x: 560, y: 550, toRight: true }, // 관계
  "8-0-3": { pageIdx: 3, x: 420, y: 519, toRight: true }, // 주소
  "8-0-4": { pageIdx: 3, x: 560, y: 519, toRight: true }, // 전화번호
};
