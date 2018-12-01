package com.englandstudio.aloha;

import android.content.res.ColorStateList;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.englandstudio.aloha.objects.Inbox;
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
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageActivity extends AppCompatActivity {

    //Toolbar
    Toolbar toolbar;

    //EditText
    EditText etInbox;

    //ImageView
    CircleImageView ivAvatar;
    ImageButton ivInbox;

    //RecyclerView
    RecyclerView recyclerView;
    LinearLayoutManager llm;

    //SwipeRefreshLayout
    SwipeRefreshLayout refreshLayout;
    int limitItem = 10, count = 1;

    //Adapter
    FirebaseRecyclerAdapter<Inbox, InboxViewHolder> firebaseRecyclerAdapter;

    //Authentication
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    String current_id, user_id;
    Query query;

    //Reference
    DatabaseReference mRefUser, mRefMessage, mRefLastMessage, mRefOnline;

    //GetTimeAgo
    GetTimeAgo timeAgo = new GetTimeAgo();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        user_id = getIntent().getStringExtra("user_id");

        anhXa();
        authentication();
        reference();
        displayInfomation();
        messageListener();
        messageOnClick();
        refreshMessage();
        adapter();
    }

    private void anhXa() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        etInbox = findViewById(R.id.etInbox);

        ivInbox = findViewById(R.id.ivInbox);
        ivAvatar = findViewById(R.id.ivAvatar);

        recyclerView = findViewById(R.id.recyclerView);
        llm = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(llm);

        refreshLayout = findViewById(R.id.refreshLayout);
    }

    private void authentication() {
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        current_id = mUser.getUid();
    }

    private void reference() {
        mRefUser = FirebaseDatabase.getInstance().getReference().child("User");
        mRefMessage = FirebaseDatabase.getInstance().getReference().child("Message");
        mRefLastMessage = FirebaseDatabase.getInstance().getReference().child("LastMessage");
        mRefOnline = FirebaseDatabase.getInstance().getReference().child("Online");
    }

    private void displayInfomation() {
        mRefUser.child(user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String avatar = dataSnapshot.child("avatar").getValue().toString();
                String firstName = dataSnapshot.child("firstName").getValue().toString();
                String lastName = dataSnapshot.child("lastName").getValue().toString();

                toolbar.setTitle(firstName + " " + lastName);
                if (!avatar.equals("default")) {
                    Picasso.get().load(avatar).placeholder(R.drawable.default_avatar).into(ivAvatar);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        mRefOnline.child(user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String online = dataSnapshot.child("online").getValue().toString();
                    if (online.equals("true")) {
                        toolbar.setSubtitle("Online");
                    } else {
                        toolbar.setSubtitle(timeAgo.getTimeAgo(Long.parseLong(online)));
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void messageListener() {
        mRefMessage.child(current_id).child(user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        recyclerView.smoothScrollToPosition(999999);
                    }
                }, 200);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ivInbox.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.colorRed)));
        }
        etInbox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().equals("")) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        ivInbox.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.colorRed)));
                    }
                    ivInbox.setBackgroundResource(R.drawable.baseline_favorite_border_black);
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        ivInbox.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.colorGray)));
                    }
                    ivInbox.setBackgroundResource(R.drawable.baseline_send_black);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void messageOnClick() {
        mRefUser.child(current_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                ivInbox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final String message = etInbox.getText().toString();
                        final MediaPlayer mp = MediaPlayer.create(MessageActivity.this, R.raw.ringtone);
                        mp.start();

                        if (!TextUtils.isEmpty(message)) {
                            etInbox.setText("");
                            final String key = mRefMessage.push().getKey();
                            String avatar = dataSnapshot.child("avatar").getValue().toString();
                            String firstName = dataSnapshot.child("firstName").getValue().toString();
                            String lastName = dataSnapshot.child("lastName").getValue().toString();

                            final Map map = new HashMap();
                            map.put("avatar", avatar);
                            map.put("firstName", firstName);
                            map.put("lastName", lastName);
                            map.put("message", message);
                            map.put("type", "text");
                            map.put("from", current_id);
                            map.put("received", "false");
                            map.put("serviceid", current_id);
                            map.put("time", ServerValue.TIMESTAMP);

                            mRefMessage.child(current_id).child(user_id).child(key).updateChildren(map).addOnCompleteListener(new OnCompleteListener() {
                                @Override
                                public void onComplete(@NonNull Task task) {
                                    if (task.isSuccessful()) {
                                        mRefMessage.child(user_id).child(current_id).child(key).updateChildren(map).addOnCompleteListener(new OnCompleteListener() {
                                            @Override
                                            public void onComplete(@NonNull Task task) {
                                                if (task.isSuccessful()) {
                                                    mRefLastMessage.child(user_id).child(current_id).updateChildren(map).addOnCompleteListener(new OnCompleteListener() {
                                                        @Override
                                                        public void onComplete(@NonNull Task task) {
                                                            if (task.isSuccessful()) {
                                                                mRefUser.child(user_id).addValueEventListener(new ValueEventListener() {
                                                                    @Override
                                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                                        String avatar = dataSnapshot.child("avatar").getValue().toString();
                                                                        String firstName = dataSnapshot.child("firstName").getValue().toString();
                                                                        String lastName = dataSnapshot.child("lastName").getValue().toString();

                                                                        final Map map = new HashMap();
                                                                        map.put("avatar", avatar);
                                                                        map.put("firstName", firstName);
                                                                        map.put("lastName", lastName);
                                                                        map.put("message", message);
                                                                        map.put("type", "text");
                                                                        map.put("from", user_id);
                                                                        map.put("received", "false");
                                                                        map.put("serviceid", current_id);
                                                                        map.put("time", ServerValue.TIMESTAMP);

                                                                        mRefLastMessage.child(current_id).child(user_id).updateChildren(map).addOnCompleteListener(new OnCompleteListener() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task task) {
                                                                                if (!task.isSuccessful()) {
                                                                                    Toast.makeText(MessageActivity.this, "Có lỗi!", Toast.LENGTH_SHORT).show();
                                                                                }
                                                                            }
                                                                        });
                                                                    }

                                                                    @Override
                                                                    public void onCancelled(DatabaseError databaseError) {

                                                                    }
                                                                });
                                                            } else {
                                                                Toast.makeText(MessageActivity.this, "Có lỗi!", Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    });
                                                } else {
                                                    Toast.makeText(MessageActivity.this, "Có lỗi!", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                    } else {
                                        Toast.makeText(MessageActivity.this, "Có lỗi!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                        } else {

                            final String key = mRefMessage.push().getKey();
                            String avatar = dataSnapshot.child("avatar").getValue().toString();
                            String firstName = dataSnapshot.child("firstName").getValue().toString();
                            String lastName = dataSnapshot.child("lastName").getValue().toString();

                            final Map map = new HashMap();
                            map.put("avatar", avatar);
                            map.put("firstName", firstName);
                            map.put("lastName", lastName);
                            map.put("message", "Yêu thích");
                            map.put("type", "favorite");
                            map.put("from", current_id);
                            map.put("serviceid", current_id);
                            map.put("received", "false");
                            map.put("time", ServerValue.TIMESTAMP);

                            mRefMessage.child(current_id).child(user_id).child(key).updateChildren(map).addOnCompleteListener(new OnCompleteListener() {
                                @Override
                                public void onComplete(@NonNull Task task) {
                                    if (task.isSuccessful()) {
                                        mRefMessage.child(user_id).child(current_id).child(key).updateChildren(map).addOnCompleteListener(new OnCompleteListener() {
                                            @Override
                                            public void onComplete(@NonNull Task task) {
                                                if (task.isSuccessful()) {
                                                    mRefLastMessage.child(user_id).child(current_id).updateChildren(map).addOnCompleteListener(new OnCompleteListener() {
                                                        @Override
                                                        public void onComplete(@NonNull Task task) {
                                                            if (task.isSuccessful()) {
                                                                mRefUser.child(user_id).addValueEventListener(new ValueEventListener() {
                                                                    @Override
                                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                                        String avatar = dataSnapshot.child("avatar").getValue().toString();
                                                                        String firstName = dataSnapshot.child("firstName").getValue().toString();
                                                                        String lastName = dataSnapshot.child("lastName").getValue().toString();

                                                                        final Map map = new HashMap();
                                                                        map.put("avatar", avatar);
                                                                        map.put("firstName", firstName);
                                                                        map.put("lastName", lastName);
                                                                        map.put("message", "Yêu thích");
                                                                        map.put("type", "favorite");
                                                                        map.put("from", user_id);
                                                                        map.put("received", "false");
                                                                        map.put("serviceid", current_id);
                                                                        map.put("time", ServerValue.TIMESTAMP);

                                                                        mRefLastMessage.child(current_id).child(user_id).updateChildren(map).addOnCompleteListener(new OnCompleteListener() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task task) {
                                                                                if (!task.isSuccessful()) {
                                                                                    Toast.makeText(MessageActivity.this, "Có lỗi!", Toast.LENGTH_SHORT).show();
                                                                                }
                                                                            }
                                                                        });
                                                                    }

                                                                    @Override
                                                                    public void onCancelled(DatabaseError databaseError) {

                                                                    }
                                                                });
                                                            } else {
                                                                Toast.makeText(MessageActivity.this, "Có lỗi!", Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    });
                                                } else {
                                                    Toast.makeText(MessageActivity.this, "Có lỗi!", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                    } else {
                                        Toast.makeText(MessageActivity.this, "Có lỗi!", Toast.LENGTH_SHORT).show();
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

    private void refreshMessage() {
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        count++;
                        query = mRefMessage.child(current_id).child(user_id).limitToLast(count * limitItem);
                        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Inbox, InboxViewHolder>(
                                Inbox.class,
                                R.layout.item_bubble,
                                InboxViewHolder.class,
                                query
                        ) {
                            @Override
                            protected void populateViewHolder(final InboxViewHolder viewHolder, Inbox model, int position) {
                                final String key = getRef(position).getKey();
                                viewHolder.setDetails(model.getAvatar(), model.getMessage(), model.getTime());

                                viewHolder.checkSize(current_id, user_id, key);

                            }
                        };
                        refreshLayout.setRefreshing(false);
                        recyclerView.setAdapter(firebaseRecyclerAdapter);
                        firebaseRecyclerAdapter.notifyDataSetChanged();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                recyclerView.smoothScrollToPosition(firebaseRecyclerAdapter.getItemCount() - ((count - 1) * limitItem));
                            }
                        }, 100);
                    }
                }, 300);
            }
        });
    }


    private void adapter() {
        query = mRefMessage.child(current_id).child(user_id).limitToLast(count * limitItem);
        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Inbox, InboxViewHolder>(
                Inbox.class,
                R.layout.item_bubble,
                InboxViewHolder.class,
                query
        ) {
            @Override
            protected void populateViewHolder(final InboxViewHolder viewHolder, Inbox model, int position) {
                final String key = getRef(position).getKey();
                viewHolder.setDetails(model.getAvatar(), model.getMessage(), model.getTime());

                viewHolder.checkSize(current_id, user_id, key);



            }
        };
        recyclerView.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.notifyDataSetChanged();
        recyclerView.smoothScrollToPosition(999999);
    }

    public static class InboxViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout layoutBubble, layoutBubbleRight, layoutLeft, layoutRight;
        CircleImageView ivAvatar;
        TextView tvMessage, tvTime, tvMessageRight, tvTimeRight;

        ImageView ivHeart, ivHeartRight;

        GetTimeAgo timeAgo = new GetTimeAgo();

        DatabaseReference mRefMessage = FirebaseDatabase.getInstance().getReference().child("Message");

        public InboxViewHolder(View itemView) {
            super(itemView);

            layoutBubble = itemView.findViewById(R.id.layoutBubble);
            layoutBubbleRight = itemView.findViewById(R.id.layoutBubbleRight);
            ivAvatar = itemView.findViewById(R.id.ivAvatar);
            tvMessage = itemView.findViewById(R.id.tvMessage);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvMessageRight = itemView.findViewById(R.id.tvMessageRight);
            tvTimeRight = itemView.findViewById(R.id.tvTimeRight);
            layoutLeft = itemView.findViewById(R.id.layoutLeft);
            layoutRight = itemView.findViewById(R.id.layoutRight);

            ivHeart = itemView.findViewById(R.id.ivHeart);
            ivHeartRight = itemView.findViewById(R.id.ivHeartRight);

        }

        public void checkSize(final String current_id, String user_id, final String key) {
            mRefMessage.child(current_id).child(user_id).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child(key).exists()) {
                        String from = dataSnapshot.child(key).child("from").getValue().toString();
                        String type = dataSnapshot.child(key).child("type").getValue().toString();
                        if (current_id.equals(from)) {
                            layoutLeft.setVisibility(View.GONE);
                            layoutRight.setVisibility(View.VISIBLE);
                        } else {
                            layoutRight.setVisibility(View.GONE);
                            layoutLeft.setVisibility(View.VISIBLE);
                        }
                        if (type.equals("text")) {
                            ivHeart.setVisibility(View.INVISIBLE);
                            ivHeartRight.setVisibility(View.INVISIBLE);
                            layoutBubble.setVisibility(View.VISIBLE);
                            layoutBubbleRight.setVisibility(View.VISIBLE);
                        } else if (type.equals("favorite")) {
                            ivHeart.setVisibility(View.VISIBLE);
                            ivHeartRight.setVisibility(View.VISIBLE);
                            layoutBubble.setVisibility(View.INVISIBLE);
                            layoutBubbleRight.setVisibility(View.INVISIBLE);
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        public void setDetails(String avatar, String message, long time) {
            if (!avatar.equals("default")) {
                Picasso.get().load(avatar).placeholder(R.drawable.default_avatar).into(ivAvatar);
            }
            tvMessage.setText(message);
            tvMessageRight.setText(message);
            tvTime.setText(timeAgo.getTimeAgo(time));
            tvTimeRight.setText(timeAgo.getTimeAgo(time));
        }
    }
}
