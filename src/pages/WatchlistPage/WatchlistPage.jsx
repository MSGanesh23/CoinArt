import { useState, useEffect, useRef, useCallback } from 'react'
import { useLivePrices } from '../../hooks/useLivePrices'
import {
    getWatchlist, addToWatchlist, removeFromWatchlist, searchStocks,
    getTags, createTag, deleteTag, addTagToEntry, removeTagFromEntry
} from '../../services/watchlistService'
import './WatchlistPage.css'

const fmtINR = (n) => n && Number(n) > 0
    ? '₹' + Number(n).toLocaleString('en-IN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })
    : '—'

const PRESET_COLORS = [
    '#58A6FF', '#3FB950', '#FF4D6D', '#FFB400',
    '#A371F7', '#56D364', '#FF7B72', '#79C0FF'
]

export default function WatchlistPage() {
    const [watchlist, setWatchlist] = useState([])
    const [tags, setTags] = useState([])
    const [query, setQuery] = useState('')
    const [searchResults, setSearchResults] = useState([])
    const [searching, setSearching] = useState(false)
    const [activeTagFilter, setActiveTagFilter] = useState(null)
    const [message, setMessage] = useState(null)
    const [tagMode, setTagMode] = useState(false)
    const [newTagName, setNewTagName] = useState('')
    const [newTagColor, setNewTagColor] = useState('#58A6FF')
    const [assigningTag, setAssigningTag] = useState(null) // symbol being tagged
    const { prices } = useLivePrices()
    const debounceRef = useRef(null)

    const priceMap = Object.fromEntries(prices.map(p => [p.symbol, p]))

    const reload = useCallback(() => {
        Promise.all([getWatchlist(), getTags()])
            .then(([wl, t]) => { setWatchlist(wl); setTags(t) })
            .catch(() => { })
    }, [])

    useEffect(() => { reload() }, [reload])

    // Debounced search
    useEffect(() => {
        if (!query.trim()) { setSearchResults([]); return }
        clearTimeout(debounceRef.current)
        debounceRef.current = setTimeout(async () => {
            setSearching(true)
            try {
                const res = await searchStocks(query.trim())
                const watched = new Set(watchlist.map(w => w.symbol))
                setSearchResults(res.map(r => ({ ...r, inWatchlist: watched.has(r.symbol) })))
            } finally { setSearching(false) }
        }, 500)
        return () => clearTimeout(debounceRef.current)
    }, [query, watchlist])

    const flash = (type, text) => {
        setMessage({ type, text })
        setTimeout(() => setMessage(null), 3000)
    }

    const handleAdd = async (item) => {
        try {
            await addToWatchlist({ symbol: item.symbol, name: item.name, assetType: item.assetType, exchange: item.exchange })
            flash('success', `${item.symbol} added! ⭐`)
            reload()
        } catch (e) { flash('error', e.message) }
    }

    const handleRemove = async (symbol) => {
        try {
            await removeFromWatchlist(symbol)
            flash('info', `${symbol} removed`)
            reload()
            if (assigningTag === symbol) setAssigningTag(null)
        } catch (e) { flash('error', e.message) }
    }

    const handleCreateTag = async () => {
        if (!newTagName.trim()) return
        try {
            await createTag(newTagName.trim(), newTagColor)
            setNewTagName('')
            setNewTagColor('#58A6FF')
            reload()
            flash('success', `Tag "${newTagName}" created`)
        } catch (e) { flash('error', e.message) }
    }

    const handleDeleteTag = async (tagId, tagName) => {
        try {
            await deleteTag(tagId)
            if (activeTagFilter === tagId) setActiveTagFilter(null)
            reload()
            flash('info', `Tag "${tagName}" deleted`)
        } catch (e) { flash('error', e.message) }
    }

    const handleToggleTag = async (symbol, tagId, hasTag) => {
        try {
            if (hasTag) await removeTagFromEntry(symbol, tagId)
            else await addTagToEntry(symbol, tagId)
            reload()
        } catch (e) { flash('error', e.message) }
    }

    const watched = new Set(watchlist.map(w => w.symbol))

    const filtered = activeTagFilter
        ? watchlist.filter(w => w.tags?.some(t => t.id === activeTagFilter))
        : watchlist

    return (
        <div className="watchlist-page">
            <div className="page-header">
                <div>
                    <h1>⭐ Watchlist</h1>
                    <p>Search, track, and tag Indian stocks &amp; crypto</p>
                </div>
                <button className={`btn-tag-mgr ${tagMode ? 'active' : ''}`} onClick={() => setTagMode(!tagMode)}>
                    🏷 Manage Tags
                </button>
            </div>

            {message && <div className={`wl-message wl-message-${message.type}`}>{message.text}</div>}

            {/* ── Tag Manager Panel ────────────────────────────── */}
            {tagMode && (
                <div className="tag-manager-panel">
                    <h3>🏷 Your Tags</h3>
                    <div className="tag-list-mgr">
                        {tags.map(tag => (
                            <div key={tag.id} className="tag-mgr-row">
                                <span className="tag-chip" style={{ background: tag.color + '22', borderColor: tag.color, color: tag.color }}>
                                    {tag.name}
                                </span>
                                <button className="btn-remove" onClick={() => handleDeleteTag(tag.id, tag.name)}>✕</button>
                            </div>
                        ))}
                        {tags.length === 0 && <p className="text-muted" style={{ fontSize: 13 }}>No tags yet</p>}
                    </div>
                    <div className="new-tag-row">
                        <input
                            className="form-control"
                            type="text"
                            placeholder="Tag name…"
                            value={newTagName}
                            onChange={e => setNewTagName(e.target.value)}
                            onKeyDown={e => e.key === 'Enter' && handleCreateTag()}
                            style={{ flex: 1 }}
                        />
                        <div className="color-swatches">
                            {PRESET_COLORS.map(c => (
                                <button
                                    key={c}
                                    className={`color-swatch ${newTagColor === c ? 'selected' : ''}`}
                                    style={{ background: c }}
                                    onClick={() => setNewTagColor(c)}
                                />
                            ))}
                        </div>
                        <button className="btn btn-primary" onClick={handleCreateTag} disabled={!newTagName.trim()}>
                            + Create
                        </button>
                    </div>
                </div>
            )}

            {/* ── Search ───────────────────────────────────────── */}
            <div className="search-section">
                <div className="search-box">
                    <span className="search-icon">🔍</span>
                    <input
                        type="text" className="search-input"
                        placeholder="Search stocks, crypto… (e.g. Infosys, Bitcoin, HDFC)"
                        value={query}
                        onChange={e => setQuery(e.target.value)}
                    />
                    {query && <button className="search-clear" onClick={() => { setQuery(''); setSearchResults([]) }}>✕</button>}
                </div>
                {(searching || searchResults.length > 0) && (
                    <div className="search-results">
                        {searching && <div className="search-loading"><div className="spinner-sm" /> Searching…</div>}
                        {!searching && searchResults.map(item => (
                            <div key={item.symbol} className="search-result-row">
                                <div className="search-result-info">
                                    <strong>{item.symbol}</strong>
                                    <span>{item.name}</span>
                                    <span className={`badge ${item.assetType === 'CRYPTO' ? 'badge-blue' : 'badge-yellow'}`}>
                                        {item.exchange || item.assetType}
                                    </span>
                                </div>
                                <button
                                    className={`btn-star ${watched.has(item.symbol) ? 'starred' : ''}`}
                                    onClick={() => watched.has(item.symbol) ? handleRemove(item.symbol) : handleAdd(item)}
                                >
                                    {watched.has(item.symbol) ? '⭐ Watching' : '☆ Add'}
                                </button>
                            </div>
                        ))}
                        {!searching && searchResults.length === 0 && query.length > 1 && (
                            <div className="search-empty">No results found for "{query}"</div>
                        )}
                    </div>
                )}
            </div>

            {/* ── Tag Filter bar ───────────────────────────────── */}
            {tags.length > 0 && (
                <div className="tag-filter-bar">
                    <button
                        className={`tag-filter-btn ${activeTagFilter === null ? 'active' : ''}`}
                        onClick={() => setActiveTagFilter(null)}
                    > All ({watchlist.length})
                    </button>
                    {tags.map(tag => {
                        const count = watchlist.filter(w => w.tags?.some(t => t.id === tag.id)).length
                        return (
                            <button
                                key={tag.id}
                                className={`tag-filter-btn ${activeTagFilter === tag.id ? 'active' : ''}`}
                                style={activeTagFilter === tag.id
                                    ? { background: tag.color + '33', borderColor: tag.color, color: tag.color }
                                    : {}}
                                onClick={() => setActiveTagFilter(activeTagFilter === tag.id ? null : tag.id)}
                            >
                                <span className="tag-dot" style={{ background: tag.color }} />
                                {tag.name} ({count})
                            </button>
                        )
                    })}
                </div>
            )}

            {/* ── Watchlist Table ──────────────────────────────── */}
            <div className="watchlist-section">
                <h2>
                    {activeTagFilter
                        ? `🏷 ${tags.find(t => t.id === activeTagFilter)?.name} (${filtered.length})`
                        : `Your Watchlist (${watchlist.length})`}
                </h2>
                {filtered.length === 0 ? (
                    <div className="empty-state">
                        <p>{watchlist.length === 0 ? 'Your watchlist is empty. Search above to add stocks.' : 'No items with this tag.'}</p>
                    </div>
                ) : (
                    <div className="table-wrapper">
                        <table>
                            <thead>
                                <tr>
                                    <th>Symbol</th>
                                    <th>Name</th>
                                    <th>Tags</th>
                                    <th className="text-right">Price (₹)</th>
                                    <th className="text-right">24h</th>
                                    <th>Actions</th>
                                </tr>
                            </thead>
                            <tbody>
                                {filtered.map(item => {
                                    const live = priceMap[item.symbol]
                                    const price = live?.lastPrice ?? item.lastPrice
                                    const chg = live?.changePercent ?? 0
                                    const isAssigning = assigningTag === item.symbol
                                    return (
                                        <tr key={item.symbol}>
                                            <td><strong>{item.symbol}</strong></td>
                                            <td className="text-secondary">{item.name}</td>
                                            <td>
                                                <div className="tag-cell">
                                                    {item.tags?.map(tag => (
                                                        <span
                                                            key={tag.id}
                                                            className="tag-chip"
                                                            style={{ background: tag.color + '22', borderColor: tag.color, color: tag.color }}
                                                            onClick={() => handleToggleTag(item.symbol, tag.id, true)}
                                                            title="Click to remove"
                                                        >
                                                            {tag.name} ×
                                                        </span>
                                                    ))}
                                                    {tags.length > 0 && (
                                                        <button
                                                            className="btn-add-tag"
                                                            onClick={() => setAssigningTag(isAssigning ? null : item.symbol)}
                                                        >
                                                            {isAssigning ? '✕' : '+ tag'}
                                                        </button>
                                                    )}
                                                    {isAssigning && (
                                                        <div className="tag-dropdown">
                                                            {tags.map(tag => {
                                                                const has = item.tags?.some(t => t.id === tag.id)
                                                                return (
                                                                    <button
                                                                        key={tag.id}
                                                                        className={`tag-dropdown-item ${has ? 'checked' : ''}`}
                                                                        onClick={() => handleToggleTag(item.symbol, tag.id, has)}
                                                                    >
                                                                        <span className="tag-dot" style={{ background: tag.color }} />
                                                                        {tag.name}
                                                                        {has && ' ✓'}
                                                                    </button>
                                                                )
                                                            })}
                                                        </div>
                                                    )}
                                                </div>
                                            </td>
                                            <td className="text-right font-mono">{fmtINR(price)}</td>
                                            <td className={`text-right ${Number(chg) >= 0 ? 'text-green' : 'text-red'}`}>
                                                {Number(price) > 0 ? `${Number(chg) >= 0 ? '▲' : '▼'} ${Math.abs(Number(chg)).toFixed(2)}%` : '—'}
                                            </td>
                                            <td>
                                                <button className="btn-remove" onClick={() => handleRemove(item.symbol)}>
                                                    🗑 Remove
                                                </button>
                                            </td>
                                        </tr>
                                    )
                                })}
                            </tbody>
                        </table>
                    </div>
                )}
            </div>
        </div>
    )
}
