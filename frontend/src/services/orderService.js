import { apiGet, apiPost, apiPut } from './api'

export const getOrders = () => apiGet('/orders')
export const placeOrder = (data) => apiPost('/orders', data)
export const cancelOrder = (id) => apiPut(`/orders/${id}/cancel`)
