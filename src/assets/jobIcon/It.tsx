import { type SVGProps } from "react";

interface IProps extends SVGProps<SVGSVGElement> {}

export const It = (props: IProps) => {
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
        <path
          fill="#7360FF"
          d="M16.201 26.06a3.92 3.92 0 0 1 3.921-3.92h49.01a3.92 3.92 0 0 1 3.92 3.92v28.427h-56.85z"
        />
        <path
          fill="url(#a)"
          d="M16.201 26.06a3.92 3.92 0 0 1 3.921-3.92h49.01a3.92 3.92 0 0 1 3.92 3.92v28.427h-56.85z"
        />
        <rect
          width="51.95"
          height="27.445"
          x="18.651"
          y="24.591"
          fill="#D6D0FF"
          rx="1.96"
        />
        <rect
          width="51.95"
          height="27.445"
          x="18.651"
          y="24.591"
          fill="url(#b)"
          rx="1.96"
        />
        <path
          fill="#7360FF"
          d="M39.883 58.905h9.068l-.08 1.024a9.8 9.8 0 0 0 1.93 6.631l1.31 1.75h-15.39l1.312-1.75a9.8 9.8 0 0 0 1.929-6.631z"
        />
        <path
          fill="url(#c)"
          d="M39.883 58.905h9.068l-.08 1.024a9.8 9.8 0 0 0 1.93 6.631l1.31 1.75h-15.39l1.312-1.75a9.8 9.8 0 0 0 1.929-6.631z"
        />
        <mask
          id="e"
          width="17"
          height="11"
          x="36"
          y="58"
          maskUnits="userSpaceOnUse"
          style={{ maskType: "alpha" }}
        >
          <path
            fill="#7360FF"
            d="M39.883 58.905h9.068l-.08 1.024a9.8 9.8 0 0 0 1.93 6.631l1.31 1.75h-15.39l1.312-1.75a9.8 9.8 0 0 0 1.929-6.631z"
          />
          <path
            fill="url(#d)"
            d="M39.883 58.905h9.068l-.08 1.024a9.8 9.8 0 0 0 1.93 6.631l1.31 1.75h-15.39l1.312-1.75a9.8 9.8 0 0 0 1.929-6.631z"
          />
        </mask>
        <g mask="url(#e)">
          <path
            fill="url(#f)"
            d="m39.286 64.035.428-1.71.427-1.45 9.224-.26-.181 1.555 1.217 2.72 1.283 2.565z"
          />
        </g>
        <path
          fill="#7360FF"
          d="M16.201 58.407a3.92 3.92 0 0 0 3.921 3.921h49.01a3.92 3.92 0 0 0 3.92-3.92v-3.922h-56.85z"
        />
        <path
          fill="url(#g)"
          d="M16.201 58.407a3.92 3.92 0 0 0 3.921 3.921h49.01a3.92 3.92 0 0 0 3.92-3.92v-3.922h-56.85z"
        />
        <rect
          width="8.55"
          height="1.282"
          x="40.141"
          y="57.195"
          fill="#D6D0FF"
          rx=".641"
        />
        <path
          fill="#7360FF"
          d="M23.041 71.092a2.78 2.78 0 0 1 2.782-2.782H63.01a2.78 2.78 0 0 1 2.782 2.782.64.64 0 0 1-.638.638H23.68a.64.64 0 0 1-.638-.638"
        />
        <path
          fill="url(#h)"
          d="M23.041 71.092a2.78 2.78 0 0 1 2.782-2.782H63.01a2.78 2.78 0 0 1 2.782 2.782.64.64 0 0 1-.638.638H23.68a.64.64 0 0 1-.638-.638"
        />
        <path
          fill="#fff"
          d="M36.968 35.373a.735.735 0 1 0 0 1.47zm15.438 1.47a.735.735 0 0 0 0-1.47zm-15.345 3.492a.735.735 0 0 0 0 1.47zm15.438 1.47a.735.735 0 1 0 0-1.47zm1.01-3.492h-.735a8.087 8.087 0 0 1-8.087 8.087v1.47a9.557 9.557 0 0 0 9.557-9.557zm-8.822 8.822V46.4a8.087 8.087 0 0 1-8.087-8.087h-1.47a9.557 9.557 0 0 0 9.557 9.557zm-8.822-8.822h.735a8.087 8.087 0 0 1 8.087-8.086v-1.471a9.557 9.557 0 0 0-9.557 9.557zm8.822-8.821v.735a8.087 8.087 0 0 1 8.087 8.086h1.47a9.557 9.557 0 0 0-9.557-9.557zm0 17.643V46.4c-.29 0-.623-.131-.991-.487-.372-.359-.74-.914-1.065-1.656-.649-1.484-1.068-3.584-1.068-5.944h-1.471c0 2.513.444 4.823 1.192 6.533.374.854.838 1.59 1.39 2.124.555.538 1.237.9 2.013.9zm-3.86-8.822h.736c0-2.359.42-4.46 1.068-5.943.325-.743.693-1.298 1.065-1.657.367-.355.701-.486.991-.486v-1.471c-.776 0-1.458.363-2.013.9-.552.534-1.016 1.271-1.39 2.125-.748 1.71-1.192 4.02-1.192 6.532zm3.86 8.822v.735c.776 0 1.458-.362 2.013-.9.552-.533 1.016-1.27 1.39-2.124.747-1.71 1.192-4.02 1.192-6.533H47.81c0 2.36-.42 4.46-1.068 5.944-.325.742-.693 1.297-1.065 1.656-.367.356-.701.487-.991.487zm3.86-8.822h.735c0-2.513-.444-4.823-1.192-6.532-.374-.854-.838-1.59-1.39-2.125-.555-.537-1.237-.9-2.013-.9v1.471c.29 0 .624.13.991.486.372.36.74.914 1.065 1.657.649 1.483 1.068 3.584 1.068 5.943zm-11.579-2.205v.735h15.438v-1.47H36.968zm.093 4.962v.735h15.438v-1.47H37.06z"
        />
        <path
          fill="url(#i)"
          d="M73.053 53.996H16.2v.98l41.02 1.021a43.8 43.8 0 0 1 15.832 3.39z"
          opacity=".5"
        />
      </g>
      <defs>
        <linearGradient
          id="a"
          x1="21.2"
          x2="73.184"
          y1="22.14"
          y2="19.875"
          gradientUnits="userSpaceOnUse"
        >
          <stop offset=".309" stopColor="#7360FF" />
          <stop offset="1" stopColor="#4FBCCF" />
        </linearGradient>
        <linearGradient
          id="b"
          x1="19.7"
          x2="91.052"
          y1="22.64"
          y2="27.356"
          gradientUnits="userSpaceOnUse"
        >
          <stop offset=".391" stopColor="#B5ACFF" />
          <stop offset="1" stopColor="#4FBCCF" />
        </linearGradient>
        <linearGradient
          id="c"
          x1="29.2"
          x2="44.619"
          y1="71.14"
          y2="82.652"
          gradientUnits="userSpaceOnUse"
        >
          <stop stopColor="#7360FF" />
          <stop offset="1" stopColor="#4FBCCF" />
        </linearGradient>
        <linearGradient
          id="d"
          x1="35.368"
          x2="48.913"
          y1="73.523"
          y2="80.767"
          gradientUnits="userSpaceOnUse"
        >
          <stop stopColor="#7360FF" />
          <stop offset="1" stopColor="#4FBCCF" />
        </linearGradient>
        <linearGradient
          id="f"
          x1="47.195"
          x2="47.195"
          y1="60.749"
          y2="69.165"
          gradientUnits="userSpaceOnUse"
        >
          <stop stopColor="#222CBB" />
          <stop offset="1" stopColor="#222CBB" stopOpacity="0" />
        </linearGradient>
        <linearGradient
          id="g"
          x1="11.2"
          x2="61.54"
          y1="50.14"
          y2="29.711"
          gradientUnits="userSpaceOnUse"
        >
          <stop stopColor="#7360FF" />
          <stop offset="1" stopColor="#4FBCCF" />
        </linearGradient>
        <linearGradient
          id="h"
          x1="19.28"
          x2="48.878"
          y1="73.626"
          y2="94.335"
          gradientUnits="userSpaceOnUse"
        >
          <stop stopColor="#7360FF" />
          <stop offset="1" stopColor="#4FBCCF" />
        </linearGradient>
        <linearGradient
          id="i"
          x1="58.84"
          x2="58.84"
          y1="54.081"
          y2="59.388"
          gradientUnits="userSpaceOnUse"
        >
          <stop stopColor="#222CBB" />
          <stop offset="1" stopColor="#222CBB" stopOpacity="0" />
        </linearGradient>
      </defs>
    </svg>
  );
};
