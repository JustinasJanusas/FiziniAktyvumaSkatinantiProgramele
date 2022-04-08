package com.example.ejunasapp;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Path;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.text.Layout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Button;

import com.google.android.material.snackbar.Snackbar;

import org.w3c.dom.Text;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.zip.Inflater;

import javax.xml.transform.Source;

public class TaskDetailedActivity extends Activity {

    private String TAG = "TaskDetailedActivity";
    Button submitButton;
    Task task;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detailed_taskinfo_window);

        if (getIntent().getExtras() != null) {

            task = (Task) getIntent().getSerializableExtra("task");
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
            TaskText.setText(task.text);

            ImageView imageView = findViewById(R.id.taskImage);
            byte[] imageBytes = Base64.getDecoder().decode(task.base64_image);
            Bitmap image = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            imageView.setImageBitmap(image);

        }
        Button buttonReturn = (Button) findViewById(R.id.btn1);
        buttonReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent returnButton = new Intent(TaskDetailedActivity.this, MainActivity.class);
                returnButton.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(returnButton);
            }
        });
        submitButton = (Button) findViewById(R.id.done);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.done).setClickable(false);
                locationPermissionCheck();
            }
        });
        }
    private class PostLocationTask extends AsyncTask<String, Void, Boolean>{


        @Override
        protected void onPreExecute(){
            super.onPreExecute();
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

            if(result == null){
                Snackbar mySnackbar = Snackbar.make(findViewById(R.id.done),
                        "Įvyko klaida", 3000);
                mySnackbar.show();
            }
            else if(result){
                MainActivity.taskList = null;
                showPopupWindow(R.drawable.checkmark);
            }
            else{
                showPopupWindow(R.drawable.cross);
            }
            findViewById(R.id.done).setClickable(true);
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

            Location location = getLastKnownLocation();
            if(location != null)
                new PostLocationTask().execute(Tools.RestURL+"api-auth/task/"+task.id, location.getLatitude()+"", location.getLongitude()+"" );
            else {
                findViewById(R.id.done).setClickable(true);
                Snackbar mySnackbar = Snackbar.make(this.findViewById(R.id.done),
                        "Įvyko klaida nustatant jūsų vietovę", 5000);
                mySnackbar.show();
            }
        }
        else {
            requestPermissions(new String[] { Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION },
                                PackageManager.PERMISSION_GRANTED);
        }

    }
    @SuppressLint("MissingPermission")
    private Location getLastKnownLocation() {
        LocationManager mLocationManager = (LocationManager)getApplicationContext().getSystemService(LOCATION_SERVICE);
        List<String> providers = mLocationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
             Location l = mLocationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                bestLocation = l;
            }
        }
        return bestLocation;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED ) {
            locationPermissionCheck();
        }  else {
            Snackbar mySnackbar = Snackbar.make(this.findViewById(R.id.done),
                    "Kad galėtumėte pateikti savo atsakymą, programai reikia prieigos" +
                            " prie jūsų tikslios vietovės", 5000);
            mySnackbar.show();
            findViewById(R.id.done).setClickable(true);
        }

    }
}




