import { useState, useEffect, useRef } from 'react'
import { useLivePrices } from '../../hooks/useLivePrices'
import { placeOrder } from '../../services/orderService'
import { getFunds } from '../../services/fundsService'
import './MarketPage.css'

// INR formatter
const fmtINR = (n, crypto = false) =>
    '₹' + Number(n || 0).toLocaleString('en-IN', {
        minimumFractionDigits: crypto ? 2 : 2,
        maximumFractionDigits: crypto ? 2 : 2
    })

const fmtQty = (n) => Number(n || 0).toLocaleString('en-IN', { maximumFractionDigits: 8 })

export default function MarketPage() {
    const { prices, connected, lastUpdated } = useLivePrices()
    const [funds, setFunds] = useState(null)
    const [modal, setModal] = useState(null)
    const [orderForm, setOrderForm] = useState({ quantity: '' })
    const [submitting, setSubmitting] = useState(false)
    const [message, setMessage] = useState(null)
    const [flashMap, setFlashMap] = useState({})
    const [filter, setFilter] = useState('ALL')
    const prevPricesRef = useRef({})

    useEffect(() => {
        getFunds().then(setFunds).catch(() => { })
    }, [])

    // Flash animation when price changes
    useEffect(() => {
        if (!prices.length) return
        const newFlash = {}
        prices.forEach(item => {
            const prev = prevPricesRef.current[item.symbol]
            if (prev !== undefined && prev !== Number(item.lastPrice)) {
                newFlash[item.symbol] = Number(item.lastPrice) > prev ? 'flash-up' : 'flash-down'
            }
            prevPricesRef.current[item.symbol] = Number(item.lastPrice)
        })
        if (Object.keys(newFlash).length > 0) {
            setFlashMap(newFlash)
            setTimeout(() => setFlashMap({}), 800)
        }
    }, [prices])

    const filtered = prices.filter(i => filter === 'ALL' || i.type === filter)

    const openModal = (instrument, type) => {
        setModal({ instrument, type })
        setOrderForm({ quantity: '' })
        setMessage(null)
    }

    const closeModal = () => setModal(null)

    const handleOrder = async () => {
        setSubmitting(true)
        setMessage(null)
        try {
            await placeOrder({
                symbol: modal.instrument.symbol,
                type: modal.type,
                price: modal.instrument.lastPrice,
                quantity: parseFloat(orderForm.quantity),
            })
            setMessage({ type: 'success', text: `${modal.type} order placed successfully! 🎉` })
            const f = await getFunds()
            setFunds(f)
        } catch (err) {
            setMessage({ type: 'error', text: err.message })
        } finally {
            setSubmitting(false)
        }
    }

    const totalValue = modal && orderForm.quantity
        ? Number(modal.instrument.lastPrice) * Number(orderForm.quantity)
        : 0

    return (
        <div className="market-page">
            <div className="page-header">
                <div>
                    <h1>Market</h1>
                    <p>Indian market — NSE stocks &amp; crypto priced in ₹ INR</p>
                </div>
                <div className="market-header-right">
                    {funds && (
                        <div className="balance-chip">
                            💰 Balance: <strong>{fmtINR(funds.balance)}</strong>
                        </div>
                    )}
                    <div className={`ws-status ${connected ? 'ws-live' : 'ws-poll'}`}>
                        <span className="ws-dot" />
                        {connected ? 'Live' : 'Polling'}
                        {lastUpdated && <span className="ws-time">{lastUpdated.toLocaleTimeString('en-IN')}</span>}
                    </div>
                </div>
            </div>

            {/* Filter tabs */}
            <div className="filter-tabs">
                {['ALL', 'CRYPTO', 'STOCK'].map(f => (
                    <button
                        key={f}
                        className={`filter-tab ${filter === f ? 'active' : ''}`}
                        onClick={() => setFilter(f)}
                    >
                        {f === 'ALL' ? '🌐 All' : f === 'CRYPTO' ? '₿ Crypto' : '📈 NSE Stocks'}
                    </button>
                ))}
            </div>

            <div className="table-wrapper">
                <table>
                    <thead>
                        <tr>
                            <th>#</th>
                            <th>Symbol</th>
                            <th>Name</th>
                            <th>Type</th>
                            <th className="text-right">Price (₹)</th>
                            <th className="text-right">24h Change</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        {filtered.map((item, idx) => (
                            <tr key={item.symbol} className={flashMap[item.symbol] || ''}>
                                <td className="text-muted">{idx + 1}</td>
                                <td><strong>{item.symbol}</strong></td>
                                <td>{item.name}</td>
                                <td>
                                    <span className={`badge ${item.type === 'CRYPTO' ? 'badge-blue' : 'badge-yellow'}`}>
                                        {item.type === 'CRYPTO' ? '₿' : '📈'} {item.type}
                                    </span>
                                </td>
                                <td className={`text-right font-mono price-cell ${flashMap[item.symbol] || ''}`}>
                                    {fmtINR(item.lastPrice)}
                                </td>
                                <td className={`text-right ${Number(item.changePercent) >= 0 ? 'text-green' : 'text-red'}`}>
                                    {Number(item.changePercent) >= 0 ? '▲' : '▼'}{' '}
                                    {Math.abs(Number(item.changePercent)).toFixed(2)}%
                                </td>
                                <td>
                                    <div className="action-btns">
                                        <button className="btn-trade btn-buy" onClick={() => openModal(item, 'BUY')}>Buy</button>
                                        <button className="btn-trade btn-sell" onClick={() => openModal(item, 'SELL')}>Sell</button>
                                    </div>
                                </td>
                            </tr>
                        ))}
                        {filtered.length === 0 && (
                            <tr><td colSpan={7} className="text-center text-muted" style={{ padding: '2rem' }}>
                                Loading market data…
                            </td></tr>
                        )}
                    </tbody>
                </table>
            </div>

            {/* Trade Modal */}
            {modal && (
                <div className="modal-overlay" onClick={closeModal}>
                    <div className="modal" onClick={e => e.stopPropagation()}>
                        <div className="modal-header">
                            <h2>
                                <span className={`badge ${modal.type === 'BUY' ? 'badge-green' : 'badge-red'}`}>{modal.type}</span>
                                &nbsp;{modal.instrument.symbol} — {modal.instrument.name}
                            </h2>
                            <button className="modal-close" onClick={closeModal}>✕</button>
                        </div>
                        <div className="modal-body">
                            <div className="order-info">
                                <span>Market Price</span>
                                <strong className="text-green">{fmtINR(modal.instrument.lastPrice)}</strong>
                            </div>
                            <div className="order-info">
                                <span>24h Change</span>
                                <strong className={Number(modal.instrument.changePercent) >= 0 ? 'text-green' : 'text-red'}>
                                    {Number(modal.instrument.changePercent) >= 0 ? '▲' : '▼'} {Math.abs(Number(modal.instrument.changePercent)).toFixed(2)}%
                                </strong>
                            </div>
                            {funds && (
                                <div className="order-info">
                                    <span>Available Balance</span>
                                    <strong>{fmtINR(funds.balance)}</strong>
                                </div>
                            )}
                            <div className="form-group" style={{ marginTop: 16 }}>
                                <label>Quantity {modal.instrument.type === 'CRYPTO' ? '(units)' : '(shares)'}</label>
                                <input
                                    className="form-control"
                                    type="number"
                                    min="0"
                                    step="any"
                                    value={orderForm.quantity}
                                    onChange={e => setOrderForm({ quantity: e.target.value })}
                                    placeholder={modal.instrument.type === 'CRYPTO' ? '0.001' : '1'}
                                    autoFocus
                                />
                            </div>
                            {orderForm.quantity && (
                                <div className="order-info order-total">
                                    <span>Total Value</span>
                                    <strong>{fmtINR(totalValue)}</strong>
                                </div>
                            )}
                            {message && (
                                <div className={message.type === 'success' ? 'success-banner' : 'error-banner'}>
                                    {message.text}
                                </div>
                            )}
                            <button
                                className={`btn btn-full ${modal.type === 'BUY' ? 'btn-primary' : 'btn-danger'}`}
                                onClick={handleOrder}
                                disabled={submitting || !orderForm.quantity}
                            >
                                {submitting ? 'Processing…' : `Confirm ${modal.type} — ${fmtINR(totalValue)}`}
                            </button>
                        </div>
                    </div>
                </div>
            )}
        </div>
    )
}
