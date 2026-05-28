import { useState, useEffect } from 'react'
import { getPortfolio } from '../../services/portfolioService'
import { useLivePrices } from '../../hooks/useLivePrices'
import './PortfolioPage.css'

const fmtINR = (n) => '₹' + Number(n || 0).toLocaleString('en-IN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })
const fmtPct = (n) => `${Number(n || 0) >= 0 ? '+' : ''}${Number(n || 0).toFixed(2)}%`

export default function PortfolioPage() {
    const [holdings, setHoldings] = useState([])
    const [loading, setLoading] = useState(true)
    const [error, setError] = useState('')
    const { connected, lastUpdated } = useLivePrices()

    const refresh = () => {
        getPortfolio()
            .then(setHoldings)
            .catch(e => setError(e.message))
            .finally(() => setLoading(false))
    }

    useEffect(() => { refresh() }, [])

    // Re-fetch portfolio P&L from backend every 15s (backend uses live prices)
    useEffect(() => {
        const id = setInterval(refresh, 15000)
        return () => clearInterval(id)
    }, [])

    if (loading) return <div className="loading-state"><div className="spinner" /></div>

    const totalInvested = holdings.reduce((s, h) => s + Number(h.avgBuyPrice || 0) * Number(h.quantity || 0), 0)
    const totalCurrent = holdings.reduce((s, h) => s + Number(h.currentValue || 0), 0)
    const totalPnL = totalCurrent - totalInvested
    const totalPnLPct = totalInvested > 0 ? (totalPnL / totalInvested) * 100 : 0

    return (
        <div>
            <div className="page-header">
                <div>
                    <h1>Portfolio</h1>
                    <p>Live P&amp;L — updated in real-time with INR market prices</p>
                </div>
                <div className={`ws-status ${connected ? 'ws-live' : 'ws-poll'}`} style={{ fontSize: 12, padding: '6px 14px' }}>
                    <span className="ws-dot" />
                    {connected ? 'Live' : 'Polling'}
                    {lastUpdated && <span style={{ marginLeft: 6, opacity: 0.7 }}>{lastUpdated.toLocaleTimeString('en-IN')}</span>}
                </div>
            </div>

            {error && <div className="error-banner">{error}</div>}

            <div className="portfolio-summary">
                <div className="summary-item">
                    <span>Invested</span>
                    <strong>{fmtINR(totalInvested)}</strong>
                </div>
                <div className="summary-divider" />
                <div className="summary-item">
                    <span>Current Value</span>
                    <strong>{fmtINR(totalCurrent)}</strong>
                </div>
                <div className="summary-divider" />
                <div className="summary-item">
                    <span>Unrealized P&amp;L</span>
                    <strong className={totalPnL >= 0 ? 'text-green' : 'text-red'}>
                        {fmtINR(totalPnL)} ({fmtPct(totalPnLPct)})
                    </strong>
                </div>
            </div>

            {holdings.length === 0 ? (
                <div className="empty-state"><p>No holdings yet. Place orders from the Market page.</p></div>
            ) : (
                <div className="table-wrapper">
                    <table>
                        <thead>
                            <tr>
                                <th>Symbol</th>
                                <th className="text-right">Quantity</th>
                                <th className="text-right">Avg Buy (₹)</th>
                                <th className="text-right">Current (₹)</th>
                                <th className="text-right">Current Value</th>
                                <th className="text-right">Unrealized P&amp;L</th>
                                <th className="text-right">Change %</th>
                            </tr>
                        </thead>
                        <tbody>
                            {holdings.map(h => (
                                <tr key={h.symbol}>
                                    <td><strong>{h.symbol}</strong></td>
                                    <td className="text-right font-mono">{Number(h.quantity).toLocaleString('en-IN', { maximumFractionDigits: 8 })}</td>
                                    <td className="text-right font-mono">{fmtINR(h.avgBuyPrice)}</td>
                                    <td className="text-right font-mono">{fmtINR(h.currentPrice)}</td>
                                    <td className="text-right font-mono">{fmtINR(h.currentValue)}</td>
                                    <td className={`text-right font-mono ${Number(h.unrealizedPnl) >= 0 ? 'text-green' : 'text-red'}`}>
                                        {fmtINR(h.unrealizedPnl)}
                                    </td>
                                    <td className={`text-right ${Number(h.unrealizedPnlPercent) >= 0 ? 'text-green' : 'text-red'}`}>
                                        {fmtPct(h.unrealizedPnlPercent)}
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
