package com.coinart.service;

import com.coinart.dto.WatchlistAddRequest;
import com.coinart.dto.WatchlistDTO;
import com.coinart.dto.WatchlistTagDTO;
import com.coinart.entity.User;
import com.coinart.entity.Watchlist;
import com.coinart.entity.WatchlistTag;
import com.coinart.exception.BadRequestException;
import com.coinart.exception.ResourceNotFoundException;
import com.coinart.repository.WatchlistRepository;
import com.coinart.repository.WatchlistTagRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class WatchlistService {

    private final WatchlistRepository watchlistRepository;
    private final WatchlistTagRepository tagRepository;
    private final PriceCacheService priceCache;
    private final MarketDataFetchService marketDataFetchService;

    public WatchlistService(WatchlistRepository watchlistRepository,
            WatchlistTagRepository tagRepository,
            PriceCacheService priceCache,
            @Lazy MarketDataFetchService marketDataFetchService) {
        this.watchlistRepository = watchlistRepository;
        this.tagRepository = tagRepository;
        this.priceCache = priceCache;
        this.marketDataFetchService = marketDataFetchService;
    }

    // ── Watchlist CRUD ─────────────────────────────────────────────────────────

    public List<WatchlistDTO> getWatchlist(Long userId) {
        return watchlistRepository.findByUserIdOrderByAddedAtDesc(userId).stream()
                .map(w -> toDTO(w))
                .collect(Collectors.toList());
    }

    @Transactional
    public WatchlistDTO addToWatchlist(Long userId, WatchlistAddRequest request) {
        String symbol = request.getSymbol().toUpperCase();
        if (watchlistRepository.existsByUserIdAndSymbol(userId, symbol))
            throw new BadRequestException(symbol + " is already in your watchlist");

        User userRef = new User();
        userRef.setId(userId);
        Watchlist entry = new Watchlist();
        entry.setUser(userRef);
        entry.setSymbol(symbol);
        entry.setName(request.getName());
        entry.setAssetType(request.getAssetType() != null ? request.getAssetType() : "STOCK");
        entry.setExchange(request.getExchange() != null ? request.getExchange() : "NSE");
        watchlistRepository.save(entry);

        marketDataFetchService.registerSymbol(
                symbol, entry.getAssetType(), entry.getExchange(), entry.getName());

        return toDTO(entry);
    }

    @Transactional
    public void removeFromWatchlist(Long userId, String symbol) {
        String sym = symbol.toUpperCase();
        watchlistRepository.deleteByUserIdAndSymbol(userId, sym);
        if (watchlistRepository.countBySymbol(sym) == 0)
            marketDataFetchService.unregisterSymbol(sym);
    }

    // ── Tag CRUD ───────────────────────────────────────────────────────────────

    public List<WatchlistTagDTO> getUserTags(Long userId) {
        return tagRepository.findByUserIdOrderByNameAsc(userId).stream()
                .map(t -> tagToDTO(t))
                .collect(Collectors.toList());
    }

    @Transactional
    public WatchlistTagDTO createTag(Long userId, String name, String color) {
        if (tagRepository.existsByUserIdAndName(userId, name.trim()))
            throw new BadRequestException("Tag '" + name + "' already exists");
        User userRef = new User();
        userRef.setId(userId);
        WatchlistTag tag = new WatchlistTag();
        tag.setUser(userRef);
        tag.setName(name.trim());
        tag.setColor(color != null && !color.isBlank() ? color : "#58A6FF");
        tagRepository.save(tag);
        return tagToDTO(tag);
    }

    @Transactional
    public void deleteTag(Long userId, Long tagId) {
        tagRepository.deleteByUserIdAndId(userId, tagId);
    }

    // ── Tag assignment ─────────────────────────────────────────────────────────

    @Transactional
    public WatchlistDTO addTagToEntry(Long userId, String symbol, Long tagId) {
        Watchlist entry = watchlistRepository.findByUserIdAndSymbol(userId, symbol.toUpperCase())
                .orElseThrow(() -> new ResourceNotFoundException("Symbol not in watchlist"));
        WatchlistTag tag = tagRepository.findByUserIdAndId(userId, tagId)
                .orElseThrow(() -> new ResourceNotFoundException("Tag not found"));
        if (!entry.getTags().contains(tag))
            entry.getTags().add(tag);
        watchlistRepository.save(entry);
        return toDTO(entry);
    }

    @Transactional
    public WatchlistDTO removeTagFromEntry(Long userId, String symbol, Long tagId) {
        Watchlist entry = watchlistRepository.findByUserIdAndSymbol(userId, symbol.toUpperCase())
                .orElseThrow(() -> new ResourceNotFoundException("Symbol not in watchlist"));
        entry.getTags().removeIf(t -> t.getId().equals(tagId));
        watchlistRepository.save(entry);
        return toDTO(entry);
    }

    public boolean isWatched(Long userId, String symbol) {
        return watchlistRepository.existsByUserIdAndSymbol(userId, symbol.toUpperCase());
    }

    // ── Helpers ────────────────────────────────────────────────────────────────

    private WatchlistDTO toDTO(Watchlist w) {
        BigDecimal price = priceCache.getPrice(w.getSymbol());
        List<WatchlistTagDTO> tags = w.getTags().stream()
                .map(this::tagToDTO).collect(Collectors.toList());
        return WatchlistDTO.builder()
                .symbol(w.getSymbol()).name(w.getName())
                .assetType(w.getAssetType()).exchange(w.getExchange())
                .lastPrice(price).changePercent(BigDecimal.ZERO)
                .inWatchlist(true).tags(tags).build();
    }

    private WatchlistTagDTO tagToDTO(WatchlistTag t) {
        return WatchlistTagDTO.builder().id(t.getId()).name(t.getName()).color(t.getColor()).build();
    }
}
