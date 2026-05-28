import { Routes, Route, Navigate } from 'react-router-dom'
import { useAuth } from './context/AuthContext'
import Layout from './components/Layout/Layout'
import LoginPage from './pages/LoginPage/LoginPage'
import RegisterPage from './pages/RegisterPage/RegisterPage'
import DashboardPage from './pages/DashboardPage/DashboardPage'
import MarketPage from './pages/MarketPage/MarketPage'
import OrdersPage from './pages/OrdersPage/OrdersPage'
import PortfolioPage from './pages/PortfolioPage/PortfolioPage'
import FundsPage from './pages/FundsPage/FundsPage'
import HistoryPage from './pages/HistoryPage/HistoryPage'
import WatchlistPage from './pages/WatchlistPage/WatchlistPage'

function ProtectedRoute({ children }) {
    const { token } = useAuth()
    return token ? children : <Navigate to="/login" replace />
}

function PublicRoute({ children }) {
    const { token } = useAuth()
    return token ? <Navigate to="/dashboard" replace /> : children
}

export default function App() {
    return (
        <Routes>
            <Route path="/login" element={<PublicRoute><LoginPage /></PublicRoute>} />
            <Route path="/register" element={<PublicRoute><RegisterPage /></PublicRoute>} />
            <Route path="/" element={<ProtectedRoute><Layout /></ProtectedRoute>}>
                <Route index element={<Navigate to="/dashboard" replace />} />
                <Route path="dashboard" element={<DashboardPage />} />
                <Route path="market" element={<MarketPage />} />
                <Route path="orders" element={<OrdersPage />} />
                <Route path="portfolio" element={<PortfolioPage />} />
                <Route path="funds" element={<FundsPage />} />
                <Route path="history" element={<HistoryPage />} />
                <Route path="watchlist" element={<WatchlistPage />} />
            </Route>
            <Route path="*" element={<Navigate to="/dashboard" replace />} />
        </Routes>
    )
}
