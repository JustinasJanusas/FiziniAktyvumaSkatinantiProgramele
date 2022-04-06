package com.example.ejunasapp;

import java.io.Serializable;

public class TaskItem implements Serializable {
    public int id;
    public String name;
    public TaskItem(int id, String name){
        this.id = id;
        this.name = name;
    }
}
