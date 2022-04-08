package com.example.ejunasapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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
import android.text.Layout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Button;

import com.google.android.material.imageview.ShapeableImageView;

import org.w3c.dom.Text;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.zip.Inflater;

public class MainActivity extends AppCompatActivity{
    private String TAG = "MainActivity";
    static public List<Task> taskList;
    static public List<TaskItem> categories;
    static public List<TaskItem> levels;
    static public int categoryId =-1;
    static public int levelId = -1;
    static public int categorySelected =0;
    static public int levelSelected = 0;
     private Boolean firstTime = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ShapeableImageView accountButton = findViewById(R.id.accountImageButton);
        accountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent accountIntent = new Intent(MainActivity.this, AccountActivity.class);
                startActivity(accountIntent);
            }
        });
        if(taskList != null)
            showTasks(taskList);
        else
            getTasks();
    }

    private void getTasks(){
        new getTasksTask().execute(Tools.RestURL+"api-auth/tasks");
    }

    private class getTasksTask extends AsyncTask<String, Void, List<Task>>{

        ProgressDialog actionProgressDialog =
                new ProgressDialog(MainActivity.this);

        @Override
        protected void onPreExecute(){
            actionProgressDialog.setMessage("Gaunami duomenys...");
            actionProgressDialog.show();
            actionProgressDialog.setCancelable(false);
            super.onPreExecute();
        }

        protected List<Task> doInBackground(String... str_param){
            String RestURL = str_param[0];
            List<Task> data = null;
            try{
                if(categories == null){
                    categories = getTaskItems(Tools.RestURL+"api-auth/categories");
                    categories.add(0, new TaskItem(-1, "Visos"));
                }
                if(levels == null){
                    levels = getTaskItems(Tools.RestURL+"api-auth/levels");
                    levels.add(0, new TaskItem(-1, "Visi"));
                }
                java.lang.reflect.Type type =
                        new com.google.gson.reflect.TypeToken<List<Task>>()
                        {}.getType();
                data = (List<Task>) DataAPI.jsonToData(RestURL, type);

            }
            catch (Exception ex){
                Log.e(TAG, ex.toString());
            }
            return data;
        }
        protected void onProgressUpdate(Void... progress){}
        protected void onPostExecute(List<Task> result){
            actionProgressDialog.cancel();

            if(result != null) {
                taskList = result;
                showTasks(taskList);
            }
        }
    }

    private void showTasks(List<Task> data){
        if(firstTime) {
            firstTime = false;
            if (categories != null) {
                fillSpinner(categories, findViewById(R.id.categorySpinner), categorySelected);
            }
            if (levels != null) {
                fillSpinner(levels, findViewById(R.id.levelSpinner), levelSelected);
            }
        }
        CustomAdapter listAdapter = new CustomAdapter(getApplicationContext(), (ArrayList) data);
        ListView taskListView = findViewById(R.id.taskListView);
        taskListView.setAdapter(listAdapter);
        taskListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object o = taskListView.getItemAtPosition(position);
                Task task = (Task) o;
                showDetailedInformation(task, parent);

            }
        });
    }
    private List<TaskItem> getTaskItems(String url) throws Exception{
        java.lang.reflect.Type type =
                new com.google.gson.reflect.TypeToken<List<TaskItem>>()
                {}.getType();
        return (List<TaskItem>) DataAPI.jsonToData(url, type);
    }

    private void fillSpinner(List<TaskItem> list, Spinner spinner, int pos){

        CustomSpinnerAdapter adapter = new CustomSpinnerAdapter(MainActivity.this,
                android.R.layout.simple_spinner_item,  list.toArray(new TaskItem[list.size()]));
        spinner.setAdapter(adapter);
        spinner.setSelection(pos);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view,
                                       int position, long id) {
                getFilteredTasks();

            }
            @Override
            public void onNothingSelected(AdapterView<?> adapter) {  }
        });
    }
    private void getFilteredTasks(){
        Spinner categorySpinner = findViewById(R.id.categorySpinner);
        int cId =((TaskItem) categorySpinner.getSelectedItem()).id;
        Spinner levelSpinner = findViewById(R.id.levelSpinner);
        int lId =((TaskItem) levelSpinner.getSelectedItem()).id;
        if(cId != categoryId || lId != levelId) {
            String url = Tools.RestURL + "api-auth/tasks";
            if (cId > -1 || lId > -1) {
                url += "?";
                if (cId > -1) {
                    categoryId = cId;
                    categorySelected = categorySpinner.getSelectedItemPosition();
                    url += "category=" + cId;
                    if (lId > -1)
                        url += "&";
                }
                if (lId > -1) {
                     levelSelected = levelSpinner.getSelectedItemPosition();
                    levelId = lId;
                    url += "level=" + lId;
                }
            }
            new getTasksTask().execute(url);
        }
    }
    private class CustomAdapter extends BaseAdapter{

        private ArrayList<Task> singleRow;
        private LayoutInflater thisInflater;
        public CustomAdapter(Context context, ArrayList<Task> aRow) {

            this.singleRow = aRow;
            thisInflater = ( LayoutInflater.from(context) );

        }
        @Override
        public int getCount() {
            return singleRow.size();
        }

        @Override
        public Object getItem(int position) {
            return singleRow.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = thisInflater.inflate( R.layout.task_row, parent, false );
                TextView nameText = convertView.findViewById(R.id.nameText);
                TextView levelText = convertView.findViewById(R.id.levelText);
                TextView typeText = convertView.findViewById(R.id.typeText);
                ImageView imageView = convertView.findViewById(R.id.taskImage);
                Task currentRow = (Task) getItem(position);
                nameText.setText(currentRow.name);
                levelText.setText(currentRow.level.name);
                typeText.setText(currentRow.type.name);
                byte[] imageBytes = Base64.getDecoder().decode(currentRow.base64_image);
                Bitmap image = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                imageView.setImageBitmap(image);
            }
            return convertView;
        }
    }
    private class CustomSpinnerAdapter extends ArrayAdapter<TaskItem>{
        private Context context;
        private TaskItem[] values;
        public CustomSpinnerAdapter(Context context, int textViewResourceId, TaskItem[] values){
            super(context, textViewResourceId, values);
            this.context = context;
            this.values = values;
        }

        @Override
        public int getCount() {
            return values.length;
        }

        @Override
        public TaskItem getItem(int position){
            return values[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position,  View convertView, ViewGroup parent) {
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
    private void showDetailedInformation(Task task, View v){
        Intent myIntent = new Intent(this, TaskDetailedActivity.class);
        myIntent.putExtra("task", (Serializable) task);
        startActivity(myIntent);
    }
    //REGISTRACIJOS FORMOS PATIKRINIMAS---------------------------------------
    private void showRegistration(View v)
    {
        Intent registrIntent = new Intent(this, RegistrationActivity.class);
        startActivity(registrIntent);
    }
    //---------------TO NEREIKES VELIAU, CIA KAD PARODYTU POPWINDOW KAI PASPAUDI ANT PASIRINKTOS UZDUOTIES---------
    /*
    private void showTaskInformation(Task task, View v){
        PopupWindow window = new PopupWindow(this);
        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_task_window, null);
        TextView textV = (TextView) popupView.findViewById(R.id.popupTaskName);
        textV.setText(task.name);
        textV = (TextView) popupView.findViewById(R.id.popupCategoryName);
        textV.setText(task.category.name);
        textV = (TextView) popupView.findViewById(R.id.popupLevelName);
        textV.setText(task.level.name);
        textV = (TextView) popupView.findViewById(R.id.popupTypeName);
        textV.setText(task.type.name);
        textV = (TextView) popupView.findViewById(R.id.popupAuthorName);
        textV.setText(task.author);
        textV = (TextView) popupView.findViewById(R.id.popupTaskText);
        textV.setText(task.text);
        int width = RelativeLayout.LayoutParams.WRAP_CONTENT;
        int height = RelativeLayout.LayoutParams.WRAP_CONTENT;
        PopupWindow popupWindow = new PopupWindow(popupView, width, height, true);

        popupWindow.showAtLocation(v.getRootView(), Gravity.CENTER, 0, 0);
    }
    */
}