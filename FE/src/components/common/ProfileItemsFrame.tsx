const ProfileItemsFrame = ({ children }: { children: React.ReactNode }) => {
  return (
    <div className="flex flex-col gap-5 w-124 px-5 py-7 border border-border-normal rounded-radius-400 bg-white shadow">
      {children}
    </div>
  );
};

export default ProfileItemsFrame;
