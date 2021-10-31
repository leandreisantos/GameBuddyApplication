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
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
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
    String keyoption;
    LottieAnimationView lot;
    TextView title;

//    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//    String currentuid = user.getUid();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //postmember = new Postmember();

        //databaseReference = database.getReference("newKey");

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            keyoption = bundle.getString("k");
        }else{
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
        }

        emailEt = findViewById(R.id.register_email_et);
        passEt = findViewById(R.id.register_password_et);
        title = findViewById(R.id.txtcreate);
        confirm_pass = findViewById(R.id.register_confirmpassword_et);
        register_btn = findViewById(R.id.button_register);
        lot = findViewById(R.id.registerlot);
        back_btn = findViewById(R.id.back);
        //login_btn = findViewById(R.id.signup_to_login);
        checkBox = findViewById(R.id.register_checkbox);
        progressBar = findViewById(R.id.progressbar_login);
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
            onBackPressed();
        });

        register_btn.setOnClickListener(v -> {
            String email = emailEt.getText().toString();
            String pass = passEt.getText().toString();
            String confirm_password = confirm_pass.getText().toString();
            process(true);
            if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(pass) && !TextUtils.isEmpty(confirm_password)){
                if(pass.equals(confirm_password)){
                    //progressBar.setVisibility(View.VISIBLE);
                    mAuth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(task -> {
                        if(task.isSuccessful()){
//                                    postmember.setUid(currentuid);
//                                    databaseReference.child(currentuid).setValue(currentuid);
                            sendtoMain();
                            progressBar.setVisibility(View.INVISIBLE);
                        }else{
                            process(false);
                            String error = Objects.requireNonNull(task.getException()).getMessage();
                            Toast.makeText(RegisterActivity.this, "Error:"+error, Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    });
                }else{
                    //progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(RegisterActivity.this, "password and confirm password is not matching", Toast.LENGTH_SHORT).show();
                    process(false);
                }
            }else{
                Toast.makeText(RegisterActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                process(false);

            }
        });


    }

    private void sendtoMain() {
        Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void process(Boolean what){
        if(what) {
            progressBar.setVisibility(View.VISIBLE);
            emailEt.setEnabled(false);
            passEt.setEnabled(false);
            register_btn.setEnabled(false);
            checkBox.setEnabled(false);
            back_btn.setEnabled(false);
        }else{
            progressBar.setVisibility(View.GONE);
            emailEt.setEnabled(true);
            passEt.setEnabled(true);
            register_btn.setEnabled(true);
            checkBox.setEnabled(true);
            back_btn.setEnabled(true);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(keyoption.equals("gd")){
            lot.setAnimation(R.raw.addgamelot);
            title.setText("CREATE GAME DEVELOPER ACCOUNT");
        }
        if(keyoption.equals("c")){
            lot.setAnimation(R.raw.companylot);
            title.setText("CREATE COMPANY ACCOUNT");
        }


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){
            sendtoMain();
        }
    }
}