import React, { useState } from 'react';
import { cardGradient } from '../utils/gradients';

export function ProductCard({ product, onClick, onAddToCart, addingToCart }) {
  const imgSrc = product.imageUrl || (product.imageUrls && product.imageUrls[0]);
  const [imgError, setImgError] = useState(false);
  const showFallback = !imgSrc || imgError;

  return (
    <article className="product-card" onClick={onClick}>
      <div
        className="product-card__image"
        style={showFallback ? { background: cardGradient(product.id) } : {}}
      >
        {!showFallback ? (
          <img
            src={imgSrc}
            alt={product.name}
            className="product-card__img"
            loading="lazy"
            onError={() => setImgError(true)}
          />
        ) : (
          <span className="product-card__image-icon">
            {product.name?.[0]?.toUpperCase() || '?'}
          </span>
        )}
      </div>
      <div className="product-card__body">
        <h3 className="product-card__name">{product.name}</h3>
        <p className="product-card__price">${Number(product.basePrice).toFixed(2)}</p>
        <p className="product-card__offer">Free delivery</p>
      </div>
    </article>
  );
}
