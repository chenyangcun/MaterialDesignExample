package com.aswifter.material.news;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
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

public class NewsDetailActivity extends BaseActivity implements Updatable {
    private WebView webView;
    private ImageView titleImageView;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private Toolbar toolbar;
    private Repository<Result<NewsDetailResponse>> repository;
    private NewsDetailSupplier newsDetailSupplier;
    private Story story;
    public static final String NEWS = "news_key";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);
        initView();
        initRepository();
    }

    @Override
    protected void initView() {
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
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
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        story = getIntent().getParcelableExtra(NEWS);
        collapsingToolbarLayout.setTitle(story.getTitle());
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
        newsDetailSupplier.setKey(String.valueOf(story.getId()));
        repository.addUpdatable(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        repository.removeUpdatable(this);
    }

    @Override
    public void update() {
        if (repository.get().isPresent()) {
            repository.get().ifFailedSendTo(new Receiver<Throwable>() {
                @Override
                public void accept(@NonNull Throwable value) {
                    Toast.makeText(NewsDetailActivity.this, "加载失败", Toast.LENGTH_SHORT).show();
                }
            }).ifSucceededSendTo(new Receiver<NewsDetailResponse>() {
                @Override
                public void accept(@NonNull final NewsDetailResponse value) {
                    collapsingToolbarLayout.setTitle(value.getTitle());
                    Glide.with(NewsDetailActivity.this)
                            .load(value.getImage())
                            .asBitmap()
                            .into(titleImageView);
                    if (value.getCss() != null && value.getCss().size() > 0) {
                        final Repository<Result<String>> resultRepository = Repositories.repositoryWithInitialValue(Result.<String>absent())
                                .observe()
                                .onUpdatesPerLoop()
                                .goTo(ThreadPool.executor)
                                .thenGetFrom(new Supplier<Result<String>>() {
                                    @NonNull
                                    @Override
                                    public Result<String> get() {
                                        String result = null;
                                        try {
                                            String url = value.getCss().get(0);
                                            url = url.substring(url.lastIndexOf('=') + 1);
                                            result = AppClient.httpService.getCSS(url).execute().body();
                                        } catch (Exception e) {
                                            Log.d("NewsDetail", "WebViewError" + e);
                                        }
                                        if (result == null) {
                                            return Result.failure();
                                        } else {
                                            return Result.success(result);
                                        }
                                    }
                                })
                                .compile();
                        resultRepository.addUpdatable(new Updatable() {
                            @Override
                            public void update() {
                                String css = "<link rel=\"stylesheet\" href=\"file:///android_asset/news.css\" type=\"text/css\">";
                                String html = "<html><head>" + css + "</head><body>" + value.getBody() + "</body></html>";
                                html = html.replace("<div class=\"img-place-holder\">", "");
                                webView.loadDataWithBaseURL("file:///android_asset/", html, "text/html", "UTF-8", null);
                                resultRepository.removeUpdatable(this);
                            }
                        });
                    } else {
                        webView.loadData(value.getBody(), "text/html", "utf-8");
                    }
                }
            });
        }
    }

    class NewsDetailSupplier implements Supplier<Result<NewsDetailResponse>> {

        String key;

        public void setKey(String key) {
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
            if (newsDetailResponse == null) {
                return Result.failure();
            } else {
                return Result.success(newsDetailResponse);
            }
        }
    }
}
