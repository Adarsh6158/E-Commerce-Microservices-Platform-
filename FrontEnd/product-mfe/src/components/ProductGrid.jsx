import React from 'react';
import { ProductCard } from './ProductCard';

export function ProductGrid({ products, categoryName, onClickProduct }) {
  if (products.length === 0) {
    return (
      <div className="product-grid__empty">
        <span className="product-grid__empty-icon">🔍</span>
        <p className="product-grid__empty-text">No products found in this category</p>
      </div>
    );
  }

  return (
    <div className="product-grid-section">
      {categoryName && (
        <div className="category-heading">
          <h2 className="category-heading__title">{categoryName}</h2>
          <p className="category-heading__count">{products.length} product{products.length !== 1 ? 's' : ''}</p>
        </div>
      )}
      <div className="product-grid">
        {products.map(p => (
          <ProductCard key={p.id} product={p} onClick={() => onClickProduct(p.id)} />
        ))}
      </div>
    </div>
  );
}
