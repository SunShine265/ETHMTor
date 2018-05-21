package com.goodproductssoft.minningpool;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.multidex.MultiDexApplication;
import android.widget.Toast;

import com.applovin.sdk.AppLovinSdk;
import com.goodproductssoft.minningpool.util.IabHelper;
import com.google.android.gms.ads.MobileAds;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by user on 5/2/2018.
 */

public class CustomApp extends MultiDexApplication {
    private static CustomApp instance = null;

    final static String ADMOB_APP_ID = "ca-app-pub-1827062885697339~6560780679";
    public static final String ADS_ITEM_SKU = "com.goodproductssoft.removeads";
    String base64EncodedPublicKey =
            "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAizsJsRCr9FfvY0kFdavuGjJpEdX1aktVmR5q78mSHquHVHxZHtXo/WzD8heFkfNuaAaPLVyvKfLK6n4j7z+IKzm+yM7ZBdGgJ9BrMZcTT0wk3UfyKKeYCPu7RzYVfAU17CAYlKhhw8Bvr2FN5gyuiyPTgPV1STX4+tz8w92R+rE7GjTV3PetGhPmKMviNAwBHCe+03eghNXXZJ1qKZK7/682KyUPRboe8TdND1jEmUaI9I6aMOcCwMbPMpkz3JcCKFhRCzdizMNvlxcqCMHNyj0v6kSxVoW6vSjzq7CdTovBx2eC9aKVbW+9Mjnm/Lxg5afW9eWPjhZ1TooLXfubtwIDAQAB";

    public final static CustomApp getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        MobileAds.initialize(this, ADMOB_APP_ID);
        AppLovinSdk.initializeSdk(this);


        //List<String> networksToInit = new ArrayList<String>();
        //networksToInit.add("MainActivity.java");

    }

    public IabHelper createIaHelper(){
        return new IabHelper(CustomApp.getInstance(), base64EncodedPublicKey);
    }

    public static void showToast(String message){
        showToast(Toast.LENGTH_LONG, message);
    }

    public static void showToast(int length, String message){
        if(instance != null) {
            Toast.makeText(instance,
                    message,
                    Toast.LENGTH_LONG)
                    .show();
        }
    }


    public static OkHttpClient getHttpClient(){
        //setup cache
        File httpCacheDirectory = new File(instance.getCacheDir(), "responses");
        okhttp3.Cache cache = new okhttp3.Cache(httpCacheDirectory, 20 * 1024 * 1024);
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .cache(cache)
                .readTimeout(15, TimeUnit.SECONDS)
                .connectTimeout(15, TimeUnit.SECONDS)
                .addNetworkInterceptor(REWRITE_CACHE_CONTROL_NETWORK_INTERCEPTOR)
                .addInterceptor(REWRITE_CACHE_CONTROL_NETWORK_INTERCEPTOR)
                .build();
        return okHttpClient;
    }

    public static boolean isNetworkAvailable(final Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if ((activeNetworkInfo != null) && (activeNetworkInfo.isConnected())) {
            return true;
        } else {
            return false;
        }
    }

    private static final Interceptor REWRITE_CACHE_CONTROL_NETWORK_INTERCEPTOR = new Interceptor() {
        @Override
        public okhttp3.Response intercept(Chain chain) throws IOException {
            Request originalRequest = chain.request();
            String cacheHeaderValue = isNetworkAvailable(instance)
                    ? "public, max-age=100"
                    : "public, only-if-cached" ;
            Request request = originalRequest.newBuilder().build();
            Response response = chain.proceed(request);
            return response.newBuilder()
                    .removeHeader("Pragma")
                    .removeHeader("Cache-Control")
                    .header("Cache-Control", cacheHeaderValue)
                    .build();
        }
    };
}
