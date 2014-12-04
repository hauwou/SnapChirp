package com.agctonline.snapchirp;


//import android.app.FragmentManager;
//import android.support.v13.app.FragmentPagerAdapter;
//these standard and v13 imports go together
import android.content.Context;
//import android.app.ListFragment;
import android.support.v4.app.ListFragment;
import java.util.Locale;

//import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
//these v4 imports go together

/**
 * Created by admin on 11/30/2014.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    protected Context mContext;//this is to hold the context passed into the constructor, so to be used later in the getpagetitle method

    public SectionsPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @Override
    public ListFragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        //return PlaceholderFragment.newInstance(position + 1);

        switch (position){
            case 0:
                return new InboxFragment();
            case 1:
                return new FriendsFragment();
        }
        return null;

//The error of return type in this section is because of version incompatibility (v13-newer vs v4-older)
        //fix by using v4.app.ListFragment for the return type in Inboxfragment, and use (import android.support.v4.app.FragmentManager;        import android.support.v4.app.FragmentPagerAdapter;)
        //Note, if use import android.support.v13.app.FragmentPagerAdapter;, then must use import android.app.FragmentManager;
        //Mixing of import android.support.v13.app.FragmentPagerAdapter; and import android.support.v4.app.FragmentManager; will result in a "cannot applied" error for super(fm);

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
                return mContext.getString(R.string.title_inbox).toUpperCase(l);
            case 1:
                return mContext.getString(R.string.title_friends).toUpperCase(l);

        }
        return null;
    }
}