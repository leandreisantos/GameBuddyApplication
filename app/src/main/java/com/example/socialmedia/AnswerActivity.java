package com.example.socialmedia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class AnswerActivity extends AppCompatActivity {

    String uid,que,postkey;
    EditText editText;
    Button button;
    AnswerMember member;
    databaseReference dbr = new databaseReference();
    FirebaseDatabase database = FirebaseDatabase.getInstance(dbr.keyDb());
    DatabaseReference Allquestions,ntref;
    String name,url,time,usertoken;


    NewMember newMember;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer);

        newMember = new NewMember();
        member = new AnswerMember();
        editText = findViewById(R.id.answer_et);
        button = findViewById(R.id.btn_answer_submit);
        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            uid = bundle.getString("u");
            postkey = bundle.getString("p");
            que = bundle.getString("q");
        }else {
            Toast.makeText(this, "No extras", Toast.LENGTH_SHORT).show();
        }

        Allquestions = database.getReference("All Questions").child(postkey).child("Answer");
        ntref = database.getReference("notification").child(uid);


        button.setOnClickListener(v -> saveAnswer());
    }

    void saveAnswer() {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userid = user.getUid();

        String answer = editText.getText().toString();
        if(answer != null){

            GetCurrentTime gc = new GetCurrentTime();
            time = gc.ctime();

            member.setAnswer(answer);
            member.setTime(time);
            member.setName(name);
            member.setUid(userid);
            member.setUrl(url);


            String id = Allquestions.push().getKey();
            Allquestions.child(id).setValue(member);

            newMember.setName(name);
            newMember.setText("Replied to your Question" + answer);
            newMember.setSeen("no");
            newMember.setUid(userid);
            newMember.setUrl(url);

            String key = ntref.push().getKey();
            ntref.child(key).setValue(newMember);
            sendNotification(uid,name,answer);

            Toast.makeText(this, "Submitted", Toast.LENGTH_SHORT).show();

        }else{
            Toast.makeText(this, "Please write answer", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userid = user.getUid();
        FirebaseFirestore d = FirebaseFirestore.getInstance();
        DocumentReference reference;
        reference = d.collection("user").document(userid);
        reference.get()
                .addOnCompleteListener(task -> {
                    if(task.getResult().exists()){
                         url = task.getResult().getString("url");
                         name = task.getResult().getString("name");

                    }else{
                        Toast.makeText(AnswerActivity.this, "error", Toast.LENGTH_SHORT).show();
                    }
                });



    }

    private void sendNotification(String uid, String name, String answer){

        FirebaseDatabase.getInstance().getReference().child(uid).child("token")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        usertoken = snapshot.getValue(String.class);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        Handler handler = new Handler();
        handler.postDelayed(() -> {

            FcmNotificationSender notificationsSender =
                    new FcmNotificationSender(usertoken,"Social Media",name+" Commented on your post: " + answer,
                            getApplicationContext(),AnswerActivity.this);

            notificationsSender.SendNotifications();

        },3000);

    }
}