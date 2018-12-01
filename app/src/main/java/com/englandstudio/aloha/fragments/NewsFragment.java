package com.englandstudio.aloha.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.englandstudio.aloha.CommentActivity;
import com.englandstudio.aloha.GetTimeAgo;
import com.englandstudio.aloha.objects.Post;
import com.englandstudio.aloha.PostActivity;
import com.englandstudio.aloha.ProfileActivity;
import com.englandstudio.aloha.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.github.ivbaranov.mfb.MaterialFavoriteButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class NewsFragment extends Fragment {

    //ImageView
    CircleImageView ivAvatar;

    //RelativeLayout
    RelativeLayout layoutStatus;

    //RecyclerView
    RecyclerView recyclerView;
    LinearLayoutManager llm;

    //ScrollView
    ScrollView scrollView;

    //Adapter
    FirebaseRecyclerAdapter<Post, PostViewHolder> firebaseRecyclerAdapter;

    //Authentication
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    String current_id;

    //Reference
    DatabaseReference mRefUser, mRefPost, mRefFavorite, mRefComment, mRefNotification;

    public NewsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_news, container, false);


        anhXa(view);
        authentication();
        reference();
        displayAvatar();
        avatarOnClick();
        statusOnClick();
        adapter();

        return view;
    }

    private void anhXa(View view) {
        ivAvatar = view.findViewById(R.id.ivAvatar);

        layoutStatus = view.findViewById(R.id.layoutStatus);

        recyclerView = view.findViewById(R.id.recyclerView);
        llm = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(llm);
        llm.setReverseLayout(true);
        llm.setStackFromEnd(true);
        recyclerView.setNestedScrollingEnabled(false);
        scrollView = view.findViewById(R.id.scrollView);
    }

    private void authentication() {
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        current_id = mUser.getUid();
    }

    private void reference() {
        mRefUser = FirebaseDatabase.getInstance().getReference().child("User");
        mRefPost = FirebaseDatabase.getInstance().getReference().child("Post");
        mRefFavorite = FirebaseDatabase.getInstance().getReference().child("Favorite");
        mRefComment = FirebaseDatabase.getInstance().getReference().child("Comment");
        mRefNotification = FirebaseDatabase.getInstance().getReference().child("Notification");
    }

    private void displayAvatar() {
        mRefUser.child(current_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String avatar = dataSnapshot.child("avatar").getValue().toString();
                if (!avatar.equals("default")) {
                    Picasso.get().load(avatar).placeholder(R.drawable.default_avatar).into(ivAvatar);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void avatarOnClick() {
        ivAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ProfileActivity.class);
                intent.putExtra("type", "user");
                intent.putExtra("user_id", current_id);
                startActivity(intent);
            }
        });
    }

    private void statusOnClick() {
        layoutStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), PostActivity.class);
                startActivity(intent);
            }
        });
    }

    private void adapter() {
        Long tsLong = System.currentTimeMillis() / 1000;
        Query query = mRefPost.orderByChild("from").startAt(current_id).endAt(current_id);

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Post, PostViewHolder>(
                Post.class,
                R.layout.item_post,
                PostViewHolder.class,
                mRefPost
        ) {
            @Override
            protected void populateViewHolder(final PostViewHolder viewHolder, Post model, int position) {
                final String key = getRef(position).getKey();

                viewHolder.setDetails(model.getAvatar(), model.getFirstName(), model.getLastName(), model.getStatus(), model.getTime());

                mRefPost.child(key).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        final String user_id = dataSnapshot.child("from").getValue().toString();
                        viewHolder.tvName.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(getContext(), ProfileActivity.class);
                                intent.putExtra("user_id", user_id);
                                if (current_id.equals(user_id)) {
                                    intent.putExtra("type", "user");
                                } else {
                                    intent.putExtra("type", "guest");
                                }
                                startActivity(intent);
                            }
                        });
                        viewHolder.ivAvatar.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(getContext(), ProfileActivity.class);
                                intent.putExtra("user_id", user_id);
                                if (current_id.equals(user_id)) {
                                    intent.putExtra("type", "user");
                                } else {
                                    intent.putExtra("type", "guest");
                                }
                                startActivity(intent);
                            }
                        });

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                //Favorite
                viewHolder.btFavorite.setOnFavoriteChangeListener(new MaterialFavoriteButton.OnFavoriteChangeListener() {
                    @Override
                    public void onFavoriteChanged(MaterialFavoriteButton buttonView, boolean favorite) {
                        if (favorite) {
                            mRefFavorite.child(key).child(current_id).setValue(true);
                        } else {
                            mRefFavorite.child(key).child(current_id).removeValue();
                        }
                    }
                });
                mRefFavorite.child(key).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            if (dataSnapshot.child(current_id).exists()) {
                                viewHolder.btFavorite.setFavorite(true);
                            } else {
                                viewHolder.btFavorite.setFavorite(false);
                            }
                            long countFavorite = dataSnapshot.getChildrenCount();
                            viewHolder.setFavorite(countFavorite);
                        } else {
                            viewHolder.setFavorite(0);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                //Comment
                mRefComment.child(key).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String count = dataSnapshot.getChildrenCount() + "";
                        viewHolder.tvComment.setText(count + " bình luận");
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                mRefPost.child(key).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(final DataSnapshot dataSnapshot) {
                        viewHolder.layoutComment.setOnClickListener(new View.OnClickListener() {
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

                //Hide Status
                viewHolder.tvStatus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (viewHolder.tvStatus.getMaxLines() == 5) {
                            viewHolder.tvStatus.setMaxLines(1000);
                            //viewHolder.tvMore.setVisibility(View.GONE);
                        } else {
                            viewHolder.tvStatus.setMaxLines(5);
                            //viewHolder.tvMore.setVisibility(View.VISIBLE);
                        }

                    }
                });
                viewHolder.tvMore.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        viewHolder.tvStatus.setMaxLines(1000);
                        //viewHolder.tvMore.setVisibility(View.GONE);
                    }
                });

            }
        };
        recyclerView.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.notifyDataSetChanged();
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {

        CircleImageView ivAvatar;
        TextView tvName, tvStatus, tvTime, tvMore;

        MaterialFavoriteButton btFavorite;
        TextView tvFavorite, tvComment;

        RelativeLayout layoutComment;

        GetTimeAgo timeAgo = new GetTimeAgo();

        public PostViewHolder(View itemView) {
            super(itemView);

            ivAvatar = itemView.findViewById(R.id.ivAvatar);
            tvName = itemView.findViewById(R.id.tvName);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvMore = itemView.findViewById(R.id.tvMore);

            btFavorite = itemView.findViewById(R.id.btFavorite);
            tvFavorite = itemView.findViewById(R.id.tvFavorite);
            tvComment = itemView.findViewById(R.id.tvComment);

            layoutComment = itemView.findViewById(R.id.layoutComment);
        }

        public void setDetails(String avatar, String firstName, String lastName, String status, long time) {
            if (!avatar.equals("default")) {
                Picasso.get().load(avatar).placeholder(R.drawable.default_avatar).into(ivAvatar);
            }
            tvName.setText(firstName + " " + lastName);
            tvStatus.setText(status);
            tvTime.setText(timeAgo.getTimeAgo(time));
        }

        public void setFavorite(long countFavorite) {
            tvFavorite.setText(countFavorite + " yêu thích");
        }
    }
}
