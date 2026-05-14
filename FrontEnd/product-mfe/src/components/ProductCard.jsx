import React, { useState, useRef } from 'react';
import { motion } from 'framer-motion';
import { cardGradient } from '../utils/gradients';
export function ProductCard({ product, onClick, onAddToCart, addingToCart }) {
  const imgSrc = product.image || product.thumbnail || (product.galleryImages && product.galleryImages[0]);
  const altText = product.altText || product.name;
  const [imgError, setImgError] = useState(false);
  const [imgLoaded, setImgLoaded] = useState(false);
  const [isHovered, setIsHovered] = useState(false);
  const [isImageHovered, setIsImageHovered] = useState(false);
  const [mousePos, setMousePos] = useState({ x: 50, y: 50 });
  const imageContainerRef = useRef(null);

  const showFallback = !imgSrc || imgError;

  const handleMouseMove = (e) => {
    if (!imageContainerRef.current) return;
    const { left, top, width, height } = imageContainerRef.current.getBoundingClientRect();
    const x = ((e.clientX - left) / width) * 100;
    const y = ((e.clientY - top) / height) * 100;
    setMousePos({ x, y });
  };

  return (
    <motion.article
      className="product-card"
      onClick={onClick}
      onHoverStart={() => setIsHovered(true)}
      onHoverEnd={() => setIsHovered(false)}
      whileHover={{ y: -6 }}
      whileTap={{ scale: 0.98 }}
      transition={{ type: 'spring', stiffness: 300, damping: 20 }}
      layout
    >
      <div
        className="product-card__image"
        style={{
          ...(showFallback ? { background: cardGradient(product.id) } : {}),
          cursor: isImageHovered ? 'crosshair' : 'zoom-in'
        }}
        ref={imageContainerRef}
        onMouseEnter={() => setIsImageHovered(true)}
        onMouseLeave={() => setIsImageHovered(false)}
        onMouseMove={handleMouseMove}
      >
        {!showFallback && !imgLoaded && (
          <div className="skeleton-loader" style={{ position: 'absolute', inset: 0, background: '#f1f5f9', animation: 'pulse 2s cubic-bezier(0.4, 0, 0.6, 1) infinite' }} />
        )}
        {!showFallback ? (
          <motion.img
            src={imgSrc}
            alt={altText}
            className="product-card__img"
            loading="lazy"
            onLoad={() => setImgLoaded(true)}
            onError={() => setImgError(true)}
            initial={{ opacity: 0 }}
            animate={{ scale: isImageHovered ? 1.9 : 1, opacity: imgLoaded ? 1 : 0 }}
            style={{
              transformOrigin: `${mousePos.x}% ${mousePos.y}%`
            }}
            transition={{ duration: 0.4, ease: [0.16, 1, 0.3, 1] }}
          />
        ) : (
          <span className="product-card__image-icon">
            {product.name?.[0]?.toUpperCase() || '?'}
          </span>
        )}
        {}
        <motion.div
          className="product-card__overlay"
          initial={{ opacity: 0 }}
          animate={{ opacity: isHovered ? 1 : 0 }}
          transition={{ duration: 0.25 }}
        >
          <span className="product-card__overlay-text">View Details</span>
        </motion.div>
      </div>
      <div className="product-card__body">
        <h3 className="product-card__name">{product.name}</h3>
        <p className="product-card__price">₹{Number(product.basePrice).toFixed(2)}</p>
        <p className="product-card__offer">
          <svg width="12" height="12" viewBox="0 0 16 16" fill="none" style={{ verticalAlign: -1 }}>
            <path d="M2 8l4 4 8-8" stroke="hsl(152, 56%, 46%)" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
          </svg>
          {' '}Free delivery
        </p>
      </div>
    </motion.article>
  );
}
