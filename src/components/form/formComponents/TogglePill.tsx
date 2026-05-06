const TogglePill = ({
  isActive = false,
  setIsActive = (_b: boolean) => {},
  activeColor = "",
  className = "",
}) => {
  const toggleStyle = isActive ? `${activeColor} pl-6` : "bg-gray-500 pl-0.5";
  return (
    <div
      className={`rounded-full w-11.5 p-0.5 h-6 cursor-pointer transition-all ease-in-out ${toggleStyle} ${className}`}
      onClick={() => setIsActive(!isActive)}
    >
      <button className="w-5 h-5 bg-white rounded-full" type="button" />
    </div>
  );
};

export default TogglePill;
