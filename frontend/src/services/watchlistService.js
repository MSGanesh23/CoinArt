import { apiGet, apiPost, apiDelete } from './api'

export const getWatchlist = () => apiGet('/watchlist')
export const addToWatchlist = (item) => apiPost('/watchlist', item)
export const removeFromWatchlist = (symbol) => apiDelete(`/watchlist/${symbol}`)
export const searchStocks = (q) => apiGet(`/watchlist/search?q=${encodeURIComponent(q)}`)

// Tags
export const getTags = () => apiGet('/watchlist/tags')
export const createTag = (name, color) => apiPost('/watchlist/tags', { name, color })
export const deleteTag = (tagId) => apiDelete(`/watchlist/tags/${tagId}`)
export const addTagToEntry = (symbol, tagId) => apiPost(`/watchlist/${symbol}/tags/${tagId}`, {})
export const removeTagFromEntry = (symbol, tagId) => apiDelete(`/watchlist/${symbol}/tags/${tagId}`)
