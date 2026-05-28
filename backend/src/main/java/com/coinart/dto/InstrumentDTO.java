package com.coinart.dto;

import com.coinart.enums.InstrumentType;
import java.math.BigDecimal;

public class InstrumentDTO {
    private String symbol;
    private String name;
    private InstrumentType type;
    private BigDecimal lastPrice;
    private BigDecimal changePercent;
    private BigDecimal volume;
    private BigDecimal marketCap;

    public InstrumentDTO() {
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String symbol, name;
        private InstrumentType type;
        private BigDecimal lastPrice, changePercent, volume, marketCap;

        public Builder symbol(String v) {
            this.symbol = v;
            return this;
        }

        public Builder name(String v) {
            this.name = v;
            return this;
        }

        public Builder type(InstrumentType v) {
            this.type = v;
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

        public Builder volume(BigDecimal v) {
            this.volume = v;
            return this;
        }

        public Builder marketCap(BigDecimal v) {
            this.marketCap = v;
            return this;
        }

        public InstrumentDTO build() {
            InstrumentDTO d = new InstrumentDTO();
            d.symbol = symbol;
            d.name = name;
            d.type = type;
            d.lastPrice = lastPrice;
            d.changePercent = changePercent;
            d.volume = volume;
            d.marketCap = marketCap;
            return d;
        }
    }

    public String getSymbol() {
        return symbol;
    }

    public String getName() {
        return name;
    }

    public InstrumentType getType() {
        return type;
    }

    public BigDecimal getLastPrice() {
        return lastPrice;
    }

    public BigDecimal getChangePercent() {
        return changePercent;
    }

    public BigDecimal getVolume() {
        return volume;
    }

    public BigDecimal getMarketCap() {
        return marketCap;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(InstrumentType type) {
        this.type = type;
    }

    public void setLastPrice(BigDecimal lastPrice) {
        this.lastPrice = lastPrice;
    }

    public void setChangePercent(BigDecimal changePercent) {
        this.changePercent = changePercent;
    }

    public void setVolume(BigDecimal volume) {
        this.volume = volume;
    }

    public void setMarketCap(BigDecimal marketCap) {
        this.marketCap = marketCap;
    }
}
