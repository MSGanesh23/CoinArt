package com.coinart.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "watchlist", uniqueConstraints = @UniqueConstraint(columnNames = { "user_id", "symbol" }))
public class Watchlist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 20)
    private String symbol;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(nullable = false, length = 10)
    private String assetType;

    @Column(length = 20)
    private String exchange;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "watchlist_tag_mapping", joinColumns = @JoinColumn(name = "watchlist_id"), inverseJoinColumns = @JoinColumn(name = "tag_id"))
    private List<WatchlistTag> tags = new ArrayList<>();

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime addedAt;

    public Watchlist() {
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getName() {
        return name;
    }

    public String getAssetType() {
        return assetType;
    }

    public String getExchange() {
        return exchange;
    }

    public List<WatchlistTag> getTags() {
        return tags;
    }

    public LocalDateTime getAddedAt() {
        return addedAt;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAssetType(String assetType) {
        this.assetType = assetType;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    public void setTags(List<WatchlistTag> tags) {
        this.tags = tags;
    }

    public void setAddedAt(LocalDateTime addedAt) {
        this.addedAt = addedAt;
    }
}
