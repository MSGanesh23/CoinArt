import { useState, useEffect } from 'react'
import { getFunds, deposit, withdraw } from '../../services/fundsService'
import './FundsPage.css'

const fmt = (n) => '₹' + Number(n || 0).toLocaleString('en-IN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })

function TransactionForm({ title, btnLabel, btnClass, onSubmit }) {
    const [amount, setAmount] = useState('')
    const [loading, setLoading] = useState(false)
    const [message, setMessage] = useState(null)

    const handleSubmit = async (e) => {
        e.preventDefault()
        setMessage(null)
        setLoading(true)
        try {
            await onSubmit(parseFloat(amount))
            setMessage({ type: 'success', text: `${title} successful!` })
            setAmount('')
        } catch (err) {
            setMessage({ type: 'error', text: err.message })
        } finally {
            setLoading(false)
        }
    }

    return (
        <div className="card">
            <h3 className="form-card-title">{title}</h3>
            <form onSubmit={handleSubmit}>
                <div className="form-group">
                    <label>Amount (₹ INR)</label>
                    <input
                        className="form-control"
                        type="number"
                        min="0.01"
                        step="0.01"
                        value={amount}
                        onChange={e => setAmount(e.target.value)}
                        placeholder="0.00"
                        required
                    />
                </div>
                {message && (
                    <div className={message.type === 'success' ? 'success-banner' : 'error-banner'} style={{ marginBottom: 12 }}>
                        {message.text}
                    </div>
                )}
                <button className={`btn ${btnClass} btn-full`} type="submit" disabled={loading || !amount}>
                    {loading ? 'Processing…' : btnLabel}
                </button>
            </form>
        </div>
    )
}

export default function FundsPage() {
    const [funds, setFunds] = useState(null)
    const [loading, setLoading] = useState(true)

    const load = () => getFunds().then(setFunds).catch(console.error).finally(() => setLoading(false))

    useEffect(() => { load() }, [])

    const handleDeposit = async (amount) => { await deposit(amount); await load() }
    const handleWithdraw = async (amount) => { await withdraw(amount); await load() }

    if (loading) return <div className="loading-state"><div className="spinner" /></div>

    return (
        <div>
            <div className="page-header">
                <h1>Funds</h1>
                <p>Manage your trading capital</p>
            </div>

            <div className="funds-stats">
                <div className="funds-stat-card accent">
                    <p className="funds-stat-label">Available Balance</p>
                    <p className="funds-stat-value">{fmt(funds?.balance)}</p>
                </div>
                <div className="funds-stat-card">
                    <p className="funds-stat-label">Invested</p>
                    <p className="funds-stat-value">{fmt(funds?.invested)}</p>
                </div>
                <div className="funds-stat-card">
                    <p className="funds-stat-label">Total Portfolio Value</p>
                    <p className="funds-stat-value">{fmt(funds?.totalValue)}</p>
                </div>
            </div>

            <div className="funds-forms">
                <TransactionForm title="Deposit Funds" btnLabel="Deposit" btnClass="btn-primary" onSubmit={handleDeposit} />
                <TransactionForm title="Withdraw Funds" btnLabel="Withdraw" btnClass="btn-danger" onSubmit={handleWithdraw} />
            </div>
        </div>
    )
}
