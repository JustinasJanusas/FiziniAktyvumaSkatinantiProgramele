package com.example.ejunasapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Base64;

public class RegistrationActivity extends Activity{

private String TAG="RegistrationActivity";

@Override
protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration_activity);
        Button backButton = findViewById(R.id.btn1);
        backButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                        Intent myIntent = new Intent(RegistrationActivity.this, LoginActivity.class);
                        startActivity(myIntent);
                        finish();
                }
        });
        }
}
