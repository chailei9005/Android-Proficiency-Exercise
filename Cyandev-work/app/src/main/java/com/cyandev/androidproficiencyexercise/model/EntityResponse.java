package com.cyandev.androidproficiencyexercise.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by cyandev on 2016/11/25.
 */
public class EntityResponse {

    @SerializedName("error")
    public boolean hasError;

    @SerializedName("results")
    public List<Entity> entities;

}
