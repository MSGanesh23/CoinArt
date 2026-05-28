import { apiPost } from './api'

export const register = (data) => apiPost('/auth/register', data)
export const login = (data) => apiPost('/auth/login', data)
