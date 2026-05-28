package com.coinart.dto;

import com.coinart.enums.OrderStatus;
import com.coinart.enums.OrderType;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class OrderResponse {
    private Long id;
    private String symbol;
    private OrderType type;
    private BigDecimal price;
    private BigDecimal quantity;
    private BigDecimal total;
    private OrderStatus status;
    private LocalDateTime createdAt;

    public OrderResponse() {
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private String symbol;
        private OrderType type;
        private BigDecimal price, quantity, total;
        private OrderStatus status;
        private LocalDateTime createdAt;

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

        public Builder status(OrderStatus v) {
            this.status = v;
            return this;
        }

        public Builder createdAt(LocalDateTime v) {
            this.createdAt = v;
            return this;
        }

        public OrderResponse build() {
            OrderResponse r = new OrderResponse();
            r.id = id;
            r.symbol = symbol;
            r.type = type;
            r.price = price;
            r.quantity = quantity;
            r.total = total;
            r.status = status;
            r.createdAt = createdAt;
            return r;
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

    public OrderStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
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

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
