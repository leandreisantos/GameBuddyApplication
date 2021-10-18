package com.example.socialmedia;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {

    EditText emailEt,passEt,confirm_pass;
    Button register_btn;
    CheckBox checkBox;
    ProgressBar progressBar;
    FirebaseAuth mAuth;
    ImageView back_btn;

//    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//    String currentuid = user.getUid();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //postmember = new Postmember();

        //databaseReference = database.getReference("newKey");

        emailEt = findViewById(R.id.register_email_et);
        passEt = findViewById(R.id.register_password_et);
        confirm_pass = findViewById(R.id.register_confirmpassword_et);
        register_btn = findViewById(R.id.button_register);
        back_btn = findViewById(R.id.back);
        //login_btn = findViewById(R.id.signup_to_login);
        checkBox = findViewById(R.id.register_checkbox);
        progressBar = findViewById(R.id.progressbar_register);
        mAuth = FirebaseAuth.getInstance();

        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked){
                passEt.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                confirm_pass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());

            }else{
                passEt.setTransformationMethod(PasswordTransformationMethod.getInstance());
                confirm_pass.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
        });

        back_btn.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this,WaitingActivity.class);
            startActivity(intent);
        });

        register_btn.setOnClickListener(v -> {
            String email = emailEt.getText().toString();
            String pass = passEt.getText().toString();
            String confirm_password = confirm_pass.getText().toString();

            if(!TextUtils.isEmpty(email) || !TextUtils.isEmpty(pass) || !TextUtils.isEmpty(confirm_password)){
                if(pass.equals(confirm_password)){
                    progressBar.setVisibility(View.VISIBLE);
                    mAuth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(task -> {
                        if(task.isSuccessful()){
//                                    postmember.setUid(currentuid);
//                                    databaseReference.child(currentuid).setValue(currentuid);
                            sendtoMain();
                            progressBar.setVisibility(View.INVISIBLE);
                        }else{
                            String error = Objects.requireNonNull(task.getException()).getMessage();
                            Toast.makeText(RegisterActivity.this, "Error:"+error, Toast.LENGTH_SHORT).show();
                        }
                    });
                }else{
                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(RegisterActivity.this, "password and confirm password is not matching", Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(RegisterActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();

            }
        });

//        login_btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
//                startActivity(intent);
//            }
//        });

    }

    private void sendtoMain() {
        Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){
            sendtoMain();
        }
    }
}