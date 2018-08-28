package com.rodolforpr.challangemockup;


import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.johnhiott.darkskyandroidlib.ForecastApi;

import org.w3c.dom.Text;

import java.util.Arrays;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 1;
    private static final String TAG = "MainActivity";
    private static final int ERROR_DIALOG_REQUEST = 9001;

    public Double latitude, longitude;
    Location location;

    ImageButton imgButtomCalendar;
    ImageButton imgButtomGoogleMaps;
    ImageButton imgButtomCamera;
    TextView txtUser;

    Button btnLogout;


    // Firebase instance variables
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthFirebaseAuthStateListenner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imgButtomCalendar = (ImageButton) findViewById(R.id.imgButton_Calendar);
        imgButtomGoogleMaps = (ImageButton) findViewById(R.id.imgButton_GoogleMaps);
        imgButtomCamera = (ImageButton) findViewById(R.id.imgButton_Camera);
        btnLogout = (Button) findViewById(R.id.btn_logout);
        txtUser = (TextView) findViewById(R.id.txtView_user);

        btnLogout.setVisibility(View.INVISIBLE);
        ForecastApi.create("f6a2725e020c9502cf718e044c13374a");



        // save myLocation
        /*latitude = location.getLatitude();
        longitude = location.getLongitude();
        Log.d(TAG, "save myLocation: Latitude: "+ latitude +"\n" + "Longitude: "+ longitude);
        Log.d(TAG, "Current location:\n" + location +"\n");*/






        // Initialize Firebase components
        mFirebaseAuth = FirebaseAuth.getInstance();

        if(isServicesOK()){
            init();
        }



        mAuthFirebaseAuthStateListenner = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                final FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null){
                    // signed in
                    btnLogout.setVisibility(View.VISIBLE);
                    //Toast.makeText(MainActivity.this, "You are Signed IN, " + user.getDisplayName(), Toast.LENGTH_LONG).show();
                    txtUser.setText("WELCOME " + user.getDisplayName().toUpperCase());

                } else {
                    // signet out
                    /*
                    * Escolhi para usar somente os modos
                    * e-mail e senha : se email não existe = nova UI para cadastro
                    * conta G-mail
                    * */
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setIsSmartLockEnabled(false)
                                    .setAvailableProviders(Arrays.asList(
                                            new AuthUI.IdpConfig.GoogleBuilder().build(),
                                            new AuthUI.IdpConfig.EmailBuilder().build()))
                                    .build(),
                            RC_SIGN_IN);

                }

                btnLogout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mFirebaseAuth.signOut();
                        Toast.makeText(MainActivity.this, "You are Signed OUT, " + user.getDisplayName(), Toast.LENGTH_LONG).show();

                    }
                });
            }
        };

        // on click do botão imgButtomCalendar
        imgButtomCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, CalendarActivity.class));
            }
        });

        // onClick do botão Google Maps
        imgButtomGoogleMaps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, MapsGoogleActivity.class));
            }
        });

        // onClick do botão Camera
        imgButtomCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, CameraActivity.class));
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        // Toast Only to test
        //Toast.makeText(MainActivity.this, "onResume called... (addAuthStateListener)", Toast.LENGTH_LONG).show();
        mFirebaseAuth.addAuthStateListener(mAuthFirebaseAuthStateListenner);
    }



    @Override
    protected void onPause() {
        super.onPause();
        // Toast only to test
        //Toast.makeText(MainActivity.this, "onPause called... (removeAuthStateListener)", Toast.LENGTH_LONG).show();
        mFirebaseAuth.removeAuthStateListener(mAuthFirebaseAuthStateListenner);
    }

    private void init(){

        imgButtomGoogleMaps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, MapsGoogleActivity.class);
                startActivity(intent);
            }
        });
    }

    public boolean isServicesOK(){
        Log.d(TAG, "isServicesOK: checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MainActivity.this);

        if(available == ConnectionResult.SUCCESS){
            //everything is fine and the user can make map requests
            Log.d(TAG, "isServicesOK: Google Play Services is working");
            return true;
        }
        else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            //an error occured but we can resolve it
            Log.d(TAG, "isServicesOK: an error occured but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(MainActivity.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        }else{
            Toast.makeText(this, "You can't make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    public void savemyLocation (){

    }

    /*public void saveShared() {
        SharedPreferences.Editor editor = getSharedPreferences("pref", MODE_PRIVATE).edit();
        SharedPreferences.Editor latExtra = editor.putString("latitudeExtra", Double.toString(latitude));
        SharedPreferences.Editor longExtra = editor.putString("longitudeExtra", Double.toString(longitude));
        editor.commit();
    }*/




}
