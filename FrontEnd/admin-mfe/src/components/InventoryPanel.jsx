import React, { useState } from 'react';
import { checkInventory, addStock } from '../api';

export function InventoryPanel() {
  const [productId, setProductId] = useState('');
  const [inv, setInv] = useState(null);
  const [error, setError] = useState('');
  const [addQty, setAddQty] = useState('');
  const [addSku, setAddSku] = useState('');
  const [addMsg, setAddMsg] = useState('');

  const check = async () => {
    setError('');
    try {
      const data = await checkInventory(productId);
      setInv(data);
      if (!addSku && data.sku) setAddSku(data.sku);
    } catch (e) {
      setError(e.message);
      setInv(null);
    }
  };

  const handleAddStock = async () => {
    setAddMsg('');
    if (!productId || !addQty) return;
    try {
      await addStock({
        productId,
        sku: addSku || 'N/A',
        quantity: parseInt(addQty, 10),
      });
      setAddMsg(`Added ${addQty} units`);
      setAddQty('');
      check();
    } catch (e) {
      setAddMsg(e.message);
    }
  };

  return (
    <div>
      <h3 className="panel-title">Inventory Management</h3>

      <div className="inventory-form">
        <input
          value={productId}
          onChange={e => setProductId(e.target.value)}
          placeholder="Product ID"
          className="form-input"
        />
        <button onClick={check} className="btn-action">Check</button>
      </div>

      {error && (
        <p className="form-error" style={{ marginTop: 8 }}>
          {error}
        </p>
      )}

      {inv && (
        <div className="inventory-result">
          <div className="inventory-stats">
            <div className="inventory-stat">
              <span className="inventory-stat__value">
                {inv.availableQuantity}
              </span>
              <span className="inventory-stat__label">Available</span>
            </div>

            <div className="inventory-stat">
              <span className="inventory-stat__value">
                {inv.reservedQuantity}
              </span>
              <span className="inventory-stat__label">Reserved</span>
            </div>

            <div className="inventory-stat">
              <span className="inventory-stat__value">
                {(inv.availableQuantity || 0) + (inv.reservedQuantity || 0)}
              </span>
              <span className="inventory-stat__label">Total</span>
            </div>
          </div>

          <p style={{ fontSize: 13, color: '#8e9aaf', margin: '8px 0 0' }}>
            <strong>SKU:</strong> {inv.sku} &nbsp;&nbsp;
            <strong>ID:</strong> {inv.productId}
          </p>

          <div className="add-stock-section">
            <h4 className="add-stock-section__title">Add Stock</h4>

            <div className="inventory-form">
              <input
                value={addSku}
                onChange={e => setAddSku(e.target.value)}
                placeholder="SKU"
                className="form-input"
                style={{ flex: '0 0 120px' }}
              />
              <input
                value={addQty}
                onChange={e => setAddQty(e.target.value)}
                placeholder="Quantity"
                type="number"
                min="1"
                className="form-input"
                style={{ flex: '0 0 100px' }}
              />
              <button
                onClick={handleAddStock}
                className="btn-action btn-action--success"
              >
                + Add Stock
              </button>
            </div>

            {addMsg && <p className="add-stock-msg">{addMsg}</p>}
          </div>
        </div>
      )}
    </div>
  );
}