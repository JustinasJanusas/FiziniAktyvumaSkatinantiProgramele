package com.example.ejunasapp;

import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class WebAPI {
    public static String getData(String url)
            throws Exception{

        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection)
                obj.openConnection();
        con.setRequestMethod("GET");

        int responseCode = con.getResponseCode();


        if(responseCode == HttpURLConnection.HTTP_OK){
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(
                            con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = in.readLine()) != null){
                response.append(inputLine);
            }
            in.close();

            return response.toString();
        }

        return null;
    }
}
