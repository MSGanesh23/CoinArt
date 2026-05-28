const BASE_URL = '/api'

function getAuthHeaders() {
    const token = localStorage.getItem('coinart_token')
    return {
        'Content-Type': 'application/json',
        ...(token ? { Authorization: `Bearer ${token}` } : {}),
    }
}

async function handleResponse(res) {
    const data = await res.json().catch(() => ({}))
    if (!res.ok) {
        const message = data.message || data.error || `HTTP ${res.status}`
        throw new Error(message)
    }
    return data
}

export async function apiGet(path) {
    const res = await fetch(`${BASE_URL}${path}`, { headers: getAuthHeaders() })
    return handleResponse(res)
}

export async function apiPost(path, body) {
    const res = await fetch(`${BASE_URL}${path}`, {
        method: 'POST',
        headers: getAuthHeaders(),
        body: JSON.stringify(body),
    })
    return handleResponse(res)
}

export async function apiPut(path) {
    const res = await fetch(`${BASE_URL}${path}`, {
        method: 'PUT',
        headers: getAuthHeaders(),
    })
    return handleResponse(res)
}

export async function apiDelete(path) {
    const res = await fetch(`${BASE_URL}${path}`, {
        method: 'DELETE',
        headers: getAuthHeaders(),
    })
    return handleResponse(res)
}
