import { useAuth } from '../../context/AuthContext'
import { useNavigate } from 'react-router-dom'
import './NavBar.css'

export default function NavBar() {
    const { user, logout } = useAuth()
    const navigate = useNavigate()

    const handleLogout = () => {
        logout()
        navigate('/login')
    }

    return (
        <header className="navbar">
            <div className="navbar-brand">
                <span className="brand-icon">◈</span>
                <span className="brand-name">CoinArt</span>
            </div>
            <div className="navbar-right">
                <div className="user-info">
                    <span className="user-avatar">{user?.username?.charAt(0).toUpperCase()}</span>
                    <span className="user-name">{user?.username}</span>
                </div>
                <button className="btn btn-ghost logout-btn" onClick={handleLogout}>
                    Logout
                </button>
            </div>
        </header>
    )
}
