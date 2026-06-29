import { useState, useEffect } from 'react';
import { Settings, Save, AlertCircle, Loader2 } from 'lucide-react';
import axios from 'axios';

const API_BASE = 'http://localhost:8080/api/v1';

export default function Configuration() {
  const [selectedCountry, setSelectedCountry] = useState('BR');
  const [config, setConfig] = useState(null);
  const [isSaved, setIsSaved] = useState(false);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState('');

  // Map local select (BR, CO, EC) to display labels
  const countryLabels = {
    'BR': 'BRESIL',
    'CO': 'COLOMBIE',
    'EC': 'EQUATEUR'
  };

  // 1. Fetch Configuration on mount or country change
  useEffect(() => {
    const fetchConfig = async () => {
      setIsLoading(true);
      setError('');
      try {
        const token = localStorage.getItem('futurekawa_token');
        const res = await axios.get(`${API_BASE}/configurations/${selectedCountry}`, {
          headers: { Authorization: `Bearer ${token}` }
        });
        setConfig(res.data);
      } catch (err) {
        console.error(err);
        if (err.response && (err.response.status === 401 || err.response.status === 403)) {
            setError("Accès refusé. Veuillez vous déconnecter puis vous reconnecter avec vos identifiants administrateur.");
        } else {
            setError("Impossible de récupérer la configuration pour ce pays.");
        }
      } finally {
        setIsLoading(false);
      }
    };

    fetchConfig();
  }, [selectedCountry]);

  const handleCountryChange = (e) => {
    setSelectedCountry(e.target.value);
    setIsSaved(false);
  };

  const handleSave = async (e) => {
    e.preventDefault();
    setIsSaved(false);
    setError('');
    
    try {
      const token = localStorage.getItem('futurekawa_token');
      await axios.put(`${API_BASE}/configurations/${selectedCountry}`, config, {
        headers: { Authorization: `Bearer ${token}` }
      });
      setIsSaved(true);
      setTimeout(() => setIsSaved(false), 3000);
    } catch (err) {
      console.error(err);
      if (err.response && (err.response.status === 401 || err.response.status === 403)) {
          setError("Session expirée. Veuillez vous déconnecter et vous reconnecter.");
      } else {
          setError("Erreur lors de la sauvegarde.");
      }
    }
  };

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '24px', maxWidth: '900px' }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <div>
          <h1 style={{ fontSize: '1.5rem', fontWeight: '600', color: 'var(--brand-primary)' }}>Gestion des Configurations</h1>
          <p style={{ color: 'var(--text-secondary)', fontSize: '0.875rem', marginTop: '4px' }}>Ajustez les seuils d'alerte pour les entrepôts par pays.</p>
        </div>
        <span style={{ background: 'var(--brand-primary)', color: 'white', padding: '6px 12px', borderRadius: '4px', fontSize: '0.75rem', fontWeight: '600', display: 'flex', alignItems: 'center', gap: '6px' }}>
          <Settings size={14} /> Accès Administrateur
        </span>
      </div>

      <div className="enterprise-card" style={{ display: 'flex', gap: '32px' }}>
        {/* Menu latéral de sélection du pays */}
        <div style={{ width: '250px', borderRight: '1px solid var(--border-color)', paddingRight: '24px' }}>
          <h3 style={{ fontSize: '0.875rem', fontWeight: '600', color: 'var(--text-secondary)', textTransform: 'uppercase', letterSpacing: '0.05em', marginBottom: '16px' }}>Pays Actifs</h3>
          <div style={{ display: 'flex', flexDirection: 'column', gap: '8px' }}>
            {Object.keys(countryLabels).map(countryCode => (
              <button
                key={countryCode}
                onClick={() => handleCountryChange({ target: { value: countryCode } })}
                style={{
                  width: '100%', textAlign: 'left', padding: '10px 16px', borderRadius: '6px', border: 'none', cursor: 'pointer',
                  fontWeight: selectedCountry === countryCode ? '600' : '500',
                  color: selectedCountry === countryCode ? 'var(--brand-accent)' : 'var(--text-primary)',
                  backgroundColor: selectedCountry === countryCode ? 'rgba(37, 99, 235, 0.08)' : 'transparent',
                  transition: 'all 0.2s'
                }}
              >
                Entrepôt : {countryLabels[countryCode]}
              </button>
            ))}
          </div>
        </div>

        {/* Formulaire de configuration */}
        <div style={{ flex: 1 }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '8px', marginBottom: '24px', paddingBottom: '16px', borderBottom: '1px solid var(--border-color)' }}>
            <h2 style={{ fontSize: '1.25rem', fontWeight: '600', color: 'var(--brand-primary)' }}>Paramètres : {countryLabels[selectedCountry]}</h2>
          </div>
          
          {error && (
              <div style={{ padding: '16px', background: 'var(--status-error-bg)', color: 'var(--status-error)', borderRadius: '8px', marginBottom: '16px' }}>
                  {error}
              </div>
          )}

          {isLoading || !config ? (
              <div style={{ display: 'flex', justifyContent: 'center', padding: '60px', color: 'var(--brand-accent)' }}>
                  <Loader2 className="animate-spin" size={48} />
              </div>
          ) : (
          <form onSubmit={handleSave} style={{ display: 'flex', flexDirection: 'column', gap: '24px' }}>
            
            <div style={{ background: 'var(--bg-color)', padding: '16px', borderRadius: '6px', display: 'flex', gap: '12px', alignItems: 'flex-start' }}>
              <AlertCircle size={20} color="var(--status-warning)" style={{ flexShrink: 0, marginTop: '2px' }} />
              <p style={{ fontSize: '0.875rem', color: 'var(--text-secondary)', lineHeight: '1.5' }}>
                Toute modification de ces seuils affectera immédiatement la génération des alertes (statut ALERTE) pour tous les containers physiquement stockés dans le pays sélectionné.
              </p>
            </div>

            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '24px' }}>
              <div>
                <label style={{ display: 'block', marginBottom: '8px', fontSize: '0.875rem', fontWeight: '500', color: 'var(--text-primary)' }}>Température Idéale (°C)</label>
                <div style={{ position: 'relative' }}>
                  <input type="number" step="0.1" className="input-enterprise" value={config.temperatureIdeal} onChange={(e) => setConfig({...config, temperatureIdeal: parseFloat(e.target.value)})} required />
                  <span style={{ position: 'absolute', right: '12px', top: '9px', color: 'var(--text-secondary)', fontSize: '0.875rem' }}>°C</span>
                </div>
              </div>
              <div>
                <label style={{ display: 'block', marginBottom: '8px', fontSize: '0.875rem', fontWeight: '500', color: 'var(--text-primary)' }}>Tolérance Température (±)</label>
                <div style={{ position: 'relative' }}>
                  <input type="number" step="0.1" className="input-enterprise" value={config.temperatureTolerance} onChange={(e) => setConfig({...config, temperatureTolerance: parseFloat(e.target.value)})} required />
                  <span style={{ position: 'absolute', right: '12px', top: '9px', color: 'var(--text-secondary)', fontSize: '0.875rem' }}>°C</span>
                </div>
              </div>
            </div>

            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '24px' }}>
              <div>
                <label style={{ display: 'block', marginBottom: '8px', fontSize: '0.875rem', fontWeight: '500', color: 'var(--text-primary)' }}>Humidité Idéale (%)</label>
                <div style={{ position: 'relative' }}>
                  <input type="number" step="0.1" className="input-enterprise" value={config.humidityIdeal} onChange={(e) => setConfig({...config, humidityIdeal: parseFloat(e.target.value)})} required />
                  <span style={{ position: 'absolute', right: '12px', top: '9px', color: 'var(--text-secondary)', fontSize: '0.875rem' }}>%</span>
                </div>
              </div>
              <div>
                <label style={{ display: 'block', marginBottom: '8px', fontSize: '0.875rem', fontWeight: '500', color: 'var(--text-primary)' }}>Tolérance Humidité (±)</label>
                <div style={{ position: 'relative' }}>
                  <input type="number" step="0.1" className="input-enterprise" value={config.humidityTolerance} onChange={(e) => setConfig({...config, humidityTolerance: parseFloat(e.target.value)})} required />
                  <span style={{ position: 'absolute', right: '12px', top: '9px', color: 'var(--text-secondary)', fontSize: '0.875rem' }}>%</span>
                </div>
              </div>
            </div>

            <div style={{ display: 'flex', justifyContent: 'flex-end', marginTop: '16px', gap: '16px', alignItems: 'center' }}>
              {isSaved && <span style={{ color: 'var(--status-success)', fontSize: '0.875rem', fontWeight: '500' }}>Configuration sauvegardée avec succès.</span>}
              <button type="submit" className="btn-primary">
                <Save size={16} /> Enregistrer
              </button>
            </div>
          </form>
          )}
        </div>
      </div>
    </div>
  );
}
