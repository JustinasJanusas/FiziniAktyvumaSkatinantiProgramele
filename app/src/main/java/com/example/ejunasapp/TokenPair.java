package com.example.ejunasapp;

import android.content.Context;
import android.content.SharedPreferences;

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
    public static void wipeData(){
        refreshToken = "";
        authenticationToken = "";
    }
}
