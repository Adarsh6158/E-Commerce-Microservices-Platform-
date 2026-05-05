import React, { useState, useRef, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../lib/auth';
import { api } from '../lib/api';
import './ProfilePage.css';

export function ProfilePage() {
  const { user, isAuthenticated, updateProfile } = useAuth();
  const navigate = useNavigate();
  const fileInputRef = useRef(null);

  const [firstName, setFirstName] = useState('');
  const [lastName, setLastName] = useState('');
  const [profileImageUrl, setProfileImageUrl] = useState('');
  const [saving, setSaving] = useState(false);
  const [success, setSuccess] = useState('');
  const [error, setError] = useState('');
  const [loadingProfile, setLoadingProfile] = useState(true);

  useEffect(() => {
    if (!isAuthenticated) {
      navigate('/login');
      return;
    }

    api.get('/auth/profile')
      .then(data => {
        setFirstName(data.firstName || '');
        setLastName(data.lastName || '');
        setProfileImageUrl(data.profileImageUrl || '');
      })
      .catch(() => {
        setFirstName(user?.firstName || '');
        setLastName(user?.lastName || '');
        setProfileImageUrl(user?.profileImageUrl || '');
      })
      .finally(() => setLoadingProfile(false));
  }, [isAuthenticated, navigate, user]);

  const handleImageUpload = (e) => {
    const file = e.target.files[0];
    if (!file) return;

    if (file.size > 500 * 1024) {
      setError('Image must be under 500KB');
      return;
    }

    if (!file.type.startsWith('image/')) {
      setError('Please select an image file');
      return;
    }

    const reader = new FileReader();
    reader.onload = () => setProfileImageUrl(reader.result);
    reader.readAsDataURL(file);
    setError('');
  };

  const handleSave = async (e) => {
    e.preventDefault();
    setSaving(true);
    setError('');
    setSuccess('');

    try {
      await updateProfile({
        firstName,
        lastName,
        profileImageUrl: profileImageUrl || null
      });
      setSuccess('Profile updated successfully!');
      setTimeout(() => setSuccess(''), 3000);
    } catch (err) {
      setError(err.message || 'Failed to update profile');
    } finally {
      setSaving(false);
    }
  };

  if (!isAuthenticated) return null;

  if (loadingProfile) {
    return (
      <div className="profile-page">
        <div className="spinner"></div>
      </div>
    );
  }

  const avatarLetter = (firstName || user?.email || 'U')[0].toUpperCase();

  return (
    <div className="profile-page">
      <div className="profile-card">
        <div className="profile-card__banner" />

        <div className="profile-card__avatar-wrap">
          <div
            className="profile-card__avatar"
            onClick={() => fileInputRef.current?.click()}
          >
            {profileImageUrl ? (
              <img src={profileImageUrl} alt="Profile" />
            ) : (
              avatarLetter
            )}
            <div className="profile-card__avatar-overlay">Change</div>
          </div>

          <input
            ref={fileInputRef}
            type="file"
            accept="image/*"
            className="file-input-hidden"
            onChange={handleImageUpload}
          />

          <h2 className="profile-card__name">
            {firstName} {lastName}
          </h2>
          <p className="profile-card__email">{user?.email}</p>
          {user?.roles && (
            <span className="profile-card__role-badge">{user.roles}</span>
          )}
        </div>

        <form onSubmit={handleSave} className="profile-form">
          <div className="profile-form__row">
            <div className="form-group">
              <label className="form-label">First Name</label>
              <input
                type="text"
                value={firstName}
                onChange={(e) => setFirstName(e.target.value)}
                className="form-input"
              />
            </div>

            <div className="form-group">
              <label className="form-label">Last Name</label>
              <input
                type="text"
                value={lastName}
                onChange={(e) => setLastName(e.target.value)}
                className="form-input"
              />
            </div>
          </div>

          {error && <p className="profile-error">{error}</p>}
          {success && <p className="profile-success">{success}</p>}

          <div className="profile-form__actions">
            <button
              type="submit"
              disabled={saving}
              className="btn-save-profile"
            >
              {saving ? 'Saving...' : 'Save Profile'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}