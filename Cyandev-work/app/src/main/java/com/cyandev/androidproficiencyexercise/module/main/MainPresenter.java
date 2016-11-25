package com.cyandev.androidproficiencyexercise.module.main;

import java.util.Arrays;

/**
 * Created by cyandev on 2016/11/25.
 */
public class MainPresenter extends MainContract.Presenter {

    @Override
    void loadCategories() {
        // It was set that there are only three categories.
        getView().setTabs(Arrays.asList("Android", "iOS", "前端"));
    }

    @Override
    public void onAttachView(MainContract.View view) {
        super.onAttachView(view);
        loadCategories();
    }
}
