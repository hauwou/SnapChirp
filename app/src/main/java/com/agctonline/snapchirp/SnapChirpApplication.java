package com.agctonline.snapchirp;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseObject;

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
    }


}
