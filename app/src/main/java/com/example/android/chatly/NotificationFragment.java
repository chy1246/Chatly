package com.example.android.chatly;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.android.chatly.controls.user.ContactFirebaseHandler;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class NotificationFragment extends Fragment {

    private DatabaseReference mDatabase;
    private FirebaseUser user;
    private ContactFirebaseHandler _contactFirebaseHandler;
    public NotificationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        user = FirebaseAuth.getInstance().getCurrentUser();
        _contactFirebaseHandler = new ContactFirebaseHandler(null);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notification, container, false);
        return view;
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        mDatabase.child("Notification").getRef().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
               for(DataSnapshot notificationData : dataSnapshot.child(user.getUid()).getChildren()){
                   Notification notification = notificationData.getValue(Notification.class);
                   //if(! notification.getAccepted()){
                       System.out.print(notification.getAccepted());
                       ArrayList<Notification> notifications = new ArrayList<>();
                       notifications.add(notification);
                       setAdapterforView(notifications);
                   //}
               }

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void setAdapterforView( ArrayList<Notification> notifications){
        View view = getView();
        NotificationAdapter adapter = new NotificationAdapter(getContext(), notifications);
        ListView listNotificationView = (ListView) view.findViewById(R.id.NotificationListView);
        //listNotificationView.setAdapter(adapter);

        FirebaseListAdapter<Notification> notificationFirebaseListAdapter = new FirebaseListAdapter<Notification>(getActivity(),
                Notification.class, R.layout.itemnotification,  mDatabase.child("Notification").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).orderByChild("accepted").equalTo(false)) {
            @Override
            protected void populateView(View v, Notification model, int position) {

                final Notification notification = getItem(position);
                boolean accepted = notification.getAccepted();
                // Check if an existing view is being reused, otherwise inflate the view
                View convertView = v;
                // Lookup view for data population
                TextView userName = (TextView) convertView.findViewById(R.id.request_name);
                Button accept = (Button) convertView.findViewById(R.id.btn_accept);
                Button reject = (Button) convertView.findViewById(R.id.btn_reject);

                accept.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //remove(notification);
                        mDatabase.child("Notification").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                        .child(notification.getFromID()).child("accepted").setValue(true);
                        _contactFirebaseHandler.addToContactList(notification.getFromID());


                    }
                });

                reject.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mDatabase.child("Notification").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .child(notification.getFromID()).child("accepted").setValue(true);

                    }
                });
                // Populate the data into the template view using the data object
                userName.setText(notification.getFrom() + " wants to add you into contacts");
                // Return the completed view to render on screen

            }


        };
        listNotificationView.setAdapter(notificationFirebaseListAdapter);
    }
}
