import React, { useState, useRef, useEffect } from 'react';
import { searchProducts, fetchSuggestions as fetchSuggestionsApi, fetchRecommendations, fetchCategories } from './api';
import { SearchForm } from './components/SearchForm';
import { SearchCard } from './components/SearchCard';
import { Recommendations } from './components/Recommendations';
import './App.css';

export default function App() {
  const [query, setQuery] = useState('');
  const [results, setResults] = useState([]);
  const [loading, setLoading] = useState(false);
  const [searched, setSearched] = useState(false);
  const [error, setError] = useState('');
  const [filters, setFilters] = useState({ brand: '', minPrice: '', maxPrice: '', category: '' });

  const [recommendations, setRecommendations] = useState([]);
  const [categories, setCategories] = useState([]);

  const [suggestions, setSuggestions] = useState([]);
  const [showSuggestions, setShowSuggestions] = useState(false);
  const [selectedSugIdx, setSelectedSugIdx] = useState(-1);
  const suggestTimer = useRef(null);

  useEffect(() => {
    fetchRecommendations().then(setRecommendations).catch(() => {});
    fetchCategories().then(setCategories).catch(() => {});
  }, []);

  const loadSuggestions = (q) => {
    clearTimeout(suggestTimer.current);
    if (!q || q.trim().length < 1) {
      setSuggestions([]);
      setShowSuggestions(false);
      return;
    }

    suggestTimer.current = setTimeout(async () => {
      const data = await fetchSuggestionsApi(q);
      setSuggestions(data);
      setShowSuggestions(data.length > 0);
      setSelectedSugIdx(-1);
    }, 200);
  };

  const handleQueryChange = (e) => {
    setQuery(e.target.value);
    loadSuggestions(e.target.value);
  };

  const selectSuggestion = (sug) => {
    setQuery(sug.name);
    setShowSuggestions(false);
    setSuggestions([]);
    doSearch(sug.name);
  };

  const handleKeyDown = (e) => {
    if (!showSuggestions || suggestions.length === 0) return;

    if (e.key === 'ArrowDown') {
      e.preventDefault();
      setSelectedSugIdx(prev => Math.min(prev + 1, suggestions.length - 1));
    } else if (e.key === 'ArrowUp') {
      e.preventDefault();
      setSelectedSugIdx(prev => Math.max(prev - 1, -1));
    } else if (e.key === 'Enter' && selectedSugIdx >= 0) {
      e.preventDefault();
      selectSuggestion(suggestions[selectedSugIdx]);
    } else if (e.key === 'Escape') {
      setShowSuggestions(false);
    }
  };

  const doSearch = async (searchQuery) => {
    const q = searchQuery || query;
    if (!q.trim() && !filters.brand && !filters.minPrice && !filters.category) return;

    setLoading(true);
    setError('');
    setSearched(true);
    setShowSuggestions(false);

    try {
      const params = new URLSearchParams();
      if (q.trim()) params.set('q', q.trim());
      if (filters.brand) params.set('brand', filters.brand);
      if (filters.category) params.set('categoryId', filters.category);
      if (filters.minPrice) params.set('minPrice', filters.minPrice);
      if (filters.maxPrice) params.set('maxPrice', filters.maxPrice);

      setResults(await searchProducts(params));
    } catch (e) {
      setError(e.message);
    } finally {
      setLoading(false);
    }
  };

  const search = (e) => {
    if (e) e.preventDefault();
    doSearch();
  };

  const handleProductClick = (product) => {
    window.dispatchEvent(new CustomEvent('mfe:product:view', {
      detail: { productId: product.id }
    }));
  };

  return (
    <div>
      <div className="search-header">
        <h2 className="search-header__title">Search Products</h2>
        <p className="search-header__subtitle">Find exactly what you are looking for</p>
      </div>

      <SearchForm
        query={query}
        onQueryChange={handleQueryChange}
        filters={filters}
        onFiltersChange={setFilters}
        categories={categories}
        suggestions={suggestions}
        showSuggestions={showSuggestions}
        selectedSugIdx={selectedSugIdx}
        onSelectSuggestion={selectSuggestion}
        onKeyDown={handleKeyDown}
        onFocusInput={(show) =>
          show && suggestions.length > 0
            ? setShowSuggestions(true)
            : setShowSuggestions(false)
        }
        onSubmit={search}
        loading={loading}
      />

      {error && <p className="search-error">{error}</p>}

      {loading && (
        <div className="search-loading">
          <div className="spinner"></div>
          <p className="search-loading__text">Searching...</p>
        </div>
      )}

      {!loading && searched && results.length === 0 && (
        <div className="search-empty">
          <div className="search-empty__icon">Q</div>
          <p className="search-empty__text">No results found. Try a different search.</p>
        </div>
      )}

      {!loading && results.length > 0 && (
        <div className="search-results" role="list" aria-label="Search results">
          {results.map((p, i) => (
            <SearchCard
              key={p.id ?? i}
              product={p}
              index={i}
              onClick={() => handleProductClick(p)}
            />
          ))}
        </div>
      )}

      {!searched && !loading && (
        <Recommendations
          recommendations={recommendations}
          onSelect={(p) => handleProductClick(p)}
        />
      )}
    </div>
  );
}