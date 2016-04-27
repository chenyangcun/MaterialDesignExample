package com.aswifter.material.book;

import android.support.annotation.NonNull;

import com.google.android.agera.Result;
import com.google.android.agera.Supplier;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

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
        HttpUrl url = HttpUrl.parse(getAbsoluteUrl("book/search"))
                .newBuilder()
                .addQueryParameter("q", key)
                .addQueryParameter("start", "0")
                .addQueryParameter("end", "50")
                .build();

        Request request = new Request.Builder()
                .url(url)
                .build();

        try {
            Response response = client.newCall(request).execute();
            JSONObject json = new JSONObject(response.body().string());
            JSONArray jaBooks = json.optJSONArray("books");
            Gson gson = new Gson();
            List<Book> books = gson.fromJson(jaBooks.toString(), new TypeToken<List<Book>>() {
            }.getType());
            return books;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
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
