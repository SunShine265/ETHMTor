package com.goodproductssoft.minningpool;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by user on 5/8/2018.
 */

public interface WebService {
    @GET("/miner/{id}/settings")
    Call<ResponseBody> GetPoolSetings(@Path("id") String id);//function to call api

    @GET("/miner/{id}/currentStats")
    Call<ResponseBody> GetCurrentStats(@Path("id") String id);//function to call api

    @GET("/miner/{id}/history")
    Call<ResponseBody> GetHistory(@Path("id") String id);//function to call api

    @GET("/miner/{id}/payouts")
    Call<ResponseBody> GetPayouts(@Path("id") String id);//function to call api

    @GET("/miner/{id}/Settings")
    Call<ResponseBody> GetSettings(@Path("id") String id);//function to call api

    @GET("/miner/{id}/workers")
    Call<ResponseBody> GetWorkers(@Path("id") String id);//function to call api
}
