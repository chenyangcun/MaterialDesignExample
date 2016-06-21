package com.aswifter.material.news;



public class TopStory{

    private static final String FIELD_TYPE = "type";
    private static final String FIELD_ID = "id";
    private static final String FIELD_GA_PREFIX = "ga_prefix";
    private static final String FIELD_TITLE = "title";
    private static final String FIELD_IMAGE = "image";


    private int mType;
    private long mId;
    private int mGaPrefix;
    private String mTitle;
    private String mImage;


    public TopStory(){

    }

    public void setType(int type) {
        mType = type;
    }

    public int getType() {
        return mType;
    }

    public void setId(long id) {
        mId = id;
    }

    public long getId() {
        return mId;
    }

    public void setGaPrefix(int gaPrefix) {
        mGaPrefix = gaPrefix;
    }

    public int getGaPrefix() {
        return mGaPrefix;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setImage(String image) {
        mImage = image;
    }

    public String getImage() {
        return mImage;
    }

    @Override
    public boolean equals(Object obj){
        if(obj instanceof TopStory){
            return ((TopStory) obj).getId() == mId;
        }
        return false;
    }

    @Override
    public int hashCode(){
        return ((Long)mId).hashCode();
    }


}