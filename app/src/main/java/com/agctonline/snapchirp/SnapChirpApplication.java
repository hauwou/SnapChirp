package com.agctonline.snapchirp;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePushBroadcastReceiver;
import com.parse.ParseUser;
import com.parse.PushService;



/**
 * Created by admin on 11/25/2014.
 */
public class SnapChirpApplication extends Application{


    @Override
    public void onCreate() {
        super.onCreate();
        Parse.initialize(this, "d6WxO9g2PbiEyJP7ivIJPeAvPohkrlWz6hG4rC7a", "RZ70XklmneU7MITnSXrLKxN0zkZwqzSJBkCGWXW3");

        /*ParseObject testObject = new ParseObject("TestObject1");
        testObject.put("yoo", "test");
        testObject.saveInBackground();*/ //parse example code

        PushService.setDefaultPushCallback(this, MainActivity.class);//This redirect parse notification to specific activity
        ParseInstallation.getCurrentInstallation().saveInBackground();//This gets device info and save in parse database for future device filter.
    }


    public static void updateParseInstallation(ParseUser user) {
        //This method creates a userId field, and save the objectId into the userId field for each installation.
        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
        installation.put(ParseConstants.KEY_USER_ID, user.getObjectId());
        installation.saveInBackground();
    }
}
