package com.example.ejunasapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.imageview.ShapeableImageView;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class FriendActivity extends AppCompatActivity {
    private String TAG = "FriendActivity";
    private List<User> FriendList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friends_activity);
        ImageButton buttonBack = findViewById(R.id.friendBackButton);
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        new getFriends().execute(Tools.RestURL+"auth/friends");



    }
    private class getFriends extends AsyncTask<String, Void, List<User>> {

        ProgressDialog actionProgressDialog =
                new ProgressDialog(FriendActivity.this);
        int type = -1;
        @Override
        protected void onPreExecute(){
            actionProgressDialog.setMessage("Gaunami duomenys...");
            actionProgressDialog.show();
            actionProgressDialog.setCancelable(false);
            super.onPreExecute();
        }

        protected List<User> doInBackground(String... str_param){

            String RestURL = str_param[0];
            List<User> data = null;
            try{
                java.lang.reflect.Type type =
                        new com.google.gson.reflect.TypeToken<List<User>>()
                        {}.getType();
                data = (List<User>) DataAPI.jsonToData(RestURL, type);

            }
            catch (Exception ex){ }

            return data;
        }
        protected void onProgressUpdate(Void... progress){}
        protected void onPostExecute(List<User> result){

            super.onPostExecute(result);
            actionProgressDialog.cancel();
            result.removeIf(n -> (n.id == 1));
            FriendList = result;
            if(FriendList != null) {
                showUsers();
            }
        }
    }
    private void showUsers(){
        int id = -1;
        CustomAdapter listAdapter;
        listAdapter = new CustomAdapter(getApplicationContext(), (ArrayList) FriendList, R.layout.friend_row);
        ListView friendListView = findViewById(R.id.friendsListView);
        friendListView.setAdapter(listAdapter);
        friendListView.setClickable(false);
    }
    private class CustomAdapter extends BaseAdapter {

        private ArrayList<User> singleRow;
        private LayoutInflater thisInflater;
        int rowID = -1;
        int count = -1;
        public CustomAdapter(Context context, ArrayList<User> aRow, int id) {

            this.singleRow = aRow;
            thisInflater = ( LayoutInflater.from(context) );
            rowID = id;
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
                convertView = thisInflater.inflate( rowID, parent, false );
                convertView.setClickable(false);
                TextView nameText = convertView.findViewById(R.id.userRowNameText);
                TextView scoreText = convertView.findViewById(R.id.userScoreText);
                TextView rowNumber = convertView.findViewById(R.id.rowNumber);

                rowNumber.setText((position+1)+"");
                ShapeableImageView imageView = convertView.findViewById(R.id.rowAccountImage);
                User currentRow = (User) getItem(position);
                nameText.setText(currentRow.user.first_name+ " "+currentRow.user.last_name);
                scoreText.setText(currentRow.points+"");
                if(currentRow.base64_picture != null && currentRow.base64_picture != "") {

                    byte[] imageBytes = Base64.getDecoder().decode(currentRow.base64_picture);
                    imageView.setImageBitmap( BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length));
                }

            }
            return convertView;
        }

    }
}

