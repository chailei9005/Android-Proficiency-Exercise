package com.cyandev.androidproficiencyexercise.dao;

import java.util.List;

/**
 * Created by cyandev on 2016/12/9.
 */
public interface BaseDAO<Model, QueryParams> {

    long insert(Model model);
    void delete(QueryParams queryParams);
    void update(QueryParams queryParams, Model model);
    List<Model> query(QueryParams queryParams);

}
