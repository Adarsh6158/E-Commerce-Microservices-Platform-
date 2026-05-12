import { useRef, useEffect, useState, useCallback, useMemo } from 'react';
import {
  useInView,
  useScroll,
  useTransform,
  useMotionValue,
  useSpring,
  useReducedMotion,
  useAnimation,
} from 'framer-motion';
import { spring, duration, stagger as staggerConfig } from '../motion/constants.js';
export function useReducedMotionSafe() {
  return useReducedMotion();
}
export function useScrollReveal({
  threshold = 0.15,
  once = true,
  delay = 0,
  direction = 'up',
  distance = 20,
} = {}) {
  const prefersReducedMotion = useReducedMotion();
  const ref = useRef(null);
  const isInView = useInView(ref, {
    once,
    amount: threshold,
  });
  const getInitialTransform = () => {
    if (prefersReducedMotion) return {};
    switch (direction) {
      case 'up':    return { y: distance };
      case 'down':  return { y: -distance };
      case 'left':  return { x: distance };
      case 'right': return { x: -distance };
      default:      return {};
    }
  };
  const style = useMemo(() => {
    if (prefersReducedMotion) {
      return { opacity: 1 };
    }
    const initial = getInitialTransform();
    return {
      opacity: isInView ? 1 : 0,
      ...Object.fromEntries(
        Object.entries(initial).map(([key, val]) => [key, isInView ? 0 : val])
      ),
      transition: `all 0.6s cubic-bezier(0.16, 1, 0.3, 1) ${delay}s`,
    };
  }, [isInView, prefersReducedMotion, direction, distance, delay]);
  return { ref, style, isInView };
}
export function useParallax(speed = 0.5, range = [-100, 100]) {
  const prefersReducedMotion = useReducedMotion();
  const { scrollY } = useScroll();
  const rawY = useTransform(scrollY, [0, 1000], [range[0] * speed, range[1] * speed]);
  const smoothY = useSpring(rawY, {
    stiffness: 50,
    damping: 30,
    mass: 1,
  });
  if (prefersReducedMotion) {
    return useMotionValue(0);
  }
  return smoothY;
}
export function useSmoothHover() {
  const prefersReducedMotion = useReducedMotion();
  const scale = useMotionValue(1);
  const y = useMotionValue(0);
  const springScale = useSpring(scale, { stiffness: 300, damping: 20 });
  const springY = useSpring(y, { stiffness: 300, damping: 20 });
  const onHoverStart = useCallback(() => {
    if (prefersReducedMotion) return;
    scale.set(1.02);
    y.set(-2);
  }, [prefersReducedMotion, scale, y]);
  const onHoverEnd = useCallback(() => {
    scale.set(1);
    y.set(0);
  }, [scale, y]);
  return {
    style: prefersReducedMotion ? {} : { scale: springScale, y: springY },
    onHoverStart,
    onHoverEnd,
  };
}
export function useStaggerChildren({
  staggerDelay = staggerConfig.normal,
  initialDelay = 0.1,
  once = true,
} = {}) {
  const prefersReducedMotion = useReducedMotion();
  const ref = useRef(null);
  const isInView = useInView(ref, { once, amount: 0.1 });
  const variants = useMemo(() => ({
    initial: {},
    animate: {
      transition: {
        staggerChildren: prefersReducedMotion ? 0 : staggerDelay,
        delayChildren: prefersReducedMotion ? 0 : initialDelay,
      },
    },
  }), [prefersReducedMotion, staggerDelay, initialDelay]);
  return {
    ref,
    variants,
    initial: 'initial',
    animate: isInView ? 'animate' : 'initial',
  };
}
export function useScrollProgress() {
  const ref = useRef(null);
  const { scrollYProgress } = useScroll({
    target: ref,
    offset: ['start end', 'end start'],
  });
  return { ref, progress: scrollYProgress };
}
export function useMagneticHover(strength = 0.2) {
  const prefersReducedMotion = useReducedMotion();
  const x = useMotionValue(0);
  const y = useMotionValue(0);
  const springX = useSpring(x, { stiffness: 200, damping: 15 });
  const springY = useSpring(y, { stiffness: 200, damping: 15 });
  const ref = useRef(null);
  const handleMouseMove = useCallback((e) => {
    if (prefersReducedMotion || !ref.current) return;
    const rect = ref.current.getBoundingClientRect();
    const centerX = rect.left + rect.width / 2;
    const centerY = rect.top + rect.height / 2;
    x.set((e.clientX - centerX) * strength);
    y.set((e.clientY - centerY) * strength);
  }, [prefersReducedMotion, strength, x, y]);
  const handleMouseLeave = useCallback(() => {
    x.set(0);
    y.set(0);
  }, [x, y]);
  return {
    ref,
    style: prefersReducedMotion ? {} : { x: springX, y: springY },
    onMouseMove: handleMouseMove,
    onMouseLeave: handleMouseLeave,
  };
}
export function useCountUp(target, trigger = true, decimals = 0) {
  const prefersReducedMotion = useReducedMotion();
  const motionValue = useMotionValue(0);
  const springValue = useSpring(motionValue, {
    stiffness: 60,
    damping: 20,
    mass: 1,
  });
  const [display, setDisplay] = useState(prefersReducedMotion ? target : 0);
  useEffect(() => {
    if (trigger) {
      motionValue.set(target);
    }
  }, [trigger, target, motionValue]);
  useEffect(() => {
    const unsubscribe = springValue.on('change', (v) => {
      setDisplay(Number(v.toFixed(decimals)));
    });
    return unsubscribe;
  }, [springValue, decimals]);
  return display;
}
export function useTypewriter(text, trigger = true, speed = 50) {
  const prefersReducedMotion = useReducedMotion();
  const [displayText, setDisplayText] = useState('');
  useEffect(() => {
    if (!trigger) return;
    if (prefersReducedMotion) {
      setDisplayText(text);
      return;
    }
    let i = 0;
    setDisplayText('');
    const interval = setInterval(() => {
      if (i < text.length) {
        setDisplayText(text.slice(0, i + 1));
        i++;
      } else {
        clearInterval(interval);
      }
    }, speed);
    return () => clearInterval(interval);
  }, [text, trigger, speed, prefersReducedMotion]);
  return displayText;
}
