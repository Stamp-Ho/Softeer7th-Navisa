import type React from "react";

export type ModalProps = {
  children: React.ReactNode;
  className?: string;
  onClose?: () => void;
};
