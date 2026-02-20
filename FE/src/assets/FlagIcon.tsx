interface FlagIconProps {
  nationIndex?: number;
  className?: string;
  onClick?: (e: React.MouseEvent<HTMLDivElement>) => void;
  title?: string;
  "data-testid"?: string;
}

const FlagIcon = ({ nationIndex = 0, className, onClick, title, "data-testid": testId }: FlagIconProps) => {
  // 인덱스 범위를 벗어날 경우를 대비한 안전 장치
  //const flagSrc = FLAG_IMAGES[nationIndex] || FLAG_IMAGES[0];
  const flagSrc = `/flag/${COUNTRY_NAMES[nationIndex % COUNTRY_NAMES.length]}.png`;

  return (
    <div className={className} onClick={onClick} title={title} data-testid={testId}>
      <img
        src={flagSrc}
        alt={`flag-${nationIndex}`}
        // 필요시 width/height를 props로 받도록 확장 가능
      />
    </div>
  );
};

export default FlagIcon;

const COUNTRY_NAMES: string[] = [
  "Algeria",
  "Argentina",
  "Australia",
  "Bangladesh",
  "Brazil",
  "Canada",
  "Chile",
  "China",
  "Colombia",
  "Denmark",
  "Egypt",
  "Ethiopia",
  "Finland",
  "France",
  "Germany",
  "Ghana",
  "Hongkong",
  "India",
  "Iran",
  "Iraq",
  "Israel",
  "Italy",
  "Japan",
  "Kazakhstan",
  "Kenya",
  "Korea",
  "Kuwait",
  "Kyrgyzstan",
  "Mexico",
  "Mongolia",
  "Morocco",
  "Nepal",
  "Netherlands",
  "NewZealand",
  "Nigeria",
  "Norway",
  "Pakistan",
  "Peru",
  "Qatar",
  "Russia",
  "SaudiArabia",
  "SouthAfrica",
  "Cambodia",
  "Indonesia",
  "Laos",
  "Malaysia",
  "Myanmar",
  "Philippines",
  "Singapore",
  "Thailand",
  "Vietnam",
  "Spain",
  "SriLanka",
  "Sweden",
  "Tajikistan",
  "theUnitedKingdom",
  "Tiwan",
  "Tunisia",
  "Turkey",
  "Ukraine",
  "UnitedArabEmirates",
  "USA",
  "Uzbekistan",
];
