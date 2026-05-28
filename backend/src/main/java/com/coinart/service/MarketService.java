package com.coinart.service;

import com.coinart.dto.InstrumentDTO;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * MarketService now delegates entirely to PriceCacheService.
 * Prices are updated live by MarketDataFetchService every 15 seconds.
 */
@Service
public class MarketService {

        private final PriceCacheService priceCache;

        public MarketService(PriceCacheService priceCache) {
                this.priceCache = priceCache;
        }

        public List<InstrumentDTO> getAllInstruments() {
                return priceCache.getAll();
        }

        /**
         * Used by PortfolioService for P&L calculation — always reads live INR price.
         */
        public BigDecimal getPriceForSymbol(String symbol) {
                return priceCache.getPrice(symbol.toUpperCase());
        }
}
