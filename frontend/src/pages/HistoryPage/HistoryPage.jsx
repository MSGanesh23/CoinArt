import { useState, useEffect } from 'react'
import { getHistory } from '../../services/historyService'

const fmt = (n) => '₹' + Number(n || 0).toLocaleString('en-IN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })
const fmtDate = (d) => new Date(d).toLocaleString('en-IN', { month: 'short', day: 'numeric', hour: '2-digit', minute: '2-digit' })

export default function HistoryPage() {
    const [history, setHistory] = useState([])
    const [loading, setLoading] = useState(true)
    const [error, setError] = useState('')

    useEffect(() => {
        getHistory()
            .then(setHistory)
            .catch(e => setError(e.message))
            .finally(() => setLoading(false))
    }, [])

    if (loading) return <div className="loading-state"><div className="spinner" /></div>

    const totalRealizedPnL = history.reduce((s, t) => s + Number(t.realizedPnl || 0), 0)

    return (
        <div>
            <div className="page-header">
                <h1>Trade History</h1>
                <p>
                    All executed trades &nbsp;·&nbsp;
                    Total Realized P&amp;L:&nbsp;
                    <strong className={totalRealizedPnL >= 0 ? 'text-green' : 'text-red'}>
                        {fmt(totalRealizedPnL)}
                    </strong>
                </p>
            </div>

            {error && <div className="error-banner">{error}</div>}

            {history.length === 0 ? (
                <div className="empty-state"><p>No executed trades yet.</p></div>
            ) : (
                <div className="table-wrapper">
                    <table>
                        <thead>
                            <tr>
                                <th>Symbol</th>
                                <th>Type</th>
                                <th className="text-right">Price</th>
                                <th className="text-right">Quantity</th>
                                <th className="text-right">Total Value</th>
                                <th className="text-right">Realized P&amp;L</th>
                                <th>Date</th>
                            </tr>
                        </thead>
                        <tbody>
                            {history.map(t => (
                                <tr key={t.id}>
                                    <td><strong>{t.symbol}</strong></td>
                                    <td><span className={`badge ${t.type === 'BUY' ? 'badge-green' : 'badge-red'}`}>{t.type}</span></td>
                                    <td className="text-right">{fmt(t.price)}</td>
                                    <td className="text-right">{t.quantity}</td>
                                    <td className="text-right">{fmt(t.total)}</td>
                                    <td className={`text-right ${Number(t.realizedPnl) > 0 ? 'text-green' : Number(t.realizedPnl) < 0 ? 'text-red' : 'text-muted'}`}>
                                        {Number(t.realizedPnl) === 0 ? '—' : fmt(t.realizedPnl)}
                                    </td>
                                    <td className="text-muted">{fmtDate(t.executedAt)}</td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                </div>
            )}
        </div>
    )
}
