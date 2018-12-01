package com.englandstudio.aloha;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;

import com.englandstudio.aloha.service.FriendRequestService;
import com.englandstudio.aloha.service.MessageService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    //ViewPager
    public ViewPager viewPager;
    ViewPagerAdapter viewPagerAdapter;

    //BottomNavigationView
    public BottomNavigationView navigationView;

    //ImageView
    CircleImageView ivAvatar;

    //RelativeLayout
    RelativeLayout layoutSearch;

    //Authentication
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    String current_id;

    //Reference
    DatabaseReference mRefUser, mRefOnline;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        anhXa();
        reference();
        authentication();
        displayAvatar();
        searchOnClick();
        avatarOnClick();
        view();
        navigation();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mUser != null) {
            current_id = mUser.getUid();
            mRefOnline.child(current_id).child("online").setValue(ServerValue.TIMESTAMP);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mUser != null) {
            current_id = mUser.getUid();
            mRefOnline.child(current_id).child("online").setValue(ServerValue.TIMESTAMP);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mUser != null) {
            current_id = mUser.getUid();
            mRefOnline.child(current_id).child("online").setValue("true");
        }
    }

    private void anhXa() {
        viewPager = findViewById(R.id.viewPager);
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.setOffscreenPageLimit(7);

        navigationView = findViewById(R.id.navigationView);

        ivAvatar = findViewById(R.id.ivAvatar);

        layoutSearch = findViewById(R.id.layoutSearch);
    }

    private void authentication() {
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        if (mUser == null) {
            Intent intent = new Intent(MainActivity.this, SigninActivity.class);
            startActivity(intent);
            finish();
        } else {
            current_id = mUser.getUid();
            mRefOnline.child(current_id).child("online").setValue("true");
            startService(new Intent(MainActivity.this, MessageService.class));
            startService(new Intent(MainActivity.this, FriendRequestService.class));
        }
    }

    private void reference() {
        mRefUser = FirebaseDatabase.getInstance().getReference().child("User");
        mRefOnline = FirebaseDatabase.getInstance().getReference().child("Online");
    }

    private void displayAvatar() {
        if (current_id != null) {
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
    }

    private void searchOnClick() {
        layoutSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                startActivity(intent);
            }
        });
    }

    private void avatarOnClick() {
        ivAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                intent.putExtra("type", "user");
                intent.putExtra("user_id", current_id);
                startActivity(intent);
            }
        });
    }

    private void view() {
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                navigationView.getMenu().getItem(position).setChecked(true);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void navigation() {
        navigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.item_newsFeed:
                                viewPager.setCurrentItem(0);
                                break;
                            case R.id.item_message:
                                viewPager.setCurrentItem(1);
                                break;
                            case R.id.item_notification:
                                viewPager.setCurrentItem(2);
                                break;
                            case R.id.item_friend:
                                viewPager.setCurrentItem(3);
                                break;
                            case R.id.item_option:
                                viewPager.setCurrentItem(4);
                                break;
                        }
                        return false;
                    }
                });
    }
}
