import React, { useState, useRef } from 'react';
import { motion } from 'framer-motion';
import { cardGradient } from '../utils/gradients';
import { ReviewSection } from './ReviewSection';

function ProductImage({ product }) {
  const images = product.galleryImages?.length > 0
    ? product.galleryImages
    : product.image ? [product.image] : [];
  const altText = product.altText || product.name;
  const [idx, setIdx] = useState(0);
  const [imgError, setImgError] = useState(false);
  const [imgLoaded, setImgLoaded] = useState(false);
  const [isImageHovered, setIsImageHovered] = useState(false);
  const [mousePos, setMousePos] = useState({ x: 50, y: 50 });
  const imageContainerRef = useRef(null);

  const handleMouseMove = (e) => {
    if (!imageContainerRef.current) return;
    const { left, top, width, height } = imageContainerRef.current.getBoundingClientRect();
    const x = ((e.clientX - left) / width) * 100;
    const y = ((e.clientY - top) / height) * 100;
    setMousePos({ x, y });
  };

  if (images.length === 0 || imgError) {
    return (
      <div className="detail-image" style={{ background: cardGradient(product.id) }}>
        <span className="detail-image__fallback">{product.name?.[0]?.toUpperCase() || '?'}</span>
      </div>
    );
  }

  return (
    <div className="detail-image">
      <div
        style={{ width: '100%', height: '300px', overflow: 'hidden', position: 'relative', display: 'flex', alignItems: 'center', justifyContent: 'center', cursor: isImageHovered ? 'crosshair' : 'zoom-in' }}
        ref={imageContainerRef}
        onMouseEnter={() => setIsImageHovered(true)}
        onMouseLeave={() => setIsImageHovered(false)}
        onMouseMove={handleMouseMove}
      >
        {!imgLoaded && (
          <div className="skeleton-loader" style={{ position: 'absolute', inset: 0, background: '#f1f5f9', animation: 'pulse 2s cubic-bezier(0.4, 0, 0.6, 1) infinite' }} />
        )}
        <motion.img
          src={images[idx]}
          alt={altText}
          className="detail-image__img"
          loading="lazy"
          onLoad={() => setImgLoaded(true)}
          onError={() => setImgError(true)}
          initial={{ opacity: 0 }}
          animate={{ scale: isImageHovered ? 1.9 : 1, opacity: imgLoaded ? 1 : 0 }}
          style={{ transformOrigin: `${mousePos.x}% ${mousePos.y}%` }}
          transition={{ duration: 0.4, ease: [0.16, 1, 0.3, 1] }}
        />
      </div>
      {images.length > 1 && (
        <div className="detail-image__thumbs">
          {images.map((src, i) => (
            <img
              key={i}
              src={src}
              alt={`${altText} ${i + 1}`}
              loading="lazy"
              className={`detail-image__thumb ${i === idx ? 'detail-image__thumb--active' : ''}`}
              onClick={() => { setIdx(i); setImgLoaded(false); }}
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
              <span className="detail-meta__value">₹{Number(product.basePrice).toFixed(2)}</span>
            </div>
            {price && (
              <div className="detail-meta__item">
                <span className="detail-meta__label">Your Price</span>
                <span className="detail-meta__value detail-meta__value--highlight">₹{Number(finalPrice).toFixed(2)}</span>
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
