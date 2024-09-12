package com.pleasanttours.myapplication;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ApiService {
    @Headers({
            "Content-Type: application/json",
            "Accept: application/json"
    })
    @POST("registar_dados.php")
    Call<ResponseBody> sendUsageData(@Body UsageData usageData);

    @GET("obter_dados.php")
    Call<List<UsageData>> getUsageData();
}