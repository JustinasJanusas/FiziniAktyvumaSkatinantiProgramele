package com.example.ejunasapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;

import com.google.android.material.snackbar.Snackbar;

public class RemindPasswordActivity extends Activity {
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
            //new LoginActivity.tryRemind().execute(Tools.RestURL+"auth/login", name, pass);
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
}

