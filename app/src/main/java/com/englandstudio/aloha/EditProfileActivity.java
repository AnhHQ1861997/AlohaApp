package com.englandstudio.aloha;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileActivity extends AppCompatActivity {

    //EditText
    EditText etfirstName, etLastName, etEmail, etTitle, etPhone;
    Spinner etLocal;

    //ArrayList
    ArrayList<String> ls;
    ArrayAdapter<String> arrayAdapter;

    //RadioButton
    RadioButton rbtMale, rbtFemale;

    //ImageView
    CircleImageView ivAvatar;
    ImageView ivUpdate;

    //ToolBar
    Toolbar toolbar;

    //Infomation
    String avatar, firstName, lastName, email, title, local, phone, gender;

    //Authentication
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    String current_id;

    //Reference
    DatabaseReference mRefUser, mRefPost;

    //Storage
    StorageReference mStorage;

    //Dialog
    ProgressDialog progressDialog;

    //AvatarChoosing
    Uri filePath;
    final int PICK_IMAGE_REQUEST = 71;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        anhXa();
        dialog();
        authentication();
        reference();
        storage();
        getLocal();
        getInfomation();
        checkGender();
        displayInfomation();
        chooseAvatar();
        updateInfomation();
    }

    private void anhXa() {
        etfirstName = findViewById(R.id.etFirstName);
        etLastName = findViewById(R.id.etLastName);
        etEmail = findViewById(R.id.etEmail);
        etTitle = findViewById(R.id.etTitle);
        etLocal = findViewById(R.id.etLocal);
        etPhone = findViewById(R.id.etPhone);

        rbtMale = findViewById(R.id.rbtMale);
        rbtFemale = findViewById(R.id.rbtFemale);

        ivAvatar = findViewById(R.id.ivAvatar);
        ivUpdate = findViewById(R.id.ivUpdate);

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

    private void checkGender() {
        rbtMale.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    rbtFemale.setChecked(false);
                }
            }
        });
        rbtFemale.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    rbtMale.setChecked(false);
                }
            }
        });
    }

    private void storage() {
        mStorage = FirebaseStorage.getInstance().getReference();
    }

    private void getLocal() {
        ls = new ArrayList<>();

        ls.add("Vô gia cư ^^");

        ls.add("An Giang");
        ls.add("Bà Rịa - Vũng Tàu");
        ls.add("Bắc Giang");
        ls.add("Bắc Kạn");
        ls.add("Bạc Liêu");
        ls.add("Bắc Ninh");
        ls.add("Bến Tre");
        ls.add("Bình Định");
        ls.add("Bình Dương");
        ls.add("Bình Phước");
        ls.add("Bình Thuận");
        ls.add("Cà Mau");
        ls.add("Cao Bằng");
        ls.add("Cần Thơ");
        ls.add("Đà Nẵng");
        ls.add("Đắk Lắk");
        ls.add("Đắk Nông");
        ls.add("Điện Biên");
        ls.add("Đồng Nai");
        ls.add("Đồng Tháp");
        ls.add("Gia Lai");
        ls.add("Hà Giang");
        ls.add("Hà Nam");
        ls.add("Hà Nội");
        ls.add("Hà Tĩnh");
        ls.add("Hải Dương");
        ls.add("Hải Phòng");
        ls.add("Hậu Giang");
        ls.add("Hòa Bình");
        ls.add("Hưng Yên");
        ls.add("Khánh Hòa");
        ls.add("Kiên Giang");
        ls.add("Kon Tum");
        ls.add("Lai Châu");
        ls.add("Lâm Đồng");
        ls.add("Lạng Sơn");
        ls.add("Lào Cai");
        ls.add("Long An");
        ls.add("Nam Định");
        ls.add("Nghệ An");
        ls.add("Ninh Bình");
        ls.add("Ninh Thuận");
        ls.add("Phú Thọ");
        ls.add("Quảng Bình");
        ls.add("Quảng Nam");
        ls.add("Quảng Ngãi");
        ls.add("Quảng Ninh");
        ls.add("Quảng Trị");
        ls.add("Sóc Trăng");
        ls.add("Sơn La");
        ls.add("Tây Ninh");
        ls.add("Thái Bình");
        ls.add("Thái Nguyên");
        ls.add("Thanh Hóa");
        ls.add("Thừa Thiên Huế");
        ls.add("Tiền Giang");
        ls.add("TP HCM");
        ls.add("Trà Vinh");
        ls.add("Tuyên Quang");
        ls.add("Vĩnh Long");
        ls.add("Vĩnh Phúc");
        ls.add("Yên Bái");
        ls.add("Phú Yên");

        arrayAdapter = new ArrayAdapter<String>(this, R.layout.item_spinner, ls);
        etLocal.setAdapter(arrayAdapter);

    }

    private void getInfomation() {
        avatar = getIntent().getStringExtra("avatar");
        firstName = getIntent().getStringExtra("firstName");
        lastName = getIntent().getStringExtra("lastName");
        email = getIntent().getStringExtra("email");
        title = getIntent().getStringExtra("title");
        local = getIntent().getStringExtra("local");
        phone = getIntent().getStringExtra("phone");
        gender = getIntent().getStringExtra("gender");
    }

    private void displayInfomation() {
        if (!avatar.equals("default")) {
            Picasso.get().load(avatar).placeholder(R.drawable.default_avatar).into(ivAvatar);
        }
        etfirstName.setText(firstName);
        etLastName.setText(lastName);
        etEmail.setText(email);
        etTitle.setText(title);
        for (int i = 0; i < ls.size(); i++) {
            if (ls.get(i).equals(local)) {
                etLocal.setSelection(i);
            }
        }
        etPhone.setText(phone);
        if (gender.equals("Male")) {
            rbtMale.setChecked(true);
        } else {
            rbtFemale.setChecked(true);
        }
    }

    private void chooseAvatar() {
        ivAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Chọn ảnh"), PICK_IMAGE_REQUEST);
            }
        });
    }

    private void updateInfomation() {
        ivUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (filePath != null) {
                    String key = mRefUser.child(current_id).push().getKey();
                    final StorageReference ref = mStorage.child("image/" + current_id + "/" +  key + ".jpg");
                    ref.putFile(filePath).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if (task.isSuccessful()) {
                                ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        avatar = uri.toString();
                                        firstName = etfirstName.getText().toString();
                                        lastName = etLastName.getText().toString();
                                        title = etTitle.getText().toString();
                                        local = etLocal.getSelectedItem().toString();
                                        phone = etPhone.getText().toString();
                                        if (rbtMale.isChecked()) {
                                            gender = "Male";
                                        } else {
                                            gender = "Female";
                                        }

                                        if (!TextUtils.isEmpty(firstName) &&
                                                !TextUtils.isEmpty(lastName) &&
                                                !TextUtils.isEmpty(title) &&
                                                !TextUtils.isEmpty(phone)) {

                                            Map map = new HashMap();
                                            map.put("avatar", avatar);
                                            map.put("firstName", firstName);
                                            map.put("lastName", lastName);
                                            map.put("fullName", firstName.toLowerCase() + " " + lastName.toLowerCase());
                                            map.put("title", title);
                                            map.put("local", local);
                                            map.put("phone", phone);
                                            map.put("gender", gender);

                                            mRefUser.child(current_id).updateChildren(map).addOnCompleteListener(new OnCompleteListener() {
                                                @Override
                                                public void onComplete(@NonNull Task task) {
                                                    if (task.isSuccessful()) {
                                                        mRefPost.addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                                                                    String key = snap.getKey();
                                                                    String user_id = snap.child("from").getValue().toString();
                                                                    Map postMap = new HashMap();
                                                                    postMap.put("avatar", avatar);
                                                                    postMap.put("firstName", firstName);
                                                                    postMap.put("lastName", lastName);
                                                                    if (current_id.equals(user_id)) {
                                                                        mRefPost.child(key).updateChildren(postMap);
                                                                    }
                                                                }
                                                            }

                                                            @Override
                                                            public void onCancelled(DatabaseError databaseError) {

                                                            }
                                                        });
                                                        Toast.makeText(EditProfileActivity.this, "Cập nhật thông tin thành công!", Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        Toast.makeText(EditProfileActivity.this, "Cập nhật không thành công!", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                        } else {
                                            Toast.makeText(EditProfileActivity.this, "Vui lòng nhập đủ thông tin!", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            } else {
                                Toast.makeText(EditProfileActivity.this, "Có lỗi!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    firstName = etfirstName.getText().toString();
                    lastName = etLastName.getText().toString();
                    title = etTitle.getText().toString();
                    local = etLocal.getSelectedItem().toString();
                    phone = etPhone.getText().toString();
                    if (rbtMale.isChecked()) {
                        gender = "Male";
                    } else {
                        gender = "Female";
                    }

                    if (!TextUtils.isEmpty(firstName) &&
                            !TextUtils.isEmpty(lastName) &&
                            !TextUtils.isEmpty(title) &&
                            !TextUtils.isEmpty(local) &&
                            !TextUtils.isEmpty(phone)) {

                        Map map = new HashMap();
                        map.put("avatar", avatar);
                        map.put("firstName", firstName);
                        map.put("lastName", lastName);
                        map.put("fullName", firstName + " " + lastName);
                        map.put("title", title);
                        map.put("local", local);
                        map.put("phone", phone);
                        map.put("gender", gender);

                        mRefUser.child(current_id).updateChildren(map).addOnCompleteListener(new OnCompleteListener() {
                            @Override
                            public void onComplete(@NonNull Task task) {
                                if (task.isSuccessful()) {
                                    mRefPost.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            for (DataSnapshot snap : dataSnapshot.getChildren()) {
                                                String key = snap.getKey();
                                                String user_id = snap.child("from").getValue().toString();
                                                Map postMap = new HashMap();
                                                postMap.put("avatar", avatar);
                                                postMap.put("firstName", firstName);
                                                postMap.put("lastName", lastName);
                                                if (current_id.equals(user_id)) {
                                                    mRefPost.child(key).updateChildren(postMap);
                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                                    Toast.makeText(EditProfileActivity.this, "Cập nhật thông tin thành công!", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(EditProfileActivity.this, "Cập nhật không thành công!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {
                        Toast.makeText(EditProfileActivity.this, "Vui lòng nhập đủ thông tin!", Toast.LENGTH_SHORT).show();
                    }
                }
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                ivAvatar.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
