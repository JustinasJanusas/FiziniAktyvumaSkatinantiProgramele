package com.example.ejunasapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.snackbar.Snackbar;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.nio.ByteBuffer;
import java.util.Base64;
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
                        .setMessage("Ar tikrai norite ištrinti paskyrą?")

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
    private class tryChangePicture extends AsyncTask<String, Void, Boolean> {

        ProgressDialog actionProgressDialog =
                new ProgressDialog(AccountActivity.this);
        String picture;
        @Override
        protected void onPreExecute(){
            actionProgressDialog.setMessage("Siunčiama...");
            actionProgressDialog.show();
            actionProgressDialog.setCancelable(false);
            super.onPreExecute();
        }

        protected Boolean doInBackground(String... str_param){
            String RestURL = str_param[0];
             picture = str_param[1];
            try{
                return WebAPI.changePicture(RestURL, picture);
            }
            catch (Exception ex){
                Log.e(TAG, ex.toString());
            }
            return false;
        }
        protected void onProgressUpdate(Void... progress){}
        protected void onPostExecute(Boolean result){
            actionProgressDialog.cancel();
            if(result){
                byte[] imageBytes = Base64.getDecoder().decode(picture);
                ShapeableImageView sImage = findViewById(R.id.accountImage);
                sImage.setImageBitmap( BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length));
                Tools.user.base64_picture = picture;
                Tools.userUpdated = true;
            }
            else{
                Snackbar snackbar = Snackbar.make(findViewById(R.id.logoutButton), "Įvyko klaida",
                        3000);
                snackbar.show();
            }
        }
    }
    private void logout(){
        Intent logoutIntent = new Intent(this, LoginActivity.class);
        Tools.user = null;
        MainActivity.otherTaskList = null;
        MainActivity.favTaskList = null;
        MainActivity.doneTaskList = null;
        MainActivity.changed = false;
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

        finishAffinity();
    }
    private  void showUser(){
        TextView textView = findViewById(R.id.accountNameText);
        textView.setText(Tools.user.user.first_name+" "+Tools.user.user.last_name);
        ShapeableImageView shapeableImageView = findViewById(R.id.accountImage);
        if(Tools.user.base64_picture != null && Tools.user.base64_picture != "") {

            byte[] imageBytes = Base64.getDecoder().decode(Tools.user.base64_picture);
            shapeableImageView.setImageBitmap( BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length));
        }
        shapeableImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(AccountActivity.this)
                        .setTitle("")
                        .setMessage("Ar norite pakeisti paskyros nuotrauką?")
                        .setNegativeButton(R.string.cancel, null)
                        .setPositiveButton(R.string.change, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                startActivityForResult(pickPhoto , 1);
                            }}).show();

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case 1:
                if(resultCode == RESULT_OK){
                    Uri selectedImage = data.getData();
                    try {
                        InputStream stream = getContentResolver().openInputStream(selectedImage);
                        ShapeableImageView imageView = findViewById(R.id.accountImage);
                        byte[] b = getBytes(stream);
                        String base64Image = Base64.getEncoder().encodeToString(b);
                        new tryChangePicture().execute(Tools.RestURL+"auth/photo", base64Image);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
                break;
            default:
                return;
        }
    }
    public byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }
}
