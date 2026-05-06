import { type SVGProps } from "react";

interface IProps extends SVGProps<SVGSVGElement> {}

export const AllJobs = (props: IProps) => {
  return (
    <svg
      width="90"
      height="90"
      viewBox="0 0 90 90"
      fill="none"
      xmlns="http://www.w3.org/2000/svg"
      {...props}
    >
      <mask
        id="mask0_648_40035"
        style={{ maskType: "alpha" }}
        maskUnits="userSpaceOnUse"
        x="23"
        y="23"
        width="44"
        height="45"
      >
        <g opacity="0.7">
          <rect
            x="23.4004"
            y="23.85"
            width="20.16"
            height="20.16"
            rx="2.88"
            fill="#7360FF"
          />
          <rect
            x="23.4004"
            y="46.89"
            width="20.16"
            height="20.16"
            rx="2.88"
            fill="#7360FF"
          />
          <rect
            x="46.4395"
            y="23.85"
            width="20.16"
            height="20.16"
            rx="2.88"
            fill="#7360FF"
          />
          <rect
            x="46.4395"
            y="46.89"
            width="20.16"
            height="20.16"
            rx="2.88"
            fill="#7360FF"
          />
        </g>
      </mask>
      <g mask="url(#mask0_648_40035)">
        <rect x="17" y="22" width="70" height="64" fill="#7360FF" />
        <rect
          x="17"
          y="22"
          width="70"
          height="64"
          fill="url(#paint0_linear_648_40035)"
        />
      </g>
      <defs>
        <linearGradient
          id="paint0_linear_648_40035"
          x1="18.2496"
          y1="21.8172"
          x2="89.3889"
          y2="59.0295"
          gradientUnits="userSpaceOnUse"
        >
          <stop offset="0.343539" stopColor="#7360FF" />
          <stop offset="1" stopColor="#4FBCCF" />
        </linearGradient>
      </defs>
    </svg>
  );
};
