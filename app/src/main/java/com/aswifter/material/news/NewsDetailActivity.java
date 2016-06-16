package com.aswifter.material.news;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.Toast;

import com.aswifter.material.R;
import com.aswifter.material.common.AppClient;
import com.aswifter.material.common.BaseActivity;
import com.aswifter.material.common.ThreadPool;
import com.bumptech.glide.Glide;
import com.google.android.agera.Receiver;
import com.google.android.agera.Repositories;
import com.google.android.agera.Repository;
import com.google.android.agera.Result;
import com.google.android.agera.Supplier;
import com.google.android.agera.Updatable;

import java.io.IOException;

public class NewsDetailActivity extends BaseActivity implements Updatable{
    private WebView webView;
    private ImageView titleImageView;
    private Toolbar toolbar;
    private Repository<Result<NewsDetailResponse>> repository;
    private NewsDetailSupplier newsDetailSupplier;
    public static final String NEWSKEY = "news_key";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);
        initView();
        initRepository();
    }

    @Override
    protected void initView() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        titleImageView = (ImageView) findViewById(R.id.ivImage);
        webView = (WebView) findViewById(R.id.webView);
        webView.setInitialScale(1);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setDomStorageEnabled(true);
    }

    @Override
    protected void initRepository() {
        newsDetailSupplier = new NewsDetailSupplier();
        repository = Repositories.repositoryWithInitialValue(Result.<NewsDetailResponse>absent())
                .observe()
                .onUpdatesPerLoop()
                .goTo(ThreadPool.executor)
                .thenGetFrom(newsDetailSupplier)
                .compile();
    }

    @Override
    protected void onResume() {
        super.onResume();
        newsDetailSupplier.setKey(getIntent().getStringExtra(NEWSKEY));
        repository.addUpdatable(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        repository.removeUpdatable(this);
    }

    @Override
    public void update() {
        if(repository.get().isPresent()){
            repository.get().ifFailedSendTo(new Receiver<Throwable>() {
                @Override
                public void accept(@NonNull Throwable value) {
                    Toast.makeText(NewsDetailActivity.this, "加载失败", Toast.LENGTH_SHORT).show();
                }
            }).ifSucceededSendTo(new Receiver<NewsDetailResponse>() {
                @Override
                public void accept(@NonNull NewsDetailResponse value) {
                    toolbar.setTitle(value.getTitle());
                    Glide.with(NewsDetailActivity.this)
                            .load(value.getImages().get(0))
                            .asBitmap()
                            .into(titleImageView);
                    webView.loadData(value.getBody(),"text/html","utf-8");
                }
            });
        }
    }

    class NewsDetailSupplier implements Supplier<Result<NewsDetailResponse>> {

        String key;

        public void setKey(String key){
            this.key = key;

        }
        @NonNull
        @Override
        public Result<NewsDetailResponse> get() {
            NewsDetailResponse newsDetailResponse = null;
            try {
                newsDetailResponse = AppClient.httpService.getNewsDetail(key).execute().body();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(newsDetailResponse == null){
                return Result.failure();
            }else{
                return Result.success(newsDetailResponse);
            }
        }
    }
}
