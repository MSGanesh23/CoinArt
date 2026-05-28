package com.coinart.dto;

public class WatchlistAddRequest {
    private String symbol;
    private String name;
    private String assetType;
    private String exchange;

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
}
