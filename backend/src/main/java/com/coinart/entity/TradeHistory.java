package com.coinart.entity;

import com.coinart.enums.OrderType;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "trade_history")
public class TradeHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 20)
    private String symbol;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private OrderType type;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal price;

    @Column(nullable = false, precision = 19, scale = 8)
    private BigDecimal quantity;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal total;

    @Column(precision = 19, scale = 4)
    private BigDecimal realizedPnl;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime executedAt;

    public TradeHistory() {
    }

    private TradeHistory(Builder b) {
        this.user = b.user;
        this.symbol = b.symbol;
        this.type = b.type;
        this.price = b.price;
        this.quantity = b.quantity;
        this.total = b.total;
        this.realizedPnl = b.realizedPnl;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private User user;
        private String symbol;
        private OrderType type;
        private BigDecimal price, quantity, total, realizedPnl;

        public Builder user(User v) {
            this.user = v;
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

        public TradeHistory build() {
            return new TradeHistory(this);
        }
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

    public void setUser(User user) {
        this.user = user;
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
