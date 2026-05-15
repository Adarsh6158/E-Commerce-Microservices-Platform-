import React from 'react';
import './SettingsPage.css';

export function SettingsPage() {
  return (
    <div className="settings-page">
      <div className="settings-container">
        <header className="settings-header">
          <h1 className="settings-title">Account Settings</h1>
          <p className="settings-subtitle">Manage your account preferences, themes, and personal details.</p>
        </header>

        <div className="settings-grid">
          <aside className="settings-nav">
            <button className="settings-nav-item active">General</button>
            <button className="settings-nav-item">Theme & Appearance</button>
            <button className="settings-nav-item">Language</button>
            <button className="settings-nav-item">Addresses</button>
            <button className="settings-nav-item">Security</button>
          </aside>

          <main className="settings-content">
            <section className="settings-section">
              <h2 className="section-title">General Preferences</h2>
              <div className="placeholder-content">
                <p>Theme, Language, and Address settings are coming soon.</p>
                <div className="placeholder-illustration">⚙️</div>
              </div>
            </section>
          </main>
        </div>
      </div>
    </div>
  );
}
