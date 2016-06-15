package com.aswifter.material.news;

import java.util.List;


public class NewsResponse{

    private static final String FIELD_TOP_STORIES = "top_stories";
    private static final String FIELD_STORIES = "stories";
    private static final String FIELD_DATE = "date";


    private List<TopStory> topStories;
    private List<Story> stories;
    private int date;


    public NewsResponse(){

    }

    public void setTopStories(List<TopStory> topStories) {
        this.topStories = topStories;
    }

    public List<TopStory> getTopStories() {
        return topStories;
    }

    public void setStories(List<Story> stories) {
        this.stories = stories;
    }

    public List<Story> getStories() {
        return stories;
    }

    public void setDate(int date) {
        this.date = date;
    }

    public int getDate() {
        return date;
    }


}