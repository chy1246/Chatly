package com.example.android.chatly.views.fragments;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.FileProvider;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.view.menu.MenuPopupHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.chatly.R;
import com.example.android.chatly.controls.chat.ChatContract;
import com.example.android.chatly.controls.chat.ChatFirebaseHandler;
import com.example.android.chatly.models.Chat;
import com.example.android.chatly.models.ContactEntry;
import com.example.android.chatly.utils.Constants;
import com.google.firebase.auth.FirebaseAuth;

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
public class ChatFragment extends Fragment implements ChatContract.View{


    ChatFirebaseHandler _chatFirebaseHandler;

    EmojiconEditText _chatInput;
    ImageView _sendButton;
    ImageView _extraFuncButton;
    ImageView _emojiButton;

    ListView _chatDetailListView;

    ContactEntry _receiver;
    String _currentUserUID =  FirebaseAuth.getInstance().getCurrentUser().getUid();
    //String _currentUserUID = "current_user";

    static final int PICK_IMAGE_REQUEST = 111;
    static final int REQUEST_TAKE_PHOTO = 112;
    static final int REQUEST_TAKE_VIDEO = 113;
    String _currentPhotoPath;
    Uri _captureURI;

    public ChatFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.

     * @return A new instance of fragment ChatFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ChatFragment newInstance(ContactEntry contact) {
        ChatFragment fragment = new ChatFragment();
        Bundle args = new Bundle();
        args.putParcelable(Constants.CONTACT_PARCEL, contact);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            _receiver = getArguments().getParcelable(Constants.CONTACT_PARCEL);
        }
    }

    private void initialize(){

        _chatFirebaseHandler = new ChatFirebaseHandler(this);
        _chatFirebaseHandler.setChatDetailAdapter(getActivity(), _chatDetailListView, _receiver);
        _chatFirebaseHandler.updateChatList(_receiver);

    }

    private void bindViews(View view) {
        _chatInput = (EmojiconEditText) view.findViewById(R.id.emojicon_edit_text);
        _sendButton = (ImageView) view.findViewById(R.id.send_button);
        _extraFuncButton = (ImageView) view.findViewById(R.id.extra_func_button);
        _emojiButton = (ImageView) view.findViewById(R.id.emoji_btn);
        _chatDetailListView = (ListView) view.findViewById(R.id.chatMessageListView);

        TextView topBannerText = (TextView) view.findViewById(R.id.topBannerTextView);
        topBannerText.setText(_receiver.get_displayName());

        final EmojIconActions emojIcon = new EmojIconActions(getActivity(), view, _chatInput, _emojiButton);
        emojIcon.ShowEmojIcon();

        _sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(getActivity(), "Clicked", Toast.LENGTH_SHORT);

                String inputMsg = _chatInput.getText().toString();

                if (inputMsg != "") {
                    Chat chat = new Chat(_currentUserUID, _receiver.get_contactUID(), inputMsg, null, null, System.currentTimeMillis());
                    _chatFirebaseHandler.sendMessage(getActivity().getApplicationContext(), chat, _receiver.get_firebaseToken());

                } else {
                    Toast.makeText(getActivity(), "Please type something to send.", Toast.LENGTH_SHORT).show();
                }

            }
        });




    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.fragment_chat, container, false);
        bindViews(view);

        initialize();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        _extraFuncButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prepareExtraFuncMenu();

            }
        });

       // prepareStickers();
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

                Chat chat = new Chat(_currentUserUID, _receiver.get_contactUID(), null, _captureURI.toString(), null, System.currentTimeMillis());
                _chatFirebaseHandler.sendMessage(getActivity().getApplicationContext(), chat, _receiver.get_firebaseToken());
            }
        }

        if (requestCode == REQUEST_TAKE_VIDEO && resultCode == RESULT_OK){
            if(_captureURI != null) {

                Chat chat = new Chat(_currentUserUID, _receiver.get_contactUID(), null,  null, _captureURI.toString(),System.currentTimeMillis());
                _chatFirebaseHandler.sendMessage(getActivity().getApplicationContext(), chat, _receiver.get_firebaseToken());
            }
        }

    }

    private void prepareExtraFuncMenu(){
        PopupMenu popup = new PopupMenu(getContext(), _extraFuncButton);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.chat_extra_func_menu, popup.getMenu());

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.extra_func_menu_gallery:
                        //Toast.makeText(getActivity(), "Gallery", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                        intent.addCategory(Intent.CATEGORY_OPENABLE);
                        intent.setType("image/*");
                        startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST);

                        return true;

                    case R.id.extra_func_menu_camera:
                        //Toast.makeText(getActivity(), "Camera", Toast.LENGTH_SHORT).show();

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

                        return true;

                    case R.id.extra_func_menu_video:
                        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                        if (takeVideoIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                            File videoFile = null;
                            try {
                                videoFile = createImageFile();
                            } catch (IOException ex) {
                                // Error occurred while creating the File
                            }
                            // Continue only if the File was successfully created
                            if (videoFile != null) {
                                _captureURI = FileProvider.getUriForFile(getActivity(),
                                        "com.example.android.fileprovider",
                                        videoFile);
                                takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, _captureURI);
                                startActivityForResult(takeVideoIntent, REQUEST_TAKE_VIDEO);
                            }
                        }

                        return true;
                    default:
                        return false;
                }
            }
        });

        popup.show();
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
        _chatInput.setText("");
    }

    @Override
    public void onSendMessageFailure(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onGetMessagesSuccess(Chat chat) {

    }

    @Override
    public void onGetMessagesFailure(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();

    }


}
