package com.englandstudio.aloha;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SigninActivity extends AppCompatActivity {

    //EditText
    EditText etEmail, etPassword;

    //CheckBox
    CheckBox checkBox;

    //Button
    Button btSignIn;

    //TextView
    TextView tvSignUp;

    //Authentication
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    String current_id;
    String email, password;

    //Reference
    DatabaseReference mRefUser;

    //Dialog
    ProgressDialog progressDialog;

    //SharedPreferences
    SharedPreferences shared;
    SharedPreferences.Editor editor;

    //Error
    String ex1 = "com.google.firebase.auth.FirebaseAuthInvalidCredentialsException: The email address is badly formatted.";
    String ex2 = "com.google.firebase.auth.FirebaseAuthInvalidUserException: There is no user record corresponding to this identifier. The user may have been deleted.";
    String ex3 = "com.google.firebase.auth.FirebaseAuthInvalidCredentialsException: The password is invalid or the user does not have a password.";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        anhXa();
        sharedPreferences();
        authentication();
        reference();
        dialog();
        checkRemember();
        signInOnClick();
        signUpOnClick();
    }

    private void anhXa() {
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);

        checkBox = findViewById(R.id.checkBox);

        btSignIn = findViewById(R.id.btSignIn);

        tvSignUp = findViewById(R.id.tvSignUp);
    }

    private void sharedPreferences() {
        shared = getSharedPreferences("user", MODE_PRIVATE);
        etEmail.setText(shared.getString("email", ""));
        etPassword.setText(shared.getString("password", ""));
        checkBox.setChecked(shared.getBoolean("checked", false));
    }

    private void authentication() {
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
    }

    private void reference() {
        mRefUser = FirebaseDatabase.getInstance().getReference().child("User");
    }

    private void dialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage("Vui lòng chờ...");
    }

    private void checkRemember() {
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                email = etEmail.getText().toString();
                password = etPassword.getText().toString();
                if (isChecked) {
                    editor = shared.edit();
                    editor.putString("email", email);
                    editor.putString("password", password);
                    editor.putBoolean("checked", true);
                    editor.commit();
                } else {
                    editor = shared.edit();
                    editor.putString("email", "");
                    editor.putString("password", "");
                    editor.putBoolean("checked", false);
                    editor.commit();
                }
            }
        });
    }

    private void signInOnClick() {
        btSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                email = etEmail.getText().toString();
                password = etPassword.getText().toString();

                if (checkBox.isChecked()) {
                    editor = shared.edit();
                    editor.putString("email", email);
                    editor.putString("password", password);
                    editor.putBoolean("checked", true);
                    editor.commit();
                } else {
                    editor = shared.edit();
                    editor.putString("email", "");
                    editor.putString("password", "");
                    editor.putBoolean("checked", false);
                    editor.commit();
                }

                if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
                    mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                progressDialog.dismiss();
                                Intent intent = new Intent(SigninActivity.this, MainActivity.class);
                                startActivity(intent);
                                Toast.makeText(SigninActivity.this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                progressDialog.dismiss();
                                if (task.getException().toString().equals(ex1)) {
                                    Toast.makeText(SigninActivity.this, "Email không hợp lệ!", Toast.LENGTH_SHORT).show();
                                } else if (task.getException().toString().equals(ex2)) {
                                    Toast.makeText(SigninActivity.this, "Tài khoản không tồn tại!", Toast.LENGTH_SHORT).show();
                                } else if (task.getException().toString().equals(ex3)) {
                                    Toast.makeText(SigninActivity.this, "Sai mật khẩu!", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(SigninActivity.this, "Lỗi đăng nhập!" + "\n" + task.getException().toString(), Toast.LENGTH_LONG).show();
                                }
                            }
                        }
                    });
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(SigninActivity.this, "Vui lòng nhập đủ thông tin!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void signUpOnClick() {
        tvSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SigninActivity.this, SignupActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
