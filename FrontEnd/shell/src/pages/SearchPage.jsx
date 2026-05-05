import React, { Suspense, lazy, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { ErrorBoundary } from '../components/ErrorBoundary';
import { onMfeEvent, MfeEvents } from '../lib/events';

const SearchApp = lazy(() => import('searchMfe/App'));

function LoadingFallback() {
  return (
    <div className="search-loading">
      <div className="spinner"></div>
      <p className="search-loading__text">Loading search…</p>
    </div>
  );
}

export default function SearchPage() {
  const navigate = useNavigate();

  useEffect(() => {
    return onMfeEvent(MfeEvents.PRODUCT_VIEW, ({ productId }) => {
      if (productId) navigate(`/products?id=${productId}`);
    });
  }, [navigate]);

  return (
    <ErrorBoundary>
      <Suspense fallback={<LoadingFallback />}>
        <SearchApp />
      </Suspense>
    </ErrorBoundary>
  );
}