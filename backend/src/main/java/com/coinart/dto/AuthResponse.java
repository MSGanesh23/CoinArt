package com.coinart.dto;

public class AuthResponse {
    private String token;
    private String username;
    private String email;
    private String role;

    public AuthResponse() {
    }

    public AuthResponse(String token, String username, String email, String role) {
        this.token = token;
        this.username = username;
        this.email = email;
        this.role = role;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String token, username, email, role;

        public Builder token(String v) {
            this.token = v;
            return this;
        }

        public Builder username(String v) {
            this.username = v;
            return this;
        }

        public Builder email(String v) {
            this.email = v;
            return this;
        }

        public Builder role(String v) {
            this.role = v;
            return this;
        }

        public AuthResponse build() {
            return new AuthResponse(token, username, email, role);
        }
    }

    public String getToken() {
        return token;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
