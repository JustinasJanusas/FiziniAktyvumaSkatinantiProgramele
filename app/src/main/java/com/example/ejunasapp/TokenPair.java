package com.example.ejunasapp;

public class TokenPair {
    static private String refreshToken;
    static private String authenticationToken;

    public static void setRefreshToken(String str) {
        refreshToken = str;
    }
    public static String getRefreshToken(){
        return refreshToken;
    }
    public static void setAuthenticationToken(String str) {
        authenticationToken = str;
    }
    public static String getAuthenticationToken(){
        return authenticationToken;
    }
}
