package com.englandstudio.aloha;

import android.app.ProgressDialog;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.englandstudio.aloha.objects.Comment;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.github.ivbaranov.mfb.MaterialFavoriteButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentActivity extends AppCompatActivity {

    //ImageView
    CircleImageView ivAvatar;
    ImageView ivComment;

    //TextView
    TextView tvName, tvTime, tvStatus, tvComment, tvFavorite, tvMore;

    //EditText
    EditText etComment;

    //ScrollView
    ScrollView scrollView;

    //MaterialFavoriteButton
    MaterialFavoriteButton btFavorite;

    //String
    String avatar, firstName, lastName, time, status, key;

    //RecyclerView
    RecyclerView recyclerView;
    LinearLayoutManager llm;

    //Adapter
    FirebaseRecyclerAdapter<Comment, CommentViewHolder> firebaseRecyclerAdapter;

    //GetTimeAgo
    GetTimeAgo timeAgo;

    //Authentication
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    String current_id, user_id;

    //Reference
    DatabaseReference mRefUser, mRefFavorite, mRefComment, mRefNotification;

    //Dialog
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        anhXa();
        dialog();
        authentication();
        reference();
        getInfomation();
        displayInfomation();
        hideStatus();
        countFavorite();
        countComment();
        commentListener();
        updateComment();
        commentOnClick();
        adapter();
    }

    private void anhXa() {
        ivAvatar = findViewById(R.id.ivAvatar);
        ivComment = findViewById(R.id.ivComment);

        tvName = findViewById(R.id.tvName);
        tvTime = findViewById(R.id.tvTime);
        tvStatus = findViewById(R.id.tvStatus);
        tvFavorite = findViewById(R.id.tvFavorite);
        tvComment = findViewById(R.id.tvComment);
        tvMore = findViewById(R.id.tvMore);

        etComment = findViewById(R.id.etComment);

        btFavorite = findViewById(R.id.btFavorite);

        recyclerView = findViewById(R.id.recyclerView);
        llm = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(llm);
        recyclerView.setNestedScrollingEnabled(false);

        scrollView = findViewById(R.id.scrollView);
    }

    private void dialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage("Vui lòng chờ...");
    }

    private void authentication() {
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        current_id = mUser.getUid();
    }

    private void reference() {
        mRefUser = FirebaseDatabase.getInstance().getReference().child("User");
        mRefFavorite = FirebaseDatabase.getInstance().getReference().child("Favorite");
        mRefComment = FirebaseDatabase.getInstance().getReference().child("Comment");
        mRefNotification = FirebaseDatabase.getInstance().getReference().child("Notification");
    }

    private void getInfomation() {
        avatar = getIntent().getStringExtra("avatar");
        firstName = getIntent().getStringExtra("firstName");
        lastName = getIntent().getStringExtra("lastName");
        time = getIntent().getStringExtra("time");
        status = getIntent().getStringExtra("status");
        key = getIntent().getStringExtra("key");
        user_id = getIntent().getStringExtra("user_id");

        timeAgo = new GetTimeAgo();
    }

    private void displayInfomation() {
        if (!avatar.equals("default")) {
            Picasso.get().load(avatar).into(ivAvatar);
        }
        tvName.setText(firstName + " " + lastName);
        tvStatus.setText(status);
        tvTime.setText(timeAgo.getTimeAgo(Long.parseLong(time)));
    }

    private void hideStatus() {
        tvStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tvStatus.getMaxLines() == 5) {
                    tvStatus.setMaxLines(1000);
                    //tvMore.setVisibility(View.GONE);
                } else {
                    tvStatus.setMaxLines(5);
                    //tvMore.setVisibility(View.VISIBLE);
                }
            }
        });
        tvMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvStatus.setMaxLines(1000);
                //tvMore.setVisibility(View.GONE);
            }
        });
    }

    private void countFavorite() {
        mRefFavorite.child(key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String count = dataSnapshot.getChildrenCount() + "";
                tvFavorite.setText(count + " yêu thích");
                if (dataSnapshot.child(current_id).exists()) {
                    btFavorite.setFavorite(true);
                } else {
                    btFavorite.setFavorite(false);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        btFavorite.setOnFavoriteChangeListener(new MaterialFavoriteButton.OnFavoriteChangeListener() {
            @Override
            public void onFavoriteChanged(MaterialFavoriteButton buttonView, boolean favorite) {
                if (favorite) {
                    mRefFavorite.child(key).child(current_id).setValue(true);
                } else {
                    mRefFavorite.child(key).child(current_id).removeValue();
                }
            }
        });
    }

    private void countComment() {
        mRefComment.child(key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String count = dataSnapshot.getChildrenCount() + "";
                tvComment.setText(count + " bình luận");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void commentListener() {
        mRefComment.child(key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        scrollView.fullScroll(View.FOCUS_DOWN);
                    }
                }, 200);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void updateComment() {
        mRefUser.child(current_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                mRefComment.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        for (DataSnapshot snap : snapshot.getChildren()) {
                            String user_id = snap.child("from").getValue().toString();
                            String commentKey = snap.getKey();

                            String avatar = dataSnapshot.child("avatar").getValue().toString();
                            String firstName = dataSnapshot.child("firstName").getValue().toString();
                            String lastName = dataSnapshot.child("lastName").getValue().toString();

                            Map map = new HashMap();
                            map.put("avatar", avatar);
                            map.put("firstName", firstName);
                            map.put("lastName", lastName);

                            if (current_id.equals(user_id)) {
                                mRefComment.child(key).child(commentKey).updateChildren(map);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void commentOnClick() {
        mRefUser.child(current_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {

                ivComment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final String comment = etComment.getText().toString();
                        if (TextUtils.isEmpty(comment)) {
                            Toast.makeText(CommentActivity.this, "Không được để trống!", Toast.LENGTH_SHORT).show();
                        } else {
                            etComment.setText("");
                            final String commentKey = mRefComment.push().getKey();
                            final String avatar = dataSnapshot.child("avatar").getValue().toString();
                            final String firstName = dataSnapshot.child("firstName").getValue().toString();
                            final String lastName = dataSnapshot.child("lastName").getValue().toString();

                            Map map = new HashMap();
                            map.put("avatar", avatar);
                            map.put("firstName", firstName);
                            map.put("lastName", lastName);
                            map.put("comment", comment);
                            map.put("from", current_id);
                            map.put("time", ServerValue.TIMESTAMP);

                            mRefComment.child(key).child(commentKey).updateChildren(map).addOnCompleteListener(new OnCompleteListener() {
                                @Override
                                public void onComplete(@NonNull Task task) {
                                    if (task.isSuccessful()) {
                                        if (!current_id.equals(user_id)) {
                                            Map notificationMap = new HashMap();
                                            notificationMap.put("avatar", avatar);
                                            notificationMap.put("firstName", firstName);
                                            notificationMap.put("lastName", lastName);
                                            notificationMap.put("comment", comment);
                                            notificationMap.put("type", "comment");
                                            notificationMap.put("time", ServerValue.TIMESTAMP);
                                            mRefNotification.child(user_id).child(key).updateChildren(notificationMap);
                                        }
                                    } else {
                                        Toast.makeText(CommentActivity.this, "Có lỗi!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                        }
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void adapter() {
        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Comment, CommentViewHolder>(
                Comment.class,
                R.layout.item_comment,
                CommentViewHolder.class,
                mRefComment.child(key)
        ) {
            @Override
            protected void populateViewHolder(CommentViewHolder viewHolder, Comment model, int position) {
                viewHolder.setDetails(model.getAvatar(), model.getFirstName(), model.getLastName(), model.getComment(), model.getTime());
            }
        };
        recyclerView.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.notifyDataSetChanged();
    }

    public static class CommentViewHolder extends RecyclerView.ViewHolder {

        CircleImageView ivAvatar;
        TextView tvName, tvTime, tvComment;

        GetTimeAgo timeAgo = new GetTimeAgo();

        public CommentViewHolder(View itemView) {
            super(itemView);

            ivAvatar = itemView.findViewById(R.id.ivAvatar);
            tvName = itemView.findViewById(R.id.tvName);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvComment = itemView.findViewById(R.id.tvComment);
        }

        public void setDetails(String avatar, String firstName, String lastName, String comment, long time) {
            if (!avatar.equals("default")) {
                Picasso.get().load(avatar).into(ivAvatar);
            }
            tvName.setText(firstName + " " + lastName);
            tvComment.setText(comment);
            tvTime.setText(timeAgo.getTimeAgo(time));
        }
    }
}
