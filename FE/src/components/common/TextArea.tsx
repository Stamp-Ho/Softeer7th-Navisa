const TextArea = ({
  className = "",
  placeholder = "",
  value = "",
  setValue = (_a: string) => {},
}) => {
  return (
    <textarea
      className={`w-full px-spacing-600 py-spacing-600 bg-white rounded-radius-400
        body-l-medium focus:outline-gray-300 focus:outline-2 
        placeholder:text-text-sub min-h-40 resize-none ${className}`} // resize-none으로 수동 크기조절 방지(선택)
      placeholder={placeholder}
      value={value}
      onChange={(e) => setValue(e.target.value)}
    />
  );
};

export default TextArea;
