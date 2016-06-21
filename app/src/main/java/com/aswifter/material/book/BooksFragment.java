package com.aswifter.material.book;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.ProgressBar;

import com.afollestad.materialdialogs.MaterialDialog;
import com.aswifter.material.R;
import com.aswifter.material.common.ThreadPool;
import com.aswifter.material.widget.RecyclerItemClickListener;
import com.google.android.agera.BaseObservable;
import com.google.android.agera.Repositories;
import com.google.android.agera.Repository;
import com.google.android.agera.Result;
import com.google.android.agera.Updatable;

import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * Created by Chenyc on 15/7/1.
 */
public class BooksFragment extends Fragment implements Updatable {

    private RecyclerView mRecyclerView;
    private MyAdapter mAdapter;
    private ProgressBar mProgressBar;
    private FloatingActionButton mFabButton;

    private static final int ANIM_DURATION_FAB = 400;
    private ExecutorService networkExecutor;
    private Repository<Result<List<Book>>> booksRepository;
    private SearchObservable searchObservable;
    private BooksSupplier booksSupplier;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_books, null);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), onItemClickListener));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mProgressBar = (ProgressBar) view.findViewById(R.id.progressBar);

        mAdapter = new MyAdapter(this, getActivity());
        mRecyclerView.setAdapter(mAdapter);

        setUpFAB(view);

        setUpRepository();

        return view;
    }

    public class SearchObservable extends BaseObservable {

        public void doSearch(String key) {
            booksSupplier.setKey(key);
            dispatchUpdate();
        }

    }


    private void setUpRepository() {
        searchObservable = new SearchObservable();
        booksSupplier = new BooksSupplier(getString(R.string.default_search_keyword));
        // Set up books repository
        booksRepository = Repositories
                .repositoryWithInitialValue(Result.<List<Book>>absent())
                .observe(searchObservable)
                .onUpdatesPerLoop()
                .goTo(ThreadPool.executor)
                .thenGetFrom(booksSupplier)
                .compile();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mFabButton.setTranslationY(2 * getResources().getDimensionPixelOffset(R.dimen.btn_fab_size));
        //doSearch(getString(R.string.default_search_keyword));
    }


    @Override
    public void update() {
        mProgressBar.setVisibility(View.GONE);
        startFABAnimation();
        if (booksRepository.get().isPresent()) {
            mAdapter.updateItems(booksRepository.get().get(), true);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        booksRepository.addUpdatable(this);
    }


    @Override
    public void onPause() {
        super.onPause();
        booksRepository.removeUpdatable(this);
    }

    private void doSearch(String keyword) {
        mProgressBar.setVisibility(View.VISIBLE);
        mAdapter.clearItems();
        searchObservable.doSearch(keyword);
    }


    private void setUpFAB(View view) {
        mFabButton = (FloatingActionButton) view.findViewById(R.id.fab_normal);
        mFabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MaterialDialog.Builder(getActivity())
                        .title(R.string.search)
                        //.inputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)
                        .input(R.string.input_hint, R.string.input_prefill, new MaterialDialog.InputCallback() {
                            @Override
                            public void onInput(MaterialDialog dialog, CharSequence input) {
                                // Do something
                                if (!TextUtils.isEmpty(input)) {
                                    doSearch(input.toString());
                                }
                            }
                        }).show();
            }
        });
    }


    private void startFABAnimation() {
        mFabButton.animate()
                .translationY(0)
                .setInterpolator(new OvershootInterpolator(1.f))
                .setStartDelay(500)
                .setDuration(ANIM_DURATION_FAB)
                .start();
    }


    private RecyclerItemClickListener.OnItemClickListener onItemClickListener = new RecyclerItemClickListener.OnItemClickListener() {
        @Override
        public void onItemClick(View view, int position) {
            Book book = mAdapter.getBook(position);
            Intent intent = new Intent(getActivity(), BookDetailActivity.class);
            intent.putExtra("book", book);

            ActivityOptionsCompat options =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(),
                            view.findViewById(R.id.ivBook), getString(R.string.transition_book_img));

            ActivityCompat.startActivity(getActivity(), intent, options.toBundle());

        }
    };


}
