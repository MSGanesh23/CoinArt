import { apiGet, apiPost } from './api'

export const getFunds = () => apiGet('/funds')
export const deposit = (amount) => apiPost('/funds/deposit', { amount })
export const withdraw = (amount) => apiPost('/funds/withdraw', { amount })
