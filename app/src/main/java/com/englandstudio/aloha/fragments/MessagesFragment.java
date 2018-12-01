package com.englandstudio.aloha.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.englandstudio.aloha.GetTimeAgo;
import com.englandstudio.aloha.MessageActivity;
import com.englandstudio.aloha.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.englandstudio.aloha.objects.Message;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class MessagesFragment extends Fragment {

    //RecyclerView
    RecyclerView recyclerView;
    LinearLayoutManager llm;

    //Adapter
    FirebaseRecyclerAdapter<Message, MessageViewHolder> firebaseRecyclerAdapter;

    //Authentication
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    String current_id;

    //Reference
    DatabaseReference mRefUser, mRefLastMessage, mRefOnline;

    public MessagesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_messages, container, false);

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
        llm.setReverseLayout(true);
        llm.setStackFromEnd(true);
    }

    private void authentication() {
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        current_id = mUser.getUid();
    }

    private void reference() {
        mRefUser = FirebaseDatabase.getInstance().getReference().child("User");
        mRefLastMessage = FirebaseDatabase.getInstance().getReference().child("LastMessage");
        mRefOnline = FirebaseDatabase.getInstance().getReference().child("Online");
    }

    private void adapter() {
        Long tsLong = System.currentTimeMillis() / 1000;
        Query query = mRefLastMessage.child(current_id).orderByChild("time").startAt(tsLong);
        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Message, MessageViewHolder>(
                Message.class,
                R.layout.item_message,
                MessageViewHolder.class,
                query
        ) {
            @Override
            protected void populateViewHolder(final MessageViewHolder viewHolder, Message model, int position) {
                final String user_id = getRef(position).getKey();
                viewHolder.setDetails(model.getAvatar(), model.getFirstName(), model.getLastName(), model.getMessage(), model.getTime());

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

                //MessageActivity
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getContext(), MessageActivity.class);
                        intent.putExtra("user_id", user_id);
                        startActivity(intent);
                    }

                });

            }
        };
        recyclerView.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.notifyDataSetChanged();

    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder {

        CircleImageView ivAvatar, ivOnline;
        TextView tvName, tvMessage, tvTime;

        GetTimeAgo timeAgo = new GetTimeAgo();

        public MessageViewHolder(View itemView) {
            super(itemView);

            ivAvatar = itemView.findViewById(R.id.ivAvatar);
            tvName = itemView.findViewById(R.id.tvName);
            tvMessage = itemView.findViewById(R.id.tvMessage);
            tvTime = itemView.findViewById(R.id.tvTime);
            ivOnline = itemView.findViewById(R.id.ivOnline);
        }

        public void setDetails(String avatar, String firstName, String lastName, String message, long time) {
            if (!avatar.equals("default")) {
                Picasso.get().load(avatar).placeholder(R.drawable.default_avatar).into(ivAvatar);
            }
            tvName.setText(firstName + " " + lastName);
            tvMessage.setText(message);
            tvTime.setText(timeAgo.getHour(time));
        }
    }
}
