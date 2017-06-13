package com.example.android.chatly;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

public class Search_result extends Fragment {
    private ImageView portraitView;
    private TextView userNameView;
    private TextView genderView;
    private TextView habitView;
    private Button add;
    private DatabaseReference mDatabase;
    private StorageReference mStorageRef;
    private FirebaseUser user;
    public Search_result() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
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
        user = FirebaseAuth.getInstance().getCurrentUser();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        final String uid = getArguments().getString("userId");
        final String userName = getArguments().getString("userName");
        final String fromID = getArguments().getString("fromID");
        download(uid);
        getUser(uid);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatabase.child("users").getRef().addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (user.getUid().equals(uid)) {
                            Toast.makeText(getContext(), "You can not add yourself as a friend ",
                                    Toast.LENGTH_LONG).show();
                        } else {
                            User user1 = dataSnapshot.child(user.getUid()).getValue(User.class);
                            Notification addFriend = new Notification(false, user1.getUserName(), fromID);
                            mDatabase.child("Notification").child(uid).child(fromID).setValue(addFriend);
                            Toast.makeText(getContext(), "You have sent a request " + userName,
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                // mDatabase.child(uid)
            }
        });
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
            userNameView.setText("User Name: " + userName1);
        if(habits1 != null)
            genderView.setText("Gender: " + gender1);
        if(gender1 != null)
            habitView.setText("Habit: " + habits1);
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
