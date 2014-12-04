package com.agctonline.snapchirp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;


public class FriendProfile extends Activity {
    private String mFriendID;
    private ParseUser mProfile;
    private TextView mFirstName;
    private TextView mLastName;
    private TextView mHometown;
    private TextView mWebAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_profile);

        mFirstName= (TextView) findViewById(R.id.firstName);
        mLastName = (TextView) findViewById(R.id.lastName);
        mHometown= (TextView) findViewById(R.id.homeTown);
        mWebAddress= (TextView) findViewById(R.id.webAddress);


        Intent intentFromFriendsList = getIntent();
        //do a ctrl+q to get the purpose of getIntent method.
        //it say "Return the intent that started this activity."
        //the line above is basically grabbing the object and params
        //that was passed from the mainActivity

        mFriendID = intentFromFriendsList.getStringExtra(ParseConstants.KEY_FRIEND_ID);
        //use the getstringextra method to extra the value using the key
        //Toast.makeText(this, mFriendID, Toast.LENGTH_LONG).show();


        //mProfile = ParseUser.getQuery();
        ParseQuery<ParseObject> query = ParseQuery.getQuery("_User");

        query.getInBackground(mFriendID, new GetCallback<ParseObject>() {
            public void done(ParseObject friendProfile, ParseException e) {
                if (e == null) {
                    // success
                    //Toast.makeText(FriendProfile.this, friendProfile.getObjectId(), Toast.LENGTH_LONG).show();
                    mFirstName.setText((CharSequence) friendProfile.getString(ParseConstants.KEY_FIRST_NAME));
                    mLastName.setText((CharSequence) friendProfile.getString(ParseConstants.KEY_LAST_NAME));
                    mHometown.setText((CharSequence) friendProfile.getString(ParseConstants.KEY_HOME_TOWN));
                    mWebAddress.setText((CharSequence) friendProfile.getString(ParseConstants.KEY_WEB_ADDRESS));


                } else {
                    // something went wrong
                    Toast.makeText(FriendProfile.this, e.getMessage()+ " No Friend profile object found", Toast.LENGTH_LONG).show();
                }
            }

        });

    }



}
