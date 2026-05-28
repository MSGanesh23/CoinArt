package com.coinart.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "portfolio")
public class Portfolio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 20)
    private String symbol;

    @Column(nullable = false, precision = 19, scale = 8)
    private BigDecimal quantity;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal avgBuyPrice;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal totalInvested;

    public Portfolio() {
    }

    private Portfolio(Builder b) {
        this.user = b.user;
        this.symbol = b.symbol;
        this.quantity = b.quantity;
        this.avgBuyPrice = b.avgBuyPrice;
        this.totalInvested = b.totalInvested;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private User user;
        private String symbol;
        private BigDecimal quantity, avgBuyPrice, totalInvested;

        public Builder user(User v) {
            this.user = v;
            return this;
        }

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

        public Builder totalInvested(BigDecimal v) {
            this.totalInvested = v;
            return this;
        }

        public Portfolio build() {
            return new Portfolio(this);
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

    public BigDecimal getQuantity() {
        return quantity;
    }

    public BigDecimal getAvgBuyPrice() {
        return avgBuyPrice;
    }

    public BigDecimal getTotalInvested() {
        return totalInvested;
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

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public void setAvgBuyPrice(BigDecimal avgBuyPrice) {
        this.avgBuyPrice = avgBuyPrice;
    }

    public void setTotalInvested(BigDecimal totalInvested) {
        this.totalInvested = totalInvested;
    }
}
