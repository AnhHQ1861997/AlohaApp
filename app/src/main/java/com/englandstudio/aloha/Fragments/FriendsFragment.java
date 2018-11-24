package com.englandstudio.aloha.Fragments;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.englandstudio.aloha.MessageActivity;
import com.englandstudio.aloha.Objects.Friend;
import com.englandstudio.aloha.Objects.User;
import com.englandstudio.aloha.ProfileActivity;
import com.englandstudio.aloha.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class FriendsFragment extends Fragment {

    //RecyclerView
    RecyclerView recyclerView;
    LinearLayoutManager llm;

    //Adapter
    FirebaseRecyclerAdapter<Friend, FriendViewHolder> firebaseRecyclerAdapter;

    //Authentication
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    String current_id;

    //Reference
    DatabaseReference mRefUser, mRefFriend, mRefOnline;

    public FriendsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_friends, container, false);

        anhXa(view);
        authentication();
        reference();
        adapter();

        return view;
    }

    private void anhXa(View view) {
        recyclerView = view.findViewById(R.id.recyclerView);
        llm = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(llm);
    }

    private void authentication() {
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        current_id = mUser.getUid();
    }

    private void reference() {
        mRefUser = FirebaseDatabase.getInstance().getReference().child("User");
        mRefFriend = FirebaseDatabase.getInstance().getReference().child("Friend");
        mRefOnline = FirebaseDatabase.getInstance().getReference().child("Online");
    }

    private void adapter() {
        Query query = mRefFriend.child(current_id).orderByChild("firstName").startAt("");
        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Friend, FriendViewHolder>(
                Friend.class,
                R.layout.item_friend,
                FriendViewHolder.class,
                query

        ) {
            @Override
            protected void populateViewHolder(final FriendViewHolder viewHolder, Friend model, int position) {
                final String user_id = getRef(position).getKey();

                viewHolder.setDetails(model.getAvatar(), model.getFirstName(), model.getLastName(), model.getTitle());

                //Check Online
                mRefOnline.child(user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String online = dataSnapshot.child("online").getValue().toString();
                        if (online.equals("true")) {
                            viewHolder.ivOnline.setVisibility(View.VISIBLE);
                        } else {
                            viewHolder.ivOnline.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                //Item On Click
                mRefUser.child(user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(final DataSnapshot dataSnapshot) {
                        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                CharSequence[] option = new CharSequence[]{"Trang cá nhân", "Gửi tin nhắn"};
                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                builder.setItems(option, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (which == 0) {
                                            String avatar = dataSnapshot.child("avatar").getValue().toString();
                                            String firstName = dataSnapshot.child("firstName").getValue().toString();
                                            String lastName = dataSnapshot.child("lastName").getValue().toString();
                                            String title = dataSnapshot.child("title").getValue().toString();

                                            Map map = new HashMap();
                                            map.put("avatar", avatar);
                                            map.put("firstName", firstName);
                                            map.put("lastName", lastName);
                                            map.put("title", title);
                                            mRefFriend.child(current_id).child(user_id).updateChildren(map).addOnCompleteListener(new OnCompleteListener() {
                                                @Override
                                                public void onComplete(@NonNull Task task) {
                                                    if (task.isSuccessful()) {
                                                        Intent intent = new Intent(getContext(), ProfileActivity.class);
                                                        intent.putExtra("type", "guest");
                                                        intent.putExtra("user_id", user_id);
                                                        startActivity(intent);
                                                    } else {
                                                        Toast.makeText(getContext(), "Có lỗi!", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                        }
                                        if (which == 1) {
                                            Intent intent = new Intent(getContext(), MessageActivity.class);
                                            intent.putExtra("user_id", user_id);
                                            startActivity(intent);
                                        }
                                    }
                                });
                                builder.show();
                            }
                        });

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        };
        recyclerView.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.notifyDataSetChanged();
    }

    public static class FriendViewHolder extends RecyclerView.ViewHolder {

        CircleImageView ivAvatar, ivOnline;
        TextView tvName, tvTitle;

        public FriendViewHolder(View itemView) {
            super(itemView);

            ivAvatar = itemView.findViewById(R.id.ivAvatar);
            ivOnline = itemView.findViewById(R.id.ivOnline);
            tvName = itemView.findViewById(R.id.tvName);
            tvTitle = itemView.findViewById(R.id.tvTitle);
        }

        public void setDetails(String avatar, String firstName, String lastName, String title) {
            if (!avatar.equals("default")) {
                Picasso.get().load(avatar).placeholder(R.drawable.default_avatar).into(ivAvatar);
            }
            tvName.setText(firstName + " " + lastName);
            tvTitle.setText(title);
        }
    }
}
