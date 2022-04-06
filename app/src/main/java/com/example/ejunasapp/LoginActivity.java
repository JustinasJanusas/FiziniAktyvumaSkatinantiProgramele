package com.example.ejunasapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;

import com.google.android.material.snackbar.Snackbar;

import java.io.Serializable;
import java.util.List;

public class LoginActivity extends Activity {

    private String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        Button buttonLogin = findViewById(R.id.loginActionButton);
        Button buttonRegister = findViewById(R.id.loginRegistrationButton);
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLoginClick();
            }
        });
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(LoginActivity.this, RegistrationActivity.class);
                startActivity(myIntent);
                finish();
            }
        });
    }
    private void onLoginClick(){
        String name = ((EditText) findViewById(R.id.loginNameText)).getText().toString();
        String pass = ((EditText) findViewById(R.id.loginPasswordText)).getText().toString();
        if(!name.isEmpty() && !pass.isEmpty()){
            new tryLogin().execute(Tools.RestURL+"auth/login", name, pass);
        }
        else {
            showMessage("Įveskite prisijungimo duomenis");
        }
    }
    private class tryLogin extends AsyncTask<String, Void, Boolean> {

        ProgressDialog actionProgressDialog =
                new ProgressDialog(LoginActivity.this);

        @Override
        protected void onPreExecute(){
            actionProgressDialog.setMessage("Jungiamasi...");
            actionProgressDialog.show();
            actionProgressDialog.setCancelable(false);
            super.onPreExecute();
        }

        protected Boolean doInBackground(String... str_param){
            String RestURL = str_param[0];
            String user = str_param[1];
            String pass = str_param[2];
            Boolean loggedIn = null;
            try{
                loggedIn = WebAPI.attemptLogin(RestURL, user, pass);

            }
            catch (Exception ex){
                Log.e(TAG, ex.toString());
            }
            return loggedIn;
        }
        protected void onProgressUpdate(Void... progress){}
        protected void onPostExecute(Boolean result){
            actionProgressDialog.cancel();
            if(result == null){
                showMessage("Serveris nepasiekiamas");
            }
            else if(result){
                doLogin();
            }
            else{
                showMessage("Įvesti blogi prisijungimo duomenys");
            }
        }
    }
    private void showMessage(String msg){
        Snackbar mySnackbar = Snackbar.make(findViewById(R.id.loginPasswordText),
                msg, 3000);
        View view = mySnackbar.getView();
        FrameLayout.LayoutParams params =(FrameLayout.LayoutParams)view.getLayoutParams();
        params.gravity = Gravity.TOP;
        view.setLayoutParams(params);
        mySnackbar.show();
    }
    private void doLogin(){
        Intent serviceIntent = new Intent(LoginActivity.this, TokenService.class);
        startService(serviceIntent);
        Intent myIntent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(myIntent);
        finish();
    }
}