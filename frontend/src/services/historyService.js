import { apiGet } from './api'

export const getHistory = () => apiGet('/history')
