package com.coinart.dto;

public class WatchlistTagDTO {
    private Long id;
    private String name;
    private String color;

    public WatchlistTagDTO() {
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private String name, color;

        public Builder id(Long v) {
            this.id = v;
            return this;
        }

        public Builder name(String v) {
            this.name = v;
            return this;
        }

        public Builder color(String v) {
            this.color = v;
            return this;
        }

        public WatchlistTagDTO build() {
            WatchlistTagDTO d = new WatchlistTagDTO();
            d.id = id;
            d.name = name;
            d.color = color;
            return d;
        }
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
