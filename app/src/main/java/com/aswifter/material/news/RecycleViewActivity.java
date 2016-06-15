package com.aswifter.material.news;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.aswifter.material.R;
import com.aswifter.material.common.ThreadPool;
import com.aswifter.material.widget.DividerItemDecoration;
import com.google.android.agera.Repositories;
import com.google.android.agera.Repository;
import com.google.android.agera.Result;
import com.google.android.agera.Updatable;

import java.util.ArrayList;
import java.util.List;

public class RecycleViewActivity extends AppCompatActivity implements Updatable {

    Repository<Result<List<Story>>> repository;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private List<Story> myDataset;
    private NewsAdapter mAdapter;

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

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));

        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        repository = Repositories.repositoryWithInitialValue(Result.<List<Story>>absent())
                .observe()
                .onUpdatesPerLoop()
                .goTo(ThreadPool.executor)
                .thenGetFrom(new NewsSupplier())
                .compile();
        myDataset = new ArrayList<>();
        mAdapter = new NewsAdapter(this, this, myDataset);
        mRecyclerView.setAdapter(mAdapter);
        getData();
    }

    private void getData() {
        repository.addUpdatable(this);
    }

    @Override
    public void update() {
        mAdapter.updateData(repository.get().get());
    }
}
