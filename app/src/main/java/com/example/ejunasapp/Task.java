package com.example.ejunasapp;

import java.io.Serializable;

public class Task implements Serializable{
    public int id;
    public String name;
    public String text;
    public TaskItem category;
    public TaskItem type;
    public TaskItem level;
    public String author;
    public String base64_picture;
}
