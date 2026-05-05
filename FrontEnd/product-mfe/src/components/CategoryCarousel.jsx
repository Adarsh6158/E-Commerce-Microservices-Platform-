import React, { useRef, useState, useEffect } from 'react';
import { ProductCard } from './ProductCard';

export function CategoryCarousel({ products, onClickProduct }) {
  const scrollRef = useRef(null);
  const [canScrollLeft, setCanScrollLeft] = useState(false);
  const [canScrollRight, setCanScrollRight] = useState(false);

  const checkScroll = () => {
    const el = scrollRef.current;
    if (!el) return;
    setCanScrollLeft(el.scrollLeft > 4);
    setCanScrollRight(el.scrollLeft < el.scrollWidth - el.clientWidth - 4);
  };

  useEffect(() => { checkScroll(); }, [products]);

  const scroll = (dir) => {
    const el = scrollRef.current;
    if (!el) return;
    el.scrollBy({ left: dir * 260, behavior: 'smooth' });
    setTimeout(checkScroll, 350);
  };

  return (
    <div className="carousel">
      {canScrollLeft && (
        <button className="carousel__arrow carousel__arrow--left" onClick={() => scroll(-1)} aria-label="Scroll left">‹</button>
      )}
      <div className="carousel__track" ref={scrollRef} onScroll={checkScroll}>
        {products.map(p => (
          <ProductCard key={p.id} product={p} onClick={() => onClickProduct(p.id)} />
        ))}
      </div>
      {canScrollRight && (
        <button className="carousel__arrow carousel__arrow--right" onClick={() => scroll(1)} aria-label="Scroll right">›</button>
      )}
    </div>
  );
}