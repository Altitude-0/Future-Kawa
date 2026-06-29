import { useState, useEffect } from 'react';
import { ShieldCheck, Filter, Loader2, Clock, User } from 'lucide-react';
import axios from 'axios';

const API_BASE = 'http://localhost:8080/api/v1';

export default function Audit() {
  const [selectedCountry, setSelectedCountry] = useState('BR');
  const [audits, setAudits] = useState([]);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState('');

  useEffect(() => {
    const fetchAudits = async () => {
      setIsLoading(true);
      setError('');
      try {
        const token = localStorage.getItem('futurekawa_token');
        const res = await axios.get(`${API_BASE}/configurations/${selectedCountry}/audit`, {
          headers: { Authorization: `Bearer ${token}` }
        });
        
        // Trier du plus récent au plus ancien
        const sorted = res.data.sort((a, b) => new Date(b.changedAt) - new Date(a.changedAt));
        setAudits(sorted);
      } catch (err) {
        console.error(err);
        if (err.response && err.response.status === 401) {
            setError("Accès refusé. Veuillez vous reconnecter (Droits ADMIN requis).");
        } else {
            setError("Impossible de récupérer l'historique d'audit.");
        }
      } finally {
        setIsLoading(false);
      }
    };

    fetchAudits();
  }, [selectedCountry]);

  const formatDate = (isoString) => {
    if (!isoString) return '-';
    return new Date(isoString).toLocaleDateString('fr-FR', { 
        year: 'numeric', month: 'long', day: 'numeric', 
        hour: '2-digit', minute: '2-digit', second: '2-digit' 
    });
  };

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '24px' }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <h1 style={{ fontSize: '1.5rem', fontWeight: '600', color: 'var(--brand-primary)', display: 'flex', alignItems: 'center', gap: '12px' }}>
            <ShieldCheck size={28} /> Journal d'Audit & Traçabilité
        </h1>
        
        <div style={{ display: 'flex', gap: '12px', alignItems: 'center' }}>
          <Filter size={18} color="var(--text-secondary)" />
          <select 
            className="input-enterprise" 
            style={{ width: '250px', background: 'var(--surface-color)' }}
            value={selectedCountry}
            onChange={(e) => setSelectedCountry(e.target.value)}
          >
            <option value="BR">Configuration Brésil (BR)</option>
            <option value="CO">Configuration Colombie (CO)</option>
            <option value="EC">Configuration Équateur (EC)</option>
          </select>
        </div>
      </div>

      <div className="enterprise-card">
        <h2 style={{ fontSize: '1.125rem', fontWeight: '600', marginBottom: '16px' }}>
            Historique des modifications ({selectedCountry})
        </h2>
        
        {error && (
          <div style={{ padding: '16px', background: 'var(--status-error-bg)', color: 'var(--status-error)', borderRadius: '8px', marginBottom: '16px' }}>
            {error}
          </div>
        )}

        {isLoading ? (
            <div style={{ display: 'flex', justifyContent: 'center', padding: '60px', color: 'var(--brand-accent)' }}>
                <Loader2 className="animate-spin" size={48} />
            </div>
        ) : (
            <div style={{ overflowX: 'auto' }}>
                <table style={{ width: '100%', borderCollapse: 'collapse', textAlign: 'left' }}>
                    <thead style={{ background: 'var(--surface-color)' }}>
                        <tr style={{ borderBottom: '2px solid var(--border-color)', color: 'var(--text-secondary)', fontSize: '0.875rem' }}>
                            <th style={{ padding: '12px 16px' }}><Clock size={14} style={{display: 'inline', marginRight: '6px'}}/>Date / Heure</th>
                            <th style={{ padding: '12px 16px' }}><User size={14} style={{display: 'inline', marginRight: '6px'}}/>Acteur (Email)</th>
                            <th style={{ padding: '12px 16px' }}>Ancienne Valeur</th>
                            <th style={{ padding: '12px 16px' }}>Nouvelle Valeur</th>
                        </tr>
                    </thead>
                    <tbody>
                        {audits.length === 0 ? (
                            <tr>
                                <td colSpan="4" style={{ textAlign: 'center', padding: '48px', color: 'var(--text-secondary)' }}>
                                    Aucune modification n'a été enregistrée pour le moment.
                                </td>
                            </tr>
                        ) : audits.map((audit) => (
                            <tr key={audit.id} style={{ borderBottom: '1px solid var(--border-color)' }} className="table-row">
                                <td style={{ padding: '16px', fontSize: '0.875rem', fontWeight: '500' }}>
                                    {formatDate(audit.changedAt)}
                                </td>
                                <td style={{ padding: '16px', color: 'var(--brand-primary)', fontWeight: '500' }}>
                                    {audit.changedByEmail || 'Système'}
                                </td>
                                <td style={{ padding: '16px', fontSize: '0.875rem', color: 'var(--text-secondary)' }}>
                                    {audit.oldValue ? (
                                        <pre style={{ margin: 0, fontFamily: 'monospace', background: 'var(--bg-color)', padding: '8px', borderRadius: '4px' }}>
                                            {JSON.stringify(JSON.parse(audit.oldValue), null, 2)}
                                        </pre>
                                    ) : '-'}
                                </td>
                                <td style={{ padding: '16px', fontSize: '0.875rem', color: 'var(--status-success)' }}>
                                    {audit.newValue ? (
                                        <pre style={{ margin: 0, fontFamily: 'monospace', background: 'var(--bg-color)', padding: '8px', borderRadius: '4px', border: '1px solid var(--status-success-bg)' }}>
                                            {JSON.stringify(JSON.parse(audit.newValue), null, 2)}
                                        </pre>
                                    ) : '-'}
                                </td>
                            </tr>
                        ))}
                    </tbody>
                </table>
            </div>
        )}
      </div>
    </div>
  );
}
