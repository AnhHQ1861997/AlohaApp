package com.englandstudio.aloha;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.View;
import android.widget.TextView;

import com.englandstudio.aloha.objects.User;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class SearchActivity extends AppCompatActivity {

    //SearchView
    SearchView searchView;

    //RecyclerView
    RecyclerView recyclerView;
    LinearLayoutManager llm;

    //Adapter
    FirebaseRecyclerAdapter<User, UserViewHolder> firebaseRecyclerAdapter;
    Query query;

    //Authentication
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    String current_id;

    //Reference
    DatabaseReference mRefUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        anhXa();
        authentication();
        reference();
        adapter();
    }

    private void anhXa() {
        searchView = findViewById(R.id.searchView);
        searchView.setIconifiedByDefault(false);

        recyclerView = findViewById(R.id.recyclerView);
        llm = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(llm);
    }

    private void authentication() {
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        current_id = mUser.getUid();
    }

    private void reference() {
        mRefUser = FirebaseDatabase.getInstance().getReference().child("User");
    }

    private void adapter() {

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.equals("")) {
                    query = mRefUser.orderByChild("-").startAt("-").endAt("-");
                } else {
                    if (newText.substring(0, 1).equals("0")) {
                        query = mRefUser.orderByChild("phone").startAt(newText).endAt(newText);
                    } else {
                        query = mRefUser.orderByChild("fullName").startAt(newText.toLowerCase()).endAt(newText.toLowerCase() + "\uf8ff");
                    }
                }
                firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<User, UserViewHolder>(
                        User.class,
                        R.layout.item_search,
                        UserViewHolder.class,
                        query
                ) {
                    @Override
                    protected void populateViewHolder(UserViewHolder viewHolder, User model, int position) {

                        final String user_id = getRef(position).getKey();

                        viewHolder.setDetails(model.getAvatar(), model.getFirstName(), model.getLastName(), model.getTitle());

                        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(SearchActivity.this, ProfileActivity.class);
                                if (current_id.equals(user_id)) {
                                    intent.putExtra("type", "user");
                                } else {
                                    intent.putExtra("type", "guest");
                                }
                                intent.putExtra("user_id", user_id);
                                startActivity(intent);
                                finish();
                            }
                        });
                    }
                };
                recyclerView.setAdapter(firebaseRecyclerAdapter);
                firebaseRecyclerAdapter.notifyDataSetChanged();
                return false;
            }
        });
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {

        CircleImageView ivAvatar;
        TextView tvName, tvTitle;

        public UserViewHolder(View itemView) {
            super(itemView);

            ivAvatar = itemView.findViewById(R.id.ivAvatar);
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
