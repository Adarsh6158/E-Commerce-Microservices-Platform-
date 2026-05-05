import React, { useState } from 'react';
import { ProductsPanel } from './components/ProductsPanel';
import { InventoryPanel } from './components/InventoryPanel';
import { AnalyticsPanel } from './components/AnalyticsPanel';
import { CreateProductPanel } from './components/CreateProductPanel';
import './App.css';

const TABS = [
  { key: 'products', label: 'Products' },
  { key: 'inventory', label: 'Inventory' },
  { key: 'analytics', label: 'Analytics' },
  { key: 'create-product', label: '+ Create Product' },
];

export default function App() {
  const [tab, setTab] = useState('products');

  return (
    <div>
      <div className="admin-header">
        <h2 className="admin-header__title">Admin Dashboard</h2>
      </div>

      <div className="admin-tabs" role="tablist">
        {TABS.map(t => (
          <button
            key={t.key}
            role="tab"
            aria-selected={tab === t.key}
            onClick={() => setTab(t.key)}
            className={`admin-tab${tab === t.key ? ' admin-tab--active' : ''}`}
          >
            {t.label}
          </button>
        ))}
      </div>

      {tab === 'products' && <ProductsPanel />}
      {tab === 'inventory' && <InventoryPanel />}
      {tab === 'analytics' && <AnalyticsPanel />}
      {tab === 'create-product' && (
        <CreateProductPanel onCreated={() => setTab('products')} />
      )}
    </div>
  );
}