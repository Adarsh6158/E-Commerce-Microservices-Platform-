import React, { useState, useEffect } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { useAuth } from '../lib/auth';
import { onMfeEvent, MfeEvents } from '../lib/events';
import './Layout.css';

export function Layout({ children }) {
  const { user, logout, isAuthenticated } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();

  const [menuOpen, setMenuOpen] = useState(false);
  const [cartCount, setCartCount] = useState(0);

  // Fetch cart count
  useEffect(() => {
    const loadCartCount = async () => {
      const token = localStorage.getItem('access_token');
      if (!token) {
        setCartCount(0);
        return;
      }

      try {
        const res = await fetch('http://localhost:8080/api/cart', {
          headers: {
            'Content-Type': 'application/json',
            Authorization: `Bearer ${token}`,
          },
        });

        if (res.ok) {
          const data = await res.json();
          setCartCount(data?.items?.length || 0);
        }
      } catch {
        // ignore
      }
    };

    loadCartCount();
    return onMfeEvent(MfeEvents.CART_UPDATED, loadCartCount);
  }, [isAuthenticated]);

  const isActive = (path) =>
    location.pathname === path ||
    (path !== '/' && location.pathname.startsWith(path));

  const navItems = [
    { to: '/', label: 'Products', icon: '📦' },
    { to: '/search', label: 'Search', icon: '🔍' },
    {
      to: '/cart',
      label: 'Cart',
      icon: '🛒',
      badge: cartCount > 0 ? cartCount : null,
    },
    ...(isAuthenticated ? [{ to: '/orders', label: 'Orders', icon: '📄' }] : []),
    ...(user?.roles?.includes('ADMIN')
      ? [{ to: '/admin', label: 'Admin', icon: '⚙️' }]
      : []),
  ];

  return (
    <div>
      <header className="header">
        <div className="header__inner">
          <Link to="/" className="header__logo">
            <span className="header__logo-icon">🛍️</span>
            <span className="header__logo-text">ShopFlux</span>
          </Link>

          <button
            className="header__hamburger"
            onClick={() => setMenuOpen(!menuOpen)}
            aria-label="Toggle menu"
          >
            {menuOpen ? '✖' : '☰'}
          </button>

          <nav className={`header__nav${menuOpen ? ' header__nav--open' : ''}`}>
            {navItems.map(({ to, label, icon, badge }) => (
              <Link
                key={to}
                to={to}
                className={`nav-link ${isActive(to) ? 'nav-link--active' : ''}`}
                onClick={() => setMenuOpen(false)}
              >
                <span className="nav-link__icon">{icon}</span>
                {label}
                {badge != null && (
                  <span className="nav-link__badge">{badge}</span>
                )}
              </Link>
            ))}
          </nav>

          <div className="header__actions">
            {isAuthenticated ? (
              <>
                <Link to="/profile" className="avatar-link">
                  {user?.profileImageUrl ? (
                    <img
                      src={user.profileImageUrl}
                      alt=""
                      className="avatar avatar--img"
                    />
                  ) : (
                    <div className="avatar">
                      {(user?.firstName || user?.email || 'U')[0].toUpperCase()}
                    </div>
                  )}
                </Link>

                <Link to="/profile" className="header__username">
                  {user?.firstName || user?.email}
                </Link>

                <button
                  onClick={() => {
                    logout();
                    navigate('/');
                  }}
                  className="btn-logout"
                >
                  Sign out
                </button>
              </>
            ) : (
              <Link to="/login" className="btn-signin">
                Sign in
              </Link>
            )}
          </div>
        </div>
      </header>

      <main className="main-content">{children}</main>

      <footer className="footer">
        <div className="footer__inner">
          <div className="footer__grid">
            <div className="footer__brand">
              <div className="footer__brand-name">🛍️ ShopFlux</div>
              <p className="footer__brand-desc">
                Your one-stop destination for quality products at great prices.
                Fast shipping and excellent customer service.
              </p>
            </div>

            <div>
              <h4 className="footer__col-title">Shop</h4>
              <ul className="footer__links">
                <li><Link to="/">All Products</Link></li>
                <li><Link to="/search">Search</Link></li>
                <li><Link to="/cart">Cart</Link></li>
              </ul>
            </div>

            <div>
              <h4 className="footer__col-title">Account</h4>
              <ul className="footer__links">
                <li><Link to="/login">Sign In</Link></li>
                <li><Link to="/orders">Orders</Link></li>
              </ul>
            </div>

            <div>
              <h4 className="footer__col-title">Support</h4>
              <ul className="footer__links">
                <li><a href="#">Help Center</a></li>
                <li><a href="#">Shipping Info</a></li>
                <li><a href="#">Returns</a></li>
              </ul>
            </div>
          </div>

          <div className="footer__bottom">
            <span>© 2026 ShopFlux. All rights reserved.</span>
            <div className="footer__social">
            <a href="https://twitter.com/your_username" target="_blank" aria-label="Twitter">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="currentColor">
              <path d="M22.46 6c-.77.35-1.5.6-2.3.7a3.92 3.92 0 0 0 1.7-2.2 7.72 7.72 0 0 1-2.5 1A3.9 3.9 0 0 0 16.1 4c-2.2 0-4 1.8-4 4 0 .3 0 .6.1.9A11.1 11.1 0 0 1 3 5.1a4 4 0 0 0-.5 2c0 1.4.7 2.6 1.8 3.3a4 4 0 0 1-1.8-.5v.1c0 2 1.4 3.6 3.2 4a4 4 0 0 1-1.8.1c.5 1.6 2 2.7 3.7 2.8A7.8 7.8 0 0 1 2 19.5 11 11 0 0 0 8 21c7.2 0 11.2-6 11.2-11.2v-.5c.8-.6 1.5-1.3 2-2.3z"/>
            </svg>
            </a>

            <a href="https://github.com/your_username" target="_blank" aria-label="GitHub">
              <svg width="20" height="20" viewBox="0 0 24 24" fill="currentColor">
                <path d="M12 .5C5.7.5.7 5.6.7 12c0 5.1 3.3 9.4 7.9 10.9.6.1.8-.3.8-.6v-2.2c-3.2.7-3.9-1.5-3.9-1.5-.5-1.3-1.2-1.6-1.2-1.6-1-.7.1-.7.1-.7 1.1.1 1.7 1.2 1.7 1.2 1 .1 2-.7 2.4-1.1.1-.7.4-1.2.7-1.5-2.6-.3-5.3-1.3-5.3-5.8 0-1.3.5-2.3 1.2-3.2-.1-.3-.5-1.5.1-3.1 0 0 1-.3 3.2 1.2a11 11 0 0 1 5.8 0C17.2 2.7 18.2 3 18.2 3c.6 1.6.2 2.8.1 3.1.8.9 1.2 2 1.2 3.2 0 4.5-2.7 5.5-5.3 5.8.4.4.8 1 .8 2v3c0 .3.2.7.8.6A11.3 11.3 0 0 0 23.3 12c0-6.4-5-11.5-11.3-11.5z"/>
              </svg>
            </a>
            </div>
          </div>
        </div>
      </footer>
    </div>
  );
}