import { createContext, useContext, useState, useEffect } from 'react'

const AuthContext = createContext(null)

export function AuthProvider({ children }) {
    const [token, setToken] = useState(() => localStorage.getItem('coinart_token'))
    const [user, setUser] = useState(() => {
        const stored = localStorage.getItem('coinart_user')
        return stored ? JSON.parse(stored) : null
    })

    const login = (authResponse) => {
        setToken(authResponse.token)
        setUser({
            username: authResponse.username,
            email: authResponse.email,
            role: authResponse.role,
        })
        localStorage.setItem('coinart_token', authResponse.token)
        localStorage.setItem('coinart_user', JSON.stringify({
            username: authResponse.username,
            email: authResponse.email,
            role: authResponse.role,
        }))
    }

    const logout = () => {
        setToken(null)
        setUser(null)
        localStorage.removeItem('coinart_token')
        localStorage.removeItem('coinart_user')
    }

    return (
        <AuthContext.Provider value={{ token, user, login, logout }}>
            {children}
        </AuthContext.Provider>
    )
}

export function useAuth() {
    const ctx = useContext(AuthContext)
    if (!ctx) throw new Error('useAuth must be used within AuthProvider')
    return ctx
}
