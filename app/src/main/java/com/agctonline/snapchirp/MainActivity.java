package com.agctonline.snapchirp;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class MainActivity extends FragmentActivity implements ActionBar.TabListener {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v13.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;
    public static final String TAG = MainActivity.class.getSimpleName();
    public static final int TAKE_PHOTO_REQUEST = 0;
    public static final int TAKE_VIDEO_REQUEST = 1;
    public static final int CHOOSE_PHOTO_REQUEST = 2;
    public static final int CHOOSE_VIDEO_REQUEST = 3;

    public static final int MEDIA_TYPE_IMAGE = 4;
    public static final int MEDIA_TYPE_VIDEO = 5;

    protected Uri mMediaUri; //Uri is different than the URI version
    public  static final int FILE_SIZE_LIMIT = 1024*1024*10;//10mb


    //create dialog listener
    protected DialogInterface.OnClickListener mDialogListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case 0:
                    Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                    mMediaUri = getExternalMediaDirs(MEDIA_TYPE_IMAGE);
                    if(mMediaUri==null){

                        //error
                        Toast.makeText(MainActivity.this, "get media file error", Toast.LENGTH_LONG).show();
                    }
                    else {
                        Toast.makeText(MainActivity.this, "media storage is avail", Toast.LENGTH_LONG).show();
                        takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, mMediaUri);
                        startActivityForResult(takePhotoIntent, TAKE_PHOTO_REQUEST); //startactivityforresult will return back to the app after the camera intent is done.
                    }
                    break;
                case 1:
                    Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                    mMediaUri = getExternalMediaDirs(MEDIA_TYPE_VIDEO);
                    if(mMediaUri==null){

                        //error
                        Toast.makeText(MainActivity.this, "get video media file error", Toast.LENGTH_LONG).show();
                    }
                    else {
                        Toast.makeText(MainActivity.this, "video media storage is avail", Toast.LENGTH_LONG).show();
                        takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, mMediaUri);
                        takeVideoIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT,10);
                        takeVideoIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY,0);
                        startActivityForResult(takeVideoIntent, TAKE_VIDEO_REQUEST); //startactivityforresult will return back to the app after the camera intent is done.
                    }

                    break;

                case 2:
                    Intent choosePhotoIntent = new Intent(Intent.ACTION_GET_CONTENT);
                    choosePhotoIntent.setType("image/*");
                    startActivityForResult(choosePhotoIntent,CHOOSE_PHOTO_REQUEST);
                    break;
                case 3:
                    Intent chooseVideoIntent = new Intent(Intent.ACTION_GET_CONTENT);
                    chooseVideoIntent.setType("video/*");
                    Toast.makeText(MainActivity.this, "video must be less than 10mb size", Toast.LENGTH_LONG).show();
                    startActivityForResult(chooseVideoIntent, CHOOSE_VIDEO_REQUEST);
                    break;
            }

        }

        private Uri getExternalMediaDirs(int mediaType) {
            if (isExternalStorageAvailable()){
                //get URI

                //4. return the file's uri
                //1. get external storage dir
                File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),"SnapChirp");

                //2. create own sub dir
                if(!mediaStorageDir.exists()){
                    if(!mediaStorageDir.mkdir()){
                        Log.e(TAG, "Failed to create dir");
                        return null;
                    }
                }

                //3. create file name & create the file
                File mediaFile;
                Date now = new Date();
                String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US ).format(now);
                String path = mediaStorageDir.getPath() + File.separator;
                if(mediaType == MEDIA_TYPE_IMAGE){
                    mediaFile = new File(path + "IMG_" + timestamp + ".jpg");
                }
                else if (mediaType == MEDIA_TYPE_VIDEO){
                    mediaFile = new File(path + "VID_" + timestamp + ".mp4");
                }
                else {
                    return null;
                }

                Log.d(TAG, "File: "+ Uri.fromFile(mediaFile));
                //4. Return file's URI
                return Uri.fromFile(mediaFile);
            }
            else {
                Toast.makeText(MainActivity.this, "isExternal avail", Toast.LENGTH_LONG).show();
                return null;
            }


        }
        private boolean isExternalStorageAvailable () {
            String state = Environment.getExternalStorageState();
            if(state.equals(Environment.MEDIA_MOUNTED)){
                return true;
            }
            else {
                Toast.makeText(MainActivity.this, "isExternal not avail", Toast.LENGTH_LONG).show();
                return false;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_main);

        ParseAnalytics.trackAppOpened(getIntent());//from parse

        ParseUser currentUser = ParseUser.getCurrentUser();//Parse code

        if (currentUser == null) {


            Intent loginIntent = new Intent(this, LoginActivity.class);

            loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            //New_Task declares that login Activity is a new task
            //Clear_task clears the Main activity task
            //the goal of this is to not have the back button redirect to the MainActivity

            startActivity(loginIntent);
        }
        else {

            Log.i(TAG, currentUser.getUsername());
        }
        // Set up the action bar.
        final ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager()); // Added context as 1st param to match the constructor in the SectionsPagerAdapter refactored class. Also, must use getSupportFragmentManager instead of getFragmentmanager

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }
    }

    @Override
    //Saving a photo to gallery
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK) {
            Toast.makeText(this,"Saving to gallery",Toast.LENGTH_LONG).show();
            //success
            if (requestCode == CHOOSE_PHOTO_REQUEST || requestCode == CHOOSE_VIDEO_REQUEST){
                if(data == null){
                    Toast.makeText(this,"no vid or pic chosen",Toast.LENGTH_LONG).show();
                }
                else{
                    mMediaUri=data.getData();
                }
                Log.i(TAG, "Media URI "+mMediaUri);
                if(requestCode==CHOOSE_VIDEO_REQUEST){
                    //make sure file is less than 10mb
                    int fileSize = 0;
                    InputStream inputStream = null;
                    try {
                        inputStream = getContentResolver().openInputStream(mMediaUri);
                        fileSize=inputStream.available();
                    } catch (FileNotFoundException e) {
                        Toast.makeText(this,"no video file found " + e, Toast.LENGTH_LONG).show();
                        return;
                    } catch (IOException e) {
                        Toast.makeText(this,"IO problem " + e, Toast.LENGTH_LONG).show();
                        return;
                    }
                    finally {
                        try {
                            inputStream.close();
                        } catch (IOException e) {
                            //nothing to do here
                        }
                    }

                    if (fileSize >= FILE_SIZE_LIMIT){
                        Toast.makeText(this,"File size is too larger", Toast.LENGTH_LONG).show();
                        return;
                    }


                }


            }
            else {
                //To add pic to gallery
                //setup new intent to scan for new action
                //set the uri for the intent
                //broadcast the intent, gallery will automatically pick up the uri
                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                mediaScanIntent.setData(mMediaUri);
                sendBroadcast(mediaScanIntent);
            }

            Intent recipientsIntent = new Intent(this, RecipientsActivity.class);
            recipientsIntent.setData(mMediaUri);//attach file Uri to intent
            String fileType;
            if(requestCode==CHOOSE_PHOTO_REQUEST || requestCode == TAKE_PHOTO_REQUEST)
            {
                fileType = "photo";
            }
            else
            {
                fileType = "video";
            }
            recipientsIntent.putExtra(ParseConstants.KEY_FILE_TYPE,fileType);//attach file type to intent
            startActivity(recipientsIntent);
        }
        else if(resultCode != RESULT_CANCELED){
            Toast.makeText(this,R.string.save_gallery_error,Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        /*if (id == R.id.action_logout) {
            ParseUser.logOut();
            Intent loginIntent = new Intent(this, LoginActivity.class);

            loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            //New_Task declares that login Activity is a new task
            //Clear_task clears the Main activity task
            //the goal of this is to not have the back button redirect to the MainActivity

            startActivity(loginIntent);
            //can refactor this redirect code block, but leave it here for clarity

        }
        else if (id == R.id.action_edit_friends) {
            Intent editFriendsIntent = new Intent(this, EditFriendsActivity.class);

            startActivity(editFriendsIntent);
        }
*/
        //use switch as alternative to if else
        switch (id) {
            case R.id.action_logout:
                ParseUser.logOut();
                Intent loginIntent = new Intent(this, LoginActivity.class);
                loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(loginIntent);
                break;
            case R.id.action_edit_friends:
                Intent editFriendsIntent = new Intent(this, EditFriendsActivity.class);
                startActivity(editFriendsIntent);
                break;
            case R.id.action_camera:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setItems(R.array.camera_choices, mDialogListener); //the second param is the action when item is clicked -- null = nothing happens, or attach a dialog listener as seen. Listener is defined up top
                AlertDialog dialog = builder.create();
                dialog.show();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    /*public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            //return PlaceholderFragment.newInstance(position + 1);

             return new FriendsFragment();


        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_section1).toUpperCase(l);
                case 1:
                    return getString(R.string.title_section2).toUpperCase(l);
                case 2:
                    return getString(R.string.title_section3).toUpperCase(l);
            }
            return null;
        }
    }*/

    /**
     * A placeholder fragment containing a simple view.
     */
    /*public static class PlaceholderFragment extends Fragment {
        *//**
         * The fragment argument representing the section number for this
         * fragment.
         *//*
        private static final String ARG_SECTION_NUMBER = "section_number";

        *//**
         * Returns a new instance of this fragment for the given section
         * number.
         *//*
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putString(ARG_SECTION_NUMBER, sectionNumber + " is fragment number");
            args.putInt("page_tab", sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            int page = getArguments().getInt("page_tab");
            View rootView;
            if (page == 1){

            rootView = inflater.inflate(R.layout.fragment_inbox, container, false);
            }
            else {
                rootView = inflater.inflate(R.layout.fragment_friends, container, false);
            }


            //TextView sectionNumText = (TextView) rootView.findViewById(R.id.section_label);
            //sectionNumText.setText(Integer.toString(getArguments().getInt(ARG_SECTION_NUMBER)));
            //sectionNumText.setText((getArguments().getString(ARG_SECTION_NUMBER)));
            return rootView;
        }
    }*/

}
