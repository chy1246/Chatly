package com.example.android.chatly;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by chy on 6/2/17.
 */

public class NotificationAdapter extends ArrayAdapter<Notification>{
    public NotificationAdapter(Context context, ArrayList<Notification> notifications) {
        super(context, 0, notifications);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        final Notification notification = getItem(position);
        boolean accepted = notification.getAccepted();
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.itemnotification, parent, false);
        }
        // Lookup view for data population
        TextView userName = (TextView) convertView.findViewById(R.id.request_name);
        Button accept = (Button) convertView.findViewById(R.id.btn_accept);
        Button reject = (Button) convertView.findViewById(R.id.btn_reject);

        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                remove(notification);
            }
        });

        reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        // Populate the data into the template view using the data object
        userName.setText(notification.getFrom() + " wants to add you into contacts");
        // Return the completed view to render on screen
        return convertView;
    }
}
