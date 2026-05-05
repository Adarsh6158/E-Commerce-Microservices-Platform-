import React from 'react';
import { cardGradient } from '../utils/gradients';

export function SearchCard({ product, index, onClick }) {
  const id = product.id || String(index);
  const name = product.name ?? product.productName;

  return (
    <article
      className="search-card"
      onClick={onClick}
      role="button"
      tabIndex={0}
      onKeyDown={(e) => { if (e.key === 'Enter') onClick?.(); }}
    >
      <div
        className="search-card__image"
        style={{ background: cardGradient(id) }}
      >
        <span className="search-card__image-icon">
          {name?.[0]?.toUpperCase() || '?'}
        </span>
      </div>

      <div className="search-card__body">
        <h3 className="search-card__name">{name}</h3>
        <p className="search-card__brand">{product.brand}</p>
        <p className="search-card__price">
          ${Number(product.basePrice ?? product.price ?? 0).toFixed(2)}
        </p>

        {product.description && (
          <p className="search-card__desc">
            {product.description.substring(0, 100)}
          </p>
        )}
      </div>
    </article>
  );
}