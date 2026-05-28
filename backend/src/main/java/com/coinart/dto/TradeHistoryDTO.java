package com.coinart.dto;

import com.coinart.enums.OrderType;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TradeHistoryDTO {
    private Long id;
    private String symbol;
    private OrderType type;
    private BigDecimal price;
    private BigDecimal quantity;
    private BigDecimal total;
    private BigDecimal realizedPnl;
    private LocalDateTime executedAt;

    public TradeHistoryDTO() {
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private String symbol;
        private OrderType type;
        private BigDecimal price, quantity, total, realizedPnl;
        private LocalDateTime executedAt;

        public Builder id(Long v) {
            this.id = v;
            return this;
        }

        public Builder symbol(String v) {
            this.symbol = v;
            return this;
        }

        public Builder type(OrderType v) {
            this.type = v;
            return this;
        }

        public Builder price(BigDecimal v) {
            this.price = v;
            return this;
        }

        public Builder quantity(BigDecimal v) {
            this.quantity = v;
            return this;
        }

        public Builder total(BigDecimal v) {
            this.total = v;
            return this;
        }

        public Builder realizedPnl(BigDecimal v) {
            this.realizedPnl = v;
            return this;
        }

        public Builder executedAt(LocalDateTime v) {
            this.executedAt = v;
            return this;
        }

        public TradeHistoryDTO build() {
            TradeHistoryDTO d = new TradeHistoryDTO();
            d.id = id;
            d.symbol = symbol;
            d.type = type;
            d.price = price;
            d.quantity = quantity;
            d.total = total;
            d.realizedPnl = realizedPnl;
            d.executedAt = executedAt;
            return d;
        }
    }

    public Long getId() {
        return id;
    }

    public String getSymbol() {
        return symbol;
    }

    public OrderType getType() {
        return type;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public BigDecimal getRealizedPnl() {
        return realizedPnl;
    }

    public LocalDateTime getExecutedAt() {
        return executedAt;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public void setType(OrderType type) {
        this.type = type;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public void setRealizedPnl(BigDecimal realizedPnl) {
        this.realizedPnl = realizedPnl;
    }

    public void setExecutedAt(LocalDateTime executedAt) {
        this.executedAt = executedAt;
    }
}
