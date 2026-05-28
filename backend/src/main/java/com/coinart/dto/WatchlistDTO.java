package com.coinart.dto;

import java.math.BigDecimal;
import java.util.List;

public class WatchlistDTO {
    private String symbol;
    private String name;
    private String assetType;
    private String exchange;
    private BigDecimal lastPrice;
    private BigDecimal changePercent;
    private boolean inWatchlist;
    private List<WatchlistTagDTO> tags;

    public WatchlistDTO() {
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String symbol, name, assetType, exchange;
        private BigDecimal lastPrice, changePercent;
        private boolean inWatchlist;
        private List<WatchlistTagDTO> tags;

        public Builder symbol(String v) {
            this.symbol = v;
            return this;
        }

        public Builder name(String v) {
            this.name = v;
            return this;
        }

        public Builder assetType(String v) {
            this.assetType = v;
            return this;
        }

        public Builder exchange(String v) {
            this.exchange = v;
            return this;
        }

        public Builder lastPrice(BigDecimal v) {
            this.lastPrice = v;
            return this;
        }

        public Builder changePercent(BigDecimal v) {
            this.changePercent = v;
            return this;
        }

        public Builder inWatchlist(boolean v) {
            this.inWatchlist = v;
            return this;
        }

        public Builder tags(List<WatchlistTagDTO> v) {
            this.tags = v;
            return this;
        }

        public WatchlistDTO build() {
            WatchlistDTO d = new WatchlistDTO();
            d.symbol = symbol;
            d.name = name;
            d.assetType = assetType;
            d.exchange = exchange;
            d.lastPrice = lastPrice;
            d.changePercent = changePercent;
            d.inWatchlist = inWatchlist;
            d.tags = tags;
            return d;
        }
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

    public BigDecimal getLastPrice() {
        return lastPrice;
    }

    public BigDecimal getChangePercent() {
        return changePercent;
    }

    public boolean isInWatchlist() {
        return inWatchlist;
    }

    public List<WatchlistTagDTO> getTags() {
        return tags;
    }

    public void setSymbol(String v) {
        this.symbol = v;
    }

    public void setName(String v) {
        this.name = v;
    }

    public void setAssetType(String v) {
        this.assetType = v;
    }

    public void setExchange(String v) {
        this.exchange = v;
    }

    public void setLastPrice(BigDecimal v) {
        this.lastPrice = v;
    }

    public void setChangePercent(BigDecimal v) {
        this.changePercent = v;
    }

    public void setInWatchlist(boolean v) {
        this.inWatchlist = v;
    }

    public void setTags(List<WatchlistTagDTO> v) {
        this.tags = v;
    }
}
