package com.example.ejunasapp;

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
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Button;
import org.w3c.dom.Text;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.zip.Inflater;

public class MainActivity extends AppCompatActivity{
    private String TAG = "MainActivity";
    static public List<Task> taskList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
                data = DataAPI.jsonToTasks(RestURL);

            }
            catch (Exception ex){
                Log.e(TAG, ex.toString());
            }
            return data;
        }
        protected void onProgressUpdate(Void... progress){}
        protected void onPostExecute(List<Task> result){
            actionProgressDialog.cancel();
            taskList = result;
            showTasks(result);
        }
    }

    private void showTasks(List<Task> data){
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