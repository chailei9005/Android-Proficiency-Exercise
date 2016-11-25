package com.cyandev.androidproficiencyexercise.util;

import com.cyandev.androidproficiencyexercise.model.Entity;
import com.cyandev.androidproficiencyexercise.model.EntityResponse;

import java.util.List;

import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by cyandev on 2016/11/25.
 */
public interface GankService {

    @GET("data/{category}/{pageSize}/{page}")
    Observable<EntityResponse> listEntities(@Path("category") String category,
                                            @Path("pageSize") int pageSize,
                                            @Path("page") int page);

}
