import React from 'react';
import { cardGradient } from '../utils/gradients';

export function SearchCard({ product, index, onClick }) {
  const id = product.id || String(index);
  const name = product.name ?? product.productName;
  const imgSrc = product.image || product.thumbnail || (product.galleryImages && product.galleryImages[0]);
  const altText = product.altText || name;
  const [imgError, setImgError] = React.useState(false);
  const [imgLoaded, setImgLoaded] = React.useState(false);
  const showFallback = !imgSrc || imgError;

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
        style={showFallback ? { background: cardGradient(id) } : { position: 'relative', background: '#f8fafc', display: 'flex', alignItems: 'center', justifyContent: 'center' }}
      >
        {!showFallback && !imgLoaded && (
          <div className="skeleton-loader" style={{ position: 'absolute', inset: 0, background: '#f1f5f9', animation: 'pulse 2s cubic-bezier(0.4, 0, 0.6, 1) infinite' }} />
        )}
        {!showFallback ? (
          <img
            src={imgSrc}
            alt={altText}
            className="search-card__img"
            loading="lazy"
            onLoad={() => setImgLoaded(true)}
            onError={() => setImgError(true)}
            style={{ width: '100%', height: '100%', objectFit: 'contain', padding: '0.5rem', opacity: imgLoaded ? 1 : 0, transition: 'opacity 0.3s', position: 'relative', zIndex: 1 }}
          />
        ) : (
          <span className="search-card__image-icon">
            {name?.[0]?.toUpperCase() || '?'}
          </span>
        )}
      </div>

      <div className="search-card__body">
        <h3 className="search-card__name">{name}</h3>
        <p className="search-card__brand">{product.brand}</p>
        <p className="search-card__price">
          ₹{Number(product.basePrice ?? product.price ?? 0).toFixed(2)}
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