package com.agctonline.snapchirp;

import android.content.Context;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.ParseObject;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

import java.util.Date;
import java.util.List;

/**
 * Created by admin on 12/8/2014.
 * similar to message adapter, changed parseobject to parseuser
 */
public class UserAdapter extends ArrayAdapter<ParseUser>{
    protected Context mContext;
    protected List<ParseUser> mUsers;


    public UserAdapter(Context context, List<ParseUser> users) {
        super(context, R.layout.message_item, users);//parent constructor
        mContext = context;
        mUsers = users;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //view holder coding pattern
        ViewHolder holder;

        if (convertView==null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.user_item, null);
            holder = new ViewHolder();
            holder.userImageView = (ImageView) convertView.findViewById(R.id.userImageView);
            holder.nameLabel = (TextView) convertView.findViewById(R.id.nameLabel);
            //holder.timeLabel = (TextView) convertView.findViewById(R.id.timeLabel);
            holder.checkImageView = (ImageView) convertView.findViewById(R.id.checkImageView);
            convertView.setTag(holder);

        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        ParseUser user = mUsers.get(position);
        String email = user.getEmail().toLowerCase();

        if(email.equals("")) {
            holder.userImageView.setImageResource(R.drawable.avatar_empty);
        }
        else {
            String hash = MD5Util.md5Hex(email);
            String gravatarURL = "http://www.gravatar.com/avatar/" + hash + "?s=204&d=monsterid";
            //Log.d("TEST",gravatarURL);
            Picasso.with(mContext).load(gravatarURL).placeholder(R.drawable.avatar_empty).into(holder.userImageView);//Picasso loads web images in background

        }




       /* if(user.getString(ParseConstants.KEY_FILE_TYPE).equals("photo")) {
            //holder.iconImageView.setImageResource(R.drawable.ic_picture);
        }
        else {
            //holder.iconImageView.setImageResource(R.drawable.ic_video);
        }*/

        holder.nameLabel.setText(user.getUsername());

        GridView gridView = (GridView)parent; //parent is the param passed in from getView method, it is the "parent" container of the holder. It contains the holder custom view and the native check box view.
        if(gridView.isItemChecked(position)){
            holder.checkImageView.setVisibility(View.VISIBLE);
            //the image view was set to invisible in the custom user_item.xml layout
        }
        else {
            holder.checkImageView.setVisibility(View.INVISIBLE);
            //the image view was set to invisible in the custom user_item.xml layout
        }



        return convertView;

    }

    private static class ViewHolder {
        ImageView userImageView;
        ImageView checkImageView;
        TextView nameLabel;
        //TextView timeLabel;
    }

    public void refill(List<ParseUser> users){
       //part of the

        mUsers.clear();
        mUsers.addAll(users);
        notifyDataSetChanged();

    }

}
