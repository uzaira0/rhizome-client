package com.geekbeast.retrofit;

import java.util.Map;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface Api {

    @POST("/")
    Void post( @Body Map<String, String> blah );

    @GET("/text")
    String text();
}
