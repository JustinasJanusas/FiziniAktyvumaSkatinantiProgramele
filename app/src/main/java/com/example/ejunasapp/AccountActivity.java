package com.example.ejunasapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.android.material.snackbar.Snackbar;

import java.net.HttpURLConnection;
import java.util.List;

public class AccountActivity extends Activity {
    String TAG = "AccountAcitivity";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_activity);
        ImageButton buttonBack = findViewById(R.id.accountBackButton);
        Button buttonLogout = findViewById(R.id.logoutButton);
        Button buttonDeleteAccount = findViewById(R.id.deleteAccountButton);
        Button buttonUpdateAccout = findViewById(R.id.updateAccoutButton);
        Button buttonUpdatePass = findViewById(R.id.updatePassButton);

        buttonUpdateAccout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent updateAccountIntent = new Intent(AccountActivity.this, UpdateAccountActivity.class);
                startActivity(updateAccountIntent);
            }
        });
        buttonUpdatePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent passIntent = new Intent(AccountActivity.this, UpdatePassActivity.class);
                startActivity(passIntent);
            }
        });

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new tryLogout().execute(Tools.RestURL+"auth/logout", "POST");
            }
        });
        buttonDeleteAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(AccountActivity.this)
                        .setTitle("")
                        .setMessage("Do you really want to whatever?")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setNegativeButton(R.string.cancel, null)
                        .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                new tryLogout().execute(Tools.RestURL+"auth/delete", "DELETE");
                            }}).show();

            }
        });
        if(Tools.user == null)
            new getUser().execute(Tools.RestURL+"auth/user");
        else
            showUser();
    }
    private class getUser extends AsyncTask<String, Void, User>{

        ProgressDialog actionProgressDialog =
                new ProgressDialog(AccountActivity.this);
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
    private class tryLogout extends AsyncTask<String, Void, Integer> {

        ProgressDialog actionProgressDialog =
                new ProgressDialog(AccountActivity.this);

        @Override
        protected void onPreExecute(){
            actionProgressDialog.setMessage("Atsijungiama...");
            actionProgressDialog.show();
            actionProgressDialog.setCancelable(false);
            super.onPreExecute();
        }

        protected Integer doInBackground(String... str_param){
            String RestURL = str_param[0];
            String method = str_param[1];
            try{
                return WebAPI.sendRefreshToken(RestURL, method);
            }
            catch (Exception ex){
                Log.e(TAG, ex.toString());
            }
            return -1;
        }
        protected void onProgressUpdate(Void... progress){}
        protected void onPostExecute(Integer result){
            actionProgressDialog.cancel();
            if(result != HttpURLConnection.HTTP_RESET && result != HttpURLConnection.HTTP_NO_CONTENT){
                Snackbar snackbar = Snackbar.make(findViewById(R.id.logoutButton), "Įvyko klaida",
                        3000);
                snackbar.show();
            }
            else{
                logout();
            }
        }
    }
    private void logout(){
        Intent logoutIntent = new Intent(this, LoginActivity.class);
        logoutIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        logoutIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        logoutIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        stopService(new Intent(AccountActivity.this, TokenService.class));
        TokenPair.wipeData();
        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.token_file), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getString(R.string.refresh_token), TokenPair.getRefreshToken());
        editor.commit();
        startActivity(logoutIntent);
    }
    private  void showUser(){
        TextView textView = findViewById(R.id.accountNameText);
        textView.setText(Tools.user.first_name+" "+Tools.user.last_name);
    }
}
