package com.example.blindpersonassisstantfyp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
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

        Retrofit retrofit = new Retrofit.Builder().baseUrl("https://salik-ansari-image-caption.cognitiveservices.azure.com").addConverterFactory(GsonConverterFactory.create()).build();

        AzureService service = retrofit.create(AzureService.class);

        String imagePath = intent.getStringExtra("image_path");

        Log.d("image",imagePath);




        File file = null;
        try {
            file = FileUtils.getFileFromUri(ResultActvity.this, Uri.parse(imageUri));
        } catch (Exception e) {
            e.printStackTrace();
        }

        Bitmap fullSizeBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());

        Bitmap reducedBitmap = ImageResizer.reduceBitmapSize(fullSizeBitmap,240000);

        File reducedFile =  getBitmapFile(reducedBitmap);

        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"),reducedFile);

        MultipartBody.Part body = MultipartBody.Part.createFormData("files[]",reducedFile.getName(),requestFile);

        Call<AzureServerResponse> call =  service.getCaption(body,"73b6c92da7434627852bc6d600097fa5");

        call.enqueue(new Callback<AzureServerResponse>() {
            @Override
            public void onResponse(Call<AzureServerResponse> call, Response<AzureServerResponse> response) {
                String caption = response.body().getDescription().getCaptions().get(0).getText();
               resultCaption.setText(caption);
               speak(caption);
            }

            @Override
            public void onFailure(Call<AzureServerResponse> call, Throwable t) {
                Toast.makeText(ResultActvity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                Log.d("server_error",t.getMessage());
            }
        });


    }

    private File getBitmapFile(Bitmap reducedBitmap) {
        File file = new File( getFilesDir().getAbsolutePath(),"reduced_image");
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        reducedBitmap.compress(Bitmap.CompressFormat.JPEG,0,bos);
        byte[] bitmapdata = bos.toByteArray();

        try {
            file.createNewFile();
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(bitmapdata);
            fos.flush();
            fos.close();
            return file;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return file;
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