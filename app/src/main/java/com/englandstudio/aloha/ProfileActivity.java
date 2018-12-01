package com.englandstudio.aloha;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.englandstudio.aloha.objects.Post;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    //Toolbar
    Toolbar toolbar;

    //ImageView
    ImageView ivImage, ivGender, ivSetting;
    CircleImageView ivAvatar;

    //TextView
    TextView tvName, tvTitle, tvPhone, tvLocal;
    TextView tvAddFriend;

    //RelativeLayout
    RelativeLayout layoutOption, layoutAddFriend, layoutInbox;

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
    String current_id, user_id;
    String type;

    //Reference
    DatabaseReference mRefUser, mRefPost, mRefComment, mRefFavorite, mRefFriend, mRefAddFriend;
    //Dialog
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        user_id = getIntent().getStringExtra("user_id");
        type = getIntent().getStringExtra("type");

        anhXa();
        authentication();
        reference();
        dialog();
        checkUser();
        displayInfomation();
        checkFriend();
        optionOnClick();
        settingOnClick();
        adapter();
    }

    private void anhXa() {
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ivImage = findViewById(R.id.ivImage);
        ivGender = findViewById(R.id.ivGender);
        ivSetting = findViewById(R.id.ivSettings);
        ivAvatar = findViewById(R.id.ivAvatar);

        tvName = findViewById(R.id.tvName);
        tvTitle = findViewById(R.id.tvTitle);
        tvLocal = findViewById(R.id.tvLocal);
        tvPhone = findViewById(R.id.tvPhone);
        tvAddFriend = findViewById(R.id.tvAddFriend);

        layoutOption = findViewById(R.id.layoutOption);
        layoutAddFriend = findViewById(R.id.layoutAddFriend);
        layoutInbox = findViewById(R.id.layoutInbox);

        recyclerView = findViewById(R.id.recyclerView);
        llm = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(llm);
        llm.setReverseLayout(true);
        llm.setStackFromEnd(true);
        recyclerView.setNestedScrollingEnabled(false);
        scrollView = findViewById(R.id.scrollView);
    }

    private void authentication() {
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        current_id = mUser.getUid();
    }

    private void reference() {
        mRefUser = FirebaseDatabase.getInstance().getReference().child("User");
        mRefPost = FirebaseDatabase.getInstance().getReference().child("Post");
        mRefComment = FirebaseDatabase.getInstance().getReference().child("Comment");
        mRefFavorite = FirebaseDatabase.getInstance().getReference().child("Favorite");
        mRefFriend = FirebaseDatabase.getInstance().getReference().child("Friend");
        mRefAddFriend = FirebaseDatabase.getInstance().getReference().child("AddFriend");
    }

    private void dialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage("Vui lòng chờ...");
    }

    private void checkUser() {
        if (!type.equals("user")) {
            ivSetting.setVisibility(View.GONE);
        } else {
            layoutOption.setVisibility(View.GONE);
        }
    }

    private void displayInfomation() {
        mRefUser.child(user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String avatar = dataSnapshot.child("avatar").getValue().toString();
                String firstName = dataSnapshot.child("firstName").getValue().toString();
                String lastName = dataSnapshot.child("lastName").getValue().toString();
                String title = dataSnapshot.child("title").getValue().toString();
                String local = dataSnapshot.child("local").getValue().toString();
                String phone = dataSnapshot.child("phone").getValue().toString();
                String gender = dataSnapshot.child("gender").getValue().toString();

                if (!avatar.equals("default")) {
                    Picasso.get().load(avatar).placeholder(R.drawable.default_avatar).into(ivAvatar);
                }
                if (gender.equals("Male")) {
                    ivGender.setImageDrawable(getDrawable(R.drawable.masculine));
                } else {
                    ivGender.setImageDrawable(getDrawable(R.drawable.femenine));
                }
                tvName.setText(firstName + " " + lastName);
                tvTitle.setText(title);
                tvLocal.setText(local);
                tvPhone.setText(phone);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void checkFriend() {
        mRefFriend.child(current_id).child(user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    tvAddFriend.setText("Bạn bè");
                } else {
                    tvAddFriend.setText("Thêm bạn bè");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        mRefAddFriend.child(current_id).child(user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String type = dataSnapshot.child("type").getValue().toString();
                    if (type.equals("sent")) {
                        tvAddFriend.setText("Hủy lời mời");
                    } else {
                        tvAddFriend.setText("Yêu cầu kết bạn");
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void optionOnClick() {
        layoutAddFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String check = tvAddFriend.getText().toString();
                if (check.equals("Thêm bạn bè")) {
                    progressDialog.show();
                    mRefUser.child(user_id).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            final String avatar = dataSnapshot.child("avatar").getValue().toString();
                            final String firstName = dataSnapshot.child("firstName").getValue().toString();
                            final String lastName = dataSnapshot.child("lastName").getValue().toString();

                            Map mapSent = new HashMap();
                            mapSent.put("type", "sent");
                            mapSent.put("avatar", avatar);
                            mapSent.put("firstName", firstName);
                            mapSent.put("lastName", lastName);
                            mapSent.put("received", "false");
                            mapSent.put("from", current_id);
                            mapSent.put("time", ServerValue.TIMESTAMP);

                            mRefAddFriend.child(current_id).child(user_id).updateChildren(mapSent).addOnCompleteListener(new OnCompleteListener() {
                                @Override
                                public void onComplete(@NonNull Task task) {
                                    if (task.isSuccessful()) {
                                        mRefUser.child(current_id).addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                final String avatar = dataSnapshot.child("avatar").getValue().toString();
                                                final String firstName = dataSnapshot.child("firstName").getValue().toString();
                                                final String lastName = dataSnapshot.child("lastName").getValue().toString();

                                                Map mapReceived = new HashMap();
                                                mapReceived.put("type", "received");
                                                mapReceived.put("avatar", avatar);
                                                mapReceived.put("firstName", firstName);
                                                mapReceived.put("lastName", lastName);
                                                mapReceived.put("from", current_id);
                                                mapReceived.put("received", "false");
                                                mapReceived.put("time", ServerValue.TIMESTAMP);

                                                mRefAddFriend.child(user_id).child(current_id).updateChildren(mapReceived).addOnCompleteListener(new OnCompleteListener() {
                                                    @Override
                                                    public void onComplete(@NonNull Task task) {
                                                        progressDialog.dismiss();
                                                        if (task.isSuccessful()) {
                                                            tvAddFriend.setText("Hủy lời mời");
                                                        } else {
                                                            Toast.makeText(ProfileActivity.this, "Có lỗi!", Toast.LENGTH_SHORT).show();
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
                                        Toast.makeText(ProfileActivity.this, "Có lỗi!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                } else if (check.equals("Hủy lời mời")) {
                    progressDialog.show();
                    mRefAddFriend.child(current_id).child(user_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                mRefAddFriend.child(user_id).child(current_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        progressDialog.dismiss();
                                        if (task.isSuccessful()) {
                                            tvAddFriend.setText("Thêm bạn bè");
                                            Toast.makeText(ProfileActivity.this, "Đã hủy lời mời kết bạn", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(ProfileActivity.this, "Có lỗi!", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            } else {
                                progressDialog.dismiss();
                                Toast.makeText(ProfileActivity.this, "Có lỗi!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else if (check.equals("Yêu cầu kết bạn")) {
                    CharSequence[] options = new CharSequence[]{"Đồng ý", "Từ chối"};
                    AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
                    builder.setTitle("Yêu cầu kết bạn");
                    builder.setItems(options, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (which == 0) {
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
                                                                                            if (task.isSuccessful()) {
                                                                                                tvAddFriend.setText("Bạn bè");
                                                                                            } else {
                                                                                                Toast.makeText(ProfileActivity.this, "Có lỗi!", Toast.LENGTH_SHORT).show();
                                                                                            }
                                                                                        }
                                                                                    });
                                                                                } else {
                                                                                    progressDialog.dismiss();
                                                                                    Toast.makeText(ProfileActivity.this, "Có lỗi!", Toast.LENGTH_SHORT).show();
                                                                                }
                                                                            }
                                                                        });
                                                                    } else {
                                                                        progressDialog.dismiss();
                                                                        Toast.makeText(ProfileActivity.this, "Có lỗi!", Toast.LENGTH_SHORT).show();
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
                                                    Toast.makeText(ProfileActivity.this, "Có lỗi!", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });

                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            }
                            if (which == 1) {
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
                                                        tvAddFriend.setText("Thêm bạn bè");
                                                        Toast.makeText(ProfileActivity.this, "Từ chối lời mời kết bạn", Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        progressDialog.dismiss();
                                                        Toast.makeText(ProfileActivity.this, "Có lỗi!", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                        } else {
                                            progressDialog.dismiss();
                                            Toast.makeText(ProfileActivity.this, "Có lỗi!", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        }
                    });
                    builder.show();
                } else if (check.equals("Bạn bè")) {
                    CharSequence[] options = new CharSequence[]{"Đồng ý", "Từ chối"};
                    AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
                    builder.setTitle("Bạn muốn hủy kết bạn?");
                    builder.setItems(options, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (which == 0) {
                                progressDialog.show();
                                mRefFriend.child(current_id).child(user_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            mRefFriend.child(user_id).child(current_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    progressDialog.dismiss();
                                                    if (task.isSuccessful()) {
                                                        tvAddFriend.setText("Thêm bạn bè");
                                                        Toast.makeText(ProfileActivity.this, "Đã hủy kết bạn", Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        Toast.makeText(ProfileActivity.this, "Có lỗi!", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                        } else {
                                            progressDialog.dismiss();
                                            Toast.makeText(ProfileActivity.this, "Có lỗi!", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                            if (which == 1) {

                            }
                        }
                    });
                    builder.show();
                }
            }
        });

        layoutInbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, MessageActivity.class);
                intent.putExtra("user_id", user_id);
                startActivity(intent);
                finish();
            }
        });
    }

    private void settingOnClick() {
        mRefUser.child(user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                ivSetting.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String avatar = dataSnapshot.child("avatar").getValue().toString();
                        String firstName = dataSnapshot.child("firstName").getValue().toString();
                        String lastName = dataSnapshot.child("lastName").getValue().toString();
                        String email = dataSnapshot.child("email").getValue().toString();
                        String title = dataSnapshot.child("title").getValue().toString();
                        String local = dataSnapshot.child("local").getValue().toString();
                        String phone = dataSnapshot.child("phone").getValue().toString();
                        String gender = dataSnapshot.child("gender").getValue().toString();

                        Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
                        intent.putExtra("avatar", avatar);
                        intent.putExtra("firstName", firstName);
                        intent.putExtra("lastName", lastName);
                        intent.putExtra("email", email);
                        intent.putExtra("title", title);
                        intent.putExtra("local", local);
                        intent.putExtra("phone", phone);
                        intent.putExtra("gender", gender);
                        startActivity(intent);
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void adapter() {
        Long tsLong = System.currentTimeMillis() / 1000;
        Query query = mRefPost.orderByChild("from").startAt(user_id).endAt(user_id);

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Post, PostViewHolder>(
                Post.class,
                R.layout.item_post,
                PostViewHolder.class,
                query
        ) {
            @Override
            protected void populateViewHolder(final PostViewHolder viewHolder, Post model, int position) {
                final String key = getRef(position).getKey();

                viewHolder.setDetails(model.getAvatar(), model.getFirstName(), model.getLastName(), model.getStatus(), model.getTime());


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

                                Intent intent = new Intent(ProfileActivity.this, CommentActivity.class);
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
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                scrollView.scrollTo(0, 0);
                scrollView.fullScroll(View.FOCUS_UP);
            }
        }, 200);
    }

    @Override
    public void onResume() {
        super.onResume();
        scrollView.scrollTo(0, 0);
        scrollView.fullScroll(View.FOCUS_UP);
    }

    @Override
    protected void onStart() {
        super.onStart();
        scrollView.scrollTo(0, 0);
        scrollView.fullScroll(View.FOCUS_UP);
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
