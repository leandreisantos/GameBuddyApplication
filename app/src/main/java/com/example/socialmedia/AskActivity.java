package com.example.socialmedia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AskActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    String[] status = {"Choose category","Tech","Health","Education","Food","Sports","News","Fashion","Beuty","Lifestyle"};


    EditText editText;
    Button button;
    databaseReference dbr = new databaseReference();
    FirebaseDatabase database = FirebaseDatabase.getInstance(dbr.keyDb());
    DatabaseReference AllQuestions,UserQuestions;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference documentReference;
    QuestionMember member;
    String name,url,privacy,uid,cat_value;
    Spinner spinner;
    TextView textView;
    LinearLayout linearLayout;

    public boolean isNetworkConnected(){
        boolean connected = false;
        try{
            ConnectivityManager cm = (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo ninfo = cm.getActiveNetworkInfo();
            connected = ninfo != null && ninfo.isAvailable() && ninfo.isConnected();
            return connected;
        }catch(Exception e){
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return connected;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ask);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentUserid = user.getUid();

        linearLayout = findViewById(R.id.ll_ask);

        editText = findViewById(R.id.ask_et_question);
        button = findViewById(R.id.btn_submit);
        documentReference = db.collection("user").document(currentUserid);

        textView = findViewById(R.id.tv_cat);
        spinner = findViewById(R.id.spinner_cat);
        ArrayAdapter arrayAdapter = new ArrayAdapter(this,android.R.layout.simple_spinner_item,status);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);
        spinner.setOnItemSelectedListener(this);


        AllQuestions = database.getReference("All Questions");
        UserQuestions = database.getReference("User Questions").child(currentUserid);





        member = new QuestionMember();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String question = editText.getText().toString();

                GetCurrentTime gc = new GetCurrentTime();
                String time = gc.ctime();

                if(question.length()==0||cat_value.equals("Choose category")){
                    Toast.makeText(AskActivity.this, "Please ask a question", Toast.LENGTH_SHORT).show();
                }else if(!isNetworkConnected()){
                    showSnakbar();
                }
                else{
                    member.setQuestion(question);
                    member.setName(name);
                    member.setPrivacy(privacy);
                    member.setUrl(url);
                    member.setUserid(uid);
                    member.setTime(time);
                    member.setCategory(cat_value.toLowerCase());

                    String id = UserQuestions.push().getKey();
                    UserQuestions.child(id).setValue(member);

                    String child = AllQuestions.push().getKey();
                    member.setKey(id);
                    AllQuestions.child(child).setValue(member);
                    Toast.makeText(AskActivity.this, "Submitted", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void showSnakbar() {
        Snackbar.make(linearLayout,"Not Connected",Snackbar.LENGTH_LONG)
                .setAction("Turn on", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                })
        .setActionTextColor(getResources().getColor(R.color.icon))
                .show();
    }

    @Override
    protected void onStart() {
        super.onStart();


        documentReference.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>(){
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task){

                        if(task.getResult().exists()){
                             name = task.getResult().getString("name");
                             url = task.getResult().getString("url");
                             privacy = task.getResult().getString("privacy");
                             uid = task.getResult().getString("uid");

                        }else{
                            Toast.makeText(AskActivity.this, "Error", Toast.LENGTH_SHORT).show();
                        }


                    }
                });
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        cat_value = parent.getSelectedItem().toString();
        textView.setText(cat_value);

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        Toast.makeText(this, "Please select a category", Toast.LENGTH_SHORT).show();
    }
}