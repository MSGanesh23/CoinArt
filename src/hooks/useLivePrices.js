import { useState, useEffect, useRef, useCallback } from 'react'
import { Client } from '@stomp/stompjs'
import SockJS from 'sockjs-client'
import { getMarket } from '../services/marketService'

/**
 * useLivePrices — connects to WebSocket /ws and subscribes to /topic/prices.
 * Falls back to HTTP polling if WebSocket is unavailable.
 * Returns: { prices: InstrumentDTO[], connected: boolean, lastUpdated: Date }
 */
export function useLivePrices() {
    const [prices, setPrices] = useState([])
    const [connected, setConnected] = useState(false)
    const [lastUpdated, setLastUpdated] = useState(null)
    const clientRef = useRef(null)
    const pollRef = useRef(null)

    const updatePrices = useCallback((data) => {
        if (Array.isArray(data) && data.length > 0) {
            setPrices(data)
            setLastUpdated(new Date())
        }
    }, [])

    // HTTP fallback — poll every 15s if WebSocket fails
    const startPolling = useCallback(() => {
        const poll = async () => {
            try {
                const data = await getMarket()
                updatePrices(data)
            } catch (e) { /* silent */ }
        }
        poll()
        pollRef.current = setInterval(poll, 15000)
    }, [updatePrices])

    useEffect(() => {
        // Load initial prices immediately via HTTP
        getMarket().then(updatePrices).catch(() => { })

        const client = new Client({
            webSocketFactory: () => new SockJS('/ws'),
            reconnectDelay: 5000,
            onConnect: () => {
                setConnected(true)
                clearInterval(pollRef.current)

                client.subscribe('/topic/prices', (msg) => {
                    try {
                        updatePrices(JSON.parse(msg.body))
                    } catch (e) { /* invalid JSON */ }
                })

                // Request immediate snapshot
                client.publish({ destination: '/app/subscribe', body: '' })
            },
            onDisconnect: () => {
                setConnected(false)
                startPolling()
            },
            onStompError: () => {
                setConnected(false)
                startPolling()
            },
        })

        client.activate()
        clientRef.current = client

        return () => {
            client.deactivate()
            clearInterval(pollRef.current)
        }
    }, [updatePrices, startPolling])

    return { prices, connected, lastUpdated }
}
