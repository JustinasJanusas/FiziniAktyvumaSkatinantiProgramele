package com.example.ejunasapp;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Button;
import org.w3c.dom.Text;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.zip.Inflater;
public class TaskDetailedActivity extends Activity {

    private String TAG = "TaskDetailedActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detailed_taskinfo_window);

        if (getIntent().getExtras() != null) {

            Task task = (Task) getIntent().getSerializableExtra("task");
            TextView TextName = findViewById(R.id.nameText);
            TextName.setText(task.name);

            TextView Level = findViewById(R.id.levelText);
            Level.setText(task.level.name);

            TextView Type = findViewById(R.id.typeText);
            Type.setText(task.type.name);

            TextView Category = findViewById(R.id.categoryText);
            Category.setText(task.category.name);

            TextView Author = findViewById(R.id.authorText);
            Author.setText(task.author);

            TextView TaskText = findViewById(R.id.tasktext);
            TaskText.setText(task.text);

            ImageView imageView = findViewById(R.id.taskImage);
            byte[] imageBytes = Base64.getDecoder().decode(task.base64_image);
            Bitmap image = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            imageView.setImageBitmap(image);

        }
        Button buttonReturn = (Button) findViewById(R.id.btn1);
        buttonReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent returnButton = new Intent(TaskDetailedActivity.this, MainActivity.class);
                returnButton.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(returnButton);
            }
        });
        }
    }



