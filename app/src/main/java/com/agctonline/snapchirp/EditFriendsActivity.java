package com.agctonline.snapchirp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;


public class EditFriendsActivity extends ListActivity {
    public final static String TAG = EditFriendsActivity.class.getSimpleName();
    protected List<ParseUser> mUsers;
    protected ParseRelation<ParseUser> mFriendsRelation;
    protected ParseUser mCurrentUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        //must be called before setcontentview
        setContentView(R.layout.activity_edit_friends);

        getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        //built in method of ListActivity to set ListView options
    }

    @Override
    protected void onResume() {
        super.onResume();

        mCurrentUser = ParseUser.getCurrentUser();//get current user
        mFriendsRelation=mCurrentUser.getRelation(ParseConstants.KEY_FRIENDS_RELATION);//getRelation() is a parse function, and parse handles the logic of friend relationships in the backend

        setProgressBarIndeterminateVisibility(true);
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.orderByAscending(ParseConstants.KEY_USERNAME);
        query.setLimit(100);

        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> parseUsers, ParseException e) {
                setProgressBarIndeterminateVisibility(false);
                if(e == null){
                    //success
                    mUsers = parseUsers;
                    String[] userNames = new String[mUsers.size()];
                    int i = 0;
                    //java's foreach syntax
                    for(ParseUser user : mUsers){
                        userNames[i]=user.getUsername();
                        i++;
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(EditFriendsActivity.this, android.R.layout.simple_list_item_checked, userNames);
                    setListAdapter(adapter);

                    addFriendCheckMarks();//this function loops through the list of users and add check marks if they are "your" friends


                }
                else {
                    Log.e(TAG, e.getMessage());
                    AlertDialog.Builder builder = new AlertDialog.Builder(EditFriendsActivity.this);
                    builder.setMessage(e.getMessage())
                            .setTitle(R.string.error_title)
                            .setPositiveButton(android.R.string.ok, null);//must provide second param

                    AlertDialog dialog = builder.create();
                    dialog.show();
                }

            }
        });
    }

    //@Override
   /* public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_friends, menu);
        return true;
    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        if(getListView().isItemChecked(position)) {

            mFriendsRelation.add(mUsers.get(position)); // this statement adds the users that were tapped on to the relationship of current user. Parse creates in the background a relationship linkage.


        }
        else {

            mFriendsRelation.remove(mUsers.get(position));

        }
        mCurrentUser.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    //save success
                    Log.e(TAG, e.getMessage());
                }
            }
        });


    }

    private void addFriendCheckMarks(){
        mFriendsRelation.getQuery().findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> friends, ParseException e) {
                if (e==null){
                    //successfully retrieved list of friendsRelation

                    //this outer for loop is going through the list of all users retrieved (mUsers)
                    for(int i=0; i <mUsers.size();i++){

                        ParseUser user = mUsers.get(i);

                        //this inner foreach loop compares the id of each user in the friends list to the user in the overall list and set check mark if they match.  This is a crude method.
                        for (ParseUser friend: friends){
                            // the == operator does not compare strings correctly in java, use the .equals()
                            if(friend.getObjectId().equals(user.getObjectId())){
                               getListView().setItemChecked(i,true);
                                //set check mark statement
                            }
                        }
                    }

                }
                else {
                    Log.e(TAG,e.getMessage());
                }


            }
        });

    }
}
