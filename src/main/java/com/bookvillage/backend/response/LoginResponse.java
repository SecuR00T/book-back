package com.bookvillage.backend.response;

public class LoginResponse {
    public String token;
    public User user;

    public static class User {
        public String name;
        public String email;

        public User() {
        }

        public User(String name, String email) {
            this.name = name;
            this.email = email;
        }
    }

    public LoginResponse(String token, User user) {
        this.token = token;
        this.user = user;
    }
}
