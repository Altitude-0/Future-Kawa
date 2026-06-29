import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';

const API_BASE = 'http://localhost:8080/api/v1';

export default function Login() {
  const navigate = useNavigate();
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [isLoading, setIsLoading] = useState(false);

  const handleLogin = async (e) => {
    e.preventDefault();
    setError('');
    setIsLoading(true);

    try {
      const response = await axios.post(`${API_BASE}/auth/login`, {
        username: username,
        password: password
      });

      // Si le backend renvoie un token JWT, on le stocke
      if (response.data && response.data.token) {
        localStorage.setItem('futurekawa_token', response.data.token);
        localStorage.setItem('futurekawa_user', response.data.username);
        localStorage.setItem('futurekawa_email', response.data.email);
        // Redirection vers le dashboard
        navigate('/');
      }
    } catch (err) {
      if (err.response && err.response.status === 401) {
        setError("Identifiant ou mot de passe incorrect.");
      } else {
        setError("Impossible de contacter le serveur d'authentification.");
      }
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div style={{ display: 'flex', height: '100vh', justifyContent: 'center', alignItems: 'center', background: 'var(--bg-color)' }}>
      <div className="enterprise-card" style={{ width: '100%', maxWidth: '400px', display: 'flex', flexDirection: 'column', gap: '24px' }}>
        <div style={{ textAlign: 'center' }}>
          <img src="/fk-logo.png" alt="FutureKawa" style={{ height: '48px', marginBottom: '16px' }} />
          <h2 style={{ fontSize: '1.5rem', fontWeight: '600' }}>Connexion Sécurisée</h2>
          <p style={{ color: 'var(--text-secondary)' }}>Portail Administrateur FutureKawa</p>
        </div>
        
        {error && (
          <div style={{ background: 'var(--status-error-bg)', color: 'var(--status-error)', padding: '12px', borderRadius: '6px', fontSize: '0.875rem', textAlign: 'center' }}>
            {error}
          </div>
        )}

        <form style={{ display: 'flex', flexDirection: 'column', gap: '16px' }} onSubmit={handleLogin}>
          <div>
            <label style={{ display: 'block', marginBottom: '8px', fontSize: '0.875rem', fontWeight: '500' }}>Identifiant collaborateur</label>
            <input 
              type="text" 
              className="input-enterprise" 
              placeholder="admin_user" 
              required 
              value={username}
              onChange={(e) => setUsername(e.target.value)}
            />
          </div>
          <div>
            <label style={{ display: 'block', marginBottom: '8px', fontSize: '0.875rem', fontWeight: '500' }}>Mot de passe</label>
            <input 
              type="password" 
              className="input-enterprise" 
              placeholder="••••••••" 
              required 
              value={password}
              onChange={(e) => setPassword(e.target.value)}
            />
          </div>
          <button type="submit" className="btn-primary" style={{ width: '100%', padding: '12px', marginTop: '8px' }} disabled={isLoading}>
            {isLoading ? 'Authentification...' : "S'authentifier"}
          </button>
        </form>

        <div style={{ textAlign: 'center', fontSize: '0.75rem', color: 'var(--text-secondary)' }}>
          Accès restreint au personnel habilité FutureKawa
        </div>
      </div>
    </div>
  );
}
