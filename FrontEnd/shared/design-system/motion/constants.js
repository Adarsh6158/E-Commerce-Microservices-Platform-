export const duration = {
  instant:   0.1,     
  fast:      0.15,    
  normal:    0.2,     
  moderate:  0.3,     
  slow:      0.4,     
  slower:    0.5,     
  cinematic: 0.7,     
  epic:      1.0,     
};
export const easing = {
  standard:     'cubic-bezier(0.25, 0.1, 0.25, 1)',
  enter:        'cubic-bezier(0, 0, 0.2, 1)',
  exit:         'cubic-bezier(0.4, 0, 1, 1)',
  emphasized:   'cubic-bezier(0.2, 0, 0, 1)',
  smooth:       'cubic-bezier(0.16, 1, 0.3, 1)',
  bounce:       'cubic-bezier(0.34, 1.56, 0.64, 1)',
  elastic:      'cubic-bezier(0.68, -0.55, 0.27, 1.55)',
  linear:       'linear',
};
export const spring = {
  gentle: {
    type: 'spring',
    stiffness: 120,
    damping: 14,
    mass: 0.8,
  },
  snappy: {
    type: 'spring',
    stiffness: 300,
    damping: 20,
    mass: 0.5,
  },
  bouncy: {
    type: 'spring',
    stiffness: 400,
    damping: 10,
    mass: 0.6,
  },
  smooth: {
    type: 'spring',
    stiffness: 80,
    damping: 20,
    mass: 1,
  },
  stiff: {
    type: 'spring',
    stiffness: 500,
    damping: 30,
    mass: 0.4,
  },
  lazy: {
    type: 'spring',
    stiffness: 50,
    damping: 20,
    mass: 1.2,
  },
};
export const tween = {
  fast: {
    type: 'tween',
    duration: duration.fast,
    ease: [0.25, 0.1, 0.25, 1],
  },
  normal: {
    type: 'tween',
    duration: duration.normal,
    ease: [0.16, 1, 0.3, 1],
  },
  slow: {
    type: 'tween',
    duration: duration.moderate,
    ease: [0.16, 1, 0.3, 1],
  },
  cinematic: {
    type: 'tween',
    duration: duration.cinematic,
    ease: [0.2, 0, 0, 1],
  },
};
export const transition = {
  fast:       `all ${duration.fast}s ${easing.standard}`,
  normal:     `all ${duration.normal}s ${easing.standard}`,
  smooth:     `all ${duration.moderate}s ${easing.smooth}`,
  slow:       `all ${duration.slow}s ${easing.smooth}`,
  colors:     `color ${duration.fast}s ${easing.standard}, background-color ${duration.fast}s ${easing.standard}, border-color ${duration.fast}s ${easing.standard}`,
  opacity:    `opacity ${duration.normal}s ${easing.standard}`,
  transform:  `transform ${duration.moderate}s ${easing.emphasized}`,
  shadow:     `box-shadow ${duration.normal}s ${easing.standard}`,
};
export const stagger = {
  fast:     0.03,    
  normal:   0.05,    
  slow:     0.08,    
  dramatic: 0.12,    
};
