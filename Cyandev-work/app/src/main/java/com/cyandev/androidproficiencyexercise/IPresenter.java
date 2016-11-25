package com.cyandev.androidproficiencyexercise;

/**
 * Created by cyandev on 2016/11/25.
 */
public interface IPresenter<T> {

    void onAttachView(T view);

    void onDetachView();

}
