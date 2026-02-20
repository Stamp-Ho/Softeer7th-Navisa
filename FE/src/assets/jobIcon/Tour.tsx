import { type SVGProps } from "react";

interface IProps extends SVGProps<SVGSVGElement> {}

export const Tour = (props: IProps) => {
  return (
    <svg
      width="90"
      height="90"
      viewBox="0 0 90 90"
      fill="none"
      xmlns="http://www.w3.org/2000/svg"
      {...props}
    >
      <g clipPath="url(#clip0_648_40019)">
        <g opacity="0.7">
          <mask
            id="mask0_648_40019"
            style={{ maskType: "alpha" }}
            maskUnits="userSpaceOnUse"
            x="17"
            y="20"
            width="53"
            height="53"
          >
            <path
              d="M19.161 56.4186C18.4345 57.1452 18.799 58.3877 19.803 58.6065L29.693 60.7621L31.8486 70.6522C32.0674 71.6561 33.31 72.0207 34.0365 71.2941L34.8889 70.4418C36.4216 68.9091 37.1757 66.7634 36.9388 64.6087L36.3949 59.6608L49.6654 48.2275L57.3715 68.6999L58.8052 67.5807C62.2852 64.864 63.8126 60.3356 62.6891 56.0662L58.3428 39.5502L68.2598 29.6331C70.3137 27.5793 70.3137 24.2493 68.2598 22.1954C66.2059 20.1415 62.8759 20.1415 60.822 22.1954L50.905 32.1124L35.0001 27.9269C30.4273 26.7235 25.5958 28.5652 22.9843 32.5071L22.5843 33.111L42.2276 40.7898L30.7944 54.0602L25.8465 53.5163C23.6918 53.2795 21.5461 54.0335 20.0133 55.5663L19.161 56.4186Z"
              fill="#787F8B"
            />
            <path
              d="M21.9033 34.0215L42.2225 40.7946L50.8999 32.1172L33.4806 27.5332C29.6824 26.5337 25.6393 27.6267 22.8622 30.4039L21.3955 31.8705C20.7126 32.5534 20.9871 33.7162 21.9033 34.0215Z"
              fill="#787F8B"
            />
            <path
              d="M56.4268 68.5466L49.6538 48.2274L58.3312 39.55L62.9152 56.9693C63.9147 60.7674 62.8216 64.8106 60.0445 67.5877L58.5779 69.0544C57.895 69.7372 56.7322 69.4627 56.4268 68.5466Z"
              fill="#787F8B"
            />
          </mask>
          <g mask="url(#mask0_648_40019)">
            <rect x="15" y="14" width="60" height="64" fill="#7360FF" />
            <rect
              x="15"
              y="14"
              width="60"
              height="64"
              fill="url(#paint0_linear_648_40019)"
            />
          </g>
        </g>
        <path
          opacity="0.5"
          d="M33.1539 65.4085L29.8389 60.8104L31.9355 70.9793L32.5782 71.6416H33.4937L35.7335 69.4407L36.8242 66.7528L36.98 65.1557L36.3374 59.7492L49.9789 47.8584L64.7215 33.001L48.6286 47.5093L35.174 56.6751L34.6132 64.9663L33.1539 65.4085Z"
          fill="url(#paint1_linear_648_40019)"
        />
      </g>
      <defs>
        <linearGradient
          id="paint0_linear_648_40019"
          x1="16.0711"
          y1="13.8172"
          x2="80.7332"
          y2="42.8093"
          gradientUnits="userSpaceOnUse"
        >
          <stop offset="0.343539" stopColor="#7360FF" />
          <stop offset="1" stopColor="#4FBCCF" />
        </linearGradient>
        <linearGradient
          id="paint1_linear_648_40019"
          x1="47.2802"
          y1="33.001"
          x2="47.2802"
          y2="71.6416"
          gradientUnits="userSpaceOnUse"
        >
          <stop stopColor="#222CBB" />
          <stop offset="1" stopColor="#222CBB" stopOpacity="0" />
        </linearGradient>
        <clipPath id="clip0_648_40019">
          <rect width="90" height="90" rx="45" fill="white" />
        </clipPath>
      </defs>
    </svg>
  );
};
