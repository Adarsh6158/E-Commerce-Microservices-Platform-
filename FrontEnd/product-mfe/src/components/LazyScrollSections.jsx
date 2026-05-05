import React, { useRef, useEffect, useState } from 'react';
import { ProductCard } from './ProductCard';

function useInView(options = {}) {
  const ref = useRef(null);
  const [isVisible, setIsVisible] = useState(false);

  useEffect(() => {
    const el = ref.current;
    if (!el) return;
    const observer = new IntersectionObserver(
      ([entry]) => { if (entry.isIntersecting) { setIsVisible(true); observer.disconnect(); } },
      { rootMargin: '200px', threshold: 0.01, ...options }
    );
    observer.observe(el);
    return () => observer.disconnect();
  }, []);

  return [ref, isVisible];
}

function ScrollSection({ title, subtitle, icon, products, onClickProduct }) {
  const [ref, isVisible] = useInView();
  const [show, setShow] = useState(false);

  useEffect(() => {
    if (isVisible) {
      const t = setTimeout(() => setShow(true), 100);
      return () => clearTimeout(t);
    }
  }, [isVisible]);

  return (
    <section ref={ref} className={`scroll-section ${show ? 'scroll-section--visible' : ''}`}>
      {show && (
        <>
          <div className="scroll-section__header">
            <span className="scroll-section__icon">{icon}</span>
            <div>
              <h2 className="scroll-section__title">{title}</h2>
              {subtitle && <p className="scroll-section__subtitle">{subtitle}</p>}
            </div>
          </div>
          <div className="scroll-section__track">
            {products.map(p => (
              <ProductCard key={p.id} product={p} onClick={() => onClickProduct(p.id)} />
            ))}
          </div>
        </>
      )}
    </section>
  );
}

export function LazyScrollSections({ products, onClickProduct }) {
  if (products.length < 6) return null;

  // Derive sections from the product list using different sorting strategies
  const shuffled = [...products].sort(() => 0.5 - Math.random());

  const topDeals = [...products]
    .sort((a, b) => Number(a.basePrice) - Number(b.basePrice))
    .slice(0, 10);

  const recommended = shuffled.slice(0, 10);

  const premiumPicks = [...products]
    .sort((a, b) => Number(b.basePrice) - Number(a.basePrice))
    .slice(0, 10);

  const newArrivals = [...products].slice(-10).reverse();

  return (
    <div className="lazy-sections">
      <ScrollSection
        title="Top Deals"
        subtitle="Best prices on popular items"
        icon="🏷️"
        products={topDeals}
        onClickProduct={onClickProduct}
      />
      <ScrollSection
        title="Recommended for You"
        subtitle="Handpicked based on trending items"
        icon="✨"
        products={recommended}
        onClickProduct={onClickProduct}
      />
      <ScrollSection
        title="Premium Picks"
        subtitle="Top-tier products for the discerning buyer"
        icon="💎"
        products={premiumPicks}
        onClickProduct={onClickProduct}
      />
      <ScrollSection
        title="New Arrivals"
        subtitle="Just landed – fresh additions to the catalog"
        icon="🆕"
        products={newArrivals}
        onClickProduct={onClickProduct}
      />
    </div>
  );
}
