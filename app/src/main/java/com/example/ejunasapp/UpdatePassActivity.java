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
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import java.net.HttpURLConnection;

public class UpdatePassActivity extends Activity {
    String TAG = "UpdatePassActivity";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_password_window);
        Button backButton = findViewById(R.id.btn1);
        Button updatePassButton = findViewById(R.id.update);
        updatePassButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               onClickUpdatePass();
            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(UpdatePassActivity.this, AccountActivity.class);
                startActivity(myIntent);
                finish();
            }
        });
    }

    private void onClickUpdatePass() {

        String newpass = ((EditText) findViewById(R.id.newpassword)).getText().toString().trim();
        String newrepeatPass = ((EditText) findViewById(R.id.newrepeatPass)).getText().toString().trim();
        String old =((EditText) findViewById(R.id.old)).getText().toString().trim();

        boolean validPass = ValidatePassword(newpass, newrepeatPass);

        if (!newpass.isEmpty() && !newrepeatPass.isEmpty() && validPass) {
            new tryChangePass().execute(RestURL + "auth/change_password",
                    newpass, newrepeatPass, old);
        } else if(!validPass) {}
        else showMessage("Įveskite slaptažodžius");


    }
    private class tryChangePass extends AsyncTask<String, Void, Boolean> {

        ProgressDialog actionProgressDialog =
                new ProgressDialog(UpdatePassActivity.this);

        @Override
        protected void onPreExecute(){
            actionProgressDialog.setMessage("Keičiamas slaptažodis...");
            actionProgressDialog.show();
            actionProgressDialog.setCancelable(false);
            super.onPreExecute();
        }
        protected Boolean doInBackground(String... str_param){
            String RestURL = str_param[0];
            String newPass = str_param[1];
            String newRepeatPass = str_param[2];
            String old = str_param[3];
            Boolean updatePassIn = null;
            try{
                updatePassIn = WebAPI.attemptUpdatePass(RestURL, newPass, newRepeatPass, old);

            }
            catch (Exception ex){
                Log.e(TAG, ex.toString());
            }
            return updatePassIn;
        }

        protected void onProgressUpdate(Void... progress) {
        }

        protected void onPostExecute(Boolean result) {
            actionProgressDialog.cancel();
            if(result == null){
                showMessage("Įvesti blogi duomenys");
            }
            else if(result != false){
                doUpdatePass();
            }
            else{
                showMessage("Serveris nepasiekiamas");
            }
        }
    }
    private void doUpdatePass(){
        Toast.makeText(getApplicationContext(), "Slaptažodis pakeistas, prisijunkite iš naujo!", Toast.LENGTH_LONG).show();
        Intent logoutIntent = new Intent(this, LoginActivity.class);
        logoutIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        logoutIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        logoutIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        stopService(new Intent(UpdatePassActivity.this, TokenService.class));
        TokenPair.wipeData();
        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.token_file), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getString(R.string.refresh_token), TokenPair.getRefreshToken());
        editor.commit();
        startActivity(logoutIntent);
    }
    private void showMessage(String msg) {

        Snackbar snack = Snackbar.make(findViewById(R.id.repeat), msg, 3000);
        View view = snack.getView();
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) view.getLayoutParams();
        params.gravity = Gravity.TOP;
        view.setLayoutParams(params);
        snack.show();
    }
    public boolean ValidatePassword(String pass, String repeatpass) {
        if (pass.isEmpty() || repeatpass.isEmpty()) {
            showMessage("Neįvesti slaptažodžiai");
            return false;
        }
        if (pass.length() < 8) {
            showMessage("Slaptažodį turi sudaryti daugiau nei 8 simboliai");
            return false;
        }

        if (!pass.equals(repeatpass)) {
            showMessage("Įvesti slaptažodžiai nesutampa");
            return false;
        } else {
            return true;
        }
    }
}
