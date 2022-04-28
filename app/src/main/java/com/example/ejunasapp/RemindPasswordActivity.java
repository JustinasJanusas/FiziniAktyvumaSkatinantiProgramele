package com.example.ejunasapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;

import com.google.android.material.snackbar.Snackbar;

public class RemindPasswordActivity extends Activity {
    private static String TAG = "RemindPasswordActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.remind_password_activity);
        Button buttonRemind = findViewById(R.id.remindActionButton);
        Button buttonBack = findViewById(R.id.remindPasswordBackButton);
        buttonRemind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRemindClick();
            }
        });
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(RemindPasswordActivity.this, LoginActivity.class);
                startActivity(myIntent);
                finish();
            }
        });
    }
    private void onRemindClick(){
        String email = ((EditText) findViewById(R.id.remindPasswordEmailText)).getText().toString();
        if(isValidEmail(email)){
            new tryResetPassword().execute(Tools.RestURL+"auth/reset_password", email);
        }
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
    private void showMessage(String msg){
        Snackbar mySnackbar = Snackbar.make(findViewById(R.id.remindPasswordEmailText),
                msg, 3000);
        View view = mySnackbar.getView();
        FrameLayout.LayoutParams params =(FrameLayout.LayoutParams)view.getLayoutParams();
        params.gravity = Gravity.TOP;
        view.setLayoutParams(params);
        mySnackbar.show();
    }
    private class tryResetPassword extends AsyncTask<String, Void, Boolean> {

        ProgressDialog actionProgressDialog =
                new ProgressDialog(RemindPasswordActivity.this);

        @Override
        protected void onPreExecute(){
            actionProgressDialog.setMessage("Jungiamasi...");
            actionProgressDialog.show();
            actionProgressDialog.setCancelable(false);
            super.onPreExecute();
        }

        protected Boolean doInBackground(String... str_param){
            String RestURL = str_param[0];
            String email = str_param[1];
            Boolean passwordReset = null;
            try{
                passwordReset = WebAPI.attempResetPassword(RestURL, email);

            }
            catch (Exception ex){
                Log.e(TAG, ex.toString());
            }
            return passwordReset;
        }
        protected void onProgressUpdate(Void... progress){}
        protected void onPostExecute(Boolean result){
            actionProgressDialog.cancel();
            if(result == null){
                showMessage("Serveris nepasiekiamas");
            }
            else if(result){
                showMessage("Patikrinkite savo el paštą");;
            }
            else{

                showMessage("Įvestas blogas el. paštas");
            }
        }
    }
}

