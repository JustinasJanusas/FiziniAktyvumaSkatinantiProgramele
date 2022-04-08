package com.example.ejunasapp;

import android.preference.PreferenceActivity;
import android.util.JsonReader;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;
import java.util.List;

import javax.crypto.SecretKey;

public class WebAPI {
    public static String getData(String url)
            throws Exception{
        URL obj = new URL(url);

        HttpURLConnection con = (HttpURLConnection)
                obj.openConnection();
        String auth = "Bearer " + TokenPair.getAuthenticationToken();

        con.setRequestProperty ("Authorization", auth);
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
        String basicAuth = "Bearer " + TokenPair.getAuthenticationToken();
        con.setRequestProperty ("Authorization", basicAuth);
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
    public static boolean attemptLogin(String url, String username, String password) throws Exception {
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("POST");
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(con.getOutputStream(), "UTF-8"));
        writer.write("username="+username + "&password="+password);
        writer.flush();
        writer.close();
        int response = con.getResponseCode();
        if(response == HttpURLConnection.HTTP_OK){
            BufferedReader reader =  new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
            String line = reader.readLine();
            if(line != null){
                JSONObject json = new JSONObject(line);

                TokenPair.setAuthenticationToken(json.get("access").toString());
                TokenPair.setRefreshToken(json.get("refresh").toString());

                return true;}
            else
                return false;
        }
        else if(response == HttpURLConnection.HTTP_UNAUTHORIZED){
            return false;
        }
        throw new Exception("Problem connecting to database");
    }

    public static boolean attemptRegister(String url, String username, String name, String surname,
                                          String email, String pass, String repeatpass)
                                            throws Exception {
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("POST");
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(con.getOutputStream(), "UTF-8"));
        writer.write("username="+username + "&email="+email + "&password="+pass
                + "&password2="+repeatpass +
                "&first_name="+name + "&last_name="+surname);
        writer.flush();
        writer.close();
        int response = con.getResponseCode();

        if(response == HttpURLConnection.HTTP_CREATED){
            BufferedReader reader =  new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
            String line = reader.readLine();
            if(line != null){
                JSONObject json = new JSONObject(line);
                return true;
            }
            else
                return false;
        }
        else if(response == HttpURLConnection.HTTP_BAD_REQUEST){
            return false;
        }
        throw new Exception("Problem connecting to database");
    }

    public static boolean getToken(String url) throws Exception {
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        String auth = "Bearer " + TokenPair.getAuthenticationToken();
        con.setRequestProperty ("Authorization", auth);
        con.setRequestMethod("POST");
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(con.getOutputStream(), "UTF-8"));
        writer.write("refresh="+TokenPair.getRefreshToken());
        writer.flush();
        writer.close();
        int response = con.getResponseCode();
        if(response == HttpURLConnection.HTTP_OK){
            BufferedReader reader =  new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
            String line = reader.readLine();
            if(line != null){
                JSONObject json = new JSONObject(line);

                TokenPair.setAuthenticationToken(json.get("access").toString());
                TokenPair.setRefreshToken(json.get("refresh").toString());

                return true;}
            else
                return false;
        }
        else if(response == HttpURLConnection.HTTP_UNAUTHORIZED){
            return false;
        }
        throw new Exception("Problem connecting to database");
    }
    public static boolean CheckLoginNameEMail(String url, String username, String password) throws Exception {
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("POST");
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(con.getOutputStream(), "UTF-8"));
        writer.write("username="+username + "&password="+password);
        writer.flush();
        writer.close();
        int response = con.getResponseCode();
        if(response == HttpURLConnection.HTTP_OK){
            BufferedReader reader =  new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
            String line = reader.readLine();
            if(line != null){
                JSONObject json = new JSONObject(line);

                TokenPair.setAuthenticationToken(json.get("access").toString());
                TokenPair.setRefreshToken(json.get("refresh").toString());

                return true;}
            else
                return false;
        }
        else if(response == HttpURLConnection.HTTP_UNAUTHORIZED){
            return false;
        }
        throw new Exception("Problem connecting to database");
    }
    public static int sendRefreshToken(String url, String method) throws Exception{
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        String auth = "Bearer " + TokenPair.getAuthenticationToken();
        con.setRequestProperty ("Authorization", auth);
        con.setRequestMethod(method);
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(con.getOutputStream(), "UTF-8"));
        writer.write("refresh_token="+TokenPair.getRefreshToken());
        writer.flush();
        writer.close();
        return con.getResponseCode();
    }
}

