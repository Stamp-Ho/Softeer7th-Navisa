import Algeria from "./flag/Algeria.png";
import Argentina from "./flag/Argentina.png";
import Australia from "./flag/Australia.png";
import Bangladesh from "./flag/Bangladesh.png";
import Brazil from "./flag/Brazil.png";
import Canada from "./flag/Canada.png";
import Chile from "./flag/Chile.png";
import China from "./flag/China.png";
import Colombia from "./flag/Colombia.png";
import Denmark from "./flag/Denmark.png";
import Egypt from "./flag/Egypt.png";
import Ethiopia from "./flag/Ethiopia.png";
import Finland from "./flag/Finland.png";
import France from "./flag/France.png";
import Germany from "./flag/Germany.png";
import Ghana from "./flag/Ghana.png";
import Hongkong from "./flag/Hongkong.png";
import India from "./flag/India.png";
import Iran from "./flag/Iran.png";
import Iraq from "./flag/Iraq.png";
import Israel from "./flag/Israel.png";
import Italy from "./flag/Italy.png";
import Japan from "./flag/Japan.png";
import Kazakhstan from "./flag/Kazakhstan.png";
import Kenya from "./flag/Kenya.png";
import Korea from "./flag/Korea.png";
import Kuwait from "./flag/Kuwait.png";
import Kyrgyzstan from "./flag/Kyrgyzstan.png";
import Mexico from "./flag/Mexico.png";
import Mongolia from "./flag/Mongolia.png";
import Morocco from "./flag/Morocco.png";
import Nepal from "./flag/Nepal.png";
import Netherlands from "./flag/Netherlands.png";
import NewZealand from "./flag/NewZealand.png";
import Nigeria from "./flag/Nigeria.png";
import Norway from "./flag/Norway.png";
import Pakistan from "./flag/Pakistan.png";
import Peru from "./flag/Peru.png";
import Qatar from "./flag/Qatar.png";
import Russia from "./flag/Russia.png";
import SaudiArabia from "./flag/SaudiArabia.png";
import SouthAfrica from "./flag/SouthAfrica.png";
import SouthCambodia from "./flag/SouthCambodia.png";
import SouthIndonesia from "./flag/SouthIndonesia.png";
import SouthLaos from "./flag/SouthLaos.png";
import SouthMalaysia from "./flag/SouthMalaysia.png";
import SouthMyanmar from "./flag/SouthMyanmar.png";
import SouthPhilippines from "./flag/SouthPhilippines.png";
import SouthSingapore from "./flag/SouthSingapore.png";
import SouthThailand from "./flag/SouthThailand.png";
import SouthVietnam from "./flag/SouthVietnam.png";
import Spain from "./flag/Spain.png";
import SriLanka from "./flag/SriLanka.png";
import Sweden from "./flag/Sweden.png";
import Tajikistan from "./flag/Tajikistan.png";
import theUnitedKingdom from "./flag/theUnitedKingdom.png";
import Tiwan from "./flag/Tiwan.png";
import Tunisia from "./flag/Tunisia.png";
import Turkey from "./flag/Turkey.png";
import Ukraine from "./flag/Ukraine.png";
import UnitedArabEmirates from "./flag/UnitedArabEmirates.png";
import USA from "./flag/USA.png";
import Uzbekistan from "./flag/Uzbekistan.png";

// 이미지를 배열에 순서대로 담습니다. (index 매핑용)
const FLAG_IMAGES: string[] = [
  Algeria,
  Argentina,
  Australia,
  Bangladesh,
  Brazil,
  Canada,
  Chile,
  China,
  Colombia,
  Denmark,
  Egypt,
  Ethiopia,
  Finland,
  France,
  Germany,
  Ghana,
  Hongkong,
  India,
  Iran,
  Iraq,
  Israel,
  Italy,
  Japan,
  Kazakhstan,
  Kenya,
  Korea,
  Kuwait,
  Kyrgyzstan,
  Mexico,
  Mongolia,
  Morocco,
  Nepal,
  Netherlands,
  NewZealand,
  Nigeria,
  Norway,
  Pakistan,
  Peru,
  Qatar,
  Russia,
  SaudiArabia,
  SouthAfrica,
  SouthCambodia,
  SouthIndonesia,
  SouthLaos,
  SouthMalaysia,
  SouthMyanmar,
  SouthPhilippines,
  SouthSingapore,
  SouthThailand,
  SouthVietnam,
  Spain,
  SriLanka,
  Sweden,
  Tajikistan,
  theUnitedKingdom,
  Tiwan,
  Tunisia,
  Turkey,
  Ukraine,
  UnitedArabEmirates,
  USA,
  Uzbekistan,
];

interface FlagIconProps {
  nationIndex?: number;
  className?: string;
}

const FlagIcon = ({ nationIndex = 0, className }: FlagIconProps) => {
  // 인덱스 범위를 벗어날 경우를 대비한 안전 장치
  const flagSrc = FLAG_IMAGES[nationIndex] || FLAG_IMAGES[0];

  return (
    <img
      src={flagSrc}
      className={className}
      alt={`flag-${nationIndex}`}
      // 필요시 width/height를 props로 받도록 확장 가능
    />
  );
};

export default FlagIcon;
