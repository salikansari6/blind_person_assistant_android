package com.example.blindpersonassisstantfyp;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface CaptionService {
    @GET("/")
    Call<ServerResponse> getResponse();

    @Multipart
    @POST("/static/uploads/")
    Call<ServerResponse> getCaption(@Part MultipartBody.Part image);
}
