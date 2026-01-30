const ProgressDot = ({
  status = "empty",
  isFirst = false,
  isLast = false,
  isEditing = false,
}) => {
  const dotType = () => {
    switch (status) {
      case "empty":
        return (
          <path
            d="M9 6.5C10.4 6.5 11.5 7.628 11.5 9C11.5 10.3893 10.3809 11.5074 9 11.5C7.6195 11.5 6.5 10.3842 6.50278 9C6.5 7.62 7.62463 6.5 9 6.51Z"
            stroke="#CBCDD2"
          />
        );
      case "done":
        return (
          <path
            d="M6 8.67C6 7 7.35 5.67 9 5.67C10.66 5.67 12 7 12 8.67C12 10.33 10.67 11.67 9 11.67C7.33 11.67 6 10.33 6 8.66Z"
            fill="#6958DE"
          />
        );
      case "inProgress":
        return (
          <path
            d="M6 8.67C6 7 7.35 5.67 9 5.67C10.66 5.67 12 7 12 8.67C12 10.33 10.67 11.67 9 11.67C7.33 11.67 6 10.33 6 8.66Z"
            fill="#CBCDD2"
          />
        );
      default:
        return <></>;
    }
  };

  return (
    <div className="relative z-4">
      <svg
        width="19"
        height="19"
        viewBox="0 0 19 19"
        fill="none"
        xmlns="http://www.w3.org/2000/svg"
      >
        {!isEditing && (
          <>
            {isFirst || <path d="M9 6L9 0" stroke={"#CBCDD2"} />}
            {isLast || <path d="M9 11L9 19" stroke="#CBCDD2" />}
          </>
        )}
        {dotType()}
      </svg>
      {isEditing && (
        <>
          <div className="absolute -left-4 -top-4 z-3">
            <svg
              width="50"
              height="50"
              viewBox="0 0 50 50"
              fill="none"
              xmlns="http://www.w3.org/2000/svg"
            >
              <circle
                cx="25"
                cy="25"
                r="9"
                fill={
                  status === "done" ? "url(#violetShadow)" : "url(#grayShadow)"
                }
              />
              <path
                d="M0.0260881 25C0.0402651 11.1929 11.2446 0.0114927 25.0517 0.0256697C38.8589 0.0398467 50.0402 11.2442 50.0261 25.0513C50.0119 38.8584 38.8075 50.0398 25.0004 50.0256C11.1933 50.0115 0.0119111 38.8071 0.0260881 25Z"
                fill={
                  status === "done" ? "url(#violetShadow)" : "url(#grayShadow)"
                }
              />
              <defs>
                <radialGradient
                  id="violetShadow"
                  cx="0"
                  cy="0"
                  r="1"
                  gradientUnits="userSpaceOnUse"
                  gradientTransform="translate(25.0261 25.0257) rotate(90.0588) scale(25)"
                >
                  <stop stop-color={"#6958DE"} />
                  <stop offset="1" stop-color="white" stop-opacity="0" />
                </radialGradient>
                <radialGradient
                  id="grayShadow"
                  cx="0"
                  cy="0"
                  r="1"
                  gradientUnits="userSpaceOnUse"
                  gradientTransform="translate(25.0261 25.0257) rotate(90.0588) scale(25)"
                >
                  <stop stop-color={"#999999"} />
                  <stop offset="1" stop-color="white" stop-opacity="0" />
                </radialGradient>
              </defs>
            </svg>
          </div>
          <div className="absolute -left-4 -top-4 z-5">
            <svg
              width="50"
              height="50"
              viewBox="0 0 50 50"
              fill="none"
              xmlns="http://www.w3.org/2000/svg"
            >
              <circle
                cx="25"
                cy="24.67"
                r="9"
                fill={status === "done" ? "#C5BDFF" : "#EEEEEE"}
              />
              <circle
                cx="25"
                cy="24.67"
                r="4"
                fill={
                  status === "done"
                    ? "#6958DE"
                    : status === "inProgress"
                      ? "#CBCDD2"
                      : "#FAFAFA"
                }
              />
            </svg>
          </div>
        </>
      )}
    </div>
  );
};
export default ProgressDot;
