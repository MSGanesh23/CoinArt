package com.coinart.dto;

import java.math.BigDecimal;

public class PortfolioDTO {
    private String symbol;
    private BigDecimal quantity;
    private BigDecimal avgBuyPrice;
    private BigDecimal currentPrice;
    private BigDecimal currentValue;
    private BigDecimal unrealizedPnl;
    private BigDecimal unrealizedPnlPercent;

    public PortfolioDTO() {
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String symbol;
        private BigDecimal quantity, avgBuyPrice, currentPrice, currentValue, unrealizedPnl, unrealizedPnlPercent;

        public Builder symbol(String v) {
            this.symbol = v;
            return this;
        }

        public Builder quantity(BigDecimal v) {
            this.quantity = v;
            return this;
        }

        public Builder avgBuyPrice(BigDecimal v) {
            this.avgBuyPrice = v;
            return this;
        }

        public Builder currentPrice(BigDecimal v) {
            this.currentPrice = v;
            return this;
        }

        public Builder currentValue(BigDecimal v) {
            this.currentValue = v;
            return this;
        }

        public Builder unrealizedPnl(BigDecimal v) {
            this.unrealizedPnl = v;
            return this;
        }

        public Builder unrealizedPnlPercent(BigDecimal v) {
            this.unrealizedPnlPercent = v;
            return this;
        }

        public PortfolioDTO build() {
            PortfolioDTO d = new PortfolioDTO();
            d.symbol = symbol;
            d.quantity = quantity;
            d.avgBuyPrice = avgBuyPrice;
            d.currentPrice = currentPrice;
            d.currentValue = currentValue;
            d.unrealizedPnl = unrealizedPnl;
            d.unrealizedPnlPercent = unrealizedPnlPercent;
            return d;
        }
    }

    public String getSymbol() {
        return symbol;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public BigDecimal getAvgBuyPrice() {
        return avgBuyPrice;
    }

    public BigDecimal getCurrentPrice() {
        return currentPrice;
    }

    public BigDecimal getCurrentValue() {
        return currentValue;
    }

    public BigDecimal getUnrealizedPnl() {
        return unrealizedPnl;
    }

    public BigDecimal getUnrealizedPnlPercent() {
        return unrealizedPnlPercent;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public void setAvgBuyPrice(BigDecimal avgBuyPrice) {
        this.avgBuyPrice = avgBuyPrice;
    }

    public void setCurrentPrice(BigDecimal currentPrice) {
        this.currentPrice = currentPrice;
    }

    public void setCurrentValue(BigDecimal currentValue) {
        this.currentValue = currentValue;
    }

    public void setUnrealizedPnl(BigDecimal unrealizedPnl) {
        this.unrealizedPnl = unrealizedPnl;
    }

    public void setUnrealizedPnlPercent(BigDecimal unrealizedPnlPercent) {
        this.unrealizedPnlPercent = unrealizedPnlPercent;
    }
}
