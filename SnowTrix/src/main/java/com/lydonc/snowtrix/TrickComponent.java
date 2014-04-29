package com.lydonc.snowtrix;
import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("TrickComponent")
public class TrickComponent extends ParseObject{
    public TrickComponent(){

    }

    public boolean isDisabled(){
        return getBoolean("completed");
    }

    public void setDisabled(boolean disabled){
        put("disabled", disabled);
    }

    public String getName(){
        return getString("name");
    }

    public void setName(String name){
        put("name", name);
    }

    public String getCategory(){
        return getString("category");
    }

    public void setCategory(String category){
        put("category", category);
    }
}