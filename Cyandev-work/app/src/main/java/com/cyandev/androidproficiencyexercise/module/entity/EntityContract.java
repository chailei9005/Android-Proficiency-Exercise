package com.cyandev.androidproficiencyexercise.module.entity;

import com.cyandev.androidproficiencyexercise.BasePresenter;
import com.cyandev.androidproficiencyexercise.IPresenter;
import com.cyandev.androidproficiencyexercise.model.Entity;

import java.util.List;

/**
 * Created by cyandev on 2016/11/25.
 */
public interface EntityContract {

    interface View {

        int STATE_LOADING_IDLE = 0;
        int STATE_LOADING_REFRESHING = 1;
        int STATE_LOADING_RESERVING = 2;

        void setLoading(int state);

        void addEntities(List<Entity> entities);

        void clearEntities();

        void showNetworkError();

    }

    abstract class Presenter extends BasePresenter<View> {

        abstract void setCategory(String categoryName);

        abstract String getCategory();

        abstract void refresh();

        abstract void reserve();

        abstract void loadEntities(int page);

    }

}
