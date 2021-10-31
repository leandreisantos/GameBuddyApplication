package com.example.socialmedia;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    EditText emailEt,passEt;
    Button login_btn;
    CheckBox showPass;
    ProgressBar progressBar;
    FirebaseAuth mAuth;
    ImageView back_btn;

    TextView del_email,view_pass;
    Boolean show_pass = false;

    TextView error_icon,txt_eror;

    //Textview for fogotpass
    TextView forgotpass;
    String email,pass;

    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        forgotpass = findViewById(R.id.tv_forgot_pass);
        emailEt = findViewById(R.id.login_email_et);
        passEt = findViewById(R.id.login_password_et);
        back_btn = findViewById(R.id.back);
        login_btn = findViewById(R.id.button_login);
        showPass = findViewById(R.id.showpass_la);
        progressBar = findViewById(R.id.progressbar_login);
        del_email = findViewById(R.id.tv_delete_etEmail);
        view_pass = findViewById(R.id.tv_passview_al);
        error_icon = findViewById(R.id.error_etEmail);
        txt_eror = findViewById(R.id.tv_error_email);
        mAuth = FirebaseAuth.getInstance();

        showPass.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked) passEt.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            else passEt.setTransformationMethod(PasswordTransformationMethod.getInstance());
        });

        back_btn.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this,WaitingActivity.class);
            startActivity(intent);
        });

        login_btn.setOnClickListener(v -> {
            email = emailEt.getText().toString();
            pass = passEt.getText().toString();

            if(!email.matches(emailPattern)){
                error_icon.setVisibility(View.VISIBLE);
                del_email.setVisibility(View.GONE);
                txt_eror.setVisibility(View.VISIBLE);
            }else if(TextUtils.isEmpty(email)){
                error_icon.setVisibility(View.VISIBLE);
                del_email.setVisibility(View.GONE);
                txt_eror.setText("Input some valid Email");
                txt_eror.setVisibility(View.VISIBLE);
            }

            process(true);
            if(!TextUtils.isEmpty(email)&&!TextUtils.isEmpty(pass)){
                mAuth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(task -> {
                    if(task.isSuccessful()) sendtoMain();
                    else {
                        Toast.makeText(LoginActivity.this, "Error:" + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                        process(false);
                    }
                });
            }else {
                Toast.makeText(LoginActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                process(false);
            }
        });

        forgotpass.setOnClickListener(v -> {
            String email = emailEt.getText().toString();
            if(emailEt.getText().toString().trim().equals("")) Toast.makeText(LoginActivity.this, "Input Email first", Toast.LENGTH_SHORT).show();
            else{
                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                builder.setTitle("Reset Password")
                        .setMessage("Are you sure to reset password?")
                        .setPositiveButton("Yes", (dialog, which) -> mAuth.sendPasswordResetEmail(email).addOnSuccessListener(aVoid -> Toast.makeText(LoginActivity.this, "Reset Link sent", Toast.LENGTH_SHORT).show()).addOnFailureListener(e -> Toast.makeText(LoginActivity.this, "Error"+ e, Toast.LENGTH_SHORT).show()))
                        .setNegativeButton("No", (dialog, which) -> {
                        });

                builder.create();
                builder.show();
            }
});
        emailEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(TextUtils.isEmpty(emailEt.getText())) {
                    del_email.setVisibility(View.GONE);
                    error_icon.setVisibility(View.GONE);
                    txt_eror.setVisibility(View.GONE);

                }else{
                    if(emailEt.getText().toString().matches(emailPattern)){
                        del_email.setVisibility(View.VISIBLE);
                        error_icon.setVisibility(View.GONE);
                        txt_eror.setVisibility(View.GONE);
                    }
                }

            }
        });


        del_email.setOnClickListener(v -> emailEt.getText().clear());

        view_pass.setOnClickListener(v -> {
            if(show_pass) {
                passEt.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                view_pass.setBackgroundResource(R.drawable.hidepass_icon);
                show_pass = false;
            }
            else{
                passEt.setTransformationMethod(PasswordTransformationMethod.getInstance());
                view_pass.setBackgroundResource(R.drawable.ic_outline_visibility_24);
                show_pass = true;
            }
        });



    }

    private void process(Boolean what){
        if(what) {
            progressBar.setVisibility(View.VISIBLE);
            emailEt.setEnabled(false);
            passEt.setEnabled(false);
            forgotpass.setEnabled(false);
            login_btn.setEnabled(false);
            showPass.setEnabled(false);
            back_btn.setEnabled(false);
        }else{
            progressBar.setVisibility(View.GONE);
            emailEt.setEnabled(true);
            passEt.setEnabled(true);
            forgotpass.setEnabled(true);
            login_btn.setEnabled(true);
            showPass.setEnabled(true);
            back_btn.setEnabled(true);
        }
    }

    private void sendtoMain() {
        Intent intent = new Intent(LoginActivity.this,MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null){
            Intent intent = new Intent(LoginActivity.this,MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(this, "Login your Account", Toast.LENGTH_SHORT).show();
    }
}