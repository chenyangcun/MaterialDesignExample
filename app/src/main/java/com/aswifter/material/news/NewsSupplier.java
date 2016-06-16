package com.aswifter.material.news;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.aswifter.material.common.AppClient;
import com.google.android.agera.Result;
import com.google.android.agera.Supplier;

import java.io.IOException;
import java.util.List;

/**
 * Created by erfli on 6/15/16.
 */
public class NewsSupplier implements Supplier<Result<List<Story>>> {

    private String newsKey = "latest";
    @NonNull
    @Override
    public Result<List<Story>> get() {
        List<Story> stories = getStoryList();
        if(stories == null){
            return Result.failure();
        }else{
            return Result.success(stories);
        }
    }

    private List<Story>getStoryList(){
        try {
            if(TextUtils.isEmpty(newsKey)){
                return AppClient.httpService.getLatestNews().execute().body().getStories();
            }else
                return AppClient.httpService.getHistoryNews(newsKey).execute().body().getStories();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void setNewsKey(@NonNull String key){
        if(TextUtils.isEmpty(key)){
            newsKey = "";
        }else {
            newsKey = key;
        }
    }
}
