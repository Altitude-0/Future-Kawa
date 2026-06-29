import { Outlet, NavLink, useNavigate } from 'react-router-dom';
import { LayoutDashboard, Settings, Activity, ShieldCheck, LogOut } from 'lucide-react';
import './Layout.css';

export default function Layout() {
  const navigate = useNavigate();

  const handleLogout = () => {
    navigate('/login');
  };

  return (
    <div className="app-container">
      {/* Top Navigation Bar */}
      <header className="navbar">
        <div className="navbar-container">
          <div className="navbar-brand">
            <img src="/fk-logo.png" alt="FutureKawa" className="brand-logo" />
            <span className="brand-text">Extranet Siège</span>
          </div>

          <nav className="navbar-nav">
            <NavLink to="/" className={({isActive}) => isActive ? "nav-link active" : "nav-link"}>
              <LayoutDashboard size={18} />
              Dashboard
            </NavLink>
            <NavLink to="/configuration" className={({isActive}) => isActive ? "nav-link active" : "nav-link"}>
              <Settings size={18} />
              Configuration
            </NavLink>
            <NavLink to="/integration" className={({isActive}) => isActive ? "nav-link active" : "nav-link"}>
              <Activity size={18} />
              Intégration
            </NavLink>
            <NavLink to="/audit" className={({isActive}) => isActive ? "nav-link active" : "nav-link"}>
              <ShieldCheck size={18} />
              Audit
            </NavLink>
          </nav>

          <div className="navbar-actions">
            <div className="user-profile">
              <div className="avatar">{(localStorage.getItem('futurekawa_email') || 'admin@futurekawa.com').substring(0, 2).toUpperCase()}</div>
              <div className="user-info">
                <span className="user-name">{localStorage.getItem('futurekawa_email') || 'admin@futurekawa.com'}</span>
              </div>
            </div>
            <button className="btn-logout" onClick={handleLogout} title="Déconnexion">
              <LogOut size={18} />
            </button>
          </div>
        </div>
      </header>

      {/* Main Content Area */}
      <main className="main-content">
        <div className="content-wrapper">
          <Outlet />
        </div>
      </main>
    </div>
  );
}
