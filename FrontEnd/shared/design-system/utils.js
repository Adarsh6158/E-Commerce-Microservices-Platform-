import { clsx } from 'clsx';
export function cn(...inputs) {
  return clsx(inputs);
}
export function applyTheme(element, theme) {
  Object.entries(theme).forEach(([key, value]) => {
    element.style.setProperty(key, value);
  });
}
export function cssVar(name, fallback) {
  return `var(${name}${fallback ? `, ${fallback}` : ''})`;
}
