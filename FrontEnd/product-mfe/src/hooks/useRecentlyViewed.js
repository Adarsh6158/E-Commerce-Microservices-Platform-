import { useState, useCallback, useEffect } from 'react';

const STORAGE_KEY = 'recently_viewed_products';
const MAX_ITEMS = 10;

function loadFromStorage() {
  try {
    const raw = localStorage.getItem(STORAGE_KEY);
    return raw ? JSON.parse(raw) : [];
  } catch {
    return [];
  }
}

function saveToStorage(items) {
  try {
    localStorage.setItem(STORAGE_KEY, JSON.stringify(items));
  } catch { /* storage full or unavailable */ }
}

export function useRecentlyViewed() {
  const [items, setItems] = useState(loadFromStorage);

  // Sync across tabs
  useEffect(() => {
    const onStorage = (e) => {
      if (e.key === STORAGE_KEY) setItems(loadFromStorage());
    };

    window.addEventListener('storage', onStorage);
    return () => window.removeEventListener('storage', onStorage);
  }, []);

  const addItem = useCallback((product) => {
    if (!product?.id) return;

    setItems(prev => {
      const filtered = prev.filter(p => p.id !== product.id);
      const next = [
        {
          id: product.id,
          name: product.name,
          basePrice: product.basePrice,
          imageUrl: product.imageUrl || (product.imageUrls && product.imageUrls[0]) || ''
        },
        ...filtered,
      ].slice(0, MAX_ITEMS);

      saveToStorage(next);
      return next;
    });
  }, []);

  return { recentlyViewed: items, addRecentlyViewed: addItem };
}