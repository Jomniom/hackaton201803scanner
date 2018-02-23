package com.superaligator.hackathonscanner;
import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Comunicator {
    private static Comunicator ourInstance = new Comunicator();
    private Retrofit retrofit;
    private RestApi service;

    public static Comunicator getInstance() {
        return ourInstance;
    }

    private Comunicator() {
        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();

        /**
         * Headers interceptor
         */
        clientBuilder.addInterceptor(new HeaderInterceptor());

        /**
         * Logging intercptor
         */
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        clientBuilder.addInterceptor(loggingInterceptor);

        retrofit = new Retrofit.Builder()
                .baseUrl(Config.BASE_URL)
                .client(clientBuilder.build())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public RestApi getApiService() {
        service = retrofit.create(RestApi.class);
        return service;
    }

    class HeaderInterceptor implements Interceptor {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request originalRequest = chain.request();
            Request.Builder newBuilder = originalRequest.newBuilder();
            newBuilder.header("User-Agent", "HackathonScanner");
            newBuilder.build();
            Request newRequest = newBuilder.build();
            return chain.proceed(newRequest);
        }


    }
}
