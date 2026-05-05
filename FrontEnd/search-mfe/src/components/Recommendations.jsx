import React from 'react';
import { SearchCard } from './SearchCard';

export function Recommendations({ recommendations, onSelect }) {
  if (recommendations.length === 0) return null;

  return (
    <section className="recommendations-section" aria-label="Recommended products">
      <h3 className="recommendations-title">🔥 Trending Products</h3>
      <p className="recommendations-subtitle">Popular items you might like</p>
      <div className="search-results">
        {recommendations.map((p, i) => (
          <SearchCard key={p.id ?? i} product={p} index={i} onClick={() => onSelect(p)} />
        ))}
      </div>
    </section>
  );
}