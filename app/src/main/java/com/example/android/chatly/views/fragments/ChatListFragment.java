package com.example.android.chatly.views.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.android.chatly.R;
import com.example.android.chatly.controls.user.ChatListFirebaseHandler;
import com.example.android.chatly.models.ContactEntry;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChatListFragment extends Fragment {


    static ChatListFirebaseHandler _chatListFirebaseHandler;
    ListView _chatListView;


    public ChatListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        initialize();
        View v = inflater.inflate(R.layout.fragment_chat_list, container, false);
        initialize();
        bindViews(v);

        return v;
    }

    private void initialize(){
        _chatListFirebaseHandler = new ChatListFirebaseHandler();


    }

    private void bindViews(View view){

        TextView topBannerText = (TextView) view.findViewById(R.id.topBannerTextView);
        topBannerText.setText("Chats");

        _chatListView = (ListView) view.findViewById(R.id.ChatListView);
        _chatListFirebaseHandler.setChatListAdapter(getActivity(), _chatListView);

        _chatListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ContactEntry contact = (ContactEntry) parent.getItemAtPosition(position);
                TextView name = (TextView) view.findViewById(R.id.chatListNameText);
                contact.set_displayName(name.getText().toString());

                ChatFragment chatFragment = ChatFragment.newInstance(contact);

                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.flContent, chatFragment);
                ft.commit();
            }
        });

    }

}
