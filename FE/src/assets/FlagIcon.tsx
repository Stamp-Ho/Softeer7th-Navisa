import Algeria from "../../public/flag/Algeria.png";
import Argentina from "../../public/flag/Argentina.png";
import Australia from "../../public/flag/Australia.png";
import Bangladesh from "../../public/flag/Bangladesh.png";
import Brazil from "../../public/flag/Brazil.png";
import Canada from "../../public/flag/Canada.png";
import Chile from "../../public/flag/Chile.png";
import China from "../../public/flag/China.png";
import Colombia from "../../public/flag/Colombia.png";
import Denmark from "../../public/flag/Denmark.png";
import Egypt from "../../public/flag/Egypt.png";
import Ethiopia from "../../public/flag/Ethiopia.png";
import Finland from "../../public/flag/Finland.png";
import France from "../../public/flag/France.png";
import Germany from "../../public/flag/Germany.png";
import Ghana from "../../public/flag/Ghana.png";
import Hongkong from "../../public/flag/Hongkong.png";
import India from "../../public/flag/India.png";
import Iran from "../../public/flag/Iran.png";
import Iraq from "../../public/flag/Iraq.png";
import Israel from "../../public/flag/Israel.png";
import Italy from "../../public/flag/Italy.png";
import Japan from "../../public/flag/Japan.png";
import Kazakhstan from "../../public/flag/Kazakhstan.png";
import Kenya from "../../public/flag/Kenya.png";
import Korea from "../../public/flag/Korea.png";
import Kuwait from "../../public/flag/Kuwait.png";
import Kyrgyzstan from "../../public/flag/Kyrgyzstan.png";
import Mexico from "../../public/flag/Mexico.png";
import Mongolia from "../../public/flag/Mongolia.png";
import Morocco from "../../public/flag/Morocco.png";
import Nepal from "../../public/flag/Nepal.png";
import Netherlands from "../../public/flag/Netherlands.png";
import NewZealand from "../../public/flag/NewZealand.png";
import Nigeria from "../../public/flag/Nigeria.png";
import Norway from "../../public/flag/Norway.png";
import Pakistan from "../../public/flag/Pakistan.png";
import Peru from "../../public/flag/Peru.png";
import Qatar from "../../public/flag/Qatar.png";
import Russia from "../../public/flag/Russia.png";
import SaudiArabia from "../../public/flag/SaudiArabia.png";
import SouthAfrica from "../../public/flag/SouthAfrica.png";
import SouthCambodia from "../../public/flag/SouthCambodia.png";
import SouthIndonesia from "../../public/flag/SouthIndonesia.png";
import SouthLaos from "../../public/flag/SouthLaos.png";
import SouthMalaysia from "../../public/flag/SouthMalaysia.png";
import SouthMyanmar from "../../public/flag/SouthMyanmar.png";
import SouthPhilippines from "../../public/flag/SouthPhilippines.png";
import SouthSingapore from "../../public/flag/SouthSingapore.png";
import SouthThailand from "../../public/flag/SouthThailand.png";
import SouthVietnam from "../../public/flag/SouthVietnam.png";
import Spain from "../../public/flag/Spain.png";
import SriLanka from "../../public/flag/SriLanka.png";
import Sweden from "../../public/flag/Sweden.png";
import Tajikistan from "../../public/flag/Tajikistan.png";
import theUnitedKingdom from "../../public/flag/theUnitedKingdom.png";
import Tiwan from "../../public/flag/Tiwan.png";
import Tunisia from "../../public/flag/Tunisia.png";
import Turkey from "../../public/flag/Turkey.png";
import Ukraine from "../../public/flag/Ukraine.png";
import UnitedArabEmirates from "../../public/flag/UnitedArabEmirates.png";
import USA from "../../public/flag/USA.png";
import Uzbekistan from "../../public/flag/Uzbekistan.png";
/**
 * 인덱스맞추기
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */
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
    <div className={className}>
      <img
        src={flagSrc}
        alt={`flag-${nationIndex}`}
        // 필요시 width/height를 props로 받도록 확장 가능
      />
    </div>
  );
};

export default FlagIcon;
