package com.coinart.service;

import com.coinart.dto.InstrumentDTO;
import com.coinart.entity.Watchlist;
import com.coinart.enums.InstrumentType;
import com.coinart.repository.WatchlistRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Fetches live market data from Yahoo Finance (no API key required).
 * Tracks two sets of symbols:
 * 1. STATIC_SYMBOLS — hardcoded 20 (10 crypto + 10 NSE stocks)
 * 2. dynamicSymbols — any stock/crypto a user adds to their watchlist
 *
 * Runs every 15 seconds, updates PriceCacheService, and broadcasts via
 * WebSocket.
 */
@Service
public class MarketDataFetchService {

    private static final Logger log = LoggerFactory.getLogger(MarketDataFetchService.class);

    private static final String YAHOO_BASE = "https://query1.finance.yahoo.com/v8/finance/chart/";
    private static final String YAHOO_PARAMS = "?interval=1m&range=1d";

    /**
     * Hardcoded default symbols — always tracked. Format: ourSymbol → [yahooTicker,
     * displayName, assetType]
     */
    private static final Map<String, String[]> STATIC_SYMBOLS = new LinkedHashMap<>();

    static {
        STATIC_SYMBOLS.put("BTC", new String[] { "BTC-INR", "Bitcoin", "CRYPTO" });
        STATIC_SYMBOLS.put("ETH", new String[] { "ETH-INR", "Ethereum", "CRYPTO" });
        STATIC_SYMBOLS.put("BNB", new String[] { "BNB-INR", "Binance Coin", "CRYPTO" });
        STATIC_SYMBOLS.put("SOL", new String[] { "SOL-INR", "Solana", "CRYPTO" });
        STATIC_SYMBOLS.put("XRP", new String[] { "XRP-INR", "XRP", "CRYPTO" });
        STATIC_SYMBOLS.put("ADA", new String[] { "ADA-INR", "Cardano", "CRYPTO" });
        STATIC_SYMBOLS.put("DOGE", new String[] { "DOGE-INR", "Dogecoin", "CRYPTO" });
        STATIC_SYMBOLS.put("AVAX", new String[] { "AVAX-INR", "Avalanche", "CRYPTO" });
        STATIC_SYMBOLS.put("MATIC", new String[] { "MATIC-INR", "Polygon", "CRYPTO" });
        STATIC_SYMBOLS.put("LINK", new String[] { "LINK-INR", "Chainlink", "CRYPTO" });
        STATIC_SYMBOLS.put("RELIANCE", new String[] { "RELIANCE.NS", "Reliance Industries", "STOCK" });
        STATIC_SYMBOLS.put("TCS", new String[] { "TCS.NS", "Tata Consultancy", "STOCK" });
        STATIC_SYMBOLS.put("INFY", new String[] { "INFY.NS", "Infosys", "STOCK" });
        STATIC_SYMBOLS.put("HDFCBANK", new String[] { "HDFCBANK.NS", "HDFC Bank", "STOCK" });
        STATIC_SYMBOLS.put("ICICIBANK", new String[] { "ICICIBANK.NS", "ICICI Bank", "STOCK" });
        STATIC_SYMBOLS.put("SBIN", new String[] { "SBIN.NS", "State Bank of India", "STOCK" });
        STATIC_SYMBOLS.put("WIPRO", new String[] { "WIPRO.NS", "Wipro", "STOCK" });
        STATIC_SYMBOLS.put("TATAMOTORS", new String[] { "TATAMOTORS.NS", "Tata Motors", "STOCK" });
        STATIC_SYMBOLS.put("ADANIENT", new String[] { "ADANIENT.NS", "Adani Enterprises", "STOCK" });
        STATIC_SYMBOLS.put("BHARTIARTL", new String[] { "BHARTIARTL.NS", "Bharti Airtel", "STOCK" });
    }

    /**
     * Dynamic symbols added via user watchlist — persists in memory until server
     * restart, then reloaded from DB.
     */
    private final ConcurrentHashMap<String, String[]> dynamicSymbols = new ConcurrentHashMap<>();

    private final PriceCacheService priceCache;
    private final SimpMessagingTemplate messagingTemplate;
    private final WatchlistRepository watchlistRepository;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public MarketDataFetchService(PriceCacheService priceCache,
            SimpMessagingTemplate messagingTemplate,
            WatchlistRepository watchlistRepository) {
        this.priceCache = priceCache;
        this.messagingTemplate = messagingTemplate;
        this.watchlistRepository = watchlistRepository;
    }

    /**
     * On startup, load all existing watchlist symbols from DB so they get tracked
     * immediately.
     */
    @PostConstruct
    public void loadWatchlistSymbolsFromDb() {
        try {
            List<Watchlist> all = watchlistRepository.findAll();
            for (Watchlist w : all) {
                String sym = w.getSymbol().toUpperCase();
                if (!STATIC_SYMBOLS.containsKey(sym)) {
                    String yahooTicker = deriveYahooTicker(sym, w.getAssetType(), w.getExchange());
                    dynamicSymbols.put(sym, new String[] { yahooTicker, w.getName(), w.getAssetType() });
                }
            }
            log.info("Loaded {} watchlist symbols for live price tracking.", dynamicSymbols.size());
        } catch (Exception e) {
            log.warn("Could not load watchlist symbols on startup: {}", e.getMessage());
        }
    }

