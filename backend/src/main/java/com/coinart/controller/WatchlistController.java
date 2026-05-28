package com.coinart.controller;

import com.coinart.dto.WatchlistAddRequest;
import com.coinart.dto.WatchlistDTO;
import com.coinart.dto.WatchlistTagDTO;
import com.coinart.entity.User;
import com.coinart.exception.ResourceNotFoundException;
import com.coinart.repository.UserRepository;
import com.coinart.service.StockSearchService;
import com.coinart.service.WatchlistService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/watchlist")
public class WatchlistController {

    private final WatchlistService watchlistService;
    private final StockSearchService searchService;
    private final UserRepository userRepository;

    public WatchlistController(WatchlistService watchlistService,
            StockSearchService searchService,
            UserRepository userRepository) {
        this.watchlistService = watchlistService;
        this.searchService = searchService;
        this.userRepository = userRepository;
    }

    // ── Watchlist CRUD ─────────────────────────────────────────────────────────

    @GetMapping
    public ResponseEntity<List<WatchlistDTO>> getWatchlist(@AuthenticationPrincipal UserDetails ud) {
        return ResponseEntity.ok(watchlistService.getWatchlist(uid(ud)));
    }

    @PostMapping
    public ResponseEntity<WatchlistDTO> add(@AuthenticationPrincipal UserDetails ud,
            @RequestBody WatchlistAddRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(watchlistService.addToWatchlist(uid(ud), request));
    }

    @DeleteMapping("/{symbol}")
    public ResponseEntity<Void> remove(@AuthenticationPrincipal UserDetails ud,
            @PathVariable String symbol) {
        watchlistService.removeFromWatchlist(uid(ud), symbol);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<WatchlistDTO>> search(@RequestParam String q) {
        if (q == null || q.trim().isEmpty())
            return ResponseEntity.ok(List.of());
        return ResponseEntity.ok(searchService.search(q.trim()));
    }

    // ── Tag CRUD ───────────────────────────────────────────────────────────────

    @GetMapping("/tags")
    public ResponseEntity<List<WatchlistTagDTO>> getTags(@AuthenticationPrincipal UserDetails ud) {
        return ResponseEntity.ok(watchlistService.getUserTags(uid(ud)));
    }

    @PostMapping("/tags")
    public ResponseEntity<WatchlistTagDTO> createTag(@AuthenticationPrincipal UserDetails ud,
            @RequestBody Map<String, String> body) {
        String name = body.getOrDefault("name", "").trim();
        String color = body.getOrDefault("color", "#58A6FF");
        if (name.isEmpty())
            return ResponseEntity.badRequest().build();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(watchlistService.createTag(uid(ud), name, color));
    }

    @DeleteMapping("/tags/{tagId}")
    public ResponseEntity<Void> deleteTag(@AuthenticationPrincipal UserDetails ud,
            @PathVariable Long tagId) {
        watchlistService.deleteTag(uid(ud), tagId);
        return ResponseEntity.noContent().build();
    }

    // ── Tag assignment ─────────────────────────────────────────────────────────

    @PostMapping("/{symbol}/tags/{tagId}")
    public ResponseEntity<WatchlistDTO> addTag(@AuthenticationPrincipal UserDetails ud,
            @PathVariable String symbol,
            @PathVariable Long tagId) {
        return ResponseEntity.ok(watchlistService.addTagToEntry(uid(ud), symbol, tagId));
    }

    @DeleteMapping("/{symbol}/tags/{tagId}")
    public ResponseEntity<WatchlistDTO> removeTag(@AuthenticationPrincipal UserDetails ud,
            @PathVariable String symbol,
            @PathVariable Long tagId) {
        return ResponseEntity.ok(watchlistService.removeTagFromEntry(uid(ud), symbol, tagId));
    }

    // ── Helper ─────────────────────────────────────────────────────────────────

    private Long uid(UserDetails ud) {
        User user = userRepository.findByEmail(ud.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return user.getId();
    }
}
