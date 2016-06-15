package com.aswifter.material.book;

import android.support.annotation.NonNull;

import com.aswifter.material.common.AppClient;
import com.google.android.agera.Result;
import com.google.android.agera.Supplier;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by chenyc on 16/4/27.
 */
public class BooksSupplier implements Supplier<Result<List<Book>>> {

    public String key;

    public void setKey(String key) {
        this.key = key;
    }

    public BooksSupplier(String key) {
        this.key = key;
    }


    OkHttpClient client = new OkHttpClient();

    private static final String BASE_URL = "https://api.douban.com/v2/";

    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }


    private List<Book> getBooks() {
        Map<String, String >params = new HashMap<>();
        params.put("q",key);
        params.put("start","0");
        params.put("end","50");
        try {
            BookResponse bookResponse = AppClient.httpService.getBooks(params).execute().body();
            return bookResponse.getBooks();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    @NonNull
    @Override
    public Result<List<Book>> get() {
        List<Book> books = getBooks();
        if (books == null) {
            return Result.failure();
        } else {
            return Result.success(books);
        }
    }
}