    /**
     * Called by WatchlistService when a user adds a new symbol.
     * Immediately fetches the price and starts tracking.
     */
    public void registerSymbol(String ourSymbol, String assetType, String exchange, String name) {
        String sym = ourSymbol.toUpperCase();
        if (STATIC_SYMBOLS.containsKey(sym))
            return; // already tracked
        String yahooTicker = deriveYahooTicker(sym, assetType, exchange);
        dynamicSymbols.put(sym, new String[] { yahooTicker, name, assetType });
        log.info("Registered dynamic symbol: {} → {}", sym, yahooTicker);

        // Fetch immediately so the user sees a price right away
        InstrumentType type = "CRYPTO".equals(assetType) ? InstrumentType.CRYPTO : InstrumentType.STOCK;
        InstrumentDTO dto = fetchFromYahoo(yahooTicker, sym, name, type);
        if (dto != null)
            priceCache.update(sym, dto);
    }

    /**
     * Called by WatchlistService when a user removes a symbol from ALL their
     * watchlists.
     */
    public void unregisterSymbol(String ourSymbol) {
        dynamicSymbols.remove(ourSymbol.toUpperCase());
        log.debug("Unregistered dynamic symbol: {}", ourSymbol);
    }

    @Scheduled(fixedDelay = 15000, initialDelay = 3000)
    public void fetchAllPrices() {
        int updated = fetchSymbolMap(STATIC_SYMBOLS);
        updated += fetchSymbolMap(dynamicSymbols);

        if (updated > 0) {
            messagingTemplate.convertAndSend("/topic/prices", priceCache.getAll());
        }
        log.debug("Price fetch cycle done. Updated {} symbols.", updated);
    }

    private int fetchSymbolMap(Map<String, String[]> symbolMap) {
        int updated = 0;
        for (Map.Entry<String, String[]> entry : symbolMap.entrySet()) {
            String ourSymbol = entry.getKey();
            String[] meta = entry.getValue();
            String yahooTicker = meta[0];
            String name = meta[1];
            InstrumentType type = "CRYPTO".equals(meta[2]) ? InstrumentType.CRYPTO : InstrumentType.STOCK;
            try {
                InstrumentDTO dto = fetchFromYahoo(yahooTicker, ourSymbol, name, type);
                if (dto != null) {
                    priceCache.update(ourSymbol, dto);
                    updated++;
                }
                Thread.sleep(300); // stay under rate limit
            } catch (Exception e) {
                log.debug("Fetch error for {}: {}", yahooTicker, e.getMessage());
            }
        }
        return updated;
    }

    private InstrumentDTO fetchFromYahoo(String ticker, String symbol, String name, InstrumentType type) {
        try {
            String url = YAHOO_BASE + ticker + YAHOO_PARAMS;
            HttpHeaders headers = new HttpHeaders();
            headers.set("User-Agent", "Mozilla/5.0 (compatible; CoinArt/2.0)");
            headers.set("Accept", "application/json");
            ResponseEntity<String> response = restTemplate.exchange(
                    url, HttpMethod.GET, new HttpEntity<>(headers), String.class);

            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null)
                return null;

            JsonNode root = objectMapper.readTree(response.getBody());
            JsonNode resultArr = root.path("chart").path("result");
            if (!resultArr.isArray() || resultArr.isEmpty())
                return null;

            JsonNode chartMeta = resultArr.get(0).path("meta");
            if (chartMeta == null || chartMeta.isMissingNode())
                return null;

            double price = chartMeta.path("regularMarketPrice").asDouble(0);
            double prevClose = chartMeta.path("chartPreviousClose").asDouble(0);
            double volume = chartMeta.path("regularMarketVolume").asDouble(0);
            double changePct = prevClose > 0
                    ? ((price - prevClose) / prevClose) * 100
                    : chartMeta.path("regularMarketChangePercent").asDouble(0);

            if (price <= 0)
                return null;

            return InstrumentDTO.builder()
                    .symbol(symbol).name(name).type(type)
                    .lastPrice(BigDecimal.valueOf(price).setScale(2, RoundingMode.HALF_UP))
                    .changePercent(BigDecimal.valueOf(changePct).setScale(2, RoundingMode.HALF_UP))
                    .volume(BigDecimal.valueOf(volume).setScale(0, RoundingMode.HALF_UP))
                    .marketCap(BigDecimal.ZERO)
                    .build();

        } catch (Exception e) {
            log.debug("Parse error for {}: {}", ticker, e.getMessage());
            return null;
        }
    }

    /**
     * Derive the Yahoo Finance ticker from our symbol + asset type + exchange.
     * NSI/NSE → .NS suffix | BOM/BSE → .BO suffix | CRYPTO → -INR suffix
     */
    public static String deriveYahooTicker(String symbol, String assetType, String exchange) {
        if ("CRYPTO".equalsIgnoreCase(assetType))
            return symbol + "-INR";
        if (exchange == null || exchange.isBlank())
            return symbol + ".NS"; // default to NSE
        return switch (exchange.toUpperCase()) {
            case "NSI", "NSE", "NMS" -> symbol + ".NS";
            case "BOM", "BSE" -> symbol + ".BO";
            case "MCX" -> symbol + ".MCX";
            default -> symbol + ".NS"; // fallback to NSE
        };
    }
}
