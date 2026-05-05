import React, { useState } from 'react';
import { cardGradient } from '../utils/gradients';
import { ReviewSection } from './ReviewSection';

function ProductImage({ product }) {
  const images = product.imageUrls?.length > 0
    ? product.imageUrls
    : product.imageUrl ? [product.imageUrl] : [];
  const [idx, setIdx] = useState(0);
  const [imgError, setImgError] = useState(false);

  if (images.length === 0 || imgError) {
    return (
      <div className="detail-image" style={{ background: cardGradient(product.id) }}>
        <span className="detail-image__fallback">{product.name?.[0]?.toUpperCase() || '?'}</span>
      </div>
    );
  }

  return (
    <div className="detail-image">
      <img src={images[idx]} alt={product.name} className="detail-image__img" onError={() => setImgError(true)} />
      {images.length > 1 && (
        <div className="detail-image__thumbs">
          {images.map((src, i) => (
            <img
              key={i}
              src={src}
              alt={`${product.name} ${i + 1}`}
              className={`detail-image__thumb ${i === idx ? 'detail-image__thumb--active' : ''}`}
              onClick={() => setIdx(i)}
            />
          ))}
        </div>
      )}
    </div>
  );
}

export function ProductDetail({ product, price, reviews, rating, addingToCart, onAddToCart, onBack, onSubmitReview }) {
  const finalPrice = price?.finalPrice ?? price?.price ?? product.basePrice;

  return (
    <div>
      <button onClick={onBack} className="back-btn">← Back to products</button>
      <div className="detail-card">
        <ProductImage product={product} />
        <div className="detail-card__content">
          <div className="detail-card__header">
            <h2 className="detail-card__title">{product.name}</h2>
            <span className={`badge ${product.active ? 'badge--success' : 'badge--danger'}`}>
              {product.active ? 'In Stock' : 'Unavailable'}
            </span>
          </div>
          <p className="detail-card__brand">{product.brand}</p>
          <p className="detail-card__description">{product.description}</p>
          <div className="detail-meta">
            <div className="detail-meta__item">
              <span className="detail-meta__label">SKU</span>
              <span className="detail-meta__value">{product.sku}</span>
            </div>
            <div className="detail-meta__item">
              <span className="detail-meta__label">Base Price</span>
              <span className="detail-meta__value">${Number(product.basePrice).toFixed(2)}</span>
            </div>
            {price && (
              <div className="detail-meta__item">
                <span className="detail-meta__label">Your Price</span>
                <span className="detail-meta__value detail-meta__value--highlight">${Number(finalPrice).toFixed(2)}</span>
              </div>
            )}
          </div>
          <div className="detail-card__actions">
            <button
              onClick={() => onAddToCart(product)}
              disabled={addingToCart || !product.active}
              className="btn-add-to-cart"
            >
              {addingToCart ? 'Adding...' : 'Add to Cart'}
            </button>
          </div>
        </div>
      </div>

      <ReviewSection
        productId={product.id}
        reviews={reviews}
        rating={rating}
        onSubmit={onSubmitReview}
      />
    </div>
  );
}
