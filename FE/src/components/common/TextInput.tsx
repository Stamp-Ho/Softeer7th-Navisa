const TextInput = ({
  className = "",
  placeholder = "",
  value = "",
  setValue = (_a: string) => {},
  type = "text",
}) => {
  return (
    <input
      className={`w-full px-spacing-600 py-spacing-600 bg-gray-50 rounded-radius-400
        body-l-medium  focus:outline-gray-300 focus:outline-2
        placeholder:text-text-sub ${className}`}
      placeholder={placeholder}
      value={value}
      onChange={(e) => setValue(e.target.value)}
      type={type}
    />
  );
};

export default TextInput;
