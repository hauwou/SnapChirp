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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;


public class EditFriendsActivity extends Activity {
    public final static String TAG = EditFriendsActivity.class.getSimpleName();
    protected List<ParseUser> mUsers;
    protected ParseRelation<ParseUser> mFriendsRelation;
    protected ParseUser mCurrentUser;
    protected GridView mGridView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        //must be called before setcontentview
        setContentView(R.layout.fragment_friends_grid);

        mGridView=(GridView)findViewById(R.id.friendsGrid);
        //getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE); code for listView
        mGridView.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE);
        //built in method of ListActivity to set ListView options
        mGridView.setOnItemClickListener(mOnItemClickListener);
        //setOnclicklistener is required for gridview, but not Listview because listactivity can detect clicks without setting a listener.


        TextView emptyTextView = (TextView)findViewById(android.R.id.empty); // create this variable for hide routine because gridView does not hide empty view automatically, must manage manually
        mGridView.setEmptyView(emptyTextView);
    }


    protected AdapterView.OnItemClickListener mOnItemClickListener = new AdapterView.OnItemClickListener(){
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            //the "view" param is the root relative layout container of the user_item.xml layout, so it will be used to find the view ids.

            ImageView checkImageView = (ImageView)view.findViewById(R.id.checkImageView);
            //if omit the "view" param, the user position will not be correctly applied, so check marks will be incorrectly applied.


            //reuse code from onclicklistview, with mods
            if(mGridView.isItemChecked(position)) {

                mFriendsRelation.add(mUsers.get(position)); // this statement adds the users that were tapped on to the relationship of current user. Parse creates in the background a relationship linkage.
                checkImageView.setVisibility(View.VISIBLE);


            }
            else {

                mFriendsRelation.remove(mUsers.get(position));
                checkImageView.setVisibility(View.INVISIBLE);

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
    };



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

                    if(mGridView.getAdapter()==null){
                        UserAdapter adapter = new UserAdapter(EditFriendsActivity.this, mUsers);
                        mGridView.setAdapter(adapter);
                    }
                    else {
                        ((UserAdapter)mGridView.getAdapter()).refill(mUsers);
                    }

                    /*ArrayAdapter<String> adapter = new ArrayAdapter<String>(EditFriendsActivity.this, android.R.layout.simple_list_item_checked, userNames);
                    setListAdapter(adapter);*/

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

/*    @Override
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


    }*/

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
                               mGridView.setItemChecked(i, true);
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
