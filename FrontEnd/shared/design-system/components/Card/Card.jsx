import React, { forwardRef } from 'react';
import { motion } from 'framer-motion';
import { spring } from '../../motion/constants.js';
const variantStyles = {
  elevated: {
    background: 'var(--sf-surface, #ffffff)',
    border: '1px solid var(--sf-border-subtle, hsl(220, 14%, 93%))',
    boxShadow: '0 1px 3px hsla(220, 10%, 20%, 0.06), 0 1px 2px hsla(220, 10%, 20%, 0.04)',
    hoverShadow: '0 10px 15px -3px hsla(220, 10%, 20%, 0.08), 0 4px 6px -4px hsla(220, 10%, 20%, 0.04)',
  },
  flat: {
    background: 'var(--sf-surface, #ffffff)',
    border: '1px solid var(--sf-border, hsl(220, 13%, 88%))',
    boxShadow: 'none',
    hoverShadow: '0 4px 6px -1px hsla(220, 10%, 20%, 0.07)',
  },
  outlined: {
    background: 'transparent',
    border: '1px solid var(--sf-border-strong, hsl(220, 10%, 78%))',
    boxShadow: 'none',
    hoverShadow: 'none',
    hoverBorder: 'var(--sf-accent, hsl(346, 84%, 61%))',
  },
  ghost: {
    background: 'transparent',
    border: '1px solid transparent',
    boxShadow: 'none',
    hoverShadow: 'none',
    hoverBackground: 'var(--sf-surface-hover, hsla(220, 20%, 98%, 0.6))',
    hoverBorder: 'var(--sf-border-subtle, hsl(220, 14%, 93%))',
  },
  glass: {
    background: 'hsla(0, 0%, 100%, 0.6)',
    border: '1px solid hsla(0, 0%, 100%, 0.2)',
    boxShadow: '0 4px 30px hsla(220, 10%, 20%, 0.08)',
    backdropFilter: 'blur(16px) saturate(180%)',
    WebkitBackdropFilter: 'blur(16px) saturate(180%)',
    hoverShadow: '0 8px 32px hsla(220, 10%, 20%, 0.12)',
  },
  glassDark: {
    background: 'hsla(220, 14%, 16%, 0.6)',
    border: '1px solid hsla(0, 0%, 100%, 0.08)',
    boxShadow: '0 4px 30px hsla(0, 0%, 0%, 0.2)',
    backdropFilter: 'blur(16px) saturate(180%)',
    WebkitBackdropFilter: 'blur(16px) saturate(180%)',
    hoverShadow: '0 8px 32px hsla(0, 0%, 0%, 0.3)',
  },
  spotlight: {
    background: 'var(--sf-surface, #ffffff)',
    border: '1px solid var(--sf-border-subtle, hsl(220, 14%, 93%))',
    boxShadow: '0 1px 3px hsla(220, 10%, 20%, 0.06)',
    backgroundImage: 'radial-gradient(ellipse at 50% 0%, hsla(346, 84%, 61%, 0.04) 0%, transparent 60%)',
    hoverShadow: '0 10px 15px -3px hsla(220, 10%, 20%, 0.08)',
  },
  interactive: {
    background: 'var(--sf-surface, #ffffff)',
    border: '1px solid var(--sf-border-subtle, hsl(220, 14%, 93%))',
    boxShadow: '0 1px 3px hsla(220, 10%, 20%, 0.06)',
    cursor: 'pointer',
    hoverShadow: '0 10px 25px -5px hsla(220, 10%, 20%, 0.1), 0 8px 10px -6px hsla(220, 10%, 20%, 0.04)',
    hoverY: -3,
  },
};
const sizePresets = {
  sm: { padding: '0.75rem', borderRadius: '0.5rem' },
  md: { padding: '1.25rem', borderRadius: '0.75rem' },
  lg: { padding: '1.75rem', borderRadius: '1rem' },
  xl: { padding: '2.5rem',  borderRadius: '1.25rem' },
};
const Card = forwardRef(function Card(
  {
    variant = 'elevated',
    size = 'md',
    hoverable = false,
    noPadding = false,
    as: Component = motion.div,
    children,
    style: styleProp = {},
    className,
    ...rest
  },
  ref
) {
  const v = variantStyles[variant] || variantStyles.elevated;
  const s = sizePresets[size] || sizePresets.md;
  const isInteractive = variant === 'interactive' || hoverable;
  const baseStyle = {
    padding: noPadding ? 0 : s.padding,
    borderRadius: s.borderRadius,
    background: v.background,
    border: v.border,
    boxShadow: v.boxShadow,
    position: 'relative',
    overflow: 'hidden',
    transition: 'box-shadow 0.3s cubic-bezier(0.16, 1, 0.3, 1), border-color 0.2s, background 0.2s',
    ...(v.backdropFilter && { backdropFilter: v.backdropFilter }),
    ...(v.WebkitBackdropFilter && { WebkitBackdropFilter: v.WebkitBackdropFilter }),
    ...(v.backgroundImage && { backgroundImage: v.backgroundImage }),
    ...(v.cursor && { cursor: v.cursor }),
    ...styleProp,
  };
  const hoverProps = isInteractive
    ? {
        whileHover: {
          y: v.hoverY || -2,
          boxShadow: v.hoverShadow || v.boxShadow,
          ...(v.hoverBackground && { background: v.hoverBackground }),
          ...(v.hoverBorder && { borderColor: v.hoverBorder }),
          transition: { duration: 0.3 },
        },
        whileTap: {
          scale: 0.99,
          transition: { duration: 0.1 },
        },
      }
    : {};
  return (
    <Component
      ref={ref}
      style={baseStyle}
      {...hoverProps}
      {...rest}
    >
      {children}
    </Component>
  );
});
function CardHeader({ children, style = {}, ...props }) {
  return (
    <div
      style={{
        paddingBottom: '0.75rem',
        marginBottom: '0.75rem',
        borderBottom: '1px solid var(--sf-border-subtle, hsl(220, 14%, 93%))',
        ...style,
      }}
      {...props}
    >
      {children}
    </div>
  );
}
function CardBody({ children, style = {}, ...props }) {
  return (
    <div style={{ ...style }} {...props}>
      {children}
    </div>
  );
}
function CardFooter({ children, style = {}, ...props }) {
  return (
    <div
      style={{
        paddingTop: '0.75rem',
        marginTop: '0.75rem',
        borderTop: '1px solid var(--sf-border-subtle, hsl(220, 14%, 93%))',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'flex-end',
        gap: '0.5rem',
        ...style,
      }}
      {...props}
    >
      {children}
    </div>
  );
}
Card.Header = CardHeader;
Card.Body = CardBody;
Card.Footer = CardFooter;
export { Card };
export default Card;
