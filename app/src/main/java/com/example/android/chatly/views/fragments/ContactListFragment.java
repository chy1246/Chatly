package com.example.android.chatly.views.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.android.chatly.R;
import com.example.android.chatly.controls.user.ContactFirebaseHandler;
import com.example.android.chatly.models.ContactEntry;

/**
 * A simple {@link Fragment} subclass.
 */
public class ContactListFragment extends Fragment {


    ListView _contactList;
    ContactFirebaseHandler _contactFirebaseHandler;

    public ContactListFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_contact_list, container, false);

        initialize();
        bindViews(v);

        return v;
    }

    private void initialize(){

        _contactFirebaseHandler = new ContactFirebaseHandler(null);

    }

    private void bindViews(View view){

        TextView topBannerText = (TextView) view.findViewById(R.id.topBannerTextView);
        topBannerText.setText("Contacts");

        _contactList = (ListView) view.findViewById(R.id.contactListView);
        _contactFirebaseHandler.setContactListAdapter(getActivity(), _contactList);

        _contactList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String contactUID = ((ContactEntry) parent.getItemAtPosition(position)).get_contactUID();
                ContactDetailFragment contactDetailFragment = ContactDetailFragment.newInstance(contactUID);
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.flContent, contactDetailFragment);
                ft.commit();
            }
        });

    }
}
