import { NavLink } from 'react-router-dom'
import './SideNav.css'

const navItems = [
    { to: '/dashboard', label: 'Dashboard', icon: '⊡' },
    { to: '/market', label: 'Market', icon: '◉' },
    { to: '/orders', label: 'Orders', icon: '⊞' },
    { to: '/portfolio', label: 'Portfolio', icon: '◈' },
    { to: '/watchlist', label: 'Watchlist', icon: '⭐' },
    { to: '/history', label: 'History', icon: '≡' },
    { to: '/funds', label: 'Funds', icon: '$' },
]

export default function SideNav() {
    return (
        <aside className="sidenav">
            <nav className="sidenav-nav">
                {navItems.map((item) => (
                    <NavLink
                        key={item.to}
                        to={item.to}
                        className={({ isActive }) => `sidenav-item ${isActive ? 'active' : ''}`}
                    >
                        <span className="sidenav-icon">{item.icon}</span>
                        <span className="sidenav-label">{item.label}</span>
                    </NavLink>
                ))}
            </nav>
        </aside>
    )
}
