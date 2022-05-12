package com.example.ejunasapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.navigation.NavigationView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener{
    private String TAG = "MainActivity";
    static public List<Task> favTaskList;
    static public List<Task> otherTaskList;
    static public List<Task> doneTaskList;
    public static final int FAVORITE = 0;
    public static final int OTHER = 1;
    public static final int DONE = 2;
    static public List<TaskItem> categories;
    static public List<TaskItem> levels;
    static public int categoryId =-1;
    static public int levelId = -1;
    static public int categorySelected =0;
    static public int levelSelected = 0;
     private Boolean firstTime = true;
     private int selectedTab = 0;
    private BottomNavigationView bottomNavigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.navigation_tasks);

        if(Tools.user == null)
            new getUser().execute(Tools.RestURL+"auth/user");
        else
            showUser();
        DrawerLayout drawerLayout = findViewById(R.id.mainActivityDrawerLayout);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this,
                        drawerLayout, findViewById(R.id.customActionBar),
                R.string.open_drawer, R.string.close_drawer);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                    switch (item.getItemId()){
                        case R.id.drawer_leaderboard:
                            Intent intent = new Intent(MainActivity.this, LeaderboardActivity.class);
                            startActivity(intent);
                            break;
                        case R.id.drawer_friends:
                            Intent friends = new Intent(MainActivity.this, FriendActivity.class);
                            startActivity(friends);
                            break;
                        case R.id.drawer_account:
                            Intent accountIntent = new Intent(MainActivity.this, AccountActivity.class);
                            startActivity(accountIntent);
                            break;
                        default:
                            return false;
                    }
                    return true;
            }
        });

    }
    private class getUser extends AsyncTask<String, Void, User>{

        ProgressDialog actionProgressDialog =
                new ProgressDialog(MainActivity.this);
        int type = -1;
        @Override
        protected void onPreExecute(){
            actionProgressDialog.setMessage("Gaunami duomenys...");
            actionProgressDialog.show();
            actionProgressDialog.setCancelable(false);
            super.onPreExecute();
        }

        protected User doInBackground(String... str_param){

            String RestURL = str_param[0];
            List<User> data = null;
            try{
                java.lang.reflect.Type type =
                        new com.google.gson.reflect.TypeToken<List<User>>()
                        {}.getType();
                data = (List<User>) DataAPI.jsonObjectToData(RestURL, type);

            }
            catch (Exception ex){
                Log.e(TAG, ex.toString());
            }

            return data.get(0);
        }
        protected void onProgressUpdate(Void... progress){}
        protected void onPostExecute(User result){

            super.onPostExecute(result);
            actionProgressDialog.cancel();
            Tools.user = result;
            showUser();

        }
    }
    private  void showUser(){

        ShapeableImageView shapeableImageView = findViewById(R.id.drawerAccountImage);
        if(Tools.user.base64_picture != null && Tools.user.base64_picture != "") {

            byte[] imageBytes = Base64.getDecoder().decode(Tools.user.base64_picture);
            shapeableImageView.setImageBitmap( BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length));
        }
        TextView textView = findViewById(R.id.drawerAccountNameText);
        textView.setText(Tools.user.user.first_name+ " "+Tools.user.user.last_name);
    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.navigation_tasks:
                selectedTab = 0;
                if(favTaskList != null && otherTaskList != null){
                    showTasks(FAVORITE);}
                else {
                    if(favTaskList == null)
                        getTasks("api-auth/tasks/inprogress", FAVORITE);
                    if(otherTaskList == null)
                        getTasks("api-auth/tasks/other", OTHER);
                }
                return true;

            case R.id.navigation_history:
                selectedTab = 1;
                if(doneTaskList != null)
                    showTasks(DONE);
                else
                    getTasks("api-auth/tasks/done", DONE);
                return true;

        }
        return false;
    }

    private void getTasks(String url, int type){
        new getTasksTask().execute(Tools.RestURL+url, type +"");
    }

    private class getTasksTask extends AsyncTask<String, Void, List<Task>>{

        ProgressDialog actionProgressDialog =
                new ProgressDialog(MainActivity.this);
        int type = -1;
        @Override
        protected void onPreExecute(){
            actionProgressDialog.setMessage("Gaunami duomenys...");
            actionProgressDialog.show();
            actionProgressDialog.setCancelable(false);
            super.onPreExecute();
        }

        protected List<Task> doInBackground(String... str_param){
            String RestURL = str_param[0];
            type = Integer.parseInt(str_param[1]);
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
                switch (type){
                    case FAVORITE:
                        favTaskList = result;
                        break;
                    case OTHER:
                        otherTaskList = result;
                        break;
                    case DONE:
                        doneTaskList = result;
                        break;
                    default:
                        return;
                }
                showTasks(type);
            }
        }
    }

    private void showTasks(int type){
        if(firstTime) {
            firstTime = false;
            if (categories != null) {
                fillSpinner(categories, findViewById(R.id.categorySpinner), categorySelected);
            }
            if (levels != null) {
                fillSpinner(levels, findViewById(R.id.levelSpinner), levelSelected);
            }
        }
        int id = -1;
        CustomAdapter listAdapter;
        List<Task> data;
        switch (type){
            case FAVORITE:
            case OTHER:
                if(favTaskList != null && otherTaskList != null){
                    data = Stream.concat(favTaskList.stream(), otherTaskList.stream())
                            .collect(Collectors.toList());
                    listAdapter = new CustomAdapter(getApplicationContext(), (ArrayList) data, R.layout.task_row, R.layout.favorite_task_row , favTaskList.size());}
                else return;
                break;

            case DONE:
                data = doneTaskList;
                listAdapter = new CustomAdapter(getApplicationContext(), (ArrayList) data, R.layout.done_task_row);
                break;
            default:
                return;
        }

        ListView taskListView = findViewById(R.id.taskListView);
        taskListView.setAdapter(listAdapter);
        taskListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object o = taskListView.getItemAtPosition(position);
                Task task = (Task) o;
                int t = type;
                if(type != DONE){
                    if(position < favTaskList.size())
                        t = FAVORITE;
                    else
                        t = OTHER;
                }
                showDetailedInformation(task, parent, t);

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
        String u = "";
        int type = -1;
        if(selectedTab == 0) {
            u = "api-auth/tasks/other";
            type = OTHER;
        }
        else {
            u = "api-auth/tasks/done";
            type = DONE;
        }
        Spinner categorySpinner = findViewById(R.id.categorySpinner);
        int cId =((TaskItem) categorySpinner.getSelectedItem()).id;
        Spinner levelSpinner = findViewById(R.id.levelSpinner);
        int lId =((TaskItem) levelSpinner.getSelectedItem()).id;
        if(cId != categoryId || lId != levelId) {
            String url = Tools.RestURL + u;
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
            new getTasksTask().execute(url, type+"");
            if(type == OTHER){
                new getTasksTask().execute(url.replaceFirst("other", "inprogress"), FAVORITE+"");
            }
        }
    }
    private class CustomAdapter extends BaseAdapter{

        private ArrayList<Task> singleRow;
        private LayoutInflater thisInflater;
        int rowID = -1;
        int rowID2 = -1;
        int count = -1;
        public CustomAdapter(Context context, ArrayList<Task> aRow, int id) {

            this.singleRow = aRow;
            thisInflater = ( LayoutInflater.from(context) );
            rowID = id;
        }
        public CustomAdapter(Context context, ArrayList<Task> aRow, int id, int id2, int c) {

            this.singleRow = aRow;
            thisInflater = ( LayoutInflater.from(context) );
            rowID = id;
            rowID2 = id2;
            count = c;
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
                if (position < count)
                    convertView = thisInflater.inflate( rowID2, parent, false );
                else
                    convertView = thisInflater.inflate( rowID, parent, false );
                TextView nameText = convertView.findViewById(R.id.nameText);
                TextView levelText = convertView.findViewById(R.id.levelText);
                TextView typeText = convertView.findViewById(R.id.typeText);
                ImageView imageView = convertView.findViewById(R.id.taskImage);
                Task currentRow = (Task) getItem(position);
                nameText.setText(currentRow.name);
                levelText.setText(currentRow.level.name);
                typeText.setText(currentRow.type.name);
                int resID = getResources().getIdentifier("category_"+currentRow.category.id, "drawable", getPackageName());
                if(resID == 0)
                    resID = R.drawable.category_other;
                imageView.setImageResource(resID);

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
    private void showDetailedInformation(Task task, View v, int type){
        Intent myIntent = new Intent(this, TaskDetailedActivity.class);
        myIntent.putExtra("task", (Serializable) task);
        myIntent.putExtra("type", type);
        startActivity(myIntent);
    }
}