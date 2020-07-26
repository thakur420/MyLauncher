package com.example.mylauncher;

import android.graphics.drawable.Drawable;

public class Appobject {
    private String name,packageName;
    private Drawable image;

    public Appobject(String name,String packageName,Drawable image){
        this.name = name;
        this.packageName = packageName;
        this.image = image;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getName() {
        return name;
    }

    public Drawable getImage() {
        return image;
    }
}
