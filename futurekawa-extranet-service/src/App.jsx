import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import Layout from './components/Layout';

import Login from './pages/Login';
import Dashboard from './pages/Dashboard';
import Configuration from './pages/Configuration';
import Audit from './pages/Audit';

export default function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/login" element={<Login />} />
        
        <Route element={<Layout />}>
          <Route path="/" element={<Dashboard />} />
          <Route path="/configuration" element={<Configuration />} />
          <Route path="/integration" element={
            <div className="enterprise-card" style={{textAlign: 'center', padding: '60px'}}>
              <h2 style={{fontSize: '1.5rem', marginBottom: '16px'}}>Intégration</h2>
              <p style={{color: 'var(--text-secondary)'}}>Fonctionnalité en cours de développement (Phase 2).</p>
            </div>
          } />
          <Route path="/audit" element={<Audit />} />
        </Route>
        
        {/* Fallback */}
        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
    </BrowserRouter>
  );
}
