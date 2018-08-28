package com.rodolforpr.challangemockup;

import android.Manifest;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.johnhiott.darkskyandroidlib.ForecastApi;
import com.johnhiott.darkskyandroidlib.RequestBuilder;
import com.johnhiott.darkskyandroidlib.models.Request;
import com.johnhiott.darkskyandroidlib.models.WeatherResponse;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;


public class CameraActivity extends AppCompatActivity  {

    private Button btnCamera;

    public TextView txtTemperature;


    // CAMERA
    public final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034;
    public String photoFileName = "photo.jpg";
    public final String APP_TAG = "MyCustomApp";
    ViewPager viewPager;
    CustomSwipeAdapter adapter;
    ImageView iv;
    ArrayList<Bitmap> bitmapArrayList = new ArrayList<>();
    private Uri takenPhotoUri;
    private Bitmap takenImage;


    private static final String TAG = "CameraActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        ForecastApi.create("ae42d689184d5b9d7b091bbc88cb3e33");

        btnCamera = (Button) findViewById(R.id.btn_takePhotos);
        txtTemperature = (TextView) findViewById(R.id.txtView_temperature);

        viewPager = (ViewPager) findViewById(R.id.viewpager);

        recuperarTemp();


        // To Confirm and Adk for permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 0);
        }
        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, getPhotoFileUri(photoFileName));

                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
                }

            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();


    }

    @Override
    protected void onResume() {
        super.onResume();

    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {


                takenPhotoUri = getPhotoFileUri(photoFileName);
                takenImage = BitmapFactory.decodeFile(takenPhotoUri.getPath());
                bitmapArrayList.add(takenImage);

                adapter = new CustomSwipeAdapter(this, iv, bitmapArrayList);
                viewPager.setAdapter(adapter);
                Log.d("onActivityResult", bitmapArrayList.toString());




            } else {
                Toast.makeText(this, "Picture Wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        }
    }
    public Uri getPhotoFileUri(String fileName) {
            if (isExternalStorageAvailable()) {
                File mediaStoreDir = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), APP_TAG);

                if (!mediaStoreDir.exists() && !mediaStoreDir.mkdirs()) {
                    Log.d(APP_TAG, "Failed to create Directory");
                }
                return Uri.fromFile(new File(mediaStoreDir.getPath() + File.separator + fileName));

            }
            return null;
        }

        private boolean isExternalStorageAvailable() {
            String state = Environment.getExternalStorageState();
            return state.equals(Environment.MEDIA_MOUNTED);
        }

        public void recuperarTemp(){
            // SharedPreferences recuperando Latitude e longitude
            SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
            float latitutePref = pref.getFloat("latitudeExtra",0);
            float longPref = pref.getFloat("longitudeExtra", 0);
            Log.d("SharedPreferences", "shared recovered with sucess." + latitutePref + "," + longPref);
            String cityPref = pref.getString("cityExtra", "");
            //city = cityPref;
            // temperatura
            final RequestBuilder weather = new RequestBuilder();
            Request request = new Request();
            request.setLat(""+latitutePref);
            request.setLng(""+longPref);
            request.setUnits(Request.Units.AUTO);
            request.setLanguage(Request.Language.PORTUGUESE);
            request.addExcludeBlock(Request.Block.CURRENTLY);
            request.removeExcludeBlock(Request.Block.CURRENTLY);

            weather.getWeather(request, new Callback<WeatherResponse>() {
                @Override
                public void success(WeatherResponse weatherResponse, retrofit.client.Response response) {
                    String temp = "Temperature: "+weatherResponse.getCurrently().getTemperature()+"Cº";
                    txtTemperature.setText(temp);
                }
                @Override
                public void failure(RetrofitError retrofitError) {
                    Log.d(TAG, "Error while calling: " + retrofitError.getUrl());
                    Log.d(TAG, retrofitError.toString());
                }
            });
        }

    /*
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK) {
            Bitmap imagem = (Bitmap) data.getExtras().get("data");
            listImages.setImageBitmap(imagem);

            // SharedPreferences recuperando Latitude e longitude
            SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
            float latitutePref = pref.getFloat("latitudeExtra",0);
            float longPref = pref.getFloat("longitudeExtra", 0);
            Log.d("SharedPreferences", "shared recovered with sucess." + latitutePref + "," + longPref);
            String cityPref = pref.getString("cityExtra", "");
            city = cityPref;
            // temperatura
            final RequestBuilder weather = new RequestBuilder();
            Request request = new Request();
            request.setLat(""+latitutePref);
            request.setLng(""+longPref);
            request.setUnits(Request.Units.AUTO);
            request.setLanguage(Request.Language.PORTUGUESE);
            request.addExcludeBlock(Request.Block.CURRENTLY);
            request.removeExcludeBlock(Request.Block.CURRENTLY);

            weather.getWeather(request, new Callback<WeatherResponse>() {
                @Override
                public void success(WeatherResponse weatherResponse, retrofit.client.Response response) {
                    String temp = "Temperature: "+weatherResponse.getCurrently().getTemperature()+"Cº";
                    txtTemperature.setText(temp);
                }
                @Override
                public void failure(RetrofitError retrofitError) {
                    Log.d(TAG, "Error while calling: " + retrofitError.getUrl());
                    Log.d(TAG, retrofitError.toString());
                }
            });
        }
        super.onActivityResult(requestCode, resultCode, data);
    }*/
}






