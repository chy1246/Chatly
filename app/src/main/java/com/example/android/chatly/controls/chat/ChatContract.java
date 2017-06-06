package com.example.android.chatly.controls.chat;

import android.app.Activity;
import android.content.Context;
import android.widget.ListView;

import com.example.android.chatly.models.Chat;
import com.example.android.chatly.models.ContactEntry;

/**
 * Created by chy on 5/31/17.
 */

public interface ChatContract {
    interface View {
        void onSendMessageSuccess();

        void onSendMessageFailure(String message);

        void onGetMessagesSuccess(Chat chat);

        void onGetMessagesFailure(String message);
    }

    interface FirebaseHandler {

        void setChatDetailAdapter(Activity activity, ListView listView, ContactEntry contact);

        void updateChatList(ContactEntry entry);

        void sendMessage(Context context, Chat chat, String receiverFirebaseToken);

    }

    interface stickerHandler{
        void initialize();
        void prepareSticker();
    }
}
