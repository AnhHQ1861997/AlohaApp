package com.englandstudio.aloha.fragments;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.englandstudio.aloha.MainActivity;
import com.englandstudio.aloha.ProfileActivity;
import com.englandstudio.aloha.R;
import com.englandstudio.aloha.SearchActivity;
import com.englandstudio.aloha.SigninActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

/**
 * A simple {@link Fragment} subclass.
 */
public class OptionsFragment extends Fragment {

    //Authentication
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    String current_id;

    //Reference
    DatabaseReference mRefUser, mRefOnline;

    //Layout
    RelativeLayout layoutProfile, layoutSearch, layoutNewsFeed, layoutNotifications, layoutMessages, layoutFriends, layoutShare, layoutAbout, layoutLogout;

    //Activity
    MainActivity mainActivity;

    //Dialog
    ProgressDialog progressDialog;

    public OptionsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_options, container, false);

        anhXa(view);
        authentication();
        reference();
        dialog();
        profileOnClick();
        searchOnClick();
        swipeToFragment();
        logoutOnClick();

        return view;
    }

    private void anhXa(View view) {
        layoutProfile = view.findViewById(R.id.layoutProfile);
        layoutSearch = view.findViewById(R.id.layoutSearch);
        layoutNewsFeed = view.findViewById(R.id.layoutNewsFeed);
        layoutNotifications = view.findViewById(R.id.layoutNotifications);
        layoutMessages = view.findViewById(R.id.layoutMessages);
        layoutFriends = view.findViewById(R.id.layoutFriends);
        layoutShare = view.findViewById(R.id.layoutShare);
        layoutAbout = view.findViewById(R.id.layoutAbout);
        layoutLogout = view.findViewById(R.id.layoutLogout);

        mainActivity = (MainActivity) getActivity();
    }

    private void authentication() {
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        current_id = mUser.getUid();
    }

    private void reference() {
        mRefUser = FirebaseDatabase.getInstance().getReference().child("User");
        mRefOnline = FirebaseDatabase.getInstance().getReference().child("Online");
    }

    private void dialog() {
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage("Vui lòng chờ...");
    }

    private void profileOnClick() {
        layoutProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ProfileActivity.class);
                intent.putExtra("type", "user");
                intent.putExtra("user_id", current_id);
                startActivity(intent);
            }
        });
    }

    private void searchOnClick() {
        layoutSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), SearchActivity.class);
                startActivity(intent);
            }
        });
    }

    private void swipeToFragment() {
        layoutNewsFeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.navigationView.getMenu().getItem(0).setChecked(true);
                mainActivity.viewPager.setCurrentItem(0);
            }
        });
        layoutMessages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.navigationView.getMenu().getItem(1).setChecked(true);
                mainActivity.viewPager.setCurrentItem(1);
            }
        });
        layoutNotifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.navigationView.getMenu().getItem(2).setChecked(true);
                mainActivity.viewPager.setCurrentItem(2);
            }
        });
        layoutFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.navigationView.getMenu().getItem(3).setChecked(true);
                mainActivity.viewPager.setCurrentItem(3);
            }
        });
    }

    private void logoutOnClick() {
        layoutLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                Intent intent = new Intent(getContext(), SigninActivity.class);
                startActivity(intent);
                getActivity().finish();
                mRefOnline.child(current_id).child("online").setValue(ServerValue.TIMESTAMP);
                Toast.makeText(mainActivity, "Đã đăng xuất!", Toast.LENGTH_SHORT).show();
            }
        });
    }


}
