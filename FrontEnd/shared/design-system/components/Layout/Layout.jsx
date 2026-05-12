import React from 'react';
import { motion } from 'framer-motion';
import { pageTransition } from '../../motion/variants.js';
const MAX_WIDTHS = {
  xs: '320px', sm: '480px', md: '640px', lg: '768px',
  xl: '1024px', '2xl': '1280px', full: '100%',
};
const SECTION_SPACING = {
  sm: '2rem', md: '3rem', lg: '4rem', xl: '6rem', '2xl': '8rem',
};
export function Container({ children, size = 'xl', style = {}, noPadding = false, center = true, ...props }) {
  return (
    <div style={{ width: '100%', maxWidth: MAX_WIDTHS[size] || MAX_WIDTHS.xl,
      marginLeft: center ? 'auto' : undefined, marginRight: center ? 'auto' : undefined,
      paddingLeft: noPadding ? 0 : 'clamp(1rem, 4vw, 2rem)',
      paddingRight: noPadding ? 0 : 'clamp(1rem, 4vw, 2rem)', ...style }} {...props}>
      {children}
    </div>
  );
}
export function Section({ children, spacing = 'lg', background, style = {}, ...props }) {
  const pad = SECTION_SPACING[spacing] || SECTION_SPACING.lg;
  return (
    <section style={{ width: '100%', paddingTop: pad, paddingBottom: pad,
      ...(background && { background }), ...style }} {...props}>
      {children}
    </section>
  );
}
export function PageWrapper({ children, animate = true, style = {}, ...props }) {
  if (!animate) return <div style={{ minHeight: '100vh', width: '100%', ...style }} {...props}>{children}</div>;
  return (
    <motion.div variants={pageTransition} initial="initial" animate="animate" exit="exit"
      style={{ minHeight: '100vh', width: '100%', ...style }} {...props}>
      {children}
    </motion.div>
  );
}
export function Stack({ children, gap = '1rem', align = 'stretch', justify = 'flex-start', style = {}, ...props }) {
  return <div style={{ display: 'flex', flexDirection: 'column', gap, alignItems: align, justifyContent: justify, ...style }} {...props}>{children}</div>;
}
export function Row({ children, gap = '1rem', align = 'center', justify = 'flex-start', wrap = false, style = {}, ...props }) {
  return <div style={{ display: 'flex', flexDirection: 'row', gap, alignItems: align, justifyContent: justify, flexWrap: wrap ? 'wrap' : 'nowrap', ...style }} {...props}>{children}</div>;
}
export function Grid({ children, minWidth = '280px', gap = '1.5rem', style = {}, ...props }) {
  return <div style={{ display: 'grid', gridTemplateColumns: `repeat(auto-fill, minmax(${minWidth}, 1fr))`, gap, ...style }} {...props}>{children}</div>;
}
export function Center({ children, inline = false, style = {}, ...props }) {
  return <div style={{ display: inline ? 'inline-flex' : 'flex', alignItems: 'center', justifyContent: 'center', ...style }} {...props}>{children}</div>;
}
export function Divider({ spacing = '1.5rem', color, style = {}, ...props }) {
  return <hr style={{ border: 'none', borderTop: `1px solid ${color || 'var(--sf-border-subtle, hsl(220, 14%, 93%))'}`, marginTop: spacing, marginBottom: spacing, ...style }} {...props} />;
}
export function Spacer({ size = '1rem', axis = 'vertical' }) {
  return <div style={{ [axis === 'vertical' ? 'height' : 'width']: size, flexShrink: 0 }} aria-hidden="true" />;
}
