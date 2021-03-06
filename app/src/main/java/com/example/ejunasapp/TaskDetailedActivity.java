package com.example.ejunasapp;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Button;

import com.google.android.material.snackbar.Snackbar;

import java.util.Base64;
import java.util.function.Consumer;

public class TaskDetailedActivity extends Activity {

    private String TAG = "TaskDetailedActivity";
    Button submitButton;
    ImageButton mapButton;
    Task task;
    int type;
    CheckBox star;
    final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            new PostLocationTask().execute(Tools.RestURL+"api-auth/task/other/"+task.id, location.getLatitude()+"", location.getLongitude()+"" );
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.d("Status Changed", String.valueOf(status));
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.d("Provider Enabled", provider);
        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.d("Provider Disabled", provider);
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detailed_taskinfo_window);

        if (getIntent().getExtras() != null) {
            task = (Task) getIntent().getSerializableExtra("task");
            type = getIntent().getIntExtra("type", MainActivity.OTHER);
            TextView TextName = findViewById(R.id.nameText);
            TextName.setText(task.name);

            TextView Level = findViewById(R.id.levelText);
            Level.setText(task.level.name);

            TextView Type = findViewById(R.id.typeText);
            Type.setText(task.type.name);

            TextView Category = findViewById(R.id.categoryText);
            Category.setText(task.category.name);

            TextView Author = findViewById(R.id.authorText);
            Author.setText(task.author);
            TextView TaskText = findViewById(R.id.tasktext);
            if(task.base64_picture != null && !task.base64_picture.equals("")){
                TaskText.setVisibility(View.GONE);
                ImageView image = findViewById(R.id.detailedTaskImage);
                byte[] imageBytes = Base64.getDecoder().decode(task.base64_picture);
                image.setImageBitmap( BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length));
                image.setVisibility(View.VISIBLE);
            }
            else {

                TaskText.setText(task.text);
            }
            ImageView imageView = findViewById(R.id.taskImage);
            int resID = getResources().getIdentifier("category_"+task.category.id, "drawable", getPackageName());
            if(resID == 0)
                resID = R.drawable.category_other;
            imageView.setImageResource(resID);

        }
        Button buttonReturn = (Button) findViewById(R.id.btn1);
        buttonReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        submitButton = (Button) findViewById(R.id.done);
        mapButton = findViewById(R.id.mapButton);
        star = findViewById(R.id.checkboxStar);
        if(type == MainActivity.DONE) {
            setDone();
            star.setClickable(false);
            star.setVisibility(View.INVISIBLE);
            mapButton.setClickable(false);
            mapButton.setVisibility(View.INVISIBLE);
        }
        else {
            if(type == MainActivity.FAVORITE)
                star.setChecked(true);
            star.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    buttonView.setClickable(false);
                    changeTaskState();
                }
            });
            mapButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(TaskDetailedActivity.this, MapsActivity.class);
                    intent.putExtra("id", task.id);
                    startActivity(intent);
                }
            });
            submitButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    findViewById(R.id.done).setClickable(false);
                    locationPermissionCheck();
                }
            });
        }
    }
    private class PostLocationTask extends AsyncTask<String, Void, Boolean>{

        ProgressDialog actionProgressDialog =
                new ProgressDialog(TaskDetailedActivity.this);
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            actionProgressDialog.setMessage("Siun??iama vietov??...");
            actionProgressDialog.show();
            actionProgressDialog.setCancelable(false);
        }
        protected Boolean doInBackground(String... str_param){
            String RestURL = str_param[0];
            float latitude = Float.parseFloat(str_param[1]);
            float longtitude = Float.parseFloat(str_param[2]);
            Boolean answer = false;
            try {
                answer = WebAPI.sendLocation(RestURL, latitude, longtitude);
            }
            catch (Exception e){
                Log.e(TAG, e.toString());
                return null;
            }
            return answer;
        }
        protected void onProgressUpdate(Void... progress){}
        protected void onPostExecute(Boolean result){
            actionProgressDialog.cancel();
            if(result == null){
                Snackbar mySnackbar = Snackbar.make(findViewById(R.id.done),
                        "??vyko klaida", 3000);
                mySnackbar.show();
            }
            else if(result){
                MainActivity.doneTaskList = null;
                MainActivity.favTaskList = null;
                MainActivity.otherTaskList = null;
                Tools.user = null;
                MainActivity.changed = true;
                showPopupWindow(R.drawable.checkmark);
                setDone();
                type = MainActivity.DONE;
            }
            else{
                showPopupWindow(R.drawable.cross);
                submitButton.setClickable(true);
            }

        }
    }
    private class ChangeState extends AsyncTask<String, Void, Boolean>{


        @Override
        protected void onPreExecute(){
            super.onPreExecute();
        }
        protected Boolean doInBackground(String... str_param){
            String RestURL = str_param[0];
            String method = str_param[1];
            Boolean answer = false;
            try {
                answer = WebAPI.changeState(RestURL, method);
            }
            catch (Exception e){
                Log.e(TAG, e.toString());
                return false;
            }
            return answer;
        }
        protected void onProgressUpdate(Void... progress){}
        protected void onPostExecute(Boolean result){
            if(result){
                MainActivity.doneTaskList = null;
                MainActivity.favTaskList = null;
                MainActivity.otherTaskList = null;
                if(type == MainActivity.FAVORITE)
                    type = MainActivity.OTHER;
                else
                    type = MainActivity.FAVORITE;
                star.setClickable(true);
            }
            else{
                Snackbar mySnackbar = Snackbar.make(findViewById(R.id.done),
                        "??vyko klaida", 3000);
                mySnackbar.show();
            }

        }
    }
    private void showPopupWindow(int id){
        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_window_answer, null);
        ImageView imageView = popupView.findViewById(R.id.answerImage);
        imageView.setImageBitmap(BitmapFactory.decodeResource(this.getResources(),
                id));
        int size = LinearLayout.LayoutParams.WRAP_CONTENT;
        PopupWindow popupWindow = new PopupWindow(popupView, size, size, true);
        popupWindow.showAtLocation(findViewById(R.id.done), Gravity.CENTER, 0, 0);
    }
    private void locationPermissionCheck(){
        if (ContextCompat.checkSelfPermission(
                getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            getLocation();
        }
        else {
            requestPermissions(new String[] { Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION },
                                PackageManager.PERMISSION_GRANTED);
        }


    }
    @SuppressLint("MissingPermission")
    private void getLocation() {
        LocationManager mLocationManager = (LocationManager)getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {

            mLocationManager.getCurrentLocation(
                    mLocationManager.getBestProvider(criteria, true),
                    null,
                    this.getMainExecutor(),
                    new Consumer<Location>() {

                        @Override
                        public void accept(Location location) {
                            // code
                            new PostLocationTask().execute(Tools.RestURL+"api-auth/task/other/"+task.id, location.getLatitude()+"", location.getLongitude()+"" );
                        }
                    });

        }
        else if(Build.VERSION.SDK_INT < Build.VERSION_CODES.R){
            mLocationManager.requestSingleUpdate(mLocationManager.getBestProvider(criteria, true), locationListener, null);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED ) {
            locationPermissionCheck();
        }  else {
            Snackbar mySnackbar = Snackbar.make(this.findViewById(R.id.done),
                    "Kad gal??tum??te pateikti savo atsakym??, programai reikia prieigos" +
                            " prie j??s?? tikslios vietov??s", 5000);
            mySnackbar.show();
            findViewById(R.id.done).setClickable(true);
        }

    }
    private void setDone(){
        submitButton.setOnClickListener(null);
        submitButton.setStateListAnimator(null);
        mapButton.setClickable(false);
        mapButton.setVisibility(View.INVISIBLE);
        star.setVisibility(View.INVISIBLE);
        submitButton.setBackgroundColor(Color.WHITE);

        submitButton.setTextColor(getColor(R.color.green));
        submitButton.setText(R.string.task_done);
    }
    private void changeTaskState(){
        if(star.isChecked())
            new ChangeState().execute(Tools.RestURL+"api-auth/task/inprogress/"+task.id, "POST");
        else
            new ChangeState().execute(Tools.RestURL+"api-auth/task/inprogress/"+task.id, "DELETE");
        MainActivity.otherTaskList = null;
        MainActivity.favTaskList = null;
    }
}




