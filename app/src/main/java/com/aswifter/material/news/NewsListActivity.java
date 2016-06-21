package com.aswifter.material.news;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.aswifter.material.R;
import com.aswifter.material.common.ThreadPool;
import com.aswifter.material.widget.DividerItemDecoration;
import com.aswifter.material.widget.DividerOffsetDecoration;
import com.aswifter.material.widget.PullToRefreshLayout;
import com.aswifter.material.widget.RecyclerItemClickListener;
import com.aswifter.material.widget.RefreshLayout;
import com.google.android.agera.Receiver;
import com.google.android.agera.Repositories;
import com.google.android.agera.Repository;
import com.google.android.agera.Result;
import com.google.android.agera.Updatable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class NewsListActivity extends AppCompatActivity implements Updatable {

    Repository<Result<List<Story>>> repository;
    NewsObservable newsObservable;
    Receiver<List<Story>> receiver;
    Receiver<Throwable> throwableReceiver;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private List<Story> myDataset;
    private NewsAdapter mAdapter;
    RefreshLayout refreshLayout;

    private int page = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.latest_news);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        initRefreshView();
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
//        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST).setmDivider(getResources().getDrawable(R.drawable.md_transparent)));
        mRecyclerView.addItemDecoration(new DividerOffsetDecoration());
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        initData();
    }

    private void initRefreshView(){
        refreshLayout = (RefreshLayout) findViewById(R.id.refresh_layout);
        refreshLayout.setColorSchemeResources(R.color.google_blue,
                R.color.google_green,
                R.color.google_red,
                R.color.google_yellow);
        refreshLayout.setOnRefreshListener(new PullToRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getLatestData();
            }
        });
        refreshLayout.setOnLoadListener(new RefreshLayout.OnLoadListener() {
            @Override
            public void onLoad() {
                getHistoryData();
            }
        });
    }

    private void initData(){
        NewsSupplier newsSupplier = new NewsSupplier();
        newsObservable = new NewsObservable(newsSupplier);
        repository = Repositories.repositoryWithInitialValue(Result.<List<Story>>absent())
                .observe(newsObservable)
                .onUpdatesPerLoop()
                .goTo(ThreadPool.executor)
                .thenGetFrom(newsSupplier)
                .compile();

        receiver = new Receiver<List<Story>>() {

            @Override
            public void accept(@NonNull List<Story> value) {
                if(page > 1){
                    mAdapter.addData(value);
                }else{
                    mAdapter.updateData(value);
                }
            }
        };

        throwableReceiver = new Receiver<Throwable>() {
            @Override
            public void accept(@NonNull Throwable value) {

            }
        };
        myDataset = new ArrayList<>();
        mAdapter = new NewsAdapter(this, myDataset);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(NewsListActivity.this,NewsDetailActivity.class);
                ActivityOptionsCompat options =
                        ActivityOptionsCompat.makeSceneTransitionAnimation(NewsListActivity.this,
                                view.findViewById(R.id.news_image), getString(R.string.transition_news_img));
                intent.putExtra(NewsDetailActivity.NEWS, mAdapter.getItem(position));
                ActivityCompat.startActivity(NewsListActivity.this,intent, options.toBundle());
            }
        }));
        getLatestData();
    }

    @Override
    public void onResume() {
        super.onResume();
        repository.addUpdatable(this);
    }


    @Override
    public void onPause() {
        super.onPause();
        repository.removeUpdatable(this);
    }

    private void getLatestData() {
        newsObservable.refreshNews("");
        page = 0;
    }

    private void getHistoryData(){
        String time = getDateString(new Date());
        String key = String.valueOf(Long.valueOf(time) - page);
        newsObservable.refreshNews(key);
        page+=1;
    }

    @Override
    public void update() {
        refreshLayout.setRefreshing(false);
        repository.get().ifFailedSendTo(throwableReceiver).ifSucceededSendTo(receiver);
    }

    public static String getDateString(Date date){
        String year =(date.getYear()+1900)+"";
        String mm = (date.getMonth()+1)+"";
        if(Integer.valueOf(mm).intValue()<10){
            mm="0"+mm;
        }
        String day = date.getDate()+"";
        if(Integer.valueOf(day).intValue()<10)day="0"+day;
        return year+mm+day;
    }
}
