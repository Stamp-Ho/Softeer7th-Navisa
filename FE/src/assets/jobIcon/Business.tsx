import { type SVGProps } from "react";

interface IProps extends SVGProps<SVGSVGElement> {}

export const Business = (props: IProps) => {
  return (
    <svg
      xmlns="http://www.w3.org/2000/svg"
      width="90"
      height="90"
      fill="none"
      viewBox="0 0 90 90"
      {...props}
    >
      <g opacity=".7">
        <mask
          id="b"
          width="70"
          height="30"
          x="10"
          y="45"
          maskUnits="userSpaceOnUse"
          style={{ maskType: "alpha" }}
        >
          <ellipse
            cx="45.001"
            cy="59.87"
            fill="url(#a)"
            rx="34.65"
            ry="14.355"
            style={{ mixBlendMode: "color" }}
          />
        </mask>
        <g mask="url(#b)">
          <path
            fill="url(#c)"
            d="M69.027 54.962a3.51 3.51 0 0 1 1.76 2.358l2.346 15.65a1.53 1.53 0 0 1-1.498 1.849H45.444a.02.02 0 0 1-.02-.02v-.105q0-.002-.003 0v.019a.106.106 0 0 1-.105.106H19.209c-.973 0-1.7-.897-1.497-1.85l2.345-15.649a3.51 3.51 0 0 1 1.76-2.358l13.23-7.182a.765.765 0 0 1 1.059.35l5.724 12.266a.077.077 0 0 0 .141-.006l1.517-4.19a5.67 5.67 0 0 0-.9-5.466.878.878 0 0 1 .298-1.335l2.102-1.038a.88.88 0 0 1 .777 0l2.102 1.038a.878.878 0 0 1 .297 1.335 5.67 5.67 0 0 0-.899 5.467l1.565 4.32a.067.067 0 0 0 .124.005L54.74 48.13a.765.765 0 0 1 1.06-.349z"
          />
        </g>
        <path
          fill="#222CBB"
          d="M45.081 48.367a.75.75 0 0 1 .672 0l2.265 1.14a.78.78 0 0 1 .257 1.175 5.54 5.54 0 0 0-1.031 5.475l1.62 4.564-3.447 7.486-3.444-7.486 1.529-4.66a5.8 5.8 0 0 0-.943-5.379.78.78 0 0 1 .257-1.175z"
        />
        <path
          fill="#7360FF"
          d="M45.416 15.367c6.138 0 11.857 4.059 11.857 13.3q0 .078-.003.155a2.37 2.37 0 0 1 2.079 2.352c0 1.31-1.062 3.161-2.371 3.161q-.263-.001-.434-.056c-1.04 3.898-3.106 7.13-5.64 9.087l-.277 2.223c-.047.378-.274.71-.61.89a9.71 9.71 0 0 1-9.205 0 1.18 1.18 0 0 1-.61-.89l-.28-2.227c-2.526-1.953-4.588-5.175-5.628-9.061q-.143.032-.338.034c-1.31 0-2.37-1.851-2.371-3.16a2.37 2.37 0 0 1 1.977-2.338l-.002-.17c0-9.241 5.718-13.3 11.856-13.3"
        />
      </g>
      <defs>
        <linearGradient
          id="a"
          x1="11.588"
          x2="50.041"
          y1="45.433"
          y2="89.824"
          gradientUnits="userSpaceOnUse"
        >
          <stop offset=".344" stopColor="#7360FF" />
          <stop offset="1" stopColor="#4FBCCF" />
        </linearGradient>
        <linearGradient
          id="c"
          x1="18.668"
          x2="55.762"
          y1="47.314"
          y2="83.206"
          gradientUnits="userSpaceOnUse"
        >
          <stop offset=".344" stopColor="#7360FF" />
          <stop offset="1" stopColor="#4FBCCF" />
        </linearGradient>
      </defs>
    </svg>
  );
};
