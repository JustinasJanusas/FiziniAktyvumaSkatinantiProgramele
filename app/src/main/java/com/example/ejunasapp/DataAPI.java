package com.example.ejunasapp;

import android.util.Log;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class DataAPI {
    public static List<Task> jsonToTasks(String RestURL) throws Exception{
        List<Task> data = new ArrayList<Task>();
        String response = WebAPI.getData(RestURL);
        if(response.length() > 0){
            Gson gson;
            gson = new Gson();
            java.lang.reflect.Type type =
                    new com.google.gson.reflect.TypeToken<List<Task>>()
                    {}.getType();
            data = gson.fromJson(response, type);
        }
        return data;
    }

}
