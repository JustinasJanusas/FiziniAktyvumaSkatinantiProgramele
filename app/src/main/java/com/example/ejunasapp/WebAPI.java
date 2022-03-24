package com.example.ejunasapp;

import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
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
    public static boolean sendLocation(String url, float latitude, float longtitude) throws Exception {
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("POST");
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(con.getOutputStream(), "UTF-8"));
        writer.write("latitude="+latitude + "&longitude="+longtitude);
        writer.flush();
        writer.close();
        if(con.getResponseCode() == HttpURLConnection.HTTP_OK){
            BufferedReader reader =  new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
            String line = reader.readLine();
            if(line != null){
                return Boolean.parseBoolean(line);}
        }
        throw new Exception("Problem connecting to database");
    }
}

