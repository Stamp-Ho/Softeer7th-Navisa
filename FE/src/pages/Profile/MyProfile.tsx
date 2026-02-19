import Button from "../../components/common/Button";
import BannerBackground from "../../components/layout/BannerBackground";
import { useAuth } from "../../contexts/AuthContextProvider";

const MyProfile = () => {
  const context = useAuth();
  if (!context) return null;
  const { logOut } = context;
  return (
    <>
      <BannerBackground />
      <div className="flex flex-row gap-2 h-200">
        <Button
          variant="primary"
          className="w-50 ml-auto mt-20 mr-20"
          onClick={logOut}
        >
          로그아웃
        </Button>
      </div>
    </>
  );
};

export default MyProfile;
