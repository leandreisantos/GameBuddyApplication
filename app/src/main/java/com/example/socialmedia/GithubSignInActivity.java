package com.example.socialmedia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.OAuthProvider;

import java.util.ArrayList;
import java.util.List;

public class GithubSignInActivity extends AppCompatActivity {

    EditText gitEmail;
    Button btn;
    ProgressBar pb;

    FirebaseAuth mAuth;

    OAuthProvider.Builder provider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_github_sign_in);

        mAuth = FirebaseAuth.getInstance();

        gitEmail = findViewById(R.id.et_email_git);
        btn = findViewById(R.id.button_login);
        pb = findViewById(R.id.pb_git);


        btn.setOnClickListener(v -> {
            if(TextUtils.isEmpty(gitEmail.getText().toString())) {
                Toast.makeText(GithubSignInActivity.this, "Please input Email", Toast.LENGTH_SHORT).show();
            }else{
                pb.setVisibility(View.VISIBLE);
                gitEmail.setEnabled(false);
                btn.setEnabled(false);
               SigInWithGithubProvider(
                       OAuthProvider.newBuilder("github.com")
                               .addCustomParameter("Github Email", gitEmail.getText().toString()).setScopes(
                               new ArrayList<String>() {
                                   {
                                       add("user:email");
                                   }
                               }).build()
               );
            }
        });


    }

    private void SigInWithGithubProvider(OAuthProvider github_email) {

        Task<AuthResult> pendingResultTask = mAuth.getPendingAuthResult();
        if (pendingResultTask != null) {
            // There's something already here! Finish the sign-in for your user.
            pendingResultTask
                    .addOnSuccessListener(
                            authResult -> {
                                // User is signed in.
                                // IdP data available in
                                // authResult.getAdditionalUserInfo().getProfile().
                                // The OAuth access token can also be retrieved:
                                // authResult.getCredential().getAccessToken().

                                Toast.makeText(GithubSignInActivity.this, "User Exist", Toast.LENGTH_SHORT).show();
                                pb.setVisibility(View.GONE);
                                gitEmail.setEnabled(true);
                                btn.setEnabled(true);
                            })
                    .addOnFailureListener(
                            e -> {
                                // Handle failure.
                                Toast.makeText(GithubSignInActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                pb.setVisibility(View.GONE);
                                gitEmail.setEnabled(true);
                                btn.setEnabled(true);
                            });
        } else {
            // There's no pending result so you need to start the sign-in flow.
            // See below.
            mAuth
                    .startActivityForSignInWithProvider(/* activity= */ this, provider.build())
                    .addOnSuccessListener(
                            authResult -> {
                                // User is signed in.
                                // IdP data available in
                                // authResult.getAdditionalUserInfo().getProfile().
                                // The OAuth access token can also be retrieved:
                                // authResult.getCredential().getAccessToken().
                                Toast.makeText(GithubSignInActivity.this, "Login Succesfully", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(GithubSignInActivity.this,MainActivity.class);
                                startActivity(intent);
                            })
                    .addOnFailureListener(
                            e -> {
                                // Handle failure.
                                Toast.makeText(GithubSignInActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                pb.setVisibility(View.GONE);
                                gitEmail.setEnabled(true);
                                btn.setEnabled(true);
                            });
        }
    }

}