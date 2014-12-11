package com.agctonline.snapchirp;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.ParseObject;

import java.util.Date;
import java.util.List;

/**
 * Created by admin on 12/8/2014.
 */
public class MessageAdapter extends ArrayAdapter<ParseObject>{
    protected Context mContext;
    protected List<ParseObject> mMessages;


    public MessageAdapter(Context context,  List<ParseObject> messages) {
        super(context, R.layout.message_item, messages);//parent constructor
        mContext = context;
        mMessages = messages;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //view holder coding pattern
        ViewHolder holder;

        if (convertView==null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.message_item, null);
            holder = new ViewHolder();
            holder.iconImageView = (ImageView) convertView.findViewById(R.id.messageIcon);
            holder.nameLabel = (TextView) convertView.findViewById(R.id.senderLabel);
            holder.timeLabel = (TextView) convertView.findViewById(R.id.timeLabel);
            convertView.setTag(holder);

        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        ParseObject message = mMessages.get(position);

        Date createdAt= message.getCreatedAt();//get/set date from ParseObject
        long now = new Date().getTime();
        String convertDate = DateUtils.getRelativeTimeSpanString(createdAt.getTime(),now,DateUtils.SECOND_IN_MILLIS).toString();


        if(message.getString(ParseConstants.KEY_FILE_TYPE).equals("photo")) {
            holder.iconImageView.setImageResource(R.drawable.ic_picture);
        }
        else {
            holder.iconImageView.setImageResource(R.drawable.ic_video);
        }
        holder.nameLabel.setText(message.getString(ParseConstants.KEY_SENDER_NAME));

        holder.timeLabel.setText(convertDate);
        return convertView;

    }

    private static class ViewHolder {
        ImageView iconImageView;
        TextView nameLabel;
        TextView timeLabel;
    }

    public void refill(List<ParseObject> messages){
       //part of the

        mMessages.clear();
        mMessages.addAll(messages);
        notifyDataSetChanged();

    }

}
