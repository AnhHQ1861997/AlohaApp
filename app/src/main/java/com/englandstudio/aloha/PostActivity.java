package com.englandstudio.aloha;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

public class PostActivity extends AppCompatActivity {

    //ImageView
    CircleImageView ivAvatar;
    ImageView ivPost;

    //TextView
    TextView tvName;

    //EditText
    EditText etPost;

    //ToolBar
    Toolbar toolbar;

    //Authentication
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    String current_id;

    //Reference
    DatabaseReference mRefUser, mRefPost;

    //Dialog
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        anhXa();
        dialog();
        authentication();
        reference();
        displayInfomation();
        postOnClick();
    }

    private void anhXa() {
        ivAvatar = findViewById(R.id.ivAvatar);
        ivPost = findViewById(R.id.ivPost);

        tvName = findViewById(R.id.tvName);

        etPost = findViewById(R.id.etPost);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
        mRefPost = FirebaseDatabase.getInstance().getReference().child("Post");
    }

    private void displayInfomation() {
        mRefUser.child(current_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String avatar = dataSnapshot.child("avatar").getValue().toString();
                String firstName = dataSnapshot.child("firstName").getValue().toString();
                String lastName = dataSnapshot.child("lastName").getValue().toString();

                if (!avatar.equals("default")) {
                    Picasso.get().load(avatar).into(ivAvatar);
                }
                tvName.setText(firstName + " " + lastName);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void postOnClick() {
        mRefUser.child(current_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                ivPost.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        progressDialog.show();
                        String status = etPost.getText().toString();
                        if (!TextUtils.isEmpty(status)) {
                            String key = mRefPost.push().getKey();
                            String avatar = dataSnapshot.child("avatar").getValue().toString();
                            String firstName = dataSnapshot.child("firstName").getValue().toString();
                            String lastName = dataSnapshot.child("lastName").getValue().toString();

                            Map map = new HashMap();
                            map.put("avatar", avatar);
                            map.put("firstName", firstName);
                            map.put("lastName", lastName);
                            map.put("status", status);
                            map.put("from", current_id);
                            map.put("time", ServerValue.TIMESTAMP);

                            mRefPost.child(key).updateChildren(map).addOnCompleteListener(new OnCompleteListener() {
                                @Override
                                public void onComplete(@NonNull Task task) {
                                    progressDialog.dismiss();
                                    if (task.isSuccessful()) {
                                        Toast.makeText(PostActivity.this, "Đăng thành công!", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(PostActivity.this, "Có lỗi!", Toast.LENGTH_SHORT).show();
                                    }
                                    finish();
                                }
                            });

                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(PostActivity.this, "Không được để trống!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
