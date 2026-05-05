import React, { useEffect, useState } from 'react';
import { fetchProducts } from '../api';

export function ProductsPanel() {
  const [products, setProducts] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchProducts()
      .then(setProducts)
      .catch(() => {})
      .finally(() => setLoading(false));
  }, []);

  if (loading) return <p className="panel-loading">Loading…</p>;

  return (
    <div>
      <h3 className="panel-title">All Products ({products.length})</h3>
      <table className="admin-table">
        <thead>
          <tr>
            <th>Name</th>
            <th>SKU</th>
            <th>Brand</th>
            <th>Price</th>
            <th>Active</th>
          </tr>
        </thead>
        <tbody>
          {products.map(p => (
            <tr key={p.id}>
              <td>{p.name}</td>
              <td>{p.sku}</td>
              <td>{p.brand}</td>
              <td>${p.basePrice}</td>
              <td>{p.active ? '✓' : '✗'}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}