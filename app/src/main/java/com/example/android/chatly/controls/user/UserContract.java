package com.example.android.chatly.controls.user;

import android.app.Activity;
import android.support.annotation.Nullable;
import android.widget.ListView;

import com.example.android.chatly.models.ContactEntry;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by chy on 5/31/17.
 */

public interface UserContract {
    interface ChatListView{
        void onLoadingSuccess();

    }

    interface ContactConsumer{
        void consumeContact(ContactEntry entry);
    }
    interface AccessUserHandler{
        FirebaseUser findUserByUID(String userUID);
    }
    interface ChatListFirebaseHandler{
        void setChatListAdapter(Activity activity, ListView listView);
        void addToChatList(ContactEntry entry);
        void updateUserChatList(String contactUID, @Nullable String msg);
    }

    interface ContactFirebaseHandler{
        void setContactListAdapter(Activity activity, ListView listView);
        void addToContactList(String contactUID);
        void updateUserContactList(ContactEntry entry);
        void getContactFromFirebase(String userUID);
    }
}
