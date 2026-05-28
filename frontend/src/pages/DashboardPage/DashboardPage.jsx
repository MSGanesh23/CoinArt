import { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import { useAuth } from '../../context/AuthContext'
import { getFunds } from '../../services/fundsService'
import { getPortfolio } from '../../services/portfolioService'
import { getOrders } from '../../services/orderService'
import './DashboardPage.css'

const fmt = (n) => Number(n || 0).toLocaleString('en-US', { style: 'currency', currency: 'USD' })
const fmtPct = (n) => `${Number(n || 0) >= 0 ? '+' : ''}${Number(n || 0).toFixed(2)}%`

export default function DashboardPage() {
    const { user } = useAuth()
    const navigate = useNavigate()
    const [funds, setFunds] = useState(null)
    const [portfolio, setPortfolio] = useState([])
    const [orders, setOrders] = useState([])
    const [loading, setLoading] = useState(true)

    useEffect(() => {
        Promise.all([getFunds(), getPortfolio(), getOrders()])
            .then(([f, p, o]) => { setFunds(f); setPortfolio(p); setOrders(o) })
            .catch(console.error)
            .finally(() => setLoading(false))
    }, [])

    if (loading) return <div className="loading-state"><div className="spinner" /></div>

    const totalPnL = portfolio.reduce((s, h) => s + Number(h.unrealizedPnL || 0), 0)
    const recentOrders = orders.slice(0, 5)

    return (
        <div className="dashboard-page">
            <div className="page-header">
                <h1>Welcome back, {user?.username} 👋</h1>
                <p>Here's your trading snapshot</p>
            </div>

            <div className="stats-grid">
                <div className="stat-card">
                    <p className="stat-label">Available Balance</p>
                    <p className="stat-value">{fmt(funds?.balance)}</p>
                    <p className="stat-sub">Cash in account</p>
                </div>
                <div className="stat-card">
                    <p className="stat-label">Invested</p>
                    <p className="stat-value">{fmt(funds?.invested)}</p>
                    <p className="stat-sub">Active positions</p>
                </div>
                <div className="stat-card">
                    <p className="stat-label">Portfolio Value</p>
                    <p className="stat-value">{fmt(funds?.totalValue)}</p>
                    <p className="stat-sub">Balance + Invested</p>
                </div>
                <div className={`stat-card ${totalPnL >= 0 ? 'stat-positive' : 'stat-negative'}`}>
                    <p className="stat-label">Unrealized P&L</p>
                    <p className={`stat-value ${totalPnL >= 0 ? 'text-green' : 'text-red'}`}>{fmt(totalPnL)}</p>
                    <p className="stat-sub">Open positions</p>
                </div>
            </div>

            <div className="dashboard-grid">
                <div className="card">
                    <div className="section-header">
                        <h2>Holdings</h2>
                        <button className="btn btn-ghost" onClick={() => navigate('/portfolio')}>View All</button>
                    </div>
                    {portfolio.length === 0 ? (
                        <div className="empty-state"><p>No holdings yet. <a href="/orders">Place your first trade!</a></p></div>
                    ) : (
                        <div className="table-wrapper">
                            <table>
                                <thead>
                                    <tr><th>Symbol</th><th className="text-right">Qty</th><th className="text-right">Avg Price</th><th className="text-right">P&amp;L</th></tr>
                                </thead>
                                <tbody>
                                    {portfolio.slice(0, 5).map(h => (
                                        <tr key={h.symbol}>
                                            <td><strong>{h.symbol}</strong></td>
                                            <td className="text-right">{h.quantity}</td>
                                            <td className="text-right">{fmt(h.averagePrice)}</td>
                                            <td className={`text-right ${Number(h.unrealizedPnL) >= 0 ? 'text-green' : 'text-red'}`}>
                                                {fmt(h.unrealizedPnL)} ({fmtPct(h.unrealizedPnLPercent)})
                                            </td>
                                        </tr>
                                    ))}
                                </tbody>
                            </table>
                        </div>
                    )}
                </div>

                <div className="card">
                    <div className="section-header">
                        <h2>Recent Orders</h2>
                        <button className="btn btn-ghost" onClick={() => navigate('/orders')}>View All</button>
                    </div>
                    {recentOrders.length === 0 ? (
                        <div className="empty-state"><p>No orders yet.</p></div>
                    ) : (
                        <div className="table-wrapper">
                            <table>
                                <thead>
                                    <tr><th>Symbol</th><th>Type</th><th className="text-right">Value</th><th>Status</th></tr>
                                </thead>
                                <tbody>
                                    {recentOrders.map(o => (
                                        <tr key={o.id}>
                                            <td><strong>{o.symbol}</strong></td>
                                            <td><span className={`badge ${o.type === 'BUY' ? 'badge-green' : 'badge-red'}`}>{o.type}</span></td>
                                            <td className="text-right">{fmt(o.totalValue)}</td>
                                            <td><span className={`badge ${o.status === 'EXECUTED' ? 'badge-green' : o.status === 'CANCELLED' ? 'badge-red' : 'badge-yellow'}`}>{o.status}</span></td>
                                        </tr>
                                    ))}
                                </tbody>
                            </table>
                        </div>
                    )}
                </div>
            </div>
        </div>
    )
}
