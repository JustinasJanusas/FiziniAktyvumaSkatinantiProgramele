package com.example.ejunasapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
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


public class LeaderboardActivity extends AppCompatActivity {
    private String TAG = "LeaderboardActivity";
    private List<User> leaderboardList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.leaderboard_activity);
        ImageButton buttonBack = findViewById(R.id.leaderboardBackButton);
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        new getLeaderboard().execute(Tools.RestURL+"auth/leaderboard");

    }
    private class getLeaderboard extends AsyncTask<String, Void, List<User>> {

        ProgressDialog actionProgressDialog =
                new ProgressDialog(LeaderboardActivity.this);
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
            result.removeIf(n -> ((n.user.first_name+n.user.last_name).trim().equals("")));
            leaderboardList = result;
            if(leaderboardList != null) {
                showUsers();
            }
        }
    }
    private void showUsers(){
        int id = -1;
        CustomAdapter listAdapter;
        listAdapter = new CustomAdapter(getApplicationContext(), (ArrayList) leaderboardList, R.layout.user_row);
        ListView userListView = findViewById(R.id.leaderListView);
        userListView.setAdapter(listAdapter);
        userListView.setClickable(false);
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
            View row = null;
            if (convertView == null) {
                row = thisInflater.inflate( rowID, parent, false );

            }
            else
                row =convertView;
            row.setClickable(false);
            TextView nameText = row.findViewById(R.id.userRowNameText);
            TextView scoreText = row.findViewById(R.id.userScoreText);
            TextView rowNumber = row.findViewById(R.id.rowNumber);
            rowNumber.setText((position+1)+"");
            ShapeableImageView imageView = row.findViewById(R.id.rowAccountImage);
            User currentRow = (User) getItem(position);
            nameText.setText(currentRow.user.first_name+ " "+currentRow.user.last_name);
            scoreText.setText(currentRow.points+"");
            if(currentRow.base64_picture != null && currentRow.base64_picture != "") {

                byte[] imageBytes = Base64.getDecoder().decode(currentRow.base64_picture);
                imageView.setImageBitmap( BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length));
            }
            else
                imageView.setImageDrawable(getDrawable(R.drawable.no_profile_picture));
            if(currentRow.id == Tools.user.id){
                row.findViewById(R.id.userRow).setBackgroundColor(getColor(R.color.yellow));
                //convertView.setFocusedByDefault(true);
            }
            else
                row.findViewById(R.id.userRow).setBackgroundColor(Color.WHITE);
            return row;
        }
    }
}
