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

public class ContactFirebaseHandler implements UserContract.ContactFirebaseHandler {

    DatabaseReference _contactRef;
    UserContract.ContactConsumer _consumer;
    FirebaseListAdapter<ContactEntry> _contactListAdapter;

    Activity _activity;

    final String _currentUserUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
    //final String _currentUserUID = "current_user";

    public ContactFirebaseHandler(@Nullable UserContract.ContactConsumer consumer) {
        this._consumer = consumer;
        _contactRef = FirebaseDatabase.getInstance().getReference().child(Constants.CONTACTS).child(_currentUserUID).getRef();
    }

    @Override
    public void setContactListAdapter(Activity activity, ListView listView) {
        this._activity = activity;

        _contactListAdapter = new FirebaseListAdapter<ContactEntry>(activity, ContactEntry.class,
                R.layout.contact_list_item, _contactRef.orderByChild("_displayName")) {
            @Override
            protected void populateView(View v, ContactEntry model, int position) {

                //TODO: set layout here
                TextView name = (TextView) v.findViewById(R.id.contactNameText);
                name.setText(model.get_displayName());

                final ImageView contactImage = (ImageView) v.findViewById(R.id.contactPortraitImageView);
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
                                .into(contactImage);
                    }
                });


            }
        };

        listView.setAdapter(_contactListAdapter);

    }

    @Override
    public void getContactFromFirebase(String userUID) {
        _contactRef.child(userUID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ContactEntry contact = dataSnapshot.getValue(ContactEntry.class);
                _consumer.consumeContact(contact);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void addToContactList(ContactEntry entry) {
        _contactRef.child(entry.get_contactUID()).setValue(entry);
    }

    @Override
    public void updateUserContactList(ContactEntry entry) {
        _contactRef.child(entry.get_contactUID()).child("_displayName").setValue(entry.get_displayName());
        _contactRef.child(entry.get_contactUID()).child("_latestMsg").setValue(entry.get_latestMsg());
        _contactRef.child(entry.get_contactUID()).child("_photoUri").setValue(entry.get_photoUri());
        _contactRef.child(entry.get_contactUID()).child("_firebaseToken").setValue(entry.get_firebaseToken());

    }
}
