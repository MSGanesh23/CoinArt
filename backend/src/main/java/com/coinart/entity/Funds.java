package com.coinart.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "funds")
public class Funds {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal balance;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal invested;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal pnl;

    public Funds() {
    }

    private Funds(Builder b) {
        this.user = b.user;
        this.balance = b.balance;
        this.invested = b.invested;
        this.pnl = b.pnl;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private User user;
        private BigDecimal balance, invested, pnl;

        public Builder user(User v) {
            this.user = v;
            return this;
        }

        public Builder balance(BigDecimal v) {
            this.balance = v;
            return this;
        }

        public Builder invested(BigDecimal v) {
            this.invested = v;
            return this;
        }

        public Builder pnl(BigDecimal v) {
            this.pnl = v;
            return this;
        }

        public Funds build() {
            return new Funds(this);
        }
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public BigDecimal getInvested() {
        return invested;
    }

    public BigDecimal getPnl() {
        return pnl;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public void setInvested(BigDecimal invested) {
        this.invested = invested;
    }

    public void setPnl(BigDecimal pnl) {
        this.pnl = pnl;
    }
}
