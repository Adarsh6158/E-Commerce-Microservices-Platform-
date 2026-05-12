import { spring, tween, stagger, duration, easing } from './constants.js';
export const fade = {
  initial: { opacity: 0 },
  animate: { opacity: 1, transition: { duration: duration.normal } },
  exit:    { opacity: 0, transition: { duration: duration.fast } },
};
export const fadeUp = {
  initial: { opacity: 0, y: 12 },
  animate: {
    opacity: 1,
    y: 0,
    transition: spring.gentle,
  },
  exit: {
    opacity: 0,
    y: 8,
    transition: { duration: duration.fast },
  },
};
export const fadeDown = {
  initial: { opacity: 0, y: -12 },
  animate: {
    opacity: 1,
    y: 0,
    transition: spring.gentle,
  },
  exit: {
    opacity: 0,
    y: -8,
    transition: { duration: duration.fast },
  },
};
export const fadeLeft = {
  initial: { opacity: 0, x: -16 },
  animate: {
    opacity: 1,
    x: 0,
    transition: spring.gentle,
  },
  exit: {
    opacity: 0,
    x: -12,
    transition: { duration: duration.fast },
  },
};
export const fadeRight = {
  initial: { opacity: 0, x: 16 },
  animate: {
    opacity: 1,
    x: 0,
    transition: spring.gentle,
  },
  exit: {
    opacity: 0,
    x: 12,
    transition: { duration: duration.fast },
  },
};
export const scaleIn = {
  initial: { opacity: 0, scale: 0.95 },
  animate: {
    opacity: 1,
    scale: 1,
    transition: spring.snappy,
  },
  exit: {
    opacity: 0,
    scale: 0.98,
    transition: { duration: duration.fast },
  },
};
export const scaleUp = {
  initial: { opacity: 0, scale: 0.9, y: 8 },
  animate: {
    opacity: 1,
    scale: 1,
    y: 0,
    transition: spring.gentle,
  },
  exit: {
    opacity: 0,
    scale: 0.95,
    y: 4,
    transition: { duration: duration.fast },
  },
};
export const slideUp = {
  initial: { y: '100%' },
  animate: {
    y: 0,
    transition: spring.smooth,
  },
  exit: {
    y: '100%',
    transition: tween.slow,
  },
};
export const slideDown = {
  initial: { y: '-100%' },
  animate: {
    y: 0,
    transition: spring.smooth,
  },
  exit: {
    y: '-100%',
    transition: tween.slow,
  },
};
export const slideRight = {
  initial: { x: '100%' },
  animate: {
    x: 0,
    transition: spring.smooth,
  },
  exit: {
    x: '100%',
    transition: tween.slow,
  },
};
export const slideLeft = {
  initial: { x: '-100%' },
  animate: {
    x: 0,
    transition: spring.smooth,
  },
  exit: {
    x: '-100%',
    transition: tween.slow,
  },
};
export const staggerContainer = {
  initial: {},
  animate: {
    transition: {
      staggerChildren: stagger.normal,
      delayChildren: 0.1,
    },
  },
  exit: {
    transition: {
      staggerChildren: stagger.fast,
      staggerDirection: -1,
    },
  },
};
export const staggerContainerFast = {
  initial: {},
  animate: {
    transition: {
      staggerChildren: stagger.fast,
      delayChildren: 0.05,
    },
  },
};
export const staggerContainerSlow = {
  initial: {},
  animate: {
    transition: {
      staggerChildren: stagger.slow,
      delayChildren: 0.15,
    },
  },
};
export const pageTransition = {
  initial: {
    opacity: 0,
    y: 8,
  },
  animate: {
    opacity: 1,
    y: 0,
    transition: {
      duration: duration.moderate,
      ease: [0.16, 1, 0.3, 1],
      staggerChildren: stagger.normal,
      delayChildren: 0.1,
    },
  },
  exit: {
    opacity: 0,
    transition: {
      duration: duration.fast,
    },
  },
};
export const overlay = {
  initial: { opacity: 0 },
  animate: {
    opacity: 1,
    transition: { duration: duration.normal },
  },
  exit: {
    opacity: 0,
    transition: { duration: duration.fast },
  },
};
export const modal = {
  initial: {
    opacity: 0,
    scale: 0.96,
    y: 10,
  },
  animate: {
    opacity: 1,
    scale: 1,
    y: 0,
    transition: spring.gentle,
  },
  exit: {
    opacity: 0,
    scale: 0.98,
    y: 5,
    transition: { duration: duration.fast },
  },
};
export const tapScale = {
  whileTap: { scale: 0.97 },
};
export const hoverLift = {
  whileHover: {
    y: -2,
    transition: spring.snappy,
  },
};
export const hoverGlow = {
  whileHover: {
    boxShadow: '0 0 20px hsla(346, 84%, 61%, 0.25)',
    transition: { duration: duration.normal },
  },
};
export const hoverScale = {
  whileHover: {
    scale: 1.02,
    transition: spring.snappy,
  },
  whileTap: {
    scale: 0.98,
  },
};
export const shimmer = {
  initial: { x: '-100%' },
  animate: {
    x: '100%',
    transition: {
      duration: 1.5,
      ease: 'linear',
      repeat: Infinity,
      repeatDelay: 0.5,
    },
  },
};
export const pulse = {
  initial: { opacity: 0.6 },
  animate: {
    opacity: [0.6, 1, 0.6],
    transition: {
      duration: 2,
      ease: 'easeInOut',
      repeat: Infinity,
    },
  },
};
export const toast = {
  initial: {
    opacity: 0,
    y: -12,
    x: 12,
    scale: 0.95,
  },
  animate: {
    opacity: 1,
    y: 0,
    x: 0,
    scale: 1,
    transition: spring.snappy,
  },
  exit: {
    opacity: 0,
    x: 24,
    scale: 0.95,
    transition: tween.fast,
  },
};
export const collapse = {
  initial: {
    height: 0,
    opacity: 0,
    overflow: 'hidden',
  },
  animate: {
    height: 'auto',
    opacity: 1,
    overflow: 'hidden',
    transition: {
      height: spring.gentle,
      opacity: { duration: duration.normal, delay: 0.05 },
    },
  },
  exit: {
    height: 0,
    opacity: 0,
    overflow: 'hidden',
    transition: {
      height: { duration: duration.normal },
      opacity: { duration: duration.fast },
    },
  },
};
