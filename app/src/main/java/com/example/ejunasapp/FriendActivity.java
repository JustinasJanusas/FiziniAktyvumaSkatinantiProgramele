package com.example.ejunasapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class FriendActivity extends AppCompatActivity {
    private String TAG = "FriendActivity";
    private List<User> FriendList;
    private List<User> requestList;
    private BottomNavigationView bottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friends_activity);
        bottomNavigationView = findViewById(R.id.friend_bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_friends:
                        new getFriends().execute(Tools.RestURL+"auth/friends", "0");
                        return true;

                    case R.id.navigation_requests:
                        new getFriends().execute(Tools.RestURL+"auth/friends/pending", "1");
                        return true;

                }
                return true;
            }
        });
        bottomNavigationView.setSelectedItemId(R.id.navigation_friends);

        ImageButton buttonBack = findViewById(R.id.friendBackButton);
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ImageButton addFriendButton = findViewById(R.id.addFriendButton);
        addFriendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(FriendActivity.this);
                builder.setTitle("Pridėti draugą");
                EditText input = new EditText(FriendActivity.this);

                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);

                builder.setPositiveButton("Pridėti", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(!input.getText().toString().trim().equals("")) {
                            new getFriends().execute(Tools.RestURL + "auth/users?search=" + input.getText(), "2");
                        }
                    }
                });
                builder.setNegativeButton("Atšaukti", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
            }
        });


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
            type = Integer.parseInt(str_param[1]);
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

            if(result != null) {
                if(type == 0) {
                    FriendList = result;
                    showUsers(FriendList, R.layout.friend_row, 0);
                }
                else if (type == 1){
                    requestList = result;
                    showUsers(requestList, R.layout.request_row, 1);
                }
                else if(type == 2){
                    Dialog dialog = new Dialog(FriendActivity.this);
                    dialog.setContentView(R.layout.popup_listview_layout);
                    dialog.setTitle("Pasirinkite draugą");
                    CustomAdapter listAdapter;
                    listAdapter = new CustomAdapter(getApplicationContext(), (ArrayList) result, R.layout.popup_row, 2);
                    ListView friendListView = dialog.findViewById(R.id.popupListView);
                    friendListView.setAdapter(listAdapter);
                    Button cancelButton = dialog.findViewById(R.id.cancelButton);
                    cancelButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.cancel();
                        }
                    });
                    friendListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Object o = friendListView.getItemAtPosition(position);
                            User u = (User) o;
                            new AddFriend().execute(Tools.RestURL+"auth/user/"+u.id, "POST", "0");
                            dialog.cancel();
                        }
                    });
                    dialog.show();
                }
            }
        }
    }
    private class AddFriend extends AsyncTask<String, Void, Boolean> {

        ProgressDialog actionProgressDialog =
                new ProgressDialog(FriendActivity.this);
        int type;
        @Override
        protected void onPreExecute(){
            actionProgressDialog.setMessage("Gaunami duomenys...");
            actionProgressDialog.show();
            actionProgressDialog.setCancelable(false);
            super.onPreExecute();
        }

        protected Boolean doInBackground(String... str_param){

            String RestURL = str_param[0];
            String method = str_param[1];
            type = Integer.parseInt(str_param[2]);
            try{
                return WebAPI.changeState(RestURL, method);

            }
            catch (Exception ex){ return null; }

        }
        protected void onProgressUpdate(Void... progress){}
        protected void onPostExecute(Boolean result){

            super.onPostExecute(result);
            actionProgressDialog.cancel();

            if(result != null) {
                if(type == 0) {
                    if(result){
                        Snackbar mySnackbar = Snackbar.make(findViewById(R.id.friendsListView),
                                "Kvietimas draugauti išsiųstas", 5000);
                        mySnackbar.show();
                    }
                    else{
                        Snackbar mySnackbar = Snackbar.make(findViewById(R.id.friendsListView),
                                "Tokio naudotojo nėra arba naudotojas jau yra jūsų draugų sąraše", 5000);
                        mySnackbar.show();
                    }
                }
                else if (type == 1){
                    bottomNavigationView.setSelectedItemId(R.id.navigation_requests);
                }
                else if(type == 2){
                    bottomNavigationView.setSelectedItemId(R.id.navigation_friends);
                }
            }
        }
    }
    private void showUsers(List<User> userList, int rowId, int type){
        CustomAdapter listAdapter;
        listAdapter = new CustomAdapter(getApplicationContext(), (ArrayList) userList, rowId, type);
        ListView friendListView = findViewById(R.id.friendsListView);
        friendListView.setAdapter(listAdapter);
       // friendListView.setClickable(false);

    }

    private class CustomAdapter extends BaseAdapter {

        private ArrayList<User> singleRow;
        private LayoutInflater thisInflater;
        int rowID = -1;
        int count = -1;
        int type = -1;
        public CustomAdapter(Context context, ArrayList<User> aRow, int id, int t) {

            this.singleRow = aRow;
            thisInflater = ( LayoutInflater.from(context) );
            rowID = id;
            type = t;
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
                //convertView.setClickable(false);
                TextView nameText = convertView.findViewById(R.id.userRowNameText);
                ShapeableImageView imageView = convertView.findViewById(R.id.rowAccountImage);
                User currentRow = (User) getItem(position);
                nameText.setText(currentRow.user.first_name+ " "+currentRow.user.last_name);
                if(currentRow.base64_picture != null && currentRow.base64_picture != "") {

                    byte[] imageBytes = Base64.getDecoder().decode(currentRow.base64_picture);
                    imageView.setImageBitmap( BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length));
                }
                if(type == 2){
                    TextView usernameText = convertView.findViewById(R.id.usernameText);
                    usernameText.setText(currentRow.user.username);
                    return convertView;
                }
                TextView rowNumber = convertView.findViewById(R.id.rowNumber);
                TextView scoreText = convertView.findViewById(R.id.userScoreText);
                rowNumber.setText((position+1)+"");
                scoreText.setText(currentRow.points+"");

                int userId = currentRow.id;
                if(type == 0) {
                    Button buttonDeletefriend = (Button) convertView.findViewById(R.id.deleteFriendButton);

                    buttonDeletefriend.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            new AlertDialog.Builder(FriendActivity.this)
                                    .setTitle("")
                                    .setMessage("Ar tikrai norite pašalinti draugą?")
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .setNegativeButton(R.string.cancel, null)
                                    .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {

                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            new AddFriend().execute(Tools.RestURL+"auth/user/"+userId, "DELETE", "2");
                                        }
                                    }).show();
                        }
                    });
                }
                else if(type == 1){
                    Button buttonAccept = (Button) convertView.findViewById(R.id.acceptFriendButton);
                    Button buttonDeny = (Button) convertView.findViewById(R.id.denyFriendButton);
                    buttonAccept.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            new AddFriend().execute(Tools.RestURL+"auth/user/"+userId, "POST", "1");
                        }
                    });
                    buttonDeny.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            new AddFriend().execute(Tools.RestURL+"auth/user/"+userId, "DELETE", "1");
                        }
                    });
                }

            }
            return convertView;
        }

    }
}