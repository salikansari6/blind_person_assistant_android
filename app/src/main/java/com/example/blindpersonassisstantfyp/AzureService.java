package com.example.blindpersonassisstantfyp;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface AzureService {
    @GET("/")
    Call<ServerResponse> getResponse();

    @Multipart
    @POST("/vision/v3.2/analyze?visualFeatures=description")
    Call<AzureServerResponse> getCaption(@Part MultipartBody.Part image, @Header("Ocp-Apim-Subscription-Key") String apiKey);
}
