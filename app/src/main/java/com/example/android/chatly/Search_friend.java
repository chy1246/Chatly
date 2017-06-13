package com.example.android.chatly;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Search_friend extends Fragment {
    private Button search;
    private EditText searchName;
    private DatabaseReference mDatabase;
    private static final String TAG = "NewPostActivity";

    public Search_friend() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search_friend ,container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        search = (Button) view.findViewById(R.id.btn_search);
        searchName = (EditText) view.findViewById(R.id.search_userName);
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String userName = searchName.getText().toString();
                //System.out.println(userName);
                mDatabase.child("nameIDmap").getRef().addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChild(userName)){
                            if(dataSnapshot.child(userName) != null) {
                                String uid = (String) dataSnapshot.child(userName).getValue();
                                getUserProfile(uid, userName);
                            }else{
                                System.out.println("No such a user");
                                Toast.makeText(getContext(), "The user is not existed", Toast.LENGTH_LONG).show();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });
    }

    public void getUserProfile(String uid, String userName){
        Search_result result = new Search_result();
        Bundle args = new Bundle();
        args.putString("userId", uid);
        args.putString("userName", userName);
        args.putString("fromID", FirebaseAuth.getInstance().getCurrentUser().getUid());
        result.setArguments(args);
        System.out.println(uid);
        getFragmentManager().beginTransaction().replace(R.id.flContent, result).commit();
    }

}
