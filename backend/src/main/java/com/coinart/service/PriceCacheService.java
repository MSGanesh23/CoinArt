package com.coinart.service;

import com.coinart.dto.InstrumentDTO;
import com.coinart.enums.InstrumentType;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory price cache. Seeded with realistic INR fallback prices on startup.
 * Updated every ~10 seconds by MarketDataFetchService.
 */
@Service
public class PriceCacheService {

    // Symbol → full InstrumentDTO with latest prices
    private final ConcurrentHashMap<String, InstrumentDTO> cache = new ConcurrentHashMap<>();

    public PriceCacheService() {
        seedFallbackPrices();
    }

    public void update(String symbol, InstrumentDTO dto) {
        cache.put(symbol.toUpperCase(), dto);
    }

    public List<InstrumentDTO> getAll() {
        // Return in stable insertion-like order using the known symbol list
        List<InstrumentDTO> result = new ArrayList<>();
        for (String sym : getKnownSymbols()) {
            InstrumentDTO dto = cache.get(sym);
            if (dto != null)
                result.add(dto);
        }
        return result;
    }

    public BigDecimal getPrice(String symbol) {
        InstrumentDTO dto = cache.get(symbol.toUpperCase());
        return dto != null ? dto.getLastPrice() : BigDecimal.ZERO;
    }

    public boolean isEmpty() {
        return cache.isEmpty();
    }

    private List<String> getKnownSymbols() {
        return List.of(
                // Crypto
                "BTC", "ETH", "BNB", "SOL", "XRP", "ADA", "DOGE", "AVAX", "MATIC", "LINK",
                // NSE Stocks
                "RELIANCE", "TCS", "INFY", "HDFCBANK", "ICICIBANK",
                "SBIN", "WIPRO", "TATAMOTORS", "ADANIENT", "BHARTIARTL");
    }

    private void seedFallbackPrices() {
        // Crypto — approximate INR prices
        addCrypto("BTC", "Bitcoin", "8766000", "2.34");
        addCrypto("ETH", "Ethereum", "318000", "1.75");
        addCrypto("BNB", "Binance Coin", "49800", "-0.82");
        addCrypto("SOL", "Solana", "15450", "4.21");
        addCrypto("XRP", "XRP", "51.90", "-1.15");
        addCrypto("ADA", "Cardano", "40.20", "0.97");
        addCrypto("DOGE", "Dogecoin", "14.25", "3.44");
        addCrypto("AVAX", "Avalanche", "3430", "-2.10");
        addCrypto("MATIC", "Polygon", "87.80", "1.33");
        addCrypto("LINK", "Chainlink", "1564", "0.58");

        // NSE Stocks — approximate INR prices
        addStock("RELIANCE", "Reliance Industries", "2930", "0.42");
        addStock("TCS", "Tata Consultancy", "3890", "0.25");
        addStock("INFY", "Infosys", "1820", "-0.31");
        addStock("HDFCBANK", "HDFC Bank", "1760", "0.55");
        addStock("ICICIBANK", "ICICI Bank", "1310", "0.73");
        addStock("SBIN", "State Bank of India", "760", "1.12");
        addStock("WIPRO", "Wipro", "488", "-0.15");
        addStock("TATAMOTORS", "Tata Motors", "760", "1.88");
        addStock("ADANIENT", "Adani Enterprises", "2420", "-1.05");
        addStock("BHARTIARTL", "Bharti Airtel", "1692", "0.62");
    }

    private void addCrypto(String symbol, String name, String price, String change) {
        InstrumentDTO dto = InstrumentDTO.builder()
                .symbol(symbol).name(name).type(InstrumentType.CRYPTO)
                .lastPrice(new BigDecimal(price))
                .changePercent(new BigDecimal(change))
                .volume(BigDecimal.ZERO).marketCap(BigDecimal.ZERO).build();
        cache.put(symbol, dto);
    }

    private void addStock(String symbol, String name, String price, String change) {
        InstrumentDTO dto = InstrumentDTO.builder()
                .symbol(symbol).name(name).type(InstrumentType.STOCK)
                .lastPrice(new BigDecimal(price))
                .changePercent(new BigDecimal(change))
                .volume(BigDecimal.ZERO).marketCap(BigDecimal.ZERO).build();
        cache.put(symbol, dto);
    }
}
