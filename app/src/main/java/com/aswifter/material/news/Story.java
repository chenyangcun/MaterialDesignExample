package com.aswifter.material.news;

import java.util.List;


public class Story{

    private static final String FIELD_TYPE = "type";
    private static final String FIELD_ID = "id";
    private static final String FIELD_GA_PREFIX = "ga_prefix";
    private static final String FIELD_IMAGES = "images";
    private static final String FIELD_TITLE = "title";


    private int type;
    private long id;
    private int gaPrefix;
    private List<String> images;
    private String title;


    public Story(){

    }

    public void setType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void setGaPrefix(int gaPrefix) {
        this.gaPrefix = gaPrefix;
    }

    public int getGaPrefix() {
        return gaPrefix;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public List<String> getImages() {
        return images;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public boolean equals(Object obj){
        if(obj instanceof Story){
            return ((Story) obj).getId() == id;
        }
        return false;
    }

    @Override
    public int hashCode(){
        return ((Long) id).hashCode();
    }


}