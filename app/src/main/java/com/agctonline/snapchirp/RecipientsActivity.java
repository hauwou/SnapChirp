package com.agctonline.snapchirp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;


public class RecipientsActivity extends ListActivity {

    public final static String TAG = RecipientsActivity.class.getSimpleName();
    protected List<ParseUser> mFriends;
    protected ParseRelation<ParseUser> mFriendsRelation;
    protected ParseUser mCurrentUser;
    protected String[] mObjectIDs;
    protected MenuItem mSendMenuItem;
    protected Uri mMediaUri;
    protected String mFileType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_recipients);
        getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        mMediaUri = getIntent().getData();
        mFileType = getIntent().getExtras().getString(ParseConstants.KEY_FILE_TYPE);
    }

    @Override
    //reuse codes from editFriend Activity
    public void onResume() {
        super.onResume();

        mCurrentUser = ParseUser.getCurrentUser();//get current user
        mFriendsRelation = mCurrentUser.getRelation(ParseConstants.KEY_FRIENDS_RELATION);//getRelation() is a parse function, and parse handles the logic of friend relationships in the backend
        setProgressBarIndeterminateVisibility(true);//because setProgressbar is an activity method, must call getActivity first and then chain on the setProgressBar

        ParseQuery<ParseUser> query = mFriendsRelation.getQuery();
        query.addAscendingOrder(ParseConstants.KEY_USERNAME);
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> friends, ParseException e) {
                setProgressBarIndeterminateVisibility(false);
                if (e == null) {
                    mFriends = friends;
                    String[] friendNames = new String[mFriends.size()];
                    mObjectIDs = new String[mFriends.size()];
                    int i = 0;
                    //java's foreach syntax
                    for (ParseUser friend : mFriends) {
                        friendNames[i] = friend.getUsername();
                        mObjectIDs[i] = friend.getObjectId();
                        i++;
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getListView().getContext(), android.R.layout.simple_list_item_checked, friendNames);
                    //must use getListView().getContext() to get to the context instead of using FriendsFragment.this, because this is inside a fragment class and not a parent activity class

                    setListAdapter(adapter);


                    //onListItemClick(getListView(),getView(), getSelectedItemPosition(), getSelectedItemId()); //use Control + Space to fill in the params

                } else {

                    Log.e(TAG, e.getMessage());
                    AlertDialog.Builder builder = new AlertDialog.Builder(RecipientsActivity.this);
                    builder.setMessage(e.getMessage())
                            .setTitle(R.string.error_title)
                            .setPositiveButton(android.R.string.ok, null);//must provide second param

                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }


        });
    }

    @Override
    //This code block listens for checked friends, if 1 friend is checked, show the send Icon
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        if(l.getCheckedItemCount() > 0){
            mSendMenuItem.setVisible(true);
        }
        else {
            mSendMenuItem.setVisible(false);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_recipients, menu);
        mSendMenuItem = menu.getItem(0);//get the Send icon
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_send) {
            ParseObject message = createMessage();
            if(message==null){
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Select file error");
                builder.setTitle("Error with selecting file");
                builder.setPositiveButton(android.R.string.ok,null);
                AlertDialog dialog = builder.create();
                dialog.show();
            }
            else {
                send(message);
                finish();//finish activity and return to previous activity

            }

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    protected void send(ParseObject message) {
        message.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e==null){
                    Toast.makeText(RecipientsActivity.this, "send successful",Toast.LENGTH_LONG).show();
                }
                else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(RecipientsActivity.this);
                    builder.setMessage("Send file error");
                    builder.setTitle("Error with sending file");
                    builder.setPositiveButton(android.R.string.ok,null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        });
    }

    protected ParseObject createMessage() {
        ParseObject message = new ParseObject(ParseConstants.CLASS_MESSAGES);
        message.put(ParseConstants.KEY_SENDER_ID,ParseUser.getCurrentUser().getObjectId());
        message.put(ParseConstants.KEY_SENDER_NAME,ParseUser.getCurrentUser().getUsername());
        message.put(ParseConstants.KEY_FILE_TYPE, mFileType);
        message.put(ParseConstants.KEY_RECIPIENT_IDS,getRecipientIds());

        //convert file to bytearray then convert bytearray to parse file. Use helper classes and libraries from treehouse github
        byte[] fileBytes = FileHelper.getByteArrayFromFile(this, mMediaUri);
        if(fileBytes == null){
            return null;
        }
        else {
            if(mFileType.equals("photo")){
                fileBytes = FileHelper.reduceImageForUpload(fileBytes);
            }
            String fileName = FileHelper.getFileName(this,mMediaUri,mFileType);
            ParseFile file = new ParseFile(fileName, fileBytes);
            message.put(ParseConstants.KEY_FILE, file);

            return message;

        }

    }

    protected ArrayList<String> getRecipientIds() {
        //helper method
        ArrayList<String> recipientIds = new ArrayList<String>();
        for(int i = 0; i<getListView().getCount(); i++ )
        {
            if(getListView().isItemChecked(i)) {
                recipientIds.add(mFriends.get(i).getObjectId());
            }
        }
        return recipientIds;
    }

}
