package com.aswifter.material.news;

import java.util.List;


public class NewsResponse{
    private List<Story> stories;
    private String date;
    public NewsResponse(){

    }

    public void setStories(List<Story> stories) {
        this.stories = stories;
    }

    public List<Story> getStories() {
        return stories;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDate() {
        return date;
    }


}