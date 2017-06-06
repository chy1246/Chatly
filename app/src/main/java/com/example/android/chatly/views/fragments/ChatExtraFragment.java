package com.example.android.chatly.views.fragments;


import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.android.chatly.R;
import com.example.android.chatly.controls.chat.ChatContract;
import com.example.android.chatly.controls.chat.ChatFirebaseHandler;
import com.example.android.chatly.models.Chat;
import com.example.android.chatly.models.ContactEntry;
import com.example.android.chatly.utils.Constants;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChatExtraFragment extends Fragment implements ChatContract.View{
    ImageView _albumButton;
    ImageView _cameraButton;
    ImageView _someButton;

    static final int PICK_IMAGE_REQUEST = 111;
    static final int REQUEST_TAKE_PHOTO = 112;
    String _currentPhotoPath;
    Uri _captureURI;


    ContactEntry _receiver;
    String _currentUserUID;

    ChatFirebaseHandler _chatFirebaseHandler;


    public ChatExtraFragment() {
        // Required empty public constructor
    }

    public static ChatExtraFragment newInstance(ContactEntry contact, String currentUserUID){
        ChatExtraFragment fragment = new ChatExtraFragment();
        Bundle args = new Bundle();
        args.putParcelable(Constants.CONTACT_PARCEL, contact);
        args.putString(Constants.CURRENT_USER_UID, currentUserUID);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            _receiver = getArguments().getParcelable(Constants.CONTACT_PARCEL);
            _currentUserUID = getArguments().getString(Constants.CURRENT_USER_UID);
        }
        _chatFirebaseHandler = new ChatFirebaseHandler(this);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chat_extra, container, false);
        bindViews(view);

        return  view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        _albumButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST);
            }
        });

        _cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                    File photoFile = null;
                    try {
                        photoFile = createImageFile();
                    } catch (IOException ex) {
                        // Error occurred while creating the File
                    }
                    // Continue only if the File was successfully created
                    if (photoFile != null) {
                        _captureURI = FileProvider.getUriForFile(getActivity(),
                                "com.example.android.fileprovider",
                                photoFile);
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, _captureURI);
                        startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
                    }
                }
            }

        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageURI = data.getData();

            Chat chat = new Chat(_currentUserUID, _receiver.get_contactUID(), null , imageURI.toString(), null, System.currentTimeMillis());
            _chatFirebaseHandler.sendMessage(getActivity().getApplicationContext(), chat, _receiver.get_firebaseToken());
        }

        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {

           if(_captureURI != null) {

               Chat chat = new Chat(_currentUserUID, _receiver.get_contactUID(), null, _captureURI.toString(),null, System.currentTimeMillis());
               _chatFirebaseHandler.sendMessage(getActivity().getApplicationContext(), chat, _receiver.get_firebaseToken());
           }
        }

    }



    private void bindViews(View view){
        _albumButton = (ImageView) view.findViewById(R.id.album_imageView);
        _cameraButton = (ImageView) view.findViewById(R.id.camera_imageView);
        _someButton = (ImageView) view.findViewById(R.id.contact_card_imageView);

    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        _currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    public void onSendMessageSuccess() {

    }

    @Override
    public void onSendMessageFailure(String message) {

    }

    @Override
    public void onGetMessagesSuccess(Chat chat) {

    }

    @Override
    public void onGetMessagesFailure(String message) {

    }
}
