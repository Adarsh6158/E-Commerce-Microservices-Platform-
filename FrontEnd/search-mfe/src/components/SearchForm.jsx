import React, { useRef, useState, useEffect } from 'react';

export function SearchForm({
  query, onQueryChange, filters, onFiltersChange, categories,
  suggestions, showSuggestions, selectedSugIdx, onSelectSuggestion,
  onKeyDown, onFocusInput, onSubmit, loading,
}) {
  const suggestRef = useRef(null);

  useEffect(() => {
    const handleClickOutside = (e) => {
      if (suggestRef.current && !suggestRef.current.contains(e.target)) {
        onFocusInput(false);
      }
    };
    document.addEventListener('mousedown', handleClickOutside);
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, [onFocusInput]);

  return (
    <form onSubmit={onSubmit} className="search-form" role="search">
      <div className="search-input-wrapper" ref={suggestRef}>
        <input
          value={query}
          onChange={onQueryChange}
          onKeyDown={onKeyDown}
          onFocus={() => onFocusInput(true)}
          placeholder="Search by keyword..."
          className="search-input"
          autoComplete="off"
          aria-label="Search products"
          aria-autocomplete="list"
          aria-expanded={showSuggestions}
        />

        {showSuggestions && suggestions.length > 0 && (
          <div className="suggestions-dropdown" role="listbox">
            {suggestions.map((sug, i) => (
              <div
                key={sug.id}
                role="option"
                aria-selected={i === selectedSugIdx}
                className={`suggestions-dropdown__item ${i === selectedSugIdx ? 'suggestions-dropdown__item--active' : ''}`}
                onMouseDown={() => onSelectSuggestion(sug)}
                onMouseEnter={() => {}}
              >
                <span className="suggestions-dropdown__icon">Q</span>
                <span className="suggestions-dropdown__name">{sug.name}</span>
                {sug.brand && (
                  <span className="suggestions-dropdown__brand">{sug.brand}</span>
                )}
              </div>
            ))}
          </div>
        )}
      </div>

      <input
        value={filters.brand}
        onChange={(e) => onFiltersChange({ ...filters, brand: e.target.value })}
        placeholder="Brand"
        className="search-input search-input--brand"
        aria-label="Filter by brand"
      />

      <select
        value={filters.category}
        onChange={(e) => onFiltersChange({ ...filters, category: e.target.value })}
        className="search-input search-input--category"
        aria-label="Filter by category"
      >
        <option value="">All Categories</option>
        {categories.map((c) => (
          <option key={c.id} value={c.id}>{c.name}</option>
        ))}
      </select>

      <input
        value={filters.minPrice}
        onChange={(e) => onFiltersChange({ ...filters, minPrice: e.target.value })}
        placeholder="Min $"
        type="number"
        className="search-input search-input--price"
        aria-label="Minimum price"
      />

      <input
        value={filters.maxPrice}
        onChange={(e) => onFiltersChange({ ...filters, maxPrice: e.target.value })}
        placeholder="Max $"
        type="number"
        className="search-input search-input--price"
        aria-label="Maximum price"
      />

      <button type="submit" disabled={loading} className="search-btn">
        {loading ? 'Searching…' : '🔍 Search'}
      </button>
    </form>
  );
}