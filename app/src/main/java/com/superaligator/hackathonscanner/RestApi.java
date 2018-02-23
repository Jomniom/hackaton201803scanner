package com.superaligator.hackathonscanner;


import retrofit2.Call;
import retrofit2.http.POST;

public interface RestApi {
    @POST("checkCode.php")
    Call<CheckResponse> check();

}
