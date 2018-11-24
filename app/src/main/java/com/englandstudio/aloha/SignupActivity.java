package com.englandstudio.aloha;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class SignupActivity extends AppCompatActivity {

    //EditText
    EditText etFirstName, etLastName, etEmail, etPhone, etPassword, etConfirmPassword;

    //RadioButton
    RadioButton rbtMale, rbtFemale;

    //Button
    Button btSignUp;

    //TextView
    TextView tvSignIn;

    //Authentication
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    String current_id;
    String firstName, lastName, email, phone, password, confirmPassword, gender;

    //Reference
    DatabaseReference mRefUser;

    //Dialog
    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        anhXa();
        dialog();
        authentication();
        reference();
        checkGender();
        signUpOnClick();
        signInOnClick();

    }

    private void anhXa() {
        etFirstName = findViewById(R.id.etFirstName);
        etLastName = findViewById(R.id.etLastName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);

        rbtMale = findViewById(R.id.rbtMale);
        rbtFemale = findViewById(R.id.rbtFemale);

        btSignUp = findViewById(R.id.btSignUp);

        tvSignIn = findViewById(R.id.tvSignIn);
    }

    private void dialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage("Vui lòng chờ...");
    }

    private void authentication() {
        mAuth = FirebaseAuth.getInstance();
    }

    private void reference() {
        mRefUser = FirebaseDatabase.getInstance().getReference().child("User");
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

    private void signUpOnClick() {
        btSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                firstName = etFirstName.getText().toString();
                lastName = etLastName.getText().toString();
                email = etEmail.getText().toString();
                phone = etPhone.getText().toString();
                password = etPassword.getText().toString();
                confirmPassword = etConfirmPassword.getText().toString();
                if (rbtMale.isChecked()) {
                    gender = "Male";
                } else {
                    gender = "Female";
                }
                if (!TextUtils.isEmpty(firstName) &&
                        !TextUtils.isEmpty(lastName) &&
                        !TextUtils.isEmpty(email) &&
                        !TextUtils.isEmpty(phone) &&
                        !TextUtils.isEmpty(password) &&
                        !TextUtils.isEmpty(confirmPassword)) {
                    if (isValid(email)) {
                        if (password.equals(confirmPassword)) {
                            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        mUser = mAuth.getCurrentUser();
                                        current_id = mUser.getUid();

                                        Map map = new HashMap();
                                        map.put("firstName", firstName);
                                        map.put("lastName", lastName);
                                        map.put("fullName", firstName.toLowerCase() + " " + lastName.toLowerCase());
                                        map.put("email", email);
                                        map.put("phone", phone);
                                        map.put("password", password);
                                        map.put("gender", gender);
                                        map.put("avatar", "default");
                                        map.put("title", "Chào mừng đến với Aloha!");
                                        map.put("local", "Vô gia cư ^^");
                                        map.put("id", current_id);

                                        mRefUser.child(current_id).updateChildren(map).addOnCompleteListener(new OnCompleteListener() {
                                            @Override
                                            public void onComplete(@NonNull Task task) {
                                                if (task.isSuccessful()) {
                                                    progressDialog.dismiss();
                                                    Toast.makeText(SignupActivity.this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
                                                    Intent intent = new Intent(SignupActivity.this, MainActivity.class);
                                                    startActivity(intent);
                                                    finish();
                                                } else {
                                                    progressDialog.dismiss();
                                                    Toast.makeText(SignupActivity.this, "Lỗi đăng ký!", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                    } else {
                                        progressDialog.dismiss();
                                        Toast.makeText(SignupActivity.this, "Lỗi đăng ký!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(SignupActivity.this, "Mật khẩu không giống nhau!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(SignupActivity.this, "Email không hợp lệ!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(SignupActivity.this, "Vui lòng nhập đủ thông tin!", Toast.LENGTH_SHORT).show();
                }


            }
        });
    }

    private void signInOnClick() {
        tvSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignupActivity.this, SigninActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    public boolean isValid(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\." +
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";

        Pattern pat = Pattern.compile(emailRegex);
        if (email == null)
            return false;
        return pat.matcher(email).matches();
    }
}
