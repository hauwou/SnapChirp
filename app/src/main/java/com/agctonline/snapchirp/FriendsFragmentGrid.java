package com.agctonline.snapchirp;


import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.List;

/**
 * Created by admin on 11/28/2014.
 *
 *Create the fragment for the inbox or friend view
 *
 *reused some codes from the sample placeholderfragment
 *
 */
public class FriendsFragmentGrid extends Fragment {

    public final static String TAG = FriendsFragmentGrid.class.getSimpleName();
    protected List<ParseUser> mFriends;
    protected ParseRelation<ParseUser> mFriendsRelation;
    protected ParseUser mCurrentUser;
    protected String[] mObjectIDs;
    protected GridView mGridView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_friends_grid, container, false);

        mGridView = (GridView) rootView.findViewById(R.id.friendsGrid);//unlike listView, need to link up gridview
        TextView emptyTextView = (TextView)rootView.findViewById(android.R.id.empty); // create this variable for hide routine because gridView does not hide empty view automatically
        mGridView.setEmptyView(emptyTextView);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        mCurrentUser = ParseUser.getCurrentUser();//get current user
        mFriendsRelation = mCurrentUser.getRelation(ParseConstants.KEY_FRIENDS_RELATION);//getRelation() is a parse function, and parse handles the logic of friend relationships in the backend
        getActivity().setProgressBarIndeterminateVisibility(true);//because setProgressbar is an activity method, must call getActivity first and then chain on the setProgressBar

        ParseQuery<ParseUser> query = mFriendsRelation.getQuery();
        query.addAscendingOrder(ParseConstants.KEY_USERNAME);
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> friends, ParseException e) {
                getActivity().setProgressBarIndeterminateVisibility(false);
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

                    //
                    if(mGridView.getAdapter()==null){
                        UserAdapter adapter = new UserAdapter(getActivity(),mFriends);
                        mGridView.setAdapter(adapter);
                    }
                    else {
                        ((UserAdapter)mGridView.getAdapter()).refill(mFriends);
                    }



                    //ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_expandable_list_item_1, friendNames);//switch to custom UserAdapter
                    //For ListView must use getListView().getContext() to get to the context instead of using FriendsFragment.this or getActivity, because this is inside a fragment class and not a parent activity class

                    //mGridView.setAdapter(adapter);

                } else {

                    Log.e(TAG, e.getMessage());
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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
    //This method is automatically getting run, don't run it manually. When the item is clicked, the 4 params will be passed into this method.  The Params are: ListView, the "view" refers to the individual element view (ie textview), position of the clicked element on the list, id of the clicked element
    /*public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

      *//*  AlertDialog.Builder builder = new AlertDialog.Builder(getListView().getContext());
        builder.setMessage("objectID[] size: "+ mObjectIDs[position])
                .setTitle(R.string.error_title)
                .setPositiveButton(android.R.string.ok, null);//must provide second param

        AlertDialog dialog = builder.create();
        dialog.show();*//*
        String parseObjectID = mObjectIDs[position];
        //Toast.makeText(getActivity(), "msg msg", Toast.LENGTH_LONG).show();

        Intent gotoProfileActivity = new Intent(getActivity(), FriendProfile.class);
        gotoProfileActivity.putExtra("ID", parseObjectID);

        startActivity(gotoProfileActivity);


    }*/


}//end of class

