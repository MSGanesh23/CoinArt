package com.coinart.controller;

import com.coinart.dto.InstrumentDTO;
import com.coinart.service.PriceCacheService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.util.List;

/**
 * WebSocket STOMP controller.
 * Clients send a message to /app/subscribe to immediately get a price snapshot.
 * The scheduled broadcaster (MarketDataFetchService) sends to /topic/prices
 * every 15s.
 */
@Controller
public class MarketWebSocketController {

    private final PriceCacheService priceCache;

    public MarketWebSocketController(PriceCacheService priceCache) {
        this.priceCache = priceCache;
    }

    @MessageMapping("/subscribe")
    @SendTo("/topic/prices")
    public List<InstrumentDTO> handleSubscribe() {
        // Immediately return current snapshot to the subscribing client
        return priceCache.getAll();
    }
}
