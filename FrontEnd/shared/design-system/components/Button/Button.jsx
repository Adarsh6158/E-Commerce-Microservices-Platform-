import React, { forwardRef } from 'react';
import { motion } from 'framer-motion';
import { spring } from '../../motion/constants.js';
const sizes = {
  xs: {
    padding: '0.375rem 0.75rem',
    fontSize: '0.75rem',
    height: '1.75rem',
    borderRadius: '0.375rem',
    gap: '0.25rem',
    iconSize: '0.75rem',
  },
  sm: {
    padding: '0.5rem 1rem',
    fontSize: '0.8125rem',
    height: '2rem',
    borderRadius: '0.5rem',
    gap: '0.375rem',
    iconSize: '0.875rem',
  },
  md: {
    padding: '0.625rem 1.25rem',
    fontSize: '0.875rem',
    height: '2.5rem',
    borderRadius: '0.5rem',
    gap: '0.5rem',
    iconSize: '1rem',
  },
  lg: {
    padding: '0.75rem 1.75rem',
    fontSize: '0.9375rem',
    height: '2.875rem',
    borderRadius: '0.625rem',
    gap: '0.5rem',
    iconSize: '1.125rem',
  },
};
const variantStyles = {
  primary: {
    background: 'var(--sf-accent, hsl(346, 84%, 61%))',
    color: '#ffffff',
    border: 'none',
    hover: {
      background: 'var(--sf-accent-hover, hsl(346, 84%, 54%))',
      boxShadow: '0 0 20px hsla(346, 84%, 61%, 0.25)',
    },
  },
  secondary: {
    background: 'var(--sf-surface, #ffffff)',
    color: 'var(--sf-text-primary, hsl(240, 6%, 10%))',
    border: '1px solid var(--sf-border, hsl(220, 13%, 88%))',
    hover: {
      background: 'var(--sf-surface-hover, hsl(220, 20%, 98%))',
      borderColor: 'var(--sf-border-strong, hsl(220, 10%, 78%))',
    },
  },
  ghost: {
    background: 'transparent',
    color: 'var(--sf-text-primary, hsl(240, 6%, 10%))',
    border: 'none',
    hover: {
      background: 'var(--sf-surface-hover, hsla(220, 20%, 98%, 0.8))',
    },
  },
  outline: {
    background: 'transparent',
    color: 'var(--sf-accent, hsl(346, 84%, 61%))',
    border: '1px solid var(--sf-accent, hsl(346, 84%, 61%))',
    hover: {
      background: 'var(--sf-accent-soft, hsla(346, 84%, 61%, 0.08))',
    },
  },
  danger: {
    background: 'var(--sf-error, hsl(0, 72%, 56%))',
    color: '#ffffff',
    border: 'none',
    hover: {
      background: 'hsl(0, 72%, 48%)',
      boxShadow: '0 0 16px hsla(0, 72%, 56%, 0.2)',
    },
  },
  success: {
    background: 'var(--sf-success, hsl(152, 56%, 46%))',
    color: '#ffffff',
    border: 'none',
    hover: {
      background: 'hsl(152, 56%, 40%)',
      boxShadow: '0 0 16px hsla(152, 56%, 46%, 0.2)',
    },
  },
  soft: {
    background: 'var(--sf-accent-soft, hsla(346, 84%, 61%, 0.1))',
    color: 'var(--sf-accent, hsl(346, 84%, 61%))',
    border: 'none',
    hover: {
      background: 'hsla(346, 84%, 61%, 0.16)',
    },
  },
};
const Button = forwardRef(function Button(
  {
    variant = 'primary',
    size = 'md',
    loading = false,
    disabled = false,
    fullWidth = false,
    leftIcon,
    rightIcon,
    children,
    style: styleProp = {},
    ...rest
  },
  ref
) {
  const sizeConfig = sizes[size] || sizes.md;
  const variantConfig = variantStyles[variant] || variantStyles.primary;
  const isDisabled = disabled || loading;
  const baseStyle = {
    display: 'inline-flex',
    alignItems: 'center',
    justifyContent: 'center',
    gap: sizeConfig.gap,
    padding: sizeConfig.padding,
    fontSize: sizeConfig.fontSize,
    fontWeight: 500,
    fontFamily: "'Inter', -apple-system, BlinkMacSystemFont, sans-serif",
    lineHeight: 1,
    minHeight: sizeConfig.height,
    borderRadius: sizeConfig.borderRadius,
    cursor: isDisabled ? 'not-allowed' : 'pointer',
    opacity: isDisabled ? 0.55 : 1,
    width: fullWidth ? '100%' : 'auto',
    userSelect: 'none',
    WebkitFontSmoothing: 'antialiased',
    textDecoration: 'none',
    outline: 'none',
    position: 'relative',
    overflow: 'hidden',
    transition: 'background 0.2s cubic-bezier(0.25, 0.1, 0.25, 1), box-shadow 0.2s cubic-bezier(0.25, 0.1, 0.25, 1), border-color 0.2s cubic-bezier(0.25, 0.1, 0.25, 1), opacity 0.15s',
    background: variantConfig.background,
    color: variantConfig.color,
    border: variantConfig.border,
    ...styleProp,
  };
  return (
    <motion.button
      ref={ref}
      style={baseStyle}
      disabled={isDisabled}
      whileHover={
        isDisabled
          ? {}
          : {
              ...variantConfig.hover,
              transition: { duration: 0.2 },
            }
      }
      whileTap={isDisabled ? {} : { scale: 0.97 }}
      transition={spring.snappy}
      {...rest}
    >
      {loading ? (
        <LoadingSpinner size={sizeConfig.iconSize} color={variantConfig.color} />
      ) : (
        <>
          {leftIcon && (
            <span style={{ display: 'flex', fontSize: sizeConfig.iconSize }}>
              {leftIcon}
            </span>
          )}
          {children}
          {rightIcon && (
            <span style={{ display: 'flex', fontSize: sizeConfig.iconSize }}>
              {rightIcon}
            </span>
          )}
        </>
      )}
    </motion.button>
  );
});
function LoadingSpinner({ size = '1rem', color = '#fff' }) {
  return (
    <motion.svg
      width={size}
      height={size}
      viewBox="0 0 24 24"
      fill="none"
      animate={{ rotate: 360 }}
      transition={{
        duration: 1,
        ease: 'linear',
        repeat: Infinity,
      }}
    >
      <circle
        cx="12"
        cy="12"
        r="10"
        stroke={color}
        strokeWidth="3"
        strokeLinecap="round"
        strokeDasharray="60 30"
        opacity="0.8"
      />
    </motion.svg>
  );
}
export { Button };
export default Button;
