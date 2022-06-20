package com.example.ejunasapp;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class DataAPI {
    public static List<?> jsonToData(String RestURL, Type type) throws Exception{

        List<?> data = new ArrayList<>();
        String response = WebAPI.getData(RestURL);
        if(response.length() > 0){
            Gson gson;
            gson = new Gson();

            data = gson.fromJson(response, type);
        }
        return data;
    }
    public static List<?> jsonObjectToData(String RestURL, Type type) throws Exception{

        List<?> data = new ArrayList<>();
        String response = WebAPI.getData(RestURL);
        if(response.length() > 0){
            Gson gson;
            gson = new Gson();

            data = gson.fromJson("["+response+"]", type);
        }
        return data;
    }
    public static boolean addTaskJson(String url, String taskName, String category, String type,
                                  String level, String author, String newLatitude, String newLongitute,
                                  String radiusID, String taskText)
            throws Exception {
        JSONObject main = new JSONObject ();
        JSONObject  categoryJson = new JSONObject ();
        JSONObject  typeJson = new JSONObject ();
        JSONObject  levelJson = new JSONObject ();
        JSONObject  coordinateJson = new JSONObject ();
        main.put("name", taskName);
        categoryJson.put("name", category);
        main.put("category", categoryJson);
        typeJson.put("name", type);
        main.put("type", typeJson);
        levelJson.put("name", level);
        main.put("level", levelJson);
        main.put("author", author);
        coordinateJson.put("latitude", newLatitude);
        coordinateJson.put("longitude", newLongitute);
        coordinateJson.put("radius", radiusID);
        main.put("coordinates", coordinateJson);
        main.put("text", taskText);
        return WebAPI.addTask(url, main.toString());
    }
}
