package com.cyandev.androidproficiencyexercise.module.entity;

import android.content.Context;
import android.content.SharedPreferences;

import com.cyandev.androidproficiencyexercise.App;
import com.cyandev.androidproficiencyexercise.dao.EntityDAO;
import com.cyandev.androidproficiencyexercise.db.DBOpenHelper;
import com.cyandev.androidproficiencyexercise.model.Entity;
import com.cyandev.androidproficiencyexercise.model.EntityResponse;
import com.cyandev.androidproficiencyexercise.util.FileHelper;
import com.cyandev.androidproficiencyexercise.util.GankService;
import com.cyandev.androidproficiencyexercise.util.HttpClientHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.exceptions.Exceptions;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by cyandev on 2016/11/25.
 */
public class EntityPresenter extends EntityContract.Presenter {

    private static final int PAGE_SIZE = 10;

    private File cacheFile;
    private List<Entity> cachedEntities = new ArrayList<>();
    private String category;
    private int currentPage = 1;
    private boolean fullyLoaded = false;

    private SharedPreferences cacheConfig;
    private DBOpenHelper dbOpenHelper;
    private EntityDAO dao;

    private GankService gankService;

    private Subscription loadingSubscription;

    public EntityPresenter() {
        // Presenters don't need UI relevant context, just use the global application context.

        cacheFile = FileHelper.getNamedCacheFile(App.getInstance(), "gank");

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://gank.io/api/")
                .client(HttpClientHelper.createClient(cacheFile))
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.createWithScheduler(Schedulers.io()))
                .build();

        gankService = retrofit.create(GankService.class);

        cacheConfig = App.getInstance().getSharedPreferences("cache_config", Context.MODE_PRIVATE);
        dbOpenHelper = new DBOpenHelper(App.getInstance());
        dao = new EntityDAO(dbOpenHelper.getWritableDatabase());
    }

    @Override
    public void setCategory(String categoryName) {
        this.category = categoryName;
    }

    @Override
    public String getCategory() {
        return this.category;
    }

    @Override
    public void refresh() {
        fullyLoaded = false;

        getView().setLoading(EntityContract.View.STATE_LOADING_REFRESHING);

        loadEntities(1, true);
    }

    @Override
    void reserve() {
        if (fullyLoaded) {
            return;
        }

        getView().setLoading(EntityContract.View.STATE_LOADING_RESERVING);

        loadEntities(currentPage + 1, false);
    }

    @Override
    public void onAttachView(EntityContract.View view) {
        super.onAttachView(view);

        // View recycled, no need to reload anything.
        if (cachedEntities.size() > 0) {
            getView().addEntities(cachedEntities);
            return;
        }

        // App just started up.
        // Try loading cached data.
        List<Entity> entities = dao.query(category);

        if (entities != null && entities.size() > 0) {
            currentPage = cacheConfig.getInt(getLoadedPagesPrefKey(), 1);
            cachedEntities.addAll(entities);
            getView().addEntities(entities);
        } else {
            // No cache available, fetch from network.
            refresh();
        }
    }

    @Override
    public void onDetachView() {
        super.onDetachView();

        unsubscribeIfNeeded();
    }

    private void unsubscribeIfNeeded() {
        if (loadingSubscription != null) {
            loadingSubscription.unsubscribe();
            loadingSubscription = null;
        }
    }

    private void loadEntities(int page, final boolean clearBefore) {
        unsubscribeIfNeeded();

        loadingSubscription = gankService.listEntities(category, PAGE_SIZE, page)
                .subscribe(new Action1<EntityResponse>() {
                    @Override
                    public void call(final EntityResponse response) {
                        getView().setLoading(EntityContract.View.STATE_LOADING_IDLE);

                        if (response.hasError) {
                            throw Exceptions.propagate(new Exception());
                        }

                        if (response.entities.size() == 0) {
                            fullyLoaded = true;
                            return;
                        }

                        if (clearBefore) {
                            currentPage = 1;
                            cachedEntities.clear();
                            dao.delete(category);
                        } else {
                            currentPage++;
                        }

                        for (Entity entity : response.entities) {
                            dao.insert(entity);
                        }

                        saveLoadedPageCount();

                        cachedEntities.addAll(response.entities);
                        getView().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (clearBefore) {
                                    getView().clearEntities();
                                }
                                getView().addEntities(response.entities);
                            }
                        });

                        loadingSubscription = null;
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        getView().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                getView().setLoading(EntityContract.View.STATE_LOADING_IDLE);
                                getView().showNetworkError();
                            }
                        });
                        
                        loadingSubscription = null;
                    }
                });
    }

    private void saveLoadedPageCount() {
        cacheConfig.edit().putInt(getLoadedPagesPrefKey(), currentPage).apply();
    }

    private String getLoadedPagesPrefKey() {
        return category + "_loaded_pages";
    }

}
