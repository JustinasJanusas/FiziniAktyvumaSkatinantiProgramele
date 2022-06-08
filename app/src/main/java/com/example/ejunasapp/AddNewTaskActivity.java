package com.example.ejunasapp;
import static com.example.ejunasapp.Tools.RestURL;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
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

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class AddNewTaskActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    static public int categoryId =-1;
    static public int levelId = -1;
    static public int typeId = -1;
    static public int radiusId = -1;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_new_task_activity);

        // Spinner category element
        List<TaskItem> categories = new ArrayList<TaskItem>();
        try {
            categories.add(0, new TaskItem(-1, "----"));
            java.lang.reflect.Type type =
                    new com.google.gson.reflect.TypeToken<List<TaskItem>>() {
                    }.getType();
            categories = (List<TaskItem>) DataAPI.jsonToData(Tools.RestURL + "api-auth/categories", type);

        } catch (Exception ex) {
        }
        Spinner categoryspinner = (Spinner) findViewById(R.id.categorySpinner);
        categoryspinner.setOnItemSelectedListener(this);
        AddNewTaskActivity.CustomSpinnerAdapter catadapter = new AddNewTaskActivity.CustomSpinnerAdapter(AddNewTaskActivity.this,
                android.R.layout.simple_spinner_item, categories.toArray(new TaskItem[categories.size()]));
        categoryspinner.setAdapter(catadapter);
        categoryspinner.setSelection(-1);


        //LevelSpinner
        List<TaskItem> levels = new ArrayList<TaskItem>();
        try {
            levels.add(0, new TaskItem(-1, "----"));
            java.lang.reflect.Type type =
                    new com.google.gson.reflect.TypeToken<List<TaskItem>>() {
                    }.getType();
            levels = (List<TaskItem>) DataAPI.jsonToData(Tools.RestURL + "api-auth/levels", type);

        } catch (Exception ex) {
        }
        Spinner levelspinner = (Spinner) findViewById(R.id.levelSpinner);
        levelspinner.setOnItemSelectedListener(this);
        AddNewTaskActivity.CustomSpinnerAdapter levspinner = new AddNewTaskActivity.CustomSpinnerAdapter(AddNewTaskActivity.this,
                android.R.layout.simple_spinner_item, levels.toArray(new TaskItem[levels.size()]));
        levelspinner.setAdapter(levspinner);
        levelspinner.setSelection(-1);


        //typeSpinner
        List<TaskItem> types = new ArrayList<TaskItem>();
        try {
            types.add(0, new TaskItem(-1, "----"));
            java.lang.reflect.Type type =
                    new com.google.gson.reflect.TypeToken<List<TaskItem>>() {
                    }.getType();
            types = (List<TaskItem>) DataAPI.jsonToData(Tools.RestURL + "api-auth/types", type);
        } catch (Exception ex) { }
        Spinner typespinner = (Spinner) findViewById(R.id.typeSpinner);
        typespinner.setOnItemSelectedListener(this);
        AddNewTaskActivity.CustomSpinnerAdapter typeadapter = new AddNewTaskActivity.CustomSpinnerAdapter(AddNewTaskActivity.this,
                android.R.layout.simple_spinner_item, types.toArray(new TaskItem[types.size()]));
        typespinner.setAdapter(typeadapter);
        typespinner.setSelection(-1);


        //spinner radius element
        Spinner radiusspinner = (Spinner) findViewById(R.id.radiusSpinner);
        radiusspinner.setOnItemSelectedListener(this);
        List<TaskItem> radius = new ArrayList<TaskItem>();
        radius.add(0, new TaskItem(1, "10KM"));
        radius.add(0, new TaskItem(2, "1KM"));
        radius.add(0, new TaskItem(3, "100M"));
        radius.add(0, new TaskItem(4, "10M"));
        radius.add(0, new TaskItem(5, "1M"));
        radius.add(0, new TaskItem(-1, "----"));
        AddNewTaskActivity.CustomSpinnerAdapter adapter = new AddNewTaskActivity.CustomSpinnerAdapter(AddNewTaskActivity.this,
                android.R.layout.simple_spinner_item, radius.toArray(new TaskItem[radius.size()]));
        radiusspinner.setAdapter(adapter);
        radiusspinner.setSelection(-1);



        Button backButton = findViewById(R.id.btn1);
        Button addButton = findViewById(R.id.done);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onAddClick();
            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(AddNewTaskActivity.this, MainActivity.class);
                startActivity(myIntent);
                finish();
            }
        });
    }
    private void onAddClick() {
        String taskName = ((EditText) findViewById(R.id.taskName)).getText().toString().trim();
        String taskText = ((EditText) findViewById(R.id.taskText)).getText().toString().trim();
        String newLatitude = ((EditText) findViewById(R.id.newLatitude)).getText().toString().trim();
        String newLongitude = ((EditText) findViewById(R.id.newLongitude)).getText().toString().trim();

        Spinner categorySpinner = findViewById(R.id.categorySpinner);
        int cId = ((TaskItem) categorySpinner.getSelectedItem()).id;
        String category = ((TaskItem) categorySpinner.getSelectedItem()).name;

        Spinner levelSpinner = findViewById(R.id.levelSpinner);
        int lId = ((TaskItem) levelSpinner.getSelectedItem()).id;
        String level = ((TaskItem) levelSpinner.getSelectedItem()).name;

        Spinner radiusSpinner = findViewById(R.id.radiusSpinner);
        int rId = ((TaskItem) radiusSpinner.getSelectedItem()).id;
        String radius = ((TaskItem) radiusSpinner.getSelectedItem()).name;

        Spinner typeSpinner = findViewById(R.id.typeSpinner);
        int tId = ((TaskItem) typeSpinner.getSelectedItem()).id;
        String type = ((TaskItem) typeSpinner.getSelectedItem()).name;

        String author = Tools.user.user.username;

        if (cId != categoryId || lId != levelId || rId != radiusId || tId != typeId) {

            boolean validName = ValidName(taskName);
            boolean validTaskName = ValidText(taskText);
            boolean Valilatitude = Validlatitude(newLatitude);
            boolean validLongitute = Validlongitude(newLongitude);

            if (validName && validTaskName && Valilatitude && validLongitute) {
                new tryAdd().execute(RestURL + "api-auth/task/insert",
                        taskName, category, type, level, author, newLatitude, newLongitude, radius, taskText);
            } else {
                if ((!taskName.isEmpty() && !taskText.isEmpty() && !newLatitude.isEmpty() && !newLongitude.isEmpty()
                        && !validName) || (!taskName.isEmpty() && !taskText.isEmpty() &&
                        !newLatitude.isEmpty() && !newLongitude.isEmpty()
                        && !validTaskName) || (!taskName.isEmpty() && !taskText.isEmpty() &&
                        !newLatitude.isEmpty() && !newLongitude.isEmpty()
                        && !Valilatitude) || (!taskName.isEmpty() && !taskText.isEmpty() &&
                        !newLatitude.isEmpty() && !newLongitude.isEmpty()
                        && !validLongitute)) {

                }
                showMessage("Ne visi laukai pasirinkti");
            }
        }

    }

    private class tryAdd extends AsyncTask<String, Void, Boolean> {

        ProgressDialog actionProgressDialog =
                new ProgressDialog(AddNewTaskActivity.this);

        @Override
        protected void onPreExecute() {
            actionProgressDialog.setMessage("Keliama...");
            actionProgressDialog.show();
            actionProgressDialog.setCancelable(false);
            super.onPreExecute();
        }

        protected Boolean doInBackground(String... str_param) {
            String RestURL = str_param[0];
            String name = str_param[1];
            String category = str_param[2];
            String type = str_param[3];
            String level = str_param[4];
            String author = str_param[5];
            String newLatitude = str_param[6];
            String newLongitude = str_param[6];
            String radius = str_param[6];
            String taskText = str_param[6];
            Boolean AddTask = null;
            try {

                AddTask = WebAPI.attemptAddTask(RestURL, name, category,
                            type, level, author, newLatitude, newLongitude, radius, taskText);

                }

             catch (Exception ex) {
               showMessage("Klaida!");
                Log.e("tag ", ex.toString());
            }
            return AddTask;
        }

        protected void onProgressUpdate(Void... progress) {
        }

        protected void onPostExecute(Boolean result) {
            actionProgressDialog.cancel();

            if (result == null) {
                showMessage("Serveris nepasiekiamas");
            } else if(result != false){

                showMessage("Ačiū! Jūsų užduotį patvirtintis administratorius");
            }
            else
            {
                showMessage("Klaida");
            }
        }
    }
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

       // Toast.makeText(parent.getContext(), "Selected: ", Toast.LENGTH_LONG).show();

    }

    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub

    }

    private List<TaskItem> getTaskItems(String url) throws Exception {
        java.lang.reflect.Type type =
                new com.google.gson.reflect.TypeToken<List<TaskItem>>() {
                }.getType();
        return (List<TaskItem>) DataAPI.jsonToData(url, type);
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
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            TextView label = (TextView) super.getDropDownView(position, convertView, parent);
            label.setTextColor(Color.BLACK);
            label.setText(values[position].name);
            return label;
        }
    }

    private void showMessage(String msg) {

        Snackbar snack = Snackbar.make(findViewById(R.id.TaskNameTextView), msg, 3000);
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

    public boolean Validlatitude(String latitude) {
        if (!Pattern.matches("^(\\+|-)?(?:90(?:(?:\\.0{1,6})?)|(?:[0-9]|[1-8][0-9])(?:(?:\\.[0-9]{1,6})?))$", latitude)) {

            showMessage("Neteisingai įvesta platuma");
            return false;
        } else
            return true;
    }

    public boolean Validlongitude(String longitude) {
        if (!Pattern.matches("^(\\+|-)?(?:180(?:(?:\\.0{1,6})?)|(?:[0-9]|[1-9][0-9]|1[0-7][0-9])(?:(?:\\.[0-9]{1,6})?))$", longitude)) {
            showMessage("Neteisingai įvesta ilguma");
            return false;
        } else
            return true;
    }
}