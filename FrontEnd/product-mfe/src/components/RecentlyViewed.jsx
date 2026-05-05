import React, { useRef, useEffect, useState } from 'react';
import { ProductCard } from './ProductCard';

export function RecentlyViewed({ items, onClickProduct }) {
  const ref = useRef(null);
  const [visible, setVisible] = useState(false);

  useEffect(() => {
    const el = ref.current;
    if (!el) return;
    const observer = new IntersectionObserver(
      ([entry]) => { if (entry.isIntersecting) { setVisible(true); observer.disconnect(); } },
      { rootMargin: '100px', threshold: 0.01 }
    );
    observer.observe(el);
    return () => observer.disconnect();
  }, []);

  if (!items || items.length === 0) return null;

  return (
    <section ref={ref} className={`scroll-section ${visible ? 'scroll-section--visible' : ''}`}>
      <div className="scroll-section__header">
        <span className="scroll-section__icon">🕐</span>
        <div>
          <h2 className="scroll-section__title">Recently Viewed</h2>
          <p className="scroll-section__subtitle">Products you looked at recently</p>
        </div>
      </div>
      {visible && (
        <div className="scroll-section__track">
          {items.map(p => (
            <ProductCard key={p.id} product={p} onClick={() => onClickProduct(p.id)} />
          ))}
        </div>
      )}
    </section>
  );
}
