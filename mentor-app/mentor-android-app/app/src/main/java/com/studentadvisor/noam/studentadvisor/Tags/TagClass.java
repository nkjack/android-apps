package com.studentadvisor.noam.studentadvisor.Tags;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Noam on 11/13/2015.
 */
public class TagClass {

    String code;
    String name;
    String color;

    public TagClass() {

    }

    public TagClass(String sinif, String name) {
        this.code = sinif;
        this.name = name;
        this.color = "#303F9F";//getRandomColor();

    }

    public String getRandomColor() {
        ArrayList<String> colors = new ArrayList<>();
        colors.add("#303F9F");
        /*
        colors.add("#ED7D31");
        colors.add("#00B0F0");
        colors.add("#FF0000");
        colors.add("#D0CECE");
        colors.add("#00B050");
        colors.add("#9999FF");
        colors.add("#FF5FC6");
        colors.add("#FFC000");
        colors.add("#7F7F7F");
        colors.add("#4800FF");
        */

        return colors.get(new Random().nextInt(colors.size()));
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getSinif() {
        return code;
    }

    public void setSinif(String code) {
        this.code = code;
    }


}