import React, { useState } from 'react';

export function StarRating({ value, max = 5, interactive = false, onChange }) {
  const [hover, setHover] = useState(0);
  return (
    <span className="star-rating" role={interactive ? 'radiogroup' : 'img'} aria-label={interactive ? 'Rating' : `${value} out of ${max} stars`}>
      {[...Array(max)].map((_, i) => {
        const starVal = i + 1;
        const filled = interactive ? starVal <= (hover || value) : starVal <= Math.round(value);
        return (
          <span
            key={i}
            className={`star-rating__star ${filled ? 'star-rating__star--filled' : ''} ${interactive ? 'star-rating__star--interactive' : ''}`}
            onClick={interactive ? () => onChange(starVal) : undefined}
            onMouseEnter={interactive ? () => setHover(starVal) : undefined}
            onMouseLeave={interactive ? () => setHover(0) : undefined}
            role={interactive ? 'radio' : undefined}
            aria-checked={interactive ? starVal === value : undefined}
            aria-label={interactive ? `${starVal} star${starVal !== 1 ? 's' : ''}` : undefined}
          >
            ★
          </span>
        );
      })}
    </span>
  );
}
