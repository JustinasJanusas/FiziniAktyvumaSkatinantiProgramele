package com.example.ejunasapp;

import android.util.Log;

import com.google.gson.Gson;

import java.lang.reflect.Type;
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
}
