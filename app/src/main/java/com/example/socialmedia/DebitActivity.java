package com.example.socialmedia;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.socialmedia.EventController.EventActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DebitActivity extends AppCompatActivity {

    EditText card;
    Button proceed;
    ProgressBar pv;
    TextView process;

    PaymentMember member;

    databaseReference dbr = new databaseReference();
    FirebaseDatabase database = FirebaseDatabase.getInstance(dbr.keyDb());
    DatabaseReference db1;

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String currentuid = user.getUid();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debit);

        member = new PaymentMember();

        card = findViewById(R.id.et_card_debit);
        proceed = findViewById(R.id.btn_debit);
        pv = findViewById(R.id.pv_debit);
        process = findViewById(R.id.tv_process_debit);

        db1 = database.getReference("Event Payment");

        proceed.setOnClickListener(v -> doPay());
    }


    private void doPay() {
        String card_et = card.getText().toString();
        Calendar cdate = Calendar.getInstance();
        SimpleDateFormat currentdate = new SimpleDateFormat("dd-MMMM-yyy");
        final String savedate = currentdate.format(cdate.getTime());

        Calendar ctime = Calendar.getInstance();
        SimpleDateFormat currenttime =new SimpleDateFormat("HH-mm-ss");
        final String savetime = currenttime.format(ctime.getTime());

        if(!TextUtils.isEmpty(card_et)&&card_et.equals("123456789")){
            pv.setVisibility(View.VISIBLE);
            process.setVisibility(View.VISIBLE);
            card.setEnabled(false);
            proceed.setEnabled(false);

            String id1 = db1.push().getKey();
            member.setUid(currentuid);
            member.setPost_key(id1);
            member.setTime(savetime);
            member.setDate(savedate);

            db1.child(currentuid).setValue(member);

            pv.setVisibility(View.GONE);
            process.setVisibility(View.GONE);
            showBottomSheet(id1);

        }

        else  Toast.makeText(this, "Input valid card number", Toast.LENGTH_SHORT).show();
    }

    private void showBottomSheet(String id) {
        LayoutInflater inflater = LayoutInflater.from(DebitActivity.this);
        View view = inflater.inflate(R.layout.payment_layout_popup,null);

        TextView refid = view.findViewById(R.id.tv_ref);
        TextView now = view.findViewById(R.id.tv_btn_now);
        TextView later = view.findViewById(R.id.tv_btn_later);

        refid.setText(id);

        AlertDialog alertDialog = new AlertDialog.Builder(DebitActivity.this)
                .setView(view)
                .create();

        alertDialog.show();


        now.setOnClickListener(v -> {
            Intent intent = new Intent(DebitActivity.this, EventActivity.class);
            startActivity(intent);
        });

        later.setOnClickListener(v -> {
            Intent intent = new Intent(DebitActivity.this, MainActivity.class);
            startActivity(intent);
        });
    }
}