package com.lydonc.snowtrix;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("Trick")
public class Trick extends ParseObject{
    public Trick(){

    }

    public void setUsername(String currentUsername){
        put("username", currentUsername);
    }

    public String getUsername(){
        return getString("username");
    }

    public void setTrickName(String name){
        put("trickName", name);
    }

    public String getTrickName(){
        return getString("trickName");
    }

    public void setTrickCategory(String category){
        put("trickCategory", category);
    }

    public String getTrickCategory(){
        return getString("trickCategory");
    }

    public void setTrickList(String category){
        put("trickList", category);
    }

    public String getTrickList(){
        return getString("trickList");
    }

}