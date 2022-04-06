package com.example.ejunasapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.android.material.snackbar.Snackbar;
public class RegistrationActivity extends Activity {

        private String TAG = "RegistrationActivity";

        @Override
        protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.registration_activity);


                Button backButton = findViewById(R.id.btn1);
                Button registerButton = findViewById(R.id.done);
                registerButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                                onRegisterClick();
                        }
                });
                backButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                                Intent myIntent = new Intent(RegistrationActivity.this, LoginActivity.class);
                                startActivity(myIntent);
                                finish();
                        }
                });
        }

        //------------------------------------------------------------------------
        private void onRegisterClick() {
                String username = ((EditText) findViewById(R.id.username1)).getText().toString();
                String name = ((EditText) findViewById(R.id.registrationName)).getText().toString();
                String surname = ((EditText) findViewById(R.id.registrationSurname)).getText().toString();
                String email = ((EditText) findViewById(R.id.email)).getText().toString().trim();
                String pass = ((EditText) findViewById(R.id.password)).getText().toString().trim();
                String repeatPass = ((EditText) findViewById(R.id.repeatPass)).getText().toString().trim();

                boolean validEmail=isValidEmail(email);
                boolean validPass=ValidatePassword(pass, repeatPass);
                boolean ValidName=ValidNameSurname(name);
                boolean ValidSurname=ValidNameSurname(surname);

                if (!username.isEmpty() && !name.isEmpty() && !surname.isEmpty() && !email.isEmpty()
                        && !pass.isEmpty() && !repeatPass.isEmpty() && validEmail && validPass
                && ValidName && ValidSurname) {
                        new tryRegister().execute(Tools.RestURL + "auth/register",
                                username, email, pass, repeatPass, name, surname);
                }
                else if ((!username.isEmpty() && !name.isEmpty()
                        && !surname.isEmpty() && !email.isEmpty()
                        && !pass.isEmpty() && !repeatPass.isEmpty() && (!ValidName || !ValidSurname)) ||
                        (!username.isEmpty() && !name.isEmpty()
                        && !surname.isEmpty() && !email.isEmpty()
                        && !pass.isEmpty() && !repeatPass.isEmpty()
                        && !validEmail) ||
                        (!username.isEmpty() && !name.isEmpty()
                        && !surname.isEmpty() && !email.isEmpty()
                                && !pass.isEmpty() && !repeatPass.isEmpty() && !validPass))
                {

                }
                else showMessage("Įveskite visus registracijos duomenis");
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
                                }
                                else showMessage("Klaida!");

                        }
                        catch (Exception ex) {
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
                        } else  {
                                showMessage("Registracija sėkminga, prisijunkite.");
                        }
                }
        }

        private void showMessage(String msg) {

                Snackbar snack = Snackbar.make(findViewById(R.id.registrationS), msg, 3000);
                View view = snack.getView();
                FrameLayout.LayoutParams params =(FrameLayout.LayoutParams)view.getLayoutParams();
                params.gravity = Gravity.TOP;
                view.setLayoutParams(params);
                snack.show();
        }

        private void doRegister() {
                Intent myIntent = new Intent(RegistrationActivity.this, LoginActivity.class);
                startActivity(myIntent);
                finish();
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
        public boolean ValidatePassword(String pass, String repeatpass)
        {
                if (pass.isEmpty() || repeatpass.isEmpty()) {
                        showMessage("Neįvesti slaptažodžiai");
                        return false;
                }  if (pass.length()<8) {
                showMessage("Slaptažodį turi sudaryti daugiau nei 8 simboliai");
                return false;
                }

                if (!pass.equals(repeatpass)) {
                        showMessage("Įvesti slaptažodžiai nesutampa");
                        return false;
                }else {
                        return true;

                }
        }
        public boolean ValidNameSurname(String nameSurname)
        {
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
        //---------------------------------------------------------------------------
        //GALBŪT REIKĖS---------------------------PATIKRINIMUI DĖL VIENODŲ USERNAMŲ IR EL PAŠTŲ
        //nes jei yra vienodų su DB neduoda prireginti, reikia padaryt patikrinima ir parodyt useriui
       /* static public List<User> usersList;
        private void getUsers() {new getUsersUser().execute(Tools.RestURL+"auth/user");}
        private class getUsersUser extends  AsyncTask<String, Void, List<User>> {
                ProgressDialog actionProgressDialog =
                        new ProgressDialog(RegistrationActivity.this);

                protected List<User> doInBackground(String... str_param) {
                        String RestURL = str_param[0];
                        List<User> data = null;
                        try {
                                data = DataAPI.jsonToUsers(RestURL);
                                Log.e("data", ""+data);
                        } catch (Exception ex) {
                                Log.e(TAG, ex.toString());
                        }
                        return data;
                }

                protected void onProgressUpdate(Void... progress) {
                }

                protected void onPostExecute(List<User> result) {
                        actionProgressDialog.cancel();

                        if (result != null) {
                                usersList = result;
                                Log.e("userList", ""+usersList);
                        }
                }
        }

                /*private class CustomAdapter extends BaseAdapter {

                        private ArrayList<User> singleRow;
                        private LayoutInflater thisInflater;

                        public CustomAdapter(Context context, ArrayList<User> aRow) {

                                this.singleRow = aRow;
                                thisInflater = (LayoutInflater.from(context));

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
                                      /*  convertView = thisInflater.inflate( R.layout.task_row, parent, false );
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
                                        imageView.setImageBitmap(image);*/
                               /* }
                                return convertView;
                        } */



}