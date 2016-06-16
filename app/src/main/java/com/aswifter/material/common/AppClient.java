package com.aswifter.material.common;

import com.aswifter.material.book.BookResponse;
import com.aswifter.material.news.NewsDetailResponse;
import com.aswifter.material.news.NewsResponse;

import java.util.Map;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;

/**
 * Created by erfli on 6/14/16.
 */
public class AppClient {
    public interface HttpService{
        @GET ("https://api.douban.com/v2/book/search")
        Call<BookResponse> getBooks(@QueryMap Map<String, String> options);
        @GET("news/latest")
        Call<NewsResponse>getLatestNews();
        @GET("news/before/{path}")
        Call<NewsResponse>getHistoryNews(@Path("path") String path);
        @GET("news/{key}")
        Call<NewsDetailResponse>getNewsDetail(@Path("key")String key);
    }
    public static HttpService httpService;
    public static void initAppClient(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://news-at.zhihu.com/api/4/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        httpService = retrofit.create(HttpService.class);
    }

}
