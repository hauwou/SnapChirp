package com.agctonline.snapchirp;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;
//import android.app.ListFragment;

/**
 * Created by admin on 11/28/2014.
 *
 *Create the fragment for the inbox or friend view
 *
 *reused some codes from the sample placeholderfragment
 *
 */
public class InboxFragment extends ListFragment {

    protected List<ParseObject> mMessages;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_inbox, container, false);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        getActivity().setProgressBarIndeterminateVisibility(true);

        //ParseQuery<ParseObject> query = ParseQuery.getQuery("Messages");
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(ParseConstants.CLASS_MESSAGES);
        query.whereEqualTo(ParseConstants.KEY_RECIPIENT_IDS, ParseUser.getCurrentUser().getObjectId());
        query.addAscendingOrder(ParseConstants.KEY_CREATED_AT);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> messages, ParseException e) {
                getActivity().setProgressBarIndeterminateVisibility(false);
                if(e==null){
                    //reuse code from friends fragment
                    mMessages = messages;

                    String[] friendNames = new String[mMessages.size()];

                    int i = 0;
                    //java's foreach syntax
                    for (ParseObject message : mMessages) {
                        friendNames[i] = message.getString(ParseConstants.KEY_SENDER_NAME);
                        i++;
                    }


                    //The if condition checks if the adapter is set and set the adapter
                    if(getListView().getAdapter()==null) {
                        MessageAdapter adapter = new MessageAdapter(getListView().getContext(), mMessages);

                        //old code for standard adapter
                    /*ArrayAdapter<String> adapter = new ArrayAdapter<String>(getListView().getContext(), android.R.layout.simple_expandable_list_item_1, friendNames);
                    */
                        setListAdapter(adapter);


                    }
                    else {

                        //refill the adapter if it has already been set
                        ((MessageAdapter)getListView().getAdapter()).refill(mMessages);
                    }



                }
                else {
                    //error
                }

            }
        });



    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        ParseObject message = mMessages.get(position);
        String messageType = message.getString(ParseConstants.KEY_FILE_TYPE);

        ParseFile file = message.getParseFile(ParseConstants.KEY_FILE);
        Uri fileUri = Uri.parse(file.getUrl());

        if (messageType.equals("photo")){
            //view image
            Intent gotoViewImageActivity = new Intent(getActivity(), ViewImageActivity.class);
            gotoViewImageActivity.setData(fileUri);
            startActivity(gotoViewImageActivity);
        }
        else {
            //view video
            Intent gotoViewVideo = new Intent(Intent.ACTION_VIEW, fileUri);
            gotoViewVideo.setDataAndType(fileUri,"video/*");
            startActivity(gotoViewVideo);
        }

    }
}
