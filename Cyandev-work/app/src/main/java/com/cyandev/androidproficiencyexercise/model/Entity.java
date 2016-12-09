package com.cyandev.androidproficiencyexercise.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by cyandev on 2016/11/25.
 */
public class Entity {

    @SerializedName("publishedAt")
    public String publishedAt;

    @SerializedName("source")
    public String source;

    @SerializedName("type")
    public String type;

    @SerializedName("url")
    public String url;

    @SerializedName("desc")
    public String title;

    @SerializedName("who")
    public String author;

    @SerializedName("images")
    public List<String> images;

    public String getFormattedPublishDate() {
        return publishedAt.substring(0, 10);
    }

}
