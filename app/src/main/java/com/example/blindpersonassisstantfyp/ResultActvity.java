package com.example.blindpersonassisstantfyp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ResultActvity extends AppCompatActivity {

    ImageView resultImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_actvity);
        resultImage = findViewById(R.id.result_image);

        Intent intent = getIntent();

        String imageUri = intent.getStringExtra("image_uri");

        Glide.with(this).load(imageUri).into(resultImage);

        Retrofit retrofit = new Retrofit.Builder().baseUrl("http://192.168.1.226:5000/").addConverterFactory(GsonConverterFactory.create()).build();

        CaptionService service = retrofit.create(CaptionService.class);

        String imagePath = intent.getStringExtra("image_path");

        File file = null;
        try {
            file = FileUtils.getFileFromUri(ResultActvity.this, Uri.parse(imageUri));
        } catch (Exception e) {
            e.printStackTrace();
        }

        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"),file);

        MultipartBody.Part body = MultipartBody.Part.createFormData("files[]",file.getName(),requestFile);

        Call<ServerResponse> call =  service.getCaption(body);

        call.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                Toast.makeText(ResultActvity.this, response.body().message, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                Toast.makeText(ResultActvity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                Log.d("server_error",t.getMessage());
            }
        });


    }
}