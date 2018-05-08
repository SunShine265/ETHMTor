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
    Call<ResponseBody> getPoolSetings(@Path("id") String id);//function to call api
}
