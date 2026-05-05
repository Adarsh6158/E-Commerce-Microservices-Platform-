import React from 'react';

export function OrderSummary({ total, onPlaceOrder, placingOrder }) {
  return (
    <div className="summary-card">
      <h3 className="summary-card__title">Order Summary</h3>

      <div className="summary-row">
        <span>Subtotal</span>
        <span>${total.toFixed(2)}</span>
      </div>

      <div className="summary-row">
        <span>Shipping</span>
        <span className="summary-free">Free</span>
      </div>

      <div className="summary-row summary-row--total">
        <span>Total</span>
        <span>${total.toFixed(2)}</span>
      </div>

      <button
        onClick={onPlaceOrder}
        disabled={placingOrder}
        className="btn-place-order"
      >
        {placingOrder ? (
          <span className="btn-place-order--loading">
            <span className="btn-spinner"></span> Processing...
          </span>
        ) : (
          '🛍️ Place Order'
        )}
      </button>

      <a href="/" className="link-continue-shopping">
        ← Continue Shopping
      </a>
    </div>
  );
}