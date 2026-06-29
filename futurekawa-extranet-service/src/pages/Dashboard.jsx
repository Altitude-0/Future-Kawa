import { useState, useMemo, useEffect } from 'react';
import { Activity, AlertTriangle, Filter, CalendarDays, Loader2 } from 'lucide-react';
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer } from 'recharts';
import axios from 'axios';

const API_BASE = 'http://localhost:8080/api/v1';

export default function Dashboard() {
  const [selectedCountry, setSelectedCountry] = useState('BRESIL'); // Note: En base, le code ISO est 'BR', 'CO', 'EC', il faudra filtrer par ça
  const [containers, setContainers] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(null);

  // Vue de détail
  const [selectedContainer, setSelectedContainer] = useState(null);
  const [measurements, setMeasurements] = useState([]);
  const [isChartLoading, setIsChartLoading] = useState(false);
  const [chartPeriod, setChartPeriod] = useState('ALL'); // 10, 30, ou 'ALL'

  // 1. Récupération globale au chargement
  useEffect(() => {
    const fetchDashboardData = async () => {
      try {
        setIsLoading(true);
        // Récupère les containers et les alertes en parallèle
        const [containersRes, alertsRes] = await Promise.all([
          axios.get(`${API_BASE}/containers`),
          axios.get(`${API_BASE}/alerts`)
        ]);

        const allAlerts = alertsRes.data;
        const allContainers = containersRes.data.map(c => {
          // Attacher les alertes qui correspondent à ce container
          const containerAlerts = allAlerts.filter(a => a.containerId === c.id);
          return {
            ...c,
            alerts: containerAlerts
          };
        });

        setContainers(allContainers);
      } catch (err) {
        console.error(err);
        setError("Impossible de contacter le serveur Spring Boot. Assurez-vous qu'il est démarré sur le port 8080.");
      } finally {
        setIsLoading(false);
      }
    };

    fetchDashboardData();
  }, []);

  // 2. Récupération des mesures lorsqu'on sélectionne un container
  const handleSelectContainer = async (container) => {
    setSelectedContainer(container);
    setChartPeriod('ALL'); // Reset auto-cadrage par défaut
    if (!container.sensor || !container.sensor.id) {
      setMeasurements([]);
      return;
    }

    try {
      setIsChartLoading(true);
      const res = await axios.get(`${API_BASE}/measurements/sensor/${container.sensor.id}`);
      
      // Formatage pour le graphique Recharts
      const formatted = res.data.map(m => ({
        timestamp: new Date(m.createdAt),
        date: new Date(m.createdAt).toLocaleDateString('fr-FR', { day: '2-digit', month: '2-digit' }),
        temp: m.temperature,
        hum: m.humidity
      })).sort((a, b) => a.timestamp - b.timestamp); // Tri chronologique

      setMeasurements(formatted);
    } catch (err) {
      console.error("Erreur récupération mesures:", err);
      setMeasurements([]);
    } finally {
      setIsChartLoading(false);
    }
  };

  // Filtrage par pays et tri (FIFO)
  const filteredAndSortedContainers = useMemo(() => {
    // Map le select (BRESIL) vers le countryId (UUID) de la BDD
    const countryIdMap = { 
      'BRESIL': 'c1111111-89ab-cdef-0123-456789abcdef', 
      'COLOMBIE': 'c2222222-89ab-cdef-0123-456789abcdef', 
      'EQUATEUR': 'c3333333-89ab-cdef-0123-456789abcdef' 
    };
    const targetId = countryIdMap[selectedCountry];

    return containers
      .filter(c => c.warehouse && c.warehouse.countryId === targetId)
      .sort((a, b) => new Date(a.entryDate) - new Date(b.entryDate));
  }, [containers, selectedCountry]);

  // Filtrage du graphique selon la période (10, 30 jours ou TOUT)
  const chartData = useMemo(() => {
    if (!measurements.length) return [];
    if (chartPeriod === 'ALL') return measurements; // Auto-cadrage intelligent sur toute la période
    const now = new Date();
    const cutoff = new Date(now.setDate(now.getDate() - chartPeriod));
    return measurements.filter(m => m.timestamp >= cutoff);
  }, [measurements, chartPeriod]);

  const formatDate = (isoString) => {
    if (!isoString) return '-';
    return new Date(isoString).toLocaleDateString('fr-FR', { year: 'numeric', month: 'short', day: 'numeric', hour: '2-digit', minute: '2-digit' });
  };

  const getStatusBadge = (status) => {
    if (!status) return null;
    const s = status.toUpperCase();
    switch(s) {
      case 'COMPLIANT': return <span style={{ background: 'var(--status-success-bg)', color: 'var(--status-success)', padding: '4px 8px', borderRadius: '4px', fontSize: '0.75rem', fontWeight: '600' }}>CONFORME</span>;
      case 'WARNING': return <span style={{ background: 'var(--status-warning-bg)', color: 'var(--status-warning)', padding: '4px 8px', borderRadius: '4px', fontSize: '0.75rem', fontWeight: '600' }}>ALERTE</span>;
      case 'OUTDATED': return <span style={{ background: 'var(--status-error-bg)', color: 'var(--status-error)', padding: '4px 8px', borderRadius: '4px', fontSize: '0.75rem', fontWeight: '600' }}>PÉRIMÉ (&gt; 1 AN)</span>;
      default: return <span>{s}</span>;
    }
  };

  if (isLoading) {
    return <div style={{ display: 'flex', justifyContent: 'center', padding: '100px', color: 'var(--brand-accent)' }}><Loader2 className="animate-spin" size={48} /></div>;
  }

  if (error) {
    return <div style={{ padding: '24px', background: 'var(--status-error-bg)', color: 'var(--status-error)', borderRadius: '8px' }}><strong>Erreur de connexion API :</strong> {error}</div>;
  }

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '24px' }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <h1 style={{ fontSize: '1.5rem', fontWeight: '600', color: 'var(--brand-primary)' }}>Supervision Logistique</h1>
        
        <div style={{ display: 'flex', gap: '12px', alignItems: 'center' }}>
          <Filter size={18} color="var(--text-secondary)" />
          <select 
            className="input-enterprise" 
            style={{ width: '250px', background: 'var(--surface-color)' }}
            value={selectedCountry}
            onChange={(e) => {
              setSelectedCountry(e.target.value);
              setSelectedContainer(null);
            }}
          >
            <option value="BRESIL">Entrepôt : Brésil (BR)</option>
            <option value="COLOMBIE">Entrepôt : Colombie (CO)</option>
            <option value="EQUATEUR">Entrepôt : Équateur (EC)</option>
          </select>
        </div>
      </div>

      <div className="enterprise-card">
        <h2 style={{ fontSize: '1.125rem', fontWeight: '600', marginBottom: '16px' }}>Inventaire des Containers ({selectedCountry}) - Total: {filteredAndSortedContainers.length}</h2>
        <div style={{ overflowX: 'auto', maxHeight: '500px' }}>
          <table style={{ width: '100%', borderCollapse: 'collapse', textAlign: 'left' }}>
            <thead style={{ position: 'sticky', top: 0, background: 'var(--surface-color)', zIndex: 10 }}>
              <tr style={{ borderBottom: '2px solid var(--border-color)', color: 'var(--text-secondary)', fontSize: '0.875rem' }}>
                <th style={{ padding: '12px 8px' }}>Date de stockage ↓</th>
                <th style={{ padding: '12px 8px' }}>Référence</th>
                <th style={{ padding: '12px 8px' }}>Date de sortie</th>
                <th style={{ padding: '12px 8px' }}>Statut</th>
                <th style={{ padding: '12px 8px' }}>Alertes Actives</th>
                <th style={{ padding: '12px 8px', textAlign: 'right' }}>Analyse</th>
              </tr>
            </thead>
            <tbody>
              {filteredAndSortedContainers.length === 0 ? (
                <tr><td colSpan="6" style={{ textAlign: 'center', padding: '32px', color: 'var(--text-secondary)' }}>Aucun container trouvé pour ce pays.</td></tr>
              ) : filteredAndSortedContainers.map(container => (
                <tr key={container.id} style={{ borderBottom: '1px solid var(--border-color)' }} className="table-row">
                  <td style={{ padding: '16px 8px', fontSize: '0.875rem', color: 'var(--text-secondary)' }}>{formatDate(container.entryDate)}</td>
                  <td style={{ padding: '16px 8px', fontWeight: '600', color: 'var(--brand-primary)' }}>{container.reference}</td>
                  <td style={{ padding: '16px 8px', color: 'var(--text-secondary)', fontSize: '0.875rem' }}>{formatDate(container.exitDate)}</td>
                  <td style={{ padding: '16px 8px' }}>{getStatusBadge(container.status)}</td>
                  <td style={{ padding: '16px 8px' }}>
                    {container.alerts && container.alerts.length > 0 ? (
                      <div style={{ display: 'flex', alignItems: 'center', gap: '6px', color: 'var(--status-error)', fontSize: '0.875rem', fontWeight: '500' }}>
                        <AlertTriangle size={16} />
                        {container.alerts.length} alerte(s)
                      </div>
                    ) : (
                      <span style={{ color: 'var(--text-secondary)', fontSize: '0.875rem' }}>Aucune</span>
                    )}
                  </td>
                  <td style={{ padding: '16px 8px', textAlign: 'right' }}>
                    <button className="btn-outline" style={{ padding: '6px 12px', fontSize: '0.75rem' }} onClick={() => handleSelectContainer(container)}>
                      <Activity size={14} /> Consulter
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>

      {selectedContainer && (
        <div className="enterprise-card" style={{ borderLeft: '4px solid var(--brand-accent)', scrollMarginTop: '20px' }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: '24px' }}>
            <div>
              <h3 style={{ fontSize: '1.25rem', fontWeight: '600', color: 'var(--brand-primary)' }}>Détails du Container : {selectedContainer.reference}</h3>
              <p style={{ color: 'var(--text-secondary)', fontSize: '0.875rem', marginTop: '4px' }}>Capteur associé : {selectedContainer.sensor?.reference || 'N/A'}</p>
            </div>
            <button className="btn-outline" onClick={() => setSelectedContainer(null)}>Fermer l'analyse</button>
          </div>

          {selectedContainer.alerts && selectedContainer.alerts.length > 0 && (
            <div style={{ background: 'var(--status-error-bg)', padding: '16px', borderRadius: '6px', marginBottom: '24px', border: '1px solid #fecaca' }}>
              <h4 style={{ color: 'var(--status-error)', fontWeight: '600', fontSize: '0.9rem', marginBottom: '8px', display: 'flex', alignItems: 'center', gap: '8px' }}>
                <AlertTriangle size={18} /> Historique des Alertes
              </h4>
              <ul style={{ listStylePosition: 'inside', color: '#991b1b', fontSize: '0.875rem' }}>
                {selectedContainer.alerts.map(alert => (
                  <li key={alert.id}>
                    <strong>[{alert.type}]</strong> {alert.description} <em>(le {formatDate(alert.alertedAt)})</em>
                  </li>
                ))}
              </ul>
            </div>
          )}

          <div style={{ border: '1px solid var(--border-color)', borderRadius: '8px', padding: '16px' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '16px' }}>
              <h4 style={{ fontWeight: '600', fontSize: '1rem', display: 'flex', alignItems: 'center', gap: '8px' }}>
                <CalendarDays size={18} /> Évolution de la Température
              </h4>
              <div style={{ display: 'flex', gap: '8px', background: 'var(--bg-color)', padding: '4px', borderRadius: '6px' }}>
                <button 
                  onClick={() => setChartPeriod('ALL')} 
                  style={{ border: 'none', background: chartPeriod === 'ALL' ? 'var(--surface-color)' : 'transparent', padding: '4px 12px', borderRadius: '4px', cursor: 'pointer', fontWeight: chartPeriod === 'ALL' ? '600' : '400', boxShadow: chartPeriod === 'ALL' ? 'var(--shadow-sm)' : 'none', color: chartPeriod === 'ALL' ? 'var(--brand-primary)' : 'var(--text-secondary)' }}
                >
                  Toute la période
                </button>
                <button 
                  onClick={() => setChartPeriod(30)} 
                  style={{ border: 'none', background: chartPeriod === 30 ? 'var(--surface-color)' : 'transparent', padding: '4px 12px', borderRadius: '4px', cursor: 'pointer', fontWeight: chartPeriod === 30 ? '600' : '400', boxShadow: chartPeriod === 30 ? 'var(--shadow-sm)' : 'none', color: chartPeriod === 30 ? 'var(--brand-primary)' : 'var(--text-secondary)' }}
                >
                  30 Derniers Jours
                </button>
                <button 
                  onClick={() => setChartPeriod(10)} 
                  style={{ border: 'none', background: chartPeriod === 10 ? 'var(--surface-color)' : 'transparent', padding: '4px 12px', borderRadius: '4px', cursor: 'pointer', fontWeight: chartPeriod === 10 ? '600' : '400', boxShadow: chartPeriod === 10 ? 'var(--shadow-sm)' : 'none', color: chartPeriod === 10 ? 'var(--brand-primary)' : 'var(--text-secondary)' }}
                >
                  10 Derniers Jours
                </button>
              </div>
            </div>
            
            <div style={{ height: '350px', width: '100%' }}>
              {isChartLoading ? (
                 <div style={{ display: 'flex', height: '100%', alignItems: 'center', justifyContent: 'center', color: 'var(--text-secondary)' }}><Loader2 className="animate-spin" size={32} /></div>
              ) : chartData.length === 0 ? (
                 <div style={{ display: 'flex', height: '100%', alignItems: 'center', justifyContent: 'center', color: 'var(--text-secondary)' }}>Aucune mesure disponible sur cette période.</div>
              ) : (
                <ResponsiveContainer width="100%" height="100%">
                  <LineChart data={chartData} margin={{ top: 10, right: 10, left: 0, bottom: 0 }}>
                    <CartesianGrid strokeDasharray="3 3" vertical={false} stroke="var(--border-color)" />
                    <XAxis dataKey="date" axisLine={false} tickLine={false} tick={{fill: 'var(--text-secondary)', fontSize: 12}} dy={10} />
                    <YAxis axisLine={false} tickLine={false} tick={{fill: 'var(--text-secondary)', fontSize: 12}} dx={-10} domain={['dataMin - 2', 'dataMax + 2']} />
                    <Tooltip contentStyle={{ borderRadius: '8px', border: '1px solid var(--border-color)', boxShadow: 'var(--shadow-md)' }} />
                    <Legend wrapperStyle={{ paddingTop: '20px' }} />
                    <Line type="monotone" name="Température (°C)" dataKey="temp" stroke="#ef4444" strokeWidth={2} dot={{r: 2}} activeDot={{ r: 6 }} />
                  </LineChart>
                </ResponsiveContainer>
              )}
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
