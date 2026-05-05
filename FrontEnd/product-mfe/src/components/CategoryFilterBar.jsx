import React from 'react';

const CATEGORY_ICONS = {
  'Electronics':     '🖥️',
  'Clothing':        '👕',
  'Home & Kitchen':  '🏠',
  'Books':           '📚',
  'Sports & Outdoors': '⚽',
  'Beauty & Health': '💄',
  'Toys & Games':    '🧸',
  'Groceries':       '🛒',
};

const DEFAULT_ICON = '🔥';

export function CategoryFilterBar({ categories, activeCategoryId, onSelectCategory }) {
  return (
    <div className="category-bar">
      <div className="category-bar__track">
        <button
          className={`category-bar__item ${!activeCategoryId ? 'category-bar__item--active' : ''}`}
          onClick={() => onSelectCategory(null)}
        >
          <span className="category-bar__icon">🔥</span>
          <span className="category-bar__label">For You</span>
        </button>
        {categories.map(cat => (
          <button
            key={cat.id}
            className={`category-bar__item ${activeCategoryId === cat.id ? 'category-bar__item--active' : ''}`}
            onClick={() => onSelectCategory(cat.id)}
          >
            <span className="category-bar__icon">
              {CATEGORY_ICONS[cat.name] || DEFAULT_ICON}
            </span>
            <span className="category-bar__label">{cat.name}</span>
          </button>
        ))}
      </div>
    </div>
  );
}
