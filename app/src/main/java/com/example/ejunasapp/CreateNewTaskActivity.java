package com.example.ejunasapp;


import static com.example.ejunasapp.Tools.RestURL;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import android.widget.ListView;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

public class CreateNewTaskActivity extends Activity {

    private String TAG = "CreateNewTaskActivity";
    private Boolean firstTime = true;
    static public List<TaskItem> categories;
    static public List<TaskItem> levels;
    static public List<TaskItem> types;
    static public List<TaskItem> radius;
    static public int categoryId = -1;
    static public int levelId = -1;
    static public int typeId = -1;
    static public int radiusId = -1;
    static public int categorySelected;
    static public int levelSelected;
    static public int radiusSelected;
    static public int typeSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_new_task_activity);

        fillSpiners();
        Button backButton = findViewById(R.id.btn1);
        Button doneButton = findViewById(R.id.done);
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDoneClick();
            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(CreateNewTaskActivity.this, MainActivity.class);
                startActivity(myIntent);
                finish();
            }
        });
    }
    private List<TaskItem> getTaskItems(String url) throws Exception{
        java.lang.reflect.Type type =
                new com.google.gson.reflect.TypeToken<List<TaskItem>>()
                {}.getType();
        return (List<TaskItem>) DataAPI.jsonToData(url, type);
    }
    private void fillSpiners() {
        if (firstTime) {
            firstTime = false;
            try {
                if(categories == null){
                    categories = getTaskItems(Tools.RestURL+"api-auth/categories");
                    categories.add(0, new TaskItem(-1, "----"));
                }
                if(levels == null){
                    levels = getTaskItems(Tools.RestURL+"api-auth/levels");
                    levels.add(0, new TaskItem(-1, "----"));
                }
                if(types == null){
                    types = getTaskItems(Tools.RestURL+"api-auth/types");
                    types.add(0, new TaskItem(-1, "----"));
                }
                if(radius == null){
                    radius.add(0, new TaskItem(-1, "10KM"));
                    radius.add(0, new TaskItem(1, "1KM"));
                    radius.add(0, new TaskItem(2, "100M"));
                    radius.add(0, new TaskItem(3, "10M"));
                    radius.add(0, new TaskItem(4, "1M"));
                }

            if (categories != null) {
                fillSpinner(categories, findViewById(R.id.categorySpinner), categorySelected);
            }
            if (levels != null) {
                fillSpinner(levels, findViewById(R.id.levelSpinner), levelSelected);
            }
            if (radius != null) {
                fillSpinner(radius, findViewById(R.id.radiusSpinner), radiusSelected);
            }
            if (types != null) {
                fillSpinner(types, findViewById(R.id.typeSpinner), typeSelected);
            }}
            catch (Exception ex){
                Log.e(TAG, ex.toString());
            }
        }
    }

   /* private void getFilteredTasks() {
        String u = "";
        int type = -1;
        if (selectedTab == 0) {
            u = "api-auth/tasks/other";
            type = OTHER;
        } else {
            u = "api-auth/tasks/done";
            type = DONE;
        }
        Spinner categorySpinner = findViewById(R.id.categorySpinner);
        int cId = ((TaskItem) categorySpinner.getSelectedItem()).id;
        Spinner levelSpinner = findViewById(R.id.levelSpinner);
        int lId = ((TaskItem) levelSpinner.getSelectedItem()).id;
        if (cId != categoryId || lId != levelId) {
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
            getTasks(url, type, false);
            if (type == OTHER) {
                new MainActivity.getFavoritesTask().execute(url.replaceFirst("other", "inprogress"));
            }
        }
    }*/

    //------------------------------------------------------------------------
    private void onDoneClick() {
    }

    /*
    String username = ((EditText) findViewById(R.id.username1)).getText().toString().trim();
    String name = ((EditText) findViewById(R.id.registrationName)).getText().toString().trim();
    String surname = ((EditText) findViewById(R.id.registrationSurname)).getText().toString().trim();
    String email = ((EditText) findViewById(R.id.email)).getText().toString().trim();
    String pass = ((EditText) findViewById(R.id.password)).getText().toString().trim();
    String repeatPass = ((EditText) findViewById(R.id.repeatPass)).getText().toString().trim();

    boolean validEmail = isValidEmail(email);
    boolean validPass = ValidatePassword(pass, repeatPass);
    boolean ValidName = ValidNameSurname(name);
    boolean ValidSurname = ValidNameSurname(surname);

    if (!username.isEmpty() && !name.isEmpty() && !surname.isEmpty() && !email.isEmpty()
            && !pass.isEmpty() && !repeatPass.isEmpty() && validEmail && validPass
            && ValidName && ValidSurname) {
        new RegistrationActivity.tryRegister().execute(RestURL + "auth/register",
                username, email, pass, repeatPass, name, surname);
    } else if ((!username.isEmpty() && !name.isEmpty()
            && !surname.isEmpty() && !email.isEmpty()
            && !pass.isEmpty() && !repeatPass.isEmpty() && (!ValidName || !ValidSurname)) ||
            (!username.isEmpty() && !name.isEmpty()
                    && !surname.isEmpty() && !email.isEmpty()
                    && !pass.isEmpty() && !repeatPass.isEmpty()
                    && !validEmail) ||
            (!username.isEmpty() && !name.isEmpty()
                    && !surname.isEmpty() && !email.isEmpty()
                    && !pass.isEmpty() && !repeatPass.isEmpty() && !validPass)) {

    } else showMessage("Įveskite visus registracijos duomenis");


}

private class tryRegister extends AsyncTask<String, Void, Boolean> {

    ProgressDialog actionProgressDialog =
            new ProgressDialog(RegistrationActivity.this);

    @Override
    protected void onPreExecute() {
        actionProgressDialog.setMessage("Registruojamasi...");
        actionProgressDialog.show();
        actionProgressDialog.setCancelable(false);
        super.onPreExecute();
    }

    protected Boolean doInBackground(String... str_param) {
        String RestURL = str_param[0];
        String username = str_param[1];
        String email = str_param[2];
        String pass = str_param[3];
        String repeatPass = str_param[4];
        String name = str_param[5];
        String surname = str_param[6];
        Boolean registerIn = null;
        try {

            if (ValidatePassword(pass, repeatPass) && isValidEmail(email)
                    && ValidNameSurname(name) && ValidNameSurname(surname)) {
                registerIn = WebAPI.attemptRegister(RestURL, username, name,
                        surname, email, pass, repeatPass);

            } else showMessage("Klaida!");

        } catch (Exception ex) {
            Log.e(TAG, ex.toString());
        }
        return registerIn;
    }

    protected void onProgressUpdate(Void... progress) {
    }

    protected void onPostExecute(Boolean result) {
        actionProgressDialog.cancel();

        if (result == null) {
            showMessage("Serveris nepasiekiamas");
        } else if(result != false){
            doRegister();
        }
        else
        {
            showMessage("Naudotojas su tokias duomenimis jau yra" +
                    " arba slaptažodis per paprastas");
        }
    }
}

private void showMessage(String msg) {

    Snackbar snack = Snackbar.make(findViewById(R.id.registrationS), msg, 3000);
    View view = snack.getView();
    FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) view.getLayoutParams();
    params.gravity = Gravity.TOP;
    view.setLayoutParams(params);
    snack.show();
}


*/
    private void fillSpinner(List<TaskItem> list, Spinner spinner, int pos) {

        CreateNewTaskActivity.CustomSpinnerAdapter adapter = new CreateNewTaskActivity.CustomSpinnerAdapter(CreateNewTaskActivity.this,
                android.R.layout.simple_spinner_item, list.toArray(new TaskItem[list.size()]));
        spinner.setAdapter(adapter);
        spinner.setSelection(pos);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view,
                                       int position, long id) {
                //getFilteredTasks();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapter) {
            }
        });
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
}



        //Ačiū! Jūsų užduotį patvirtintis administratorius


