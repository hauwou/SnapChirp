package com.agctonline.snapchirp;


import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by admin on 11/28/2014.
 *
 *Create the fragment for the inbox or friend view
 *
 *reused some codes from the sample placeholderfragment
 *
 */
public class InboxFragment extends ListFragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        return rootView;
    }

}
