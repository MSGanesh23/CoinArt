import { useState, useEffect } from 'react'
import { getOrders, cancelOrder } from '../../services/orderService'

const fmt = (n) => Number(n || 0).toLocaleString('en-US', { style: 'currency', currency: 'USD' })
const fmtDate = (d) => new Date(d).toLocaleString('en-US', { month: 'short', day: 'numeric', hour: '2-digit', minute: '2-digit' })

export default function OrdersPage() {
    const [orders, setOrders] = useState([])
    const [tab, setTab] = useState('ALL')
    const [loading, setLoading] = useState(true)
    const [cancelling, setCancelling] = useState(null)
    const [error, setError] = useState('')

    const load = () => {
        setLoading(true)
        getOrders()
            .then(setOrders)
            .catch(e => setError(e.message))
            .finally(() => setLoading(false))
    }

    useEffect(load, [])

    const filtered = tab === 'ALL' ? orders : orders.filter(o => o.status === tab)

    const handleCancel = async (id) => {
        setCancelling(id)
        try {
            await cancelOrder(id)
            await load()
        } catch (e) {
            setError(e.message)
        } finally {
            setCancelling(null)
        }
    }

    if (loading) return <div className="loading-state"><div className="spinner" /></div>

    return (
        <div>
            <div className="page-header">
                <h1>Orders</h1>
                <p>All your placed orders</p>
            </div>

            {error && <div className="error-banner">{error}</div>}

            <div className="tabs">
                {['ALL', 'EXECUTED', 'OPEN', 'CANCELLED'].map(t => (
                    <button key={t} className={`tab-btn ${tab === t ? 'active' : ''}`} onClick={() => setTab(t)}>{t}</button>
                ))}
            </div>

            {filtered.length === 0 ? (
                <div className="empty-state">
                    <p>No {tab !== 'ALL' ? tab.toLowerCase() : ''} orders found.</p>
                </div>
            ) : (
                <div className="table-wrapper">
                    <table>
                        <thead>
                            <tr>
                                <th>Symbol</th>
                                <th>Type</th>
                                <th className="text-right">Price</th>
                                <th className="text-right">Qty</th>
                                <th className="text-right">Total</th>
                                <th>Status</th>
                                <th>Date</th>
                                <th>Action</th>
                            </tr>
                        </thead>
                        <tbody>
                            {filtered.map(o => (
                                <tr key={o.id}>
                                    <td><strong>{o.symbol}</strong></td>
                                    <td><span className={`badge ${o.type === 'BUY' ? 'badge-green' : 'badge-red'}`}>{o.type}</span></td>
                                    <td className="text-right">{fmt(o.price)}</td>
                                    <td className="text-right">{o.quantity}</td>
                                    <td className="text-right">{fmt(o.totalValue)}</td>
                                    <td>
                                        <span className={`badge ${o.status === 'EXECUTED' ? 'badge-green' : o.status === 'CANCELLED' ? 'badge-red' : 'badge-yellow'}`}>
                                            {o.status}
                                        </span>
                                    </td>
                                    <td className="text-muted">{fmtDate(o.createdAt)}</td>
                                    <td>
                                        {o.status === 'OPEN' && (
                                            <button
                                                className="btn btn-danger"
                                                style={{ padding: '4px 10px', fontSize: '12px' }}
                                                onClick={() => handleCancel(o.id)}
                                                disabled={cancelling === o.id}
                                            >
                                                {cancelling === o.id ? '…' : 'Cancel'}
                                            </button>
                                        )}
                                    </td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                </div>
            )}
        </div>
    )
}
