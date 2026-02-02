import { type SVGProps } from "react";

interface IProps extends SVGProps<SVGSVGElement> {}

export const Chemistry = (props: IProps) => {
  return (
    <svg
      width="90"
      height="90"
      viewBox="0 0 90 90"
      fill="none"
      xmlns="http://www.w3.org/2000/svg"
      {...props}
    >
      <g opacity="0.7">
        <path
          d="M51.71 41.0229H51.8125L65.7734 65.5854C68.1294 69.7305 65.136 74.8763 60.3682 74.8764H29.2344C24.4665 74.8764 21.4721 69.7305 23.8281 65.5854L37.7891 41.0229H37.8926V20.9878H51.71V41.0229Z"
          fill="#7360FF"
        />
        <path
          d="M51.71 41.0229H51.8125L65.7734 65.5854C68.1294 69.7305 65.136 74.8763 60.3682 74.8764H29.2344C24.4665 74.8764 21.4721 69.7305 23.8281 65.5854L37.7891 41.0229H37.8926V20.9878H51.71V41.0229Z"
          fill="url(#paint0_linear_648_39797)"
        />
        <rect
          x="35.8213"
          y="16.1519"
          width="17.9627"
          height="4.83612"
          rx="2.41806"
          fill="#7360FF"
        />
        <path
          opacity="0.5"
          d="M37.8926 20.9879H51.7101V41.0233H48.2557L44.8013 23.7514H37.8926V20.9879Z"
          fill="url(#paint1_linear_648_39797)"
        />
        <path
          d="M59.707 61.0586L63.0557 66.9512L63.1699 67.1689C64.2597 69.4302 62.6185 72.1122 60.0527 72.1123H28.9189C26.2699 72.1123 24.6064 69.2537 25.915 66.9512L29.2646 61.0586H59.707Z"
          fill="url(#paint2_linear_648_39797)"
          fill-opacity="0.5"
        />
        <rect
          x="36.207"
          y="27.373"
          width="7.56"
          height="1.89"
          rx="0.945"
          fill="#F4F3FF"
        />
        <rect
          x="36.207"
          y="31.153"
          width="5.67"
          height="1.89"
          rx="0.945"
          fill="#F4F3FF"
        />
      </g>
      <defs>
        <linearGradient
          id="paint0_linear_648_39797"
          x1="30.0801"
          y1="46.75"
          x2="34.559"
          y2="76.7344"
          gradientUnits="userSpaceOnUse"
        >
          <stop stop-color="#7360FF" />
          <stop offset="1" stop-color="#4FBCCF" />
        </linearGradient>
        <linearGradient
          id="paint1_linear_648_39797"
          x1="48.2557"
          y1="21.301"
          x2="48.2557"
          y2="41.0233"
          gradientUnits="userSpaceOnUse"
        >
          <stop stop-color="#222CBB" />
          <stop offset="1" stop-color="#222CBB" stop-opacity="0" />
        </linearGradient>
        <linearGradient
          id="paint2_linear_648_39797"
          x1="44.4855"
          y1="61.0586"
          x2="44.4855"
          y2="72.1123"
          gradientUnits="userSpaceOnUse"
        >
          <stop stop-color="#F4F3FF" />
          <stop offset="1" stop-color="#F4F3FF" stop-opacity="0" />
        </linearGradient>
      </defs>
    </svg>
  );
};
