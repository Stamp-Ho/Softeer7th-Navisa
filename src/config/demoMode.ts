export const DEMO_MODE = import.meta.env.VITE_DEMO_MODE === 'true';

const parsedDelay = Number(import.meta.env.VITE_DEMO_DELAY_MS ?? 450);
export const DEMO_DELAY_MS = Number.isFinite(parsedDelay) ? parsedDelay : 450;
