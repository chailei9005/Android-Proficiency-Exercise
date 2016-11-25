package com.cyandev.androidproficiencyexercise.module.entity;

import android.content.Context;

import com.cyandev.androidproficiencyexercise.App;
import com.cyandev.androidproficiencyexercise.model.Entity;
import com.cyandev.androidproficiencyexercise.model.EntityResponse;
import com.cyandev.androidproficiencyexercise.util.FileHelper;
import com.cyandev.androidproficiencyexercise.util.GankService;
import com.cyandev.androidproficiencyexercise.util.HttpClientHelper;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Cache;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
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

    private GankService gankService;

    private Subscription loadingSubscription;

    public EntityPresenter() {
        // Presenters don't need UI relevant context, just use the global application context.

        cacheFile = HttpClientHelper.getNamedCacheFile(App.getInstance(), "gank");

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://gank.io/api/")
                .client(HttpClientHelper.createClient(cacheFile))
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.createWithScheduler(Schedulers.io()))
                .build();

        gankService = retrofit.create(GankService.class);
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

        FileHelper.recursivelyDelete(cacheFile);
        loadEntities(1, true);
    }

    @Override
    void reserve() {
        if (fullyLoaded) {
            return;
        }

        loadEntities(currentPage + 1);
    }

    @Override
    public void loadEntities(int page) {
        int state;
        if (cachedEntities.size() == 0) {
            state = EntityContract.View.STATE_LOADING_REFRESHING;
        } else {
            state = EntityContract.View.STATE_LOADING_RESERVING;
        }
        getView().setLoading(state);

        loadEntities(page, false);
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
        // This method load cache if network is unavailable.
        loadEntities(1);
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
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<EntityResponse>() {
                    @Override
                    public void call(EntityResponse response) {
                        getView().setLoading(EntityContract.View.STATE_LOADING_IDLE);

                        if (response.hasError) {
                            return;
                        }

                        if (response.entities.size() == 0) {
                            fullyLoaded = true;
                            return;
                        }

                        if (clearBefore) {
                            currentPage = 1;
                            cachedEntities.clear();
                            getView().clearEntities();
                        } else {
                            currentPage++;
                        }

                        cachedEntities.addAll(response.entities);
                        getView().addEntities(response.entities);

                        loadingSubscription = null;
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        getView().setLoading(EntityContract.View.STATE_LOADING_IDLE);
                        getView().showNetworkError();

                        loadingSubscription = null;
                    }
                });
    }

}
