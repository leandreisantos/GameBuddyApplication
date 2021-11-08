package com.example.socialmedia.ReportGameCommunityController;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.socialmedia.R;
import com.example.socialmedia.ReportController.ReportMember;
import com.example.socialmedia.databaseReference;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ReportGameActivity extends AppCompatActivity {

    String bundle_postkey;

    databaseReference dbr = new databaseReference();
    FirebaseDatabase database = FirebaseDatabase.getInstance(dbr.keyDb());
    DatabaseReference reference,refReport;

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String currentuid = user.getUid();

    TextView title,desc,close;
    Button btn;
    EditText et;
    ImageView iv;
    ProgressBar pb;


    ReportMember member;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_game);

        Bundle extras = getIntent().getExtras();
        if(extras != null) bundle_postkey = extras.getString("p");
        else Toast.makeText(this, "No Postkey", Toast.LENGTH_SHORT).show();

        reference = database.getReference("All game").child(bundle_postkey);

        title = findViewById(R.id.tv_title_arg);
        desc = findViewById(R.id.tv_desc_arg);
        btn = findViewById(R.id.button_report);
        et = findViewById(R.id.et_reason_arg);
        iv = findViewById(R.id.iv_arg);
        pb = findViewById(R.id.pb_arg);
        close = findViewById(R.id.tv_close_game);

        member = new ReportMember();

        close.setOnClickListener(v -> onBackPressed());


    }

    @Override
    protected void onStart() {
        super.onStart();

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String dps = snapshot.child("postUri1").getValue(String.class);
                String about = snapshot.child("about").getValue(String.class);
                String postkey = snapshot.child("postkey").getValue(String.class);
                String title_s = snapshot.child("title").getValue(String.class);


                Picasso.get().load(dps).into(iv);
                title.setText(title_s);
                desc.setText(about);

                btn.setOnClickListener(v -> {
                    if(TextUtils.isEmpty(et.getText().toString()))
                        Toast.makeText(ReportGameActivity.this, "Please Input Text", Toast.LENGTH_SHORT).show();
                    else{
                        pb.setVisibility(View.VISIBLE);
                        et.setEnabled(false);
                        btn.setEnabled(false);
                        close.setEnabled(false);
                        DoReport(postkey);
                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void DoReport(String postkey) {

        refReport = database.getReference("All report").child("Game").child(postkey);

        Calendar cdate = Calendar.getInstance();
        SimpleDateFormat currentdate = new SimpleDateFormat("dd-MMMM-yyy");
        final String savedate = currentdate.format(cdate.getTime());

        Calendar ctime = Calendar.getInstance();
        SimpleDateFormat currenttime =new SimpleDateFormat("HH-mm-ss");
        final String savetime = currenttime.format(ctime.getTime());

        String id = refReport.push().getKey();

        member.setPostkey(postkey);
        member.setReporterId(currentuid);
        member.setReason(et.getText().toString());
        member.setTime(savetime);
        member.setDate(savedate);

        refReport.child(id).setValue(member);
        Toast.makeText(this, "Report send to admin wait for response", Toast.LENGTH_SHORT).show();
        pb.setVisibility(View.GONE);
        onBackPressed();
    }
}