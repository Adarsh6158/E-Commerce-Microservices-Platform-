import React, { useState, useEffect, useRef, useCallback } from 'react';

const BANNERS = [
  {
    id: 1,
    gradient: 'linear-gradient(135deg, #1a1a2e 0%, #16213e 50%, #0f3460 100%)',
    title: 'Mega Electronics Sale',
    subtitle: 'Up to 70% off on top brands',
    badge: 'LIMITED TIME',
    accent: '#4facfe',
    categoryName: 'Electronics',
  },
  {
    id: 2,
    gradient: 'linear-gradient(135deg, #0f3460 0%, #533483 50%, #e94560 100%)',
    title: 'Fashion Fiesta',
    subtitle: 'New arrivals starting ₹299',
    badge: 'TRENDING',
    accent: '#fa709a',
    categoryName: 'Clothing',
  },
  {
    id: 3,
    gradient: 'linear-gradient(135deg, #11998e 0%, #38ef7d 100%)',
    title: 'Home & Kitchen Deals',
    subtitle: 'Refresh your space – flat 50% off',
    badge: 'BEST SELLERS',
    accent: '#fff',
    categoryName: 'Home & Kitchen',
  },
  {
    id: 4,
    gradient: 'linear-gradient(135deg, #f12711 0%, #f5af19 100%)',
    title: 'Sports & Fitness',
    subtitle: 'Gear up for summer – extra 20% off',
    badge: 'NEW LAUNCH',
    accent: '#fff',
    categoryName: 'Sports & Outdoors',
  },
  {
    id: 5,
    gradient: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
    title: 'Beauty Bestsellers',
    subtitle: 'Premium skincare & makeup from ₹199',
    badge: 'TOP PICKS',
    accent: '#fbc2eb',
    categoryName: 'Beauty & Health',
  },
];

export function HeroCarousel({ categories, onSelectCategory }) {
  const [current, setCurrent] = useState(0);
  const [paused, setPaused] = useState(false);
  const timerRef = useRef(null);
  const touchStartX = useRef(0);

  const goTo = useCallback((idx) => {
    setCurrent((idx + BANNERS.length) % BANNERS.length);
  }, []);

  useEffect(() => {
    if (paused) return;
    timerRef.current = setInterval(() => goTo(current + 1), 4000);
    return () => clearInterval(timerRef.current);
  }, [current, paused, goTo]);

  const handleTouchStart = (e) => { touchStartX.current = e.touches[0].clientX; };
  const handleTouchEnd = (e) => {
    const diff = touchStartX.current - e.changedTouches[0].clientX;
    if (Math.abs(diff) > 50) goTo(current + (diff > 0 ? 1 : -1));
  };

  const banner = BANNERS[current];

  const handleShopNow = () => {
    const catName = banner.categoryName;
    if (!catName || !categories?.length || !onSelectCategory) return;
    const match = categories.find(c => c.name === catName);
    if (match) onSelectCategory(match.id);
  };

  return (
    <div
      className="hero-carousel"
      onMouseEnter={() => setPaused(true)}
      onMouseLeave={() => setPaused(false)}
      onTouchStart={handleTouchStart}
      onTouchEnd={handleTouchEnd}
    >
      <div className="hero-carousel__slide" style={{ background: banner.gradient }}>
        <span className="hero-carousel__badge" style={{ color: banner.accent }}>{banner.badge}</span>
        <h2 className="hero-carousel__title">{banner.title}</h2>
        <p className="hero-carousel__subtitle">{banner.subtitle}</p>
        <button className="hero-carousel__cta" onClick={handleShopNow}>Shop Now →</button>
      </div>

      <button className="hero-carousel__arrow hero-carousel__arrow--left" onClick={() => goTo(current - 1)} aria-label="Previous slide">‹</button>
      <button className="hero-carousel__arrow hero-carousel__arrow--right" onClick={() => goTo(current + 1)} aria-label="Next slide">›</button>

      <div className="hero-carousel__dots">
        {BANNERS.map((_, i) => (
          <button
            key={i}
            className={`hero-carousel__dot ${i === current ? 'hero-carousel__dot--active' : ''}`}
            onClick={() => goTo(i)}
            aria-label={`Go to slide ${i + 1}`}
          />
        ))}
      </div>
    </div>
  );
}
