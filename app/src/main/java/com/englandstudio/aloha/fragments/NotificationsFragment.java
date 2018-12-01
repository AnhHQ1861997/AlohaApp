package com.englandstudio.aloha.fragments;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.englandstudio.aloha.CommentActivity;
import com.englandstudio.aloha.GetTimeAgo;
import com.englandstudio.aloha.objects.FriendRequest;
import com.englandstudio.aloha.objects.Notification;
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
public class NotificationsFragment extends Fragment {

    //RecyclerView
    RecyclerView recyclerViewNotification, recyclerViewFriendRequest;
    LinearLayoutManager llmNotification, llmFriendRequest;

    //ScrollView
    ScrollView scrollView;

    //Adapter
    FirebaseRecyclerAdapter<Notification, NotificationViewHolder> notificationViewHolderFirebaseRecyclerAdapter;
    FirebaseRecyclerAdapter<FriendRequest, FriendRequestViewHolder> friendRequestFriendRequestViewHolderFirebaseRecyclerAdapter;

    //Authentication
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    String current_id, user_id;

    //Reference
    DatabaseReference mRefUser, mRefNotification, mRefAddFriend, mRefFriend, mRefPost;

    //Dialog
    ProgressDialog progressDialog;

    public NotificationsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_notifications, container, false);

        anhXa(view);
        authentication();
        reference();
        dialog();
        adapter();
        return view;
    }

    private void anhXa(View view) {
        recyclerViewNotification = view.findViewById(R.id.recyclerViewNotification);
        llmNotification = new LinearLayoutManager(getContext());
        recyclerViewNotification.setLayoutManager(llmNotification);
        llmNotification.setStackFromEnd(true);
        llmNotification.setReverseLayout(true);
        recyclerViewNotification.setNestedScrollingEnabled(false);

        recyclerViewFriendRequest = view.findViewById(R.id.recyclerViewFriendRequest);
        llmFriendRequest = new LinearLayoutManager(getContext());
        recyclerViewFriendRequest.setLayoutManager(llmFriendRequest);
        llmFriendRequest.setStackFromEnd(true);
        llmFriendRequest.setReverseLayout(true);
        recyclerViewFriendRequest.setNestedScrollingEnabled(false);
        scrollView = view.findViewById(R.id.scrollView);
    }

    private void authentication() {
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        current_id = mUser.getUid();
    }

    private void reference() {
        mRefUser = FirebaseDatabase.getInstance().getReference().child("User");
        mRefNotification = FirebaseDatabase.getInstance().getReference().child("Notification");
        mRefAddFriend = FirebaseDatabase.getInstance().getReference().child("AddFriend");
        mRefFriend = FirebaseDatabase.getInstance().getReference().child("Friend");
        mRefPost = FirebaseDatabase.getInstance().getReference().child("Post");
    }

    private void dialog() {
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage("Vui lòng chờ...");
    }

    private void adapter() {
        Long tsLong = System.currentTimeMillis() / 1000;
        Query query = mRefNotification.child(current_id).orderByChild("time").startAt(tsLong).limitToLast(5);

        notificationViewHolderFirebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Notification, NotificationViewHolder>(
                Notification.class,
                R.layout.item_notification,
                NotificationViewHolder.class,
                query
        ) {
            @Override
            protected void populateViewHolder(final NotificationViewHolder viewHolder, Notification model, int position) {
                final String key = getRef(position).getKey();

                viewHolder.setDetails(model.getAvatar(), model.getFirstName(), model.getLastName(), model.getComment(), model.getType(), model.getTime());

                mRefPost.child(key).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(final DataSnapshot dataSnapshot) {
                        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String avatar = dataSnapshot.child("avatar").getValue().toString();
                                String firstName = dataSnapshot.child("firstName").getValue().toString();
                                String lastName = dataSnapshot.child("lastName").getValue().toString();
                                String status = dataSnapshot.child("status").getValue().toString();
                                String from = dataSnapshot.child("from").getValue().toString();
                                String time = dataSnapshot.child("time").getValue().toString();

                                Intent intent = new Intent(getContext(), CommentActivity.class);
                                intent.putExtra("avatar", avatar);
                                intent.putExtra("firstName", firstName);
                                intent.putExtra("lastName", lastName);
                                intent.putExtra("status", status);
                                intent.putExtra("user_id", from);
                                intent.putExtra("time", time);
                                intent.putExtra("key", key);
                                startActivity(intent);
                            }
                        });
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        };
        recyclerViewNotification.setAdapter(notificationViewHolderFirebaseRecyclerAdapter);
        notificationViewHolderFirebaseRecyclerAdapter.notifyDataSetChanged();

        Query query1 = mRefAddFriend.child(current_id).orderByChild("type").startAt("received").endAt("received");
        friendRequestFriendRequestViewHolderFirebaseRecyclerAdapter = new FirebaseRecyclerAdapter<FriendRequest, FriendRequestViewHolder>(
                FriendRequest.class,
                R.layout.item_friend_request,
                FriendRequestViewHolder.class,
                query1
        ) {
            @Override
            protected void populateViewHolder(FriendRequestViewHolder viewHolder, FriendRequest model, int position) {
                user_id = getRef(position).getKey();

                viewHolder.setDetails(model.getAvatar(), model.getFirstName(), model.getLastName(), model.getFrom());

                //Agree
                viewHolder.btAgree.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        progressDialog.show();
                        mRefUser.child(user_id).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                String avatar = dataSnapshot.child("avatar").getValue().toString();
                                String firstName = dataSnapshot.child("firstName").getValue().toString();
                                String lastName = dataSnapshot.child("lastName").getValue().toString();
                                String title = dataSnapshot.child("title").getValue().toString();

                                Map mapOtherUser = new HashMap();
                                mapOtherUser.put("avatar", avatar);
                                mapOtherUser.put("firstName", firstName);
                                mapOtherUser.put("lastName", lastName);
                                mapOtherUser.put("title", title);

                                mRefFriend.child(current_id).child(user_id).updateChildren(mapOtherUser).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            mRefUser.child(current_id).addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    String avatar = dataSnapshot.child("avatar").getValue().toString();
                                                    String firstName = dataSnapshot.child("firstName").getValue().toString();
                                                    String lastName = dataSnapshot.child("lastName").getValue().toString();
                                                    String title = dataSnapshot.child("title").getValue().toString();

                                                    Map mapCurrentUser = new HashMap();
                                                    mapCurrentUser.put("avatar", avatar);
                                                    mapCurrentUser.put("firstName", firstName);
                                                    mapCurrentUser.put("lastName", lastName);
                                                    mapCurrentUser.put("title", title);

                                                    mRefFriend.child(user_id).child(current_id).updateChildren(mapCurrentUser).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                mRefAddFriend.child(current_id).child(user_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        if (task.isSuccessful()) {
                                                                            mRefAddFriend.child(user_id).child(current_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                    progressDialog.dismiss();
                                                                                    if (!task.isSuccessful()) {
                                                                                        Toast.makeText(getContext(), "Có lỗi!", Toast.LENGTH_SHORT).show();
                                                                                    }
                                                                                }
                                                                            });
                                                                        } else {
                                                                            progressDialog.dismiss();
                                                                            Toast.makeText(getContext(), "Có lỗi!", Toast.LENGTH_SHORT).show();
                                                                        }
                                                                    }
                                                                });
                                                            } else {
                                                                progressDialog.dismiss();
                                                                Toast.makeText(getContext(), "Có lỗi!", Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    });
                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {

                                                }
                                            });
                                        } else {
                                            progressDialog.dismiss();
                                            Toast.makeText(getContext(), "Có lỗi!", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                });

                //Refuse
                viewHolder.btRefuse.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        progressDialog.show();
                        mRefAddFriend.child(current_id).child(user_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    mRefAddFriend.child(user_id).child(current_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                progressDialog.dismiss();
                                                Toast.makeText(getContext(), "Từ chối lời mời kết bạn", Toast.LENGTH_SHORT).show();
                                            } else {
                                                progressDialog.dismiss();
                                                Toast.makeText(getContext(), "Có lỗi!", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                } else {
                                    progressDialog.dismiss();
                                    Toast.makeText(getContext(), "Có lỗi!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
            }
        };
        recyclerViewFriendRequest.setAdapter(friendRequestFriendRequestViewHolderFirebaseRecyclerAdapter);
        friendRequestFriendRequestViewHolderFirebaseRecyclerAdapter.notifyDataSetChanged();
    }

    public static class NotificationViewHolder extends RecyclerView.ViewHolder {

        CircleImageView ivAvatar;
        TextView tvNotification, tvTime;

        GetTimeAgo timeAgo = new GetTimeAgo();

        public NotificationViewHolder(View itemView) {
            super(itemView);

            ivAvatar = itemView.findViewById(R.id.ivAvatar);
            tvNotification = itemView.findViewById(R.id.tvNotification);
            tvTime = itemView.findViewById(R.id.tvTime);
        }

        public void setDetails(String avatar, String firstName, String lastName, String comment, String type, long time) {
            if (!avatar.equals("default")) {
                Picasso.get().load(avatar).placeholder(R.drawable.default_avatar).into(ivAvatar);
            }
            if (type.equals("comment")) {
                tvNotification.setText(firstName + " " + lastName + " đã bình luận về bài viết của bạn: " + comment + ".");
            } else {
                tvNotification.setText(firstName + " " + lastName + " đã bày tỏ cảm xúc về bài viết của bạn.");
            }
            tvTime.setText(timeAgo.getTimeAgo(time));
        }
    }

    public static class FriendRequestViewHolder extends RecyclerView.ViewHolder {

        CircleImageView ivAvatar;
        TextView tvName, tvTitle;
        Button btAgree, btRefuse;

        public FriendRequestViewHolder(View itemView) {
            super(itemView);

            ivAvatar = itemView.findViewById(R.id.ivAvatar);
            tvName = itemView.findViewById(R.id.tvName);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            btAgree = itemView.findViewById(R.id.btAgree);
            btRefuse = itemView.findViewById(R.id.btRefuse);
        }

        public void setDetails(String avatar, String firstName, String lastName, String from) {
            if (!avatar.equals("default")) {
                Picasso.get().load(avatar).placeholder(R.drawable.default_avatar).into(ivAvatar);
            }
            tvName.setText(firstName + " " + lastName);
            tvTitle.setText("Bạn nhận được một lời mời kết bạn");
        }
    }
}
