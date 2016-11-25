package com.cyandev.androidproficiencyexercise;

import java.lang.ref.WeakReference;

/**
 * Created by cyandev on 2016/11/25.
 */
public class BasePresenter<T> implements IPresenter<T> {

    private WeakReference<T> view;

    public T getView() {
        return view.get();
    }

    @Override
    public void onAttachView(T view) {
        this.view = new WeakReference(view);
    }

    @Override
    public void onDetachView() {
        this.view.clear();
        this.view = null;
    }
}
