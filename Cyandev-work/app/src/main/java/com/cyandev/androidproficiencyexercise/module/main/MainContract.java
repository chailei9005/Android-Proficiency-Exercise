package com.cyandev.androidproficiencyexercise.module.main;

import com.cyandev.androidproficiencyexercise.BasePresenter;

import java.util.List;

/**
 * Created by cyandev on 2016/11/25.
 */
public interface MainContract {

    interface View {

        void setTabs(List<String> tabs);

    }

    abstract class Presenter extends BasePresenter<View> {

        abstract void loadCategories();

    }

}
