package com.example.ejunasapp;
import static com.example.ejunasapp.Tools.RestURL;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.Snackbar;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.regex.Pattern;


public class AddNewTaskActivity extends AppCompatActivity  {

    private static final String TAG = "AddNewTaskActivity";

    private static List<TaskItem> categories;
    private static List<TaskItem> levels;
    private static List<TaskItem> types;
    Location currentLocation;
    final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            TextView textView = findViewById(R.id.coordinateText);
            textView.setText(String.format("%.5f, %.5f", location.getLatitude(), location.getLongitude()));
            currentLocation = location;
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_new_task_activity);
        getSpinnerItems(categories, MainActivity.categories, findViewById(R.id.createTaskCategorySpinner),
                        RestURL+"api-auth/categories", 0);
        getSpinnerItems(levels, MainActivity.levels, findViewById(R.id.createTaskLevelSpinner),
                Tools.RestURL+"api-auth/levels", 1);
        getSpinnerItems(types, null, findViewById(R.id.createTaskTypeSpinner),
                    RestURL+"api-auth/types", 2);



        locationPermissionCheck();
        Button backButton = findViewById(R.id.btn1);
        Button addButton = findViewById(R.id.done);
        Button refreshButton = findViewById(R.id.refreshCoordinatesButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onAddClick();
            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locationPermissionCheck();
            }
        });
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
        TextView textView = findViewById(R.id.coordinateText);
        textView.setText("");
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

                            textView.setText(String.format("%.5f, %.5f", location.getLatitude(), location.getLongitude()));
                            currentLocation = location;
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
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED ) {
            locationPermissionCheck();
        }  else {
            Snackbar mySnackbar = Snackbar.make(this.findViewById(R.id.done),
                    "Kad galėtumėte pateikti savo užduotį, programai reikia prieigos" +
                            " prie jūsų tikslios vietovės", 5000);
            mySnackbar.show();
        }

    }
    private void getSpinnerItems(List<TaskItem> spinnerArray, List<TaskItem> otherSpinnerArray, Spinner spinner, String url, int itemType){

        if(spinnerArray != null){
            fillSpinner(spinnerArray, spinner, 0);
            return;
        }
        if(otherSpinnerArray != null){
            spinnerArray = new ArrayList<>();
            spinnerArray.add(new TaskItem(-1, "-----"));
            for(int i= 1; i < otherSpinnerArray.size(); i++)
                spinnerArray.add(otherSpinnerArray.get(i));
            fillSpinner(spinnerArray, spinner, 0);
            return;
        }
        new getTaskItems().execute(url, itemType+"");
    }
    private class getTaskItems extends AsyncTask<String, Void, List<TaskItem>>{

        int itemType;

        ProgressDialog actionProgressDialog =
                new ProgressDialog(AddNewTaskActivity.this);
        @Override
        protected void onPreExecute(){
            actionProgressDialog.setMessage("Gaunami duomenys...");
            actionProgressDialog.show();
            actionProgressDialog.setCancelable(false);
            super.onPreExecute();
        }

        protected List<TaskItem> doInBackground(String... str_param){
            String url = str_param[0];
            itemType = Integer.parseInt(str_param[1]);
            List<TaskItem> data = null;
            try{

                    data = getTaskItems(url);
                    data.add(0, new TaskItem(-1, "-----"));
            }
            catch (Exception ex){
                Log.e(TAG, ex.toString());
            }
            return data;
        }
        protected void onProgressUpdate(Void... progress){}
        protected void onPostExecute(List<TaskItem> result){
            actionProgressDialog.cancel();

            if(result != null) {
                switch (itemType){
                    case 0:
                        categories = result;
                        Spinner categorySpinner = findViewById(R.id.createTaskCategorySpinner);
                        fillSpinner(categories, categorySpinner, 0);
                        break;
                    case 1:
                        levels = result;
                        Spinner levelSpinner = findViewById(R.id.createTaskLevelSpinner);
                        fillSpinner(levels, levelSpinner, 0);
                        break;
                    case 2:
                        types = result;
                        Spinner typeSpinner = findViewById(R.id.createTaskTypeSpinner);
                        fillSpinner(types, typeSpinner, 0);
                        break;
                    default:
                        return;
                }
            }
        }
    }
    private List<TaskItem> getTaskItems(String url) throws Exception{
        java.lang.reflect.Type type =
                new com.google.gson.reflect.TypeToken<List<TaskItem>>()
                {}.getType();
        return (List<TaskItem>) DataAPI.jsonToData(url, type);
    }
    private void onAddClick() {
        String taskName = ((EditText) findViewById(R.id.taskTitleText)).getText().toString().trim();
        String taskText = ((EditText) findViewById(R.id.taskText)).getText().toString().trim();
        if(!ValidName(taskName))
            return;
        if(!ValidText(taskText))
            return;
        Spinner categorySpinner = findViewById(R.id.createTaskCategorySpinner);
        if(categorySpinner.getSelectedItemPosition() == 0){
            showMessage("Pasirinkite kategoriją");
            return;
        }
        String category = ((TaskItem) categorySpinner.getSelectedItem()).name;

        Spinner levelSpinner = findViewById(R.id.createTaskLevelSpinner);
        if(levelSpinner.getSelectedItemPosition() == 0){
            showMessage("Pasirinkite sunkumo lygį");
            return;
        }
        String level = ((TaskItem) levelSpinner.getSelectedItem()).name;

        Spinner typeSpinner = findViewById(R.id.createTaskTypeSpinner);
        if(typeSpinner.getSelectedItemPosition() == 0){
            showMessage("Pasirinkite tipą");
            return;
        }
        String type = ((TaskItem) typeSpinner.getSelectedItem()).name;

        Spinner radiusSpinner = findViewById(R.id.radiusSpinner);
        if(radiusSpinner.getSelectedItemPosition() == 0){
            showMessage("Pasirinkite spindulį");
            return;
        }
        int radiusID = radiusSpinner.getSelectedItemPosition();
        if(currentLocation == null){
            showMessage("Programa negavo vieotvės koordinačių");
            return;
        }
        new PostTask().execute(RestURL+"api-auth/task/insert", taskName, category, type, level, radiusID+"",
                                taskText);



    }
    private class PostTask extends AsyncTask<String, Void, Boolean>{


        ProgressDialog actionProgressDialog =
                new ProgressDialog(AddNewTaskActivity.this);
        @Override
        protected void onPreExecute(){
            actionProgressDialog.setMessage("Gaunami duomenys...");
            actionProgressDialog.show();
            actionProgressDialog.setCancelable(false);
            super.onPreExecute();
        }

        protected Boolean doInBackground(String... str_param){
            String url = str_param[0];
            String taskName = str_param[1];
            String category = str_param[2];
            String type = str_param[3];
            String level = str_param[4];
            String radiusID = str_param[5];
            String taskText = str_param[6];
            try{
                return DataAPI.addTaskJson(url, taskName, category, type, level, Tools.user.user.username,
                        currentLocation.getLatitude()+"", currentLocation.getLongitude()+"",
                        radiusID, taskText);
            }
            catch (Exception ex){
                Log.e(TAG, ex.toString());
            }
            return false;
        }
        protected void onProgressUpdate(Void... progress){}
        protected void onPostExecute(Boolean result){
            actionProgressDialog.cancel();

            if(result){
                AddNewTaskActivity.this.finish();
            }
            else{
                showMessage("Įvyko klaida keliant užduotį");
            }
        }
    }
    private void fillSpinner(List<TaskItem> list, Spinner spinner, int pos){

        CustomSpinnerAdapter adapter = new CustomSpinnerAdapter(this,
                android.R.layout.simple_spinner_item,  list.toArray(new TaskItem[list.size()]));
        spinner.setAdapter(adapter);
        spinner.setSelection(pos);

    }
    private class CustomSpinnerAdapter extends ArrayAdapter<TaskItem> {
        private Context context;
        private TaskItem[] values;

        public CustomSpinnerAdapter(Context context, int textViewResourceId, TaskItem[] values) {
            super(context, textViewResourceId, values);
            this.context = context;
            this.values = values;
        }

        @Override
        public int getCount() {
            return values.length;
        }

        @Override
        public TaskItem getItem(int position) {
            return values[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView label = (TextView) super.getView(position, convertView, parent);
            label.setText(values[position].name);
            return label;
        }
        @Override
        public View getDropDownView(int position,  View convertView, ViewGroup parent) {
            TextView label = (TextView) super.getDropDownView(position, convertView, parent);
            label.setTextColor(Color.BLACK);
            label.setText(values[position].name);
            return label;
        }
    }







    private void showMessage(String msg) {

        Snackbar snack = Snackbar.make(findViewById(R.id.taskTitleLabel), msg, 3000);
        View view = snack.getView();
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) view.getLayoutParams();
        params.gravity = Gravity.TOP;
        view.setLayoutParams(params);
        snack.show();
    }

    public boolean ValidName(String name) {
        if (name.isEmpty()) {
            showMessage("Neįvestas užduoties pavadinimas");
            return false;
        }
        else
            return true;
    }
    public boolean ValidText(String name)
    {
        if(name.isEmpty())
        {
            showMessage("Neįvestas užduoties tekstas");
            return false;
        }
        else
            return true;
    }

}