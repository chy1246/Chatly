package com.example.android.chatly.controls.chat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.request.RequestOptions;
import com.example.android.chatly.R;
import com.example.android.chatly.controls.user.ChatListFirebaseHandler;
import com.example.android.chatly.models.Chat;
import com.example.android.chatly.models.ContactEntry;
import com.example.android.chatly.utils.Constants;
import com.example.android.chatly.views.activities.FullscreenActivity;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import hani.momanii.supernova_emoji_library.Helper.EmojiconTextView;


/**
 * Created by chy on 5/31/17.
 */

public class ChatFirebaseHandler implements ChatContract.FirebaseHandler{

    ChatContract.View _chatView;
    DatabaseReference _chatRef;
    ChatListFirebaseHandler _chatListHandler;
    FirebaseListAdapter<Chat> _chatAdapter;
    Activity _activity;
    // FirebaseUser _currentUser;

    final String _currentUserUID = FirebaseAuth.getInstance().getCurrentUser().getUid();

    public ChatFirebaseHandler(ChatContract.View chatView){

        _chatView = chatView;

        _chatRef = FirebaseDatabase.getInstance().getReference().child(Constants.CHATS);

        _chatListHandler = new ChatListFirebaseHandler();

        // _currentUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    @Override
    public void setChatDetailAdapter(final Activity activity, ListView listView, final ContactEntry contact){
        String chat_identifier =_currentUserUID + "_" + contact.get_contactUID();
        _activity = activity;

        _chatAdapter = new FirebaseListAdapter<Chat>(activity, Chat.class, R.layout.chat_message_other_text,
                _chatRef.child(chat_identifier).orderByChild("_timestamp").limitToLast(6)) {
            @Override
            protected void populateView(View v, Chat model, int position) {
                String msg = model.get_message();
                String imageURI = model.get_imageURI();


                StorageReference userPortraitRef = FirebaseStorage.getInstance().getReference().child("Portrait");
                String key = _currentUserUID.equals(model.get_senderUID())? _currentUserUID : model.get_senderUID();



                final EmojiconTextView msgText = (EmojiconTextView) v.findViewById(R.id.chatDetailMessageText);
                final ImageView imageView = (ImageView) v.findViewById(R.id.chatMessageImageView);
                final ImageView backImageView = (ImageView) v.findViewById(R.id.chatMessageBackImageView);
                final ImageView contactImg = (ImageView) v.findViewById(R.id.chatDetailPortraitImageView);

                userPortraitRef.child(key).getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                       @Override
                       public void onComplete(@NonNull Task<Uri> task) {
                           RequestOptions options = new RequestOptions();
                           options.override(100, 100).circleCrop();
                           Uri uri = task.getResult();
                           Glide.with(_activity)
                                    .load(uri)
                                   .apply(options)
                                   .into(contactImg);
                       }
                });

                if(msg != null && !msg.equals("")) {

                    msgText.setText(model.get_message());

                }else if(imageURI != null && !imageURI.equals("")){
                    backImageView.setVisibility(View.GONE);
                    final StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(model.get_imageURI());
                    storageReference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if(task.isSuccessful()){
                               final Uri uri = task.getResult();
                                RequestOptions options = new RequestOptions();
                                options.override(200, 160);
                                Glide.with(_activity).load(uri).apply(options).into(imageView);

                                imageView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent fullScreen = new Intent(_activity, FullscreenActivity.class);
                                        fullScreen.putExtra("Image", uri);
                                        _activity.startActivity(fullScreen);

                                    }
                                });
                            }
                        }
                    });

                }else {

                    final StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(model.get_videoURI());
                    storageReference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if(task.isSuccessful()){
                                final Uri uri = task.getResult();
                                RequestOptions options = new RequestOptions();
                                options.override(200, 160);
                                Glide.with(_activity).load(uri).apply(options).into(imageView);

                                imageView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent fullScreen = new Intent(_activity, FullscreenActivity.class);
                                        fullScreen.putExtra("Video", uri);
                                        _activity.startActivity(fullScreen);

                                    }
                                });
                            }
                        }
                    });

                }

            }


            @Override
            public View getView(int position, View view, ViewGroup viewGroup) {

                View v = view;
                LayoutInflater inflater =  activity.getLayoutInflater();
                String chatReceiverUID = getItem(position).get_receiverUID();

                Chat chat =  getItem(position);

                if(chatReceiverUID.equals(_currentUserUID)){ //current user is a receiver

                    if(chat.get_message() != null && !chat.get_message().equals("")) {
                        v = inflater.inflate(R.layout.chat_message_other_text, null);
                    }else{
                        v = inflater.inflate(R.layout.chat_message_other_image, null);
                    }

                }else{
                    if(chat.get_message() != null && !chat.get_message().equals("")) {
                        v = inflater.inflate(R.layout.chat_message_self_text, null);
                    }else{
                        v = inflater.inflate(R.layout.chat_message_self_image, null);
                    }
                }

                populateView(v, getItem(position), position);

                return v;
            }

        };

        listView.setAdapter(_chatAdapter);

    }

    @Override
    public void sendMessage(final Context context, final Chat chat, final String receiverFirebaseToken) {
        final String chat_identifier1 = chat.get_senderUID() + "_" + chat.get_receiverUID();
        final String chat_identifier2 = chat.get_receiverUID() + "_" + chat.get_senderUID();

        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        final StorageReference imageStorageReference = FirebaseStorage.getInstance().getReference().child(Constants.CHAT_IMAGES);
        final StorageReference videoStorageReference = FirebaseStorage.getInstance().getReference().child(Constants.CHAT_VIDEOS);


                if(chat.get_imageURI() == null && chat.get_videoURI() == null) {

                    databaseReference.child(Constants.CHATS).child(chat_identifier1).child(String.valueOf(chat.get_timestamp())).setValue(chat);
                    databaseReference.child(Constants.CHATS).child(chat_identifier2).child(String.valueOf(chat.get_timestamp())).setValue(chat);

                    _chatView.onSendMessageSuccess();

                }else if(chat.get_videoURI() == null){

                    putImageToStorage(chat, imageStorageReference, chat_identifier1);

                    putImageToStorage(chat, imageStorageReference, chat_identifier2);

                }else{

                    putVideoToStorage(chat, videoStorageReference, chat_identifier1);

                    putVideoToStorage(chat, videoStorageReference, chat_identifier2);

                }

                // send notification to the receiver

            }

    private void putImageToStorage(final Chat chat , StorageReference storageReference, final String key){
        storageReference.child(key).child(String.valueOf(chat.get_timestamp())).putFile(Uri.parse(chat.get_imageURI())).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                @SuppressWarnings("VisibleForTests") Uri downloadUrl = task.getResult().getDownloadUrl();
                Chat resultChat = new Chat(chat.get_senderUID(), chat.get_receiverUID(), null, downloadUrl.toString(), null, chat.get_timestamp());
                _chatRef.child(key).child(String.valueOf(chat.get_timestamp())).setValue(resultChat);
            }
        });
    }

    private void putVideoToStorage(final Chat chat , StorageReference storageReference, final String key){
        storageReference.child(key).child(String.valueOf(chat.get_timestamp())).putFile(Uri.parse(chat.get_videoURI())).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                @SuppressWarnings("VisibleForTests") Uri downloadUrl = task.getResult().getDownloadUrl();
                Chat resultChat = new Chat(chat.get_senderUID(), chat.get_receiverUID(), null, null, downloadUrl.toString(), chat.get_timestamp());
                _chatRef.child(key).child(String.valueOf(chat.get_timestamp())).setValue(resultChat);
            }
        });
    }


    @Override
    public void updateChatList(final ContactEntry entry){
        final String chat_identifier = _currentUserUID + "_" + entry.get_contactUID();
        _chatRef.child(chat_identifier).getRef().addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                Chat chat = dataSnapshot.getValue(Chat.class);
                String msg = chat.get_message();
                String imgURI = chat.get_imageURI();
                if(msg != null && !msg.equals("")){
                    _chatListHandler.updateUserChatList(entry.get_contactUID(), msg);
                }else if(imgURI != null && !imgURI.equals("")){
                    _chatListHandler.updateUserChatList(entry.get_contactUID(), "[photo]");
                }else{
                    _chatListHandler.updateUserChatList(entry.get_contactUID(), "[video]");
                }

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

}
