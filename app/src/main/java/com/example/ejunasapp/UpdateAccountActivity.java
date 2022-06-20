package com.example.ejunasapp;

import static com.example.ejunasapp.Tools.RestURL;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

public class UpdateAccountActivity extends Activity {
    String TAG = "UpdateAccountActivity";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_account_activity);
        Button backButton = findViewById(R.id.btn1);
        Button updateAccountButton = findViewById(R.id.done);
        EditText Updateusername = (EditText) findViewById(R.id.updateUsername);
        EditText Updatename = (EditText) findViewById(R.id.updateName);
        EditText Updatesurname = (EditText) findViewById(R.id.updateSurname);
        EditText Updateemail = (EditText) findViewById(R.id.updateemail);
        Updateusername.setText(Tools.user.user.username);
        Updatename.setText(Tools.user.user.first_name);
        Updatesurname.setText(Tools.user.user.last_name);
        Updateemail.setText(Tools.user.user.email);
        updateAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onUpdateClick();
            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });

    }
    private void onUpdateClick() {
        String Updateusername = ((EditText) findViewById(R.id.updateUsername)).getText().toString();
        String Updatename = ((EditText) findViewById(R.id.updateName)).getText().toString();
        String Updatesurname = ((EditText) findViewById(R.id.updateSurname)).getText().toString();
        String Updateemail = ((EditText) findViewById(R.id.updateemail)).getText().toString().trim();

        boolean validEmail = isValidEmail(Updateemail);
        boolean ValidName = ValidNameSurname(Updatename);
        boolean ValidSurname = ValidNameSurname(Updatesurname);

        if (!Updateusername.isEmpty() && !Updatename.isEmpty() && !Updatesurname.isEmpty() && !Updateemail.isEmpty() && validEmail
                 && ValidName && ValidSurname) {
            new tryUpdateAccount().execute(RestURL + "auth/update_profile", Updateusername,
                    Updatename, Updatesurname, Updateemail);
        } else if ((!Updatename.isEmpty() && !Updatesurname.isEmpty()
                && !Updateemail.isEmpty() && (!ValidName || !ValidSurname)) ||
                (!Updatename.isEmpty() && !Updatesurname.isEmpty()
                        && !Updateemail.isEmpty() && !validEmail)) {
        } else showMessage("Įveskite visus registracijos duomenis");


    }
    private class tryUpdateAccount extends AsyncTask<String, Void, Boolean> {

        ProgressDialog actionProgressDialog =
                new ProgressDialog(UpdateAccountActivity.this);

        @Override
        protected void onPreExecute() {
            actionProgressDialog.setMessage("Atnaujinama...");
            actionProgressDialog.show();
            actionProgressDialog.setCancelable(false);
            super.onPreExecute();
        }

        protected Boolean doInBackground(String... str_param) {
            String RestURL = str_param[0];
            String username = str_param[1];
            String name = str_param[2];
            String surname = str_param[3];
            String email = str_param[4];
            Boolean accountupdateIn = null;
            try {

                if (isValidEmail(email)
                        && ValidNameSurname(name) && ValidNameSurname(surname)) {
                    accountupdateIn = WebAPI.attemptUpdateAccount(RestURL, username, name,
                            surname, email);

                } else showMessage("Klaida!");

            } catch (Exception ex) {
                Log.e(TAG, ex.toString());
            }
            return accountupdateIn;
        }

        protected void onProgressUpdate(Void... progress) {
        }

        protected void onPostExecute(Boolean result) {
            actionProgressDialog.cancel();

            if (result == null) {
                showMessage("Serveris nepasiekiamas");
            } else if(result != false){
                doUpdate();
            }
            else
            {
                showMessage("Klaida");
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

    private void doUpdate() {
        Toast.makeText(getApplicationContext(), "Paskyra atnaujinta, prisijunkite", Toast.LENGTH_LONG).show();
        Intent logoutIntent = new Intent(this, LoginActivity.class);
        logoutIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        logoutIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        logoutIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        stopService(new Intent(UpdateAccountActivity.this, TokenService.class));
        TokenPair.wipeData();
        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.token_file), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getString(R.string.refresh_token), TokenPair.getRefreshToken());
        editor.commit();
        startActivity(logoutIntent);
    }
    public boolean isValidEmail(String emailToText) {

        if (emailToText.isEmpty()) {
            showMessage("Neįvestas el. paštas");
            return false;

        } else if (!Patterns.EMAIL_ADDRESS.matcher(emailToText).matches()) {
            showMessage("Netaisyklingai įvestas el. paštas");
            return false;
        } else {
            return true;
        }
    }
    public boolean ValidNameSurname(String nameSurname) {
        int len = nameSurname.length();
        for (int i = 0; i < len; i++) {
            // checks whether the character is not a letter
            // if it is not a letter ,it will return false
            if ((Character.isLetter(nameSurname.charAt(i)) == false)) {

                showMessage("Naudojami neleistini simboliai varde/pavardėje");
                return false;
            }
        }
        return true;
    }

}