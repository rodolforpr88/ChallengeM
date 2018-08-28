package com.rodolforpr.challangemockup;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.johnhiott.darkskyandroidlib.ForecastApi;
import com.johnhiott.darkskyandroidlib.RequestBuilder;
import com.johnhiott.darkskyandroidlib.models.DataPoint;
import com.johnhiott.darkskyandroidlib.models.Request;
import com.johnhiott.darkskyandroidlib.models.WeatherResponse;

import org.w3c.dom.Text;

import java.util.Calendar;

import retrofit.Callback;
import retrofit.RetrofitError;



public class CalendarActivity extends AppCompatActivity  {


    DatePicker simpleDatePicker ;
    Button btnSubmit;

    public int day, month, year;

    ImageView imageIcon;
    TextView txtTempMin,txtTempMax, txtSummary, txtPrecipProbability;
    public float latitutePref;
    public float longPref;


    private static final String TAG = "CalendarActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        simpleDatePicker = (DatePicker)findViewById(R.id.simpleDatePicker); // initiate a date picker
        btnSubmit = (Button) findViewById(R.id.btn_submit);
        txtTempMin = (TextView) findViewById(R.id.txt_tempMin);
        txtTempMax = (TextView) findViewById(R.id.txt_tempMax);
        txtSummary = (TextView) findViewById(R.id.txt_Summary);
        txtPrecipProbability = (TextView) findViewById(R.id.txt_PrecipProbability);


        ForecastApi.create("ae42d689184d5b9d7b091bbc88cb3e33");

        // DATE PICKER CALENDAR
        simpleDatePicker.setSpinnersShown(false); // set false value for the spinner shown function




        // SharedPreferences recuperando Latitude e longitude
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        latitutePref = pref.getFloat("latitudeExtra", 0);
        longPref = pref.getFloat("longitudeExtra", 0);
        //String cityPref = pref.getString("cityExtra", "null");
        //city = cityPref;
        Log.d("SharedPreferences", "latitudeExtra" + latitutePref +","+longPref);



        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                day = simpleDatePicker.getDayOfMonth(); // get the selected day of the month
                month = simpleDatePicker.getMonth() + 1; // get the selected month
                year = simpleDatePicker.getYear(); // get the selected year
                //Toast.makeText(getApplicationContext(), day + "\n" + month + "\n" + year, Toast.LENGTH_LONG).show();


                //transform date
                Calendar calendar = Calendar.getInstance();
                calendar.set(simpleDatePicker.getYear(), simpleDatePicker.getMonth(), simpleDatePicker.getDayOfMonth());
                long startTime = calendar.getTimeInMillis() / 1000;
                Log.d("StartTime", String.valueOf(startTime));



                final RequestBuilder weather = new RequestBuilder();
                Request request = new Request();
                String latitude, longitude;


                latitude = String.valueOf(latitutePref);
                longitude = String.valueOf(longPref);
                request.setTime(String.valueOf(startTime));
                request.setLat(latitude);
                request.setLng(longitude);
                request.setTime(String.valueOf(startTime));
                request.addExcludeBlock(Request.Block.CURRENTLY);
                request.addExcludeBlock(Request.Block.FLAGS);
                request.setUnits(Request.Units.AUTO);
                request.setLanguage(Request.Language.PORTUGUESE);




                weather.getWeather(request,  new Callback<WeatherResponse>() {
                    @Override
                    public void success(WeatherResponse weatherResponse, retrofit.client.Response response) {
                        /*Log.d(TAG, "Current Temperature: " + weatherResponse.getCurrently().getTemperature());
                        Log.d(TAG, "Current getTime: " + weatherResponse.getCurrently().getTime());*/

                        //Log.d(TAG, "getTemperatureMin: " + weatherResponse.getDaily().getData().get(2));
                        //Log.d(TAG, "getTemperatureMaxTime: " + weatherResponse.getCurrently().getTemperatureMaxTime());
                        //Log.d(TAG, "getTemperatureMinTime: " + weatherResponse.getCurrently().getTemperatureMinTime());
                        Log.i("getIcon", weatherResponse.getDaily().getData().get(0).getIcon());
                        Log.i("getIcon", weatherResponse.getDaily().getData().get(0).getSummary());
                        txtTempMin.setText("Min: "+String.valueOf(weatherResponse.getDaily().getData().get(0).getTemperatureMin()+" Cº"));
                        txtTempMax.setText("Max: "+String.valueOf(weatherResponse.getDaily().getData().get(0).getTemperatureMax()+" Cº"));
                        txtSummary.setText(String.valueOf(weatherResponse.getDaily().getData().get(0).getSummary()));
                        double percentRain = Double.parseDouble(weatherResponse.getDaily().getData().get(0).getPrecipProbability())*100;
                        txtPrecipProbability.setText("Probability rain: "+ percentRain+"%");

                        //txtTemperature.setText("Temperature: "+String.valueOf(weatherResponse.getCurrently().getTemperature())+"Cº");

                        if (weatherResponse.getDaily().getData().get(0).getIcon() == "clear-day" || weatherResponse.getDaily().getData().get(0).getIcon() == "clear-night") {
                            Toast.makeText(getApplicationContext(), "Color yellow", Toast.LENGTH_LONG).show();
                        }
                       }



                    @Override
                    public void failure(RetrofitError retrofitError) {
                        Log.d(TAG, "Error while calling: " + retrofitError.getUrl());
                        Log.d(TAG, retrofitError.toString());
                    }
                });
            }
        });
    }
}
