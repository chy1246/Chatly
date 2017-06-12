package com.example.android.chatly.views.fragments;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.chatly.Notification;
import com.example.android.chatly.R;
import com.example.android.chatly.User;
import com.example.android.chatly.models.ContactEntry;
import com.example.android.chatly.utils.Constants;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ContactDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ContactDetailFragment extends Fragment {
    private String _contactUID;

    private ImageView portraitView;
    private TextView userNameView;
    private TextView genderView;
    private TextView habitView;
    private Button add;
    private DatabaseReference mDatabase;
    private StorageReference mStorageRef;
   // private FirebaseUser user;


    public ContactDetailFragment() {
        // Required empty public constructor
    }

    public static ContactDetailFragment newInstance(String contactUID) {
        ContactDetailFragment fragment = new ContactDetailFragment();
        Bundle args = new Bundle();
        args.putString(Constants.RECEIVER_UID, contactUID);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            _contactUID = getArguments().getString(Constants.RECEIVER_UID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search_result, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        userNameView =(TextView) view.findViewById(R.id.friendName);
        genderView = (TextView) view.findViewById(R.id.friendGender);
        habitView = (TextView) view.findViewById(R.id.friendHabit);
        portraitView = (ImageView) view.findViewById(R.id.friendPortrait);
        add = (Button) view.findViewById(R.id.btn_addFriend);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mStorageRef = FirebaseStorage.getInstance().getReference();
       // user = FirebaseAuth.getInstance().getCurrentUser();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        download(_contactUID);
        getUser(_contactUID);

        add.setText("Send Messages");
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String contactName = userNameView.getText().toString();
                ContactEntry contact = new ContactEntry(_contactUID, contactName, "", "", null, System.currentTimeMillis());
                ChatFragment chatFragment = ChatFragment.newInstance(contact);

                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.flContent, chatFragment);
                ft.commit();

            }
        });

        super.onActivityCreated(savedInstanceState);

    }

    public void getUser(final String uid){
        mDatabase.child("users").getRef().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user1 =  dataSnapshot.child(uid).getValue(User.class);
                getParameters(user1.getUserName(), user1.getHabit(), user1.getGender());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void getParameters(String userName1, String habits1, String gender1){
        if(userName1 != null)
            userNameView.setText(userName1);
        if(habits1 != null)
            genderView.setText(gender1);
        if(gender1 != null)
            habitView.setText(habits1);
    }

    public void download(String uid){
        try {
            final File localFile = File.createTempFile("portrait", "jpg");
            File root = Environment.getExternalStorageDirectory();
            mStorageRef.child("Portrait").child(uid).getFile(localFile)
                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            // Successfully downloaded data to local file
                            Bitmap bMap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                            portraitView.setImageBitmap(bMap);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle failed download
                    // ...
                }
            });
        }catch (IOException e){

        }
    }


}
