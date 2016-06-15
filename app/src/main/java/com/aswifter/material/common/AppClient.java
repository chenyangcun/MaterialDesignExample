package com.aswifter.material.common;

import com.aswifter.material.book.BookResponse;

import java.util.Map;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

/**
 * Created by erfli on 6/14/16.
 */
public class AppClient {
    public interface HttpService{
        @GET ("book/search")
        Call<BookResponse> getBooks(@QueryMap Map<String, String> options);
    }
    public static HttpService httpService;
    public static final OkHttpClient okHttpClient = new OkHttpClient();
    public static void initAppClient(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.douban.com/v2/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        httpService = retrofit.create(HttpService.class);
    }

}
