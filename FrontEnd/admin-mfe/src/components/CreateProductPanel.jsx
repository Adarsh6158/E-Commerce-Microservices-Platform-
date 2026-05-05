import React, { useState } from 'react';
import { createProduct, uploadProductImages } from '../api';

export function CreateProductPanel({ onCreated }) {
  const [form, setForm] = useState({
    name: '',
    sku: '',
    description: '',
    brand: '',
    basePrice: '',
    categoryId: ''
  });

  const [imageFiles, setImageFiles] = useState([]);
  const [imagePreviews, setImagePreviews] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const handleImageChange = (e) => {
    const files = Array.from(e.target.files);
    setImageFiles(prev => [...prev, ...files]);

    files.forEach(file => {
      const reader = new FileReader();
      reader.onload = (ev) =>
        setImagePreviews(prev => [...prev, ev.target.result]);
      reader.readAsDataURL(file);
    });
  };

  const removeImage = (index) => {
    setImageFiles(prev => prev.filter((_, i) => i !== index));
    setImagePreviews(prev => prev.filter((_, i) => i !== index));
  };

  const submit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');

    try {
      const product = await createProduct({
        ...form,
        basePrice: parseFloat(form.basePrice),
      });

      if (imageFiles.length > 0) {
        try {
          await uploadProductImages(product.id, imageFiles);
        } catch {
          console.warn('Image upload failed, product created without images');
        }
      }

      alert('Product created!');
      onCreated();
    } catch (e) {
      setError(e.message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div>
      <h3 className="panel-title">Create Product</h3>

      <form onSubmit={submit} className="create-form">
        <input
          required
          placeholder="Name"
          value={form.name}
          onChange={e => setForm(f => ({ ...f, name: e.target.value }))}
          className="form-input"
        />

        <input
          required
          placeholder="SKU"
          value={form.sku}
          onChange={e => setForm(f => ({ ...f, sku: e.target.value }))}
          className="form-input"
        />

        <input
          placeholder="Brand"
          value={form.brand}
          onChange={e => setForm(f => ({ ...f, brand: e.target.value }))}
          className="form-input"
        />

        <input
          required
          placeholder="Base Price"
          type="number"
          step="0.01"
          value={form.basePrice}
          onChange={e => setForm(f => ({ ...f, basePrice: e.target.value }))}
          className="form-input"
        />

        <textarea
          placeholder="Description"
          value={form.description}
          onChange={e => setForm(f => ({ ...f, description: e.target.value }))}
          className="form-input form-textarea"
        />

        <input
          placeholder="Category ID (optional)"
          value={form.categoryId}
          onChange={e => setForm(f => ({ ...f, categoryId: e.target.value }))}
          className="form-input"
        />

        <div className="image-upload-section">
          <label className="image-upload-label">
             📸 Add Images
            <input
              type="file"
              accept="image/jpeg,image/png,image/webp,image/gif"
              multiple
              onChange={handleImageChange}
              style={{ display: 'none' }}
            />
          </label>

          {imagePreviews.length > 0 && (
            <div className="image-preview-grid">
              {imagePreviews.map((src, i) => (
                <div key={i} className="image-preview-item">
                  <img
                    src={src}
                    alt={`Preview ${i + 1}`}
                    className="image-preview-img"
                  />
                  <button
                    type="button"
                    onClick={() => removeImage(i)}
                    className="image-preview-remove"
                  >
                    ×
                  </button>
                </div>
              ))}
            </div>
          )}
        </div>

        {error && <p className="form-error">{error}</p>}

        <button
          type="submit"
          disabled={loading}
          className="btn-action"
        >
          {loading ? 'Creating…' : 'Create Product'}
        </button>
      </form>
    </div>
  );
}