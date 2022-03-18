package com.example.blindpersonassisstantfyp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.w3c.dom.Text;

import java.io.File;
import java.util.Locale;

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
    TextView resultCaption;
    private TextToSpeech mTTS;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_actvity);
        resultImage = findViewById(R.id.result_image);
        resultCaption = findViewById(R.id.result_caption);

        mTTS = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status == TextToSpeech.SUCCESS){
                    int result = mTTS.setLanguage(Locale.US);

                    if(result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED){
                        Log.e("TTS","Language not supported");
                    }
                }
                else{
                    Log.e("TTS","Initialization failed");
                }
            }
        });


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
                String caption = response.body().message;
               resultCaption.setText(caption);
               speak(caption);
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                Toast.makeText(ResultActvity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                Log.d("server_error",t.getMessage());
            }
        });


    }

    private void speak(String caption) {
        mTTS.setSpeechRate(0.75f);
        mTTS.speak(caption,TextToSpeech.QUEUE_FLUSH,null);
    }

    @Override
    protected void onDestroy() {
        if(mTTS !=null){
            mTTS.stop();
            mTTS.shutdown();
        }

        super.onDestroy();
    }
}