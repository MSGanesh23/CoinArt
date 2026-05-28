package com.coinart.service;

import com.coinart.dto.WatchlistDTO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * Searches Yahoo Finance for Indian stocks and crypto.
 * GET
 * https://query1.finance.yahoo.com/v1/finance/search?q={query}&quotesCount=15&newsCount=0
 * No API key required.
 */
@Service
public class StockSearchService {

    private static final Logger log = LoggerFactory.getLogger(StockSearchService.class);
    private static final String SEARCH_URL = "https://query1.finance.yahoo.com/v1/finance/search";
    private static final String PRICE_URL = "https://query1.finance.yahoo.com/v8/finance/chart/";

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public List<WatchlistDTO> search(String query) {
        List<WatchlistDTO> results = new ArrayList<>();
        try {
            String url = UriComponentsBuilder.fromHttpUrl(SEARCH_URL)
                    .queryParam("q", query)
                    .queryParam("quotesCount", 15)
                    .queryParam("newsCount", 0)
                    .queryParam("listsCount", 0)
                    .toUriString();

            HttpHeaders headers = new HttpHeaders();
            headers.set("User-Agent", "Mozilla/5.0 (compatible; CoinArt/2.0)");
            headers.set("Accept", "application/json");
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null)
                return results;

            JsonNode root = objectMapper.readTree(response.getBody());

            // Yahoo Finance search has two response formats depending on version:
            // Format 1 (newer): { "quotes": [...] }
            // Format 2 (older): { "finance": { "result": [{ "quotes": [...] }] } }
            JsonNode quotes = null;
            if (root.has("quotes") && root.path("quotes").isArray()) {
                quotes = root.path("quotes");
            } else {
                JsonNode resultArray = root.path("finance").path("result");
                if (resultArray.isArray() && resultArray.size() > 0) {
                    quotes = resultArray.get(0).path("quotes");
                }
            }

            if (quotes == null || !quotes.isArray() || quotes.isEmpty())
                return results;

            for (JsonNode q : quotes) {
                String symbol = q.path("symbol").asText("");
                String shortName = q.path("shortname").asText(q.path("longname").asText(symbol));
                String exchange = q.path("exchange").asText("");

                // Filter: only show equity, ETF, or crypto
                String quoteType = q.path("quoteType").asText("");
                if (!List.of("EQUITY", "ETF", "CRYPTOCURRENCY", "MUTUALFUND").contains(quoteType))
                    continue;

                // Clean up symbol — use our short form for known ones
                String ourSymbol = deriveOurSymbol(symbol, quoteType);

                String assetType = "CRYPTOCURRENCY".equals(quoteType) ? "CRYPTO" : "STOCK";

                WatchlistDTO dto = WatchlistDTO.builder()
                        .symbol(ourSymbol)
                        .name(shortName)
                        .assetType(assetType)
                        .exchange(exchange)
                        .lastPrice(BigDecimal.ZERO)
                        .changePercent(BigDecimal.ZERO)
                        .inWatchlist(false)
                        .build();

                results.add(dto);
                if (results.size() >= 10)
                    break;
            }
        } catch (Exception e) {
            log.warn("Search failed for '{}': {}", query, e.getMessage());
        }
        return results;
    }

    /**
     * Fetch live price for a yahoo symbol (e.g. INFY.NS, BTC-INR).
     */
    public BigDecimal fetchPrice(String yahooSymbol) {
        try {
            String url = PRICE_URL + yahooSymbol + "?interval=1m&range=1d";
            HttpHeaders headers = new HttpHeaders();
            headers.set("User-Agent", "Mozilla/5.0 (compatible; CoinArt/2.0)");
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headers),
                    String.class);
            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null)
                return BigDecimal.ZERO;
            JsonNode meta = objectMapper.readTree(response.getBody())
                    .path("chart").path("result").get(0).path("meta");
            double price = meta.path("regularMarketPrice").asDouble(0);
            return price > 0 ? BigDecimal.valueOf(price).setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }

    private String deriveOurSymbol(String yahooSymbol, String quoteType) {
        if ("CRYPTOCURRENCY".equals(quoteType)) {
            // BTC-INR → BTC, ETH-USD → ETH
            return yahooSymbol.contains("-") ? yahooSymbol.split("-")[0] : yahooSymbol;
        }
        // For NSE stocks: RELIANCE.NS → RELIANCE
        return yahooSymbol.contains(".") ? yahooSymbol.split("\\.")[0] : yahooSymbol;
    }
}
