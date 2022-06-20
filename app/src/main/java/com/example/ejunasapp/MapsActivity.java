package com.example.ejunasapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    GoogleMap mMap;
    int id;
    HintCoordinates hintlocation = null;
    Button hintButton;
    private String TAG = "MapsActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.maps_layout);
        if(getIntent().getExtras() == null) {
            finish();
            return;
        }
        id = getIntent().getIntExtra("id", -1);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        ImageButton backButton = findViewById(R.id.mapBackButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        hintButton = findViewById(R.id.mapHintButton);
        hintButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showHint();
            }
        });
    }
    public void showHint(){
        if(hintlocation != null){
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(hintlocation.latitude, hintlocation.longitude), 14.0f));
            return;
        }
        if(Tools.user.hints == 0){
            Snackbar s = Snackbar.make(hintButton, "Neturite už ką pirkti užuominų", 5000);
            s.show();
            return;
        }
        new AlertDialog.Builder(MapsActivity.this)
            .setTitle("")
            .setMessage("Ar norite pirkti užuominą? Šiuo metu turite "+Tools.user.hints+" galimų užuominų")

            .setIcon(android.R.drawable.ic_dialog_info)
            .setNegativeButton(R.string.cancel, null)
            .setPositiveButton(R.string.change, new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int whichButton) {
                    new requestHint().execute(Tools.RestURL+"/api-auth/hint/"+id);
                }}).show();

    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        locationPermissionCheck();

        new getHint().execute(Tools.RestURL+"/api-auth/hint/"+id);

    }
    private void locationPermissionCheck(){
        if (ContextCompat.checkSelfPermission(
                getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                LatLng currLoc = new LatLng(location.getLatitude(), location.getLongitude());
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currLoc, 14.0f));
                            }
                        }
                    });
        }
        else {
            requestPermissions(new String[] { Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION },
                    PackageManager.PERMISSION_GRANTED);
        }


    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            locationPermissionCheck();
        } else {
            Snackbar mySnackbar = Snackbar.make(this.findViewById(R.id.done),
                    "Kad galėtumėte pateikti savo atsakymą, programai reikia prieigos" +
                            " prie jūsų tikslios vietovės", 5000);
            mySnackbar.show();
            findViewById(R.id.done).setClickable(true);
        }

    }
    private class getHint extends AsyncTask<String, Void, HintCoordinates> {

        ProgressDialog actionProgressDialog =
                new ProgressDialog(MapsActivity.this);
        @Override
        protected void onPreExecute(){
            actionProgressDialog.setMessage("Gaunami duomenys...");
            actionProgressDialog.show();
            actionProgressDialog.setCancelable(false);
            super.onPreExecute();
        }

        protected HintCoordinates doInBackground(String... str_param){

            String url = str_param[0];

            List<HintCoordinates> data = null;
            try{
                java.lang.reflect.Type type =
                        new com.google.gson.reflect.TypeToken<List<HintCoordinates>>()
                        {}.getType();
                data = (List<HintCoordinates>) DataAPI.jsonObjectToData(url, type);

            }
            catch (Exception ex){
                Log.e(TAG, ex.toString());
                return null;
            }

            return data.get(0);
        }
        protected void onProgressUpdate(Void... progress){}
        protected void onPostExecute(HintCoordinates result){

            super.onPostExecute(result);
            actionProgressDialog.cancel();
            if(result != null){
                mMap.addCircle(new CircleOptions()
                        .center(new LatLng(result.latitude, result.longitude))
                        .radius(result.radius)
                        .fillColor(0x220000FF)
                        .strokeWidth(0));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(result.latitude, result.longitude), 14.0f));
                hintButton.setText("Rodyti užuominą");
                hintlocation = result;
            }

        }
    }
    private class requestHint extends AsyncTask<String, Void, Boolean> {

        ProgressDialog actionProgressDialog =
                new ProgressDialog(MapsActivity.this);
        @Override
        protected void onPreExecute(){
            actionProgressDialog.setMessage("Gaunami duomenys...");
            actionProgressDialog.show();
            actionProgressDialog.setCancelable(false);
            super.onPreExecute();
        }

        protected Boolean doInBackground(String... str_param){

            String url = str_param[0];

            Boolean data = null;
            try{
                data = WebAPI.changeState(url, "POST");

            }
            catch (Exception ex){
                Log.e(TAG, ex.toString());
            }

            return data;
        }
        protected void onProgressUpdate(Void... progress){}
        protected void onPostExecute(Boolean result){

            super.onPostExecute(result);
            actionProgressDialog.cancel();
            if(result) {
                Tools.user.hints--;
                new getHint().execute(Tools.RestURL+"/api-auth/hint/"+id);
            }
            else{
                Snackbar mySnackbar = Snackbar.make(findViewById(R.id.map),
                        "Įvyko klaida", 3000);
                mySnackbar.show();
            }

        }
    }
}
