package com.coinart.dto;

import java.math.BigDecimal;

public class FundsDTO {
    private BigDecimal balance;
    private BigDecimal invested;
    private BigDecimal pnl;
    private BigDecimal totalValue;

    public FundsDTO() {
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private BigDecimal balance, invested, pnl, totalValue;

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

        public Builder totalValue(BigDecimal v) {
            this.totalValue = v;
            return this;
        }

        public FundsDTO build() {
            FundsDTO d = new FundsDTO();
            d.balance = balance;
            d.invested = invested;
            d.pnl = pnl;
            d.totalValue = totalValue;
            return d;
        }
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

    public BigDecimal getTotalValue() {
        return totalValue;
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

    public void setTotalValue(BigDecimal totalValue) {
        this.totalValue = totalValue;
    }
}
