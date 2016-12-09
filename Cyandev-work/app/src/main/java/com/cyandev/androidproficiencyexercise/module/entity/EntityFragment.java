package com.cyandev.androidproficiencyexercise.module.entity;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cyandev.androidproficiencyexercise.R;
import com.cyandev.androidproficiencyexercise.model.Entity;
import com.cyandev.androidproficiencyexercise.module.webview.WebViewActivity;

import java.util.List;

/**
 * Created by cyandev on 2016/11/25.
 */
public class EntityFragment extends Fragment implements EntityContract.View {

    private static final int LOAD_MORE_THRESHOLD = 3;
    private static final String KEY_CATEGORY_NAME = "KEY_CATEGORY_NAME";

    private EntityContract.Presenter presenter = new EntityPresenter();

    private RecyclerView recyclerView;
    private SwipeRefreshLayout refreshLayout;

    private EntityAdapter adapter;

    private Handler handler = new Handler();

    private int loadingState = STATE_LOADING_IDLE;

    public static EntityFragment instantiate(String category) {
        EntityFragment fragment = new EntityFragment();
        fragment.presenter.setCategory(category);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            presenter.setCategory(savedInstanceState.getString(KEY_CATEGORY_NAME));
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_entity, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.rv);
        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.layout_refresh);

        adapter = new EntityAdapter();
        adapter.setOnEntityItemClickListener(new EntityAdapter.OnEntityItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                openWebView(position);
            }
        });

        recyclerView.setAdapter(adapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                // Avoid changing data source while `OnScrollListener` method is invoking.
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        onRecyclerViewScrolled();
                    }
                });
            }
        });
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                presenter.refresh();
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        presenter.onAttachView(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        presenter.onDetachView();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_CATEGORY_NAME, presenter.getCategory());
    }

    public void onRecyclerViewScrolled() {
        if (recyclerView.getChildCount() > 0 && loadingState == STATE_LOADING_IDLE) {
            int lastChildIndex = recyclerView.getChildCount() - 1;
            View child = recyclerView.getChildAt(lastChildIndex);
            int adapterPosition = recyclerView.getChildAdapterPosition(child);
            if (adapterPosition >= adapter.getItemCount() - LOAD_MORE_THRESHOLD) {
                presenter.reserve();
            }
        }
    }

    public void setLoading(final int state) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (state == STATE_LOADING_REFRESHING) {
                    refreshLayout.setRefreshing(true);
                } else {
                    refreshLayout.setRefreshing(false);
                }

                if (state == STATE_LOADING_RESERVING) {
                    adapter.setReserving(true);
                } else {
                    adapter.setReserving(false);
                }
            }
        });
    }

    public void addEntities(List<Entity> entities) {
        if (loadingState != STATE_LOADING_IDLE) {
            throw new RuntimeException("Illegal state.");
        }

        adapter.addEntities(entities);
    }

    public void clearEntities() {
        if (loadingState != STATE_LOADING_IDLE) {
            throw new RuntimeException("Illegal state.");
        }

        adapter.clearEntities();
    }

    public void showNetworkError() {
        View view = getView();
        if (view != null) {
            Snackbar.make(view, R.string.network_error_hint, Snackbar.LENGTH_SHORT).show();
        }
    }

    @Override
    public void runOnUiThread(Runnable runnable) {
        handler.post(runnable);
    }

    private void openWebView(int entityIndex) {
        Intent intent = new Intent(getActivity(), WebViewActivity.class);
        intent.setData(Uri.parse(adapter.getEntities().get(entityIndex).url));
        startActivity(intent);
    }

}
