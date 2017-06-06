package com.example.android.chatly.controls.user;

import android.app.Activity;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.android.chatly.R;
import com.example.android.chatly.models.ContactEntry;
import com.example.android.chatly.utils.Constants;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/**
 * Created by chy on 5/31/17.
 */

public class ChatListFirebaseHandler implements UserContract.ChatListFirebaseHandler {

    DatabaseReference _chatListRef;
    FirebaseListAdapter<ContactEntry> _chatListAdapter;
    Activity _activity;
    // For test only
    //final String currentUserUID = "current_user";
    final String _currentUserUID = FirebaseAuth.getInstance().getCurrentUser().getUid();

    public ChatListFirebaseHandler() {
        this._chatListRef = FirebaseDatabase.getInstance().getReference().child(Constants.CHATLISTS).child(_currentUserUID).getRef();
    }

    @Override
    public void setChatListAdapter(Activity activity, ListView listView) {
        //final String currentUserUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        this._activity = activity;



        this._chatListAdapter = new FirebaseListAdapter<ContactEntry>(activity, ContactEntry.class,
                R.layout.chat_list_item, _chatListRef.orderByChild("_timestamp").limitToLast(10) ) {
            @Override
            protected void populateView(View v, ContactEntry model, int position){

                TextView userNameText = (TextView) v.findViewById(R.id.chatListNameText);
                userNameText.setText(model.get_displayName());

                TextView latestMsgText = (TextView) v.findViewById(R.id.latestMessageText);
                latestMsgText.setText(model.get_latestMsg());

                final ImageView userImage = (ImageView) v.findViewById(R.id.chatListUserImageView);
                StorageReference userPortraitRef = FirebaseStorage.getInstance().getReference().child("Portrait");
                userPortraitRef.child(model.get_contactUID()).getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        RequestOptions options = new RequestOptions();
                        options.override(100, 100).circleCrop();
                        Uri uri = task.getResult();
                        Glide.with(_activity)
                                .load(uri)
                                .apply(options)
                                .into(userImage);
                    }
                });

            }
        };
        listView.setAdapter(_chatListAdapter);
    }

    @Override
    public void addToChatList(final ContactEntry entry) {

        _chatListRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                _chatListRef.child(entry.get_contactUID()).setValue(entry);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void updateUserChatList(String contactUID, @Nullable String msg) {
        if(msg != null) {
            _chatListRef.child(contactUID).child("_latestMsg").setValue(msg);
        }
        _chatListRef.child(contactUID).child("_timestamp").setValue(Long.MAX_VALUE - System.currentTimeMillis());
    }
}
