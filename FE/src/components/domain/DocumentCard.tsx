import { useNavigate } from "react-router-dom";
import { useTranslation } from "react-i18next";
import { IcFile2 } from "../../assets/icon/StratisUi";
import Button from "../common/Button";
import Tag from "../common/Tag";
import type { RecentVisaFormsResponse } from "../../api/types/etc";
import { formatToLocalTime } from "../../utils/formatToLocalTime";
import { useResizeImage } from "../../hooks/useResizeImage";
import { useEffect, useState } from "react";
import ConfirmToReactiveModal from "../../pages/Documents/Component/ConfirmToReactiveModal";
import { useApplicationFormQuery } from "../../api/queries/useApplicationFormQuery";
import { useGeneratePdf } from "../../pages/Documents/hooks/useGeneratePdf";

const DocumentCard = ({ document }: { document?: RecentVisaFormsResponse }) => {
  const { t } = useTranslation(["components"]);
  const navigate = useNavigate();
  const { resizeImage, imageSize, loadingImage } = useResizeImage();
  const { data, refetch } = useApplicationFormQuery(
    document?.applicationFormId ?? "",
    false,
  );

  const { previewPdf } = useGeneratePdf();

  const [confirmModalOn, setConfirmModalOn] = useState(false);

  useEffect(() => {
    if (document?.foreignerProfileImgUrl)
      resizeImage(document.foreignerProfileImgUrl, 75, 105);
  }, [document]);

  if (!document || loadingImage) return <SkeletonUI />;

  const lastModifiedAtLocalTime = formatToLocalTime(document.lastModifiedAt);

  const formattedTime =
    lastModifiedAtLocalTime.slice(0, 12) +
    " · " +
    lastModifiedAtLocalTime.slice(14, 19);

  const onPreview = async () => {
    await refetch();
    if (data) previewPdf({ ...data.sections }, data?.foreignerProfileImgUrl);
  };
  const onOpen = () => navigate(`/document/${document.applicationFormId}`);
  const onResumeClicked = () => setConfirmModalOn(true);
  const onResume = () => {
    setConfirmModalOn(false);
    navigate(`/document/${document.applicationFormId}`);
  };
  return (
    <>
      {confirmModalOn && (
        <ConfirmToReactiveModal
          onConfirm={onResume}
          onCancel={() => setConfirmModalOn(false)}
        />
      )}

      <div className="flex flex-row w-full h-fit p-4 gap-3 bg-white rounded-[10px] shadow">
        {document.foreignerProfileImgUrl ? (
          <div className="w-18.75 h-26.25 overflow-hidden rounded-xl flex items-center justify-center outline outline-border-normal">
            <div>
              <img
                src={document.foreignerProfileImgUrl}
                width={imageSize.width}
                height={imageSize.height}
                alt={t("documentCard.profileImage")}
              />
            </div>
          </div>
        ) : (
          <div className="w-18.75 h-26.25 rounded-xl bg-gray-100" />
        )}
        <div className="flex flex-col flex-1">
          <div className="flex flex-row gap-1.5">
            <Tag variant="small_fill_violet_max">
              {document.isDone
                ? t("documentCard.completedStatus")
                : t("documentCard.writingStatus")}
            </Tag>
            <Tag variant="small_fill_green_max">
              {document.currentStep}/138{t("documentCard.cells")}
            </Tag>
            <div className="ml-auto caption-m-medium text-text-sub">
              {t("documentCard.lastModified")} · {formattedTime}
            </div>
          </div>
          <h4 className="title-m-bold mt-3">{document.title}</h4>
          <div className="ml-auto flex flex-row gap-3">
            <Button
              variant="grayLine"
              onClick={onPreview}
              size="tiny"
              className="w-10 flex items-center justify-center relative font-[pretendard] text-[8px]"
            >
              <div className="absolute">
                <IcFile2 />
              </div>
              pdf
            </Button>
            <Button
              variant={document.isDone ? "grayLine" : "primary"}
              size="tiny"
              className="w-30"
              onClick={document.isDone ? onResumeClicked : onOpen}
            >
              {document.isDone
                ? t("documentCard.editButton")
                : t("documentCard.writeButton")}
            </Button>
          </div>
        </div>
      </div>
    </>
  );
};

export default DocumentCard;

const SkeletonUI = () => {
  return (
    <div className="flex flex-row w-full h-fit p-4 gap-3 bg-white rounded-[10px] shadow">
      <div className="w-18.75 h-26.25 rounded-xl bg-gray-100" />
      <div className="flex flex-col flex-1">
        <div className="flex flex-row gap-1.5">
          <Tag variant="small_fill_gray" className="w-12" />
          <Tag variant="small_fill_gray" className="w-14" />
          <Tag variant="small_fill_gray" className="w-30 ml-auto" />
        </div>
        <Tag variant="small_fill_gray" className="w-35 mt-3 mb-1" />
        <div className="ml-auto flex flex-row gap-3">
          <Button variant="skeleton" size="tiny" className="w-10" />
          <Button variant="skeleton" size="tiny" className="w-30" />
        </div>
      </div>
    </div>
  );
};
