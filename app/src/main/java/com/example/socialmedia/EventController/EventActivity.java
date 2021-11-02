package com.example.socialmedia.EventController;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.socialmedia.DatePicker;
import com.example.socialmedia.GetCurrentTime;
import com.example.socialmedia.MainActivity;
import com.example.socialmedia.PostController.PostActivity;
import com.example.socialmedia.R;
import com.example.socialmedia.databaseReference;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointBackward;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class EventActivity extends AppCompatActivity {

    ImageView imageView,closebtn,useriv;
    ProgressBar progressBar;
    private Uri selectedUri;
    private static final int PICK_FILE = 1;
    UploadTask uploadTask;
    EditText etdesc,etTitle;
    Button btnchoosefile;
    TextView btnuploadfile,closeImage;
    VideoView videoView;
    String url,name;
    StorageReference storageReference;
    databaseReference dbr = new databaseReference();
    FirebaseDatabase database = FirebaseDatabase.getInstance(dbr.keyDb());
    DatabaseReference db1,db2,db3,event;
    CardView cv_add;
    MediaController mediaController;
    String type;
    EventMember eventMember;
    CardView closePanel;
    ConstraintLayout date_time;

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String currentuid = user.getUid();

    TextView dia_date,date,time;

    MaterialDatePicker materialDatePicker;

    int hour,minute;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);


        eventMember = new EventMember();

        mediaController = new MediaController(this);

        progressBar = findViewById(R.id.event_pb_post);
        imageView = findViewById(R.id.event_iv_post);
        useriv = findViewById(R.id.event_iv);
        videoView = findViewById(R.id.event_vv_post);
        btnuploadfile = findViewById(R.id.event_btn_uploadfile_post);
        etdesc = findViewById(R.id.event_et_decs_post);
        etTitle = findViewById(R.id.event_et_title_post);
        closebtn = findViewById(R.id.event_close);
        cv_add = findViewById(R.id.cv_addimage);
        closePanel = findViewById(R.id.cl_parentclose_ap);
        closeImage = findViewById(R.id.tv_close_iv_ap);
        date_time = findViewById(R.id.cl_date);
        date = findViewById(R.id.tv_date_ae);





        storageReference = FirebaseStorage.getInstance().getReference("User posts events");


        db1 = database.getReference("All images").child(currentuid);
        db2 = database.getReference("All videos").child(currentuid);
        db3 = database.getReference("All post").child("event");
        event = database.getReference("Event Payment");



        btnuploadfile.setOnClickListener(v -> Dopost());

        cv_add.setOnClickListener(v -> chooseImage());

        closebtn.setOnClickListener(v -> {
            Intent intent = new Intent(EventActivity.this, MainActivity.class);
            startActivity(intent);
        });
        closeImage.setOnClickListener(v -> {
            selectedUri= null;
            closePanel.setVisibility(View.GONE);
        });




        date_time.setOnClickListener(v -> showDateTime());

        //Calendar Object
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        calendar.clear();

        //Today Time
        long today = MaterialDatePicker.todayInUtcMilliseconds();

        calendar.setTimeInMillis(today);

        //Start Bound in Millis
        calendar.set(Calendar.MONTH,Calendar.MARCH);
        long startbound = calendar.getTimeInMillis();

        //End Bound
        calendar.set(Calendar.MONTH,Calendar.DECEMBER);
        long endTime = calendar.getTimeInMillis();

        //Certain date
        calendar.set(2002,Calendar.JANUARY,1);
        long startime = calendar.getTimeInMillis();

        CalendarConstraints.Builder calendarConstraints = new CalendarConstraints.Builder();
       // calendarConstraints.setValidator(DateValidatorPointForward.from(startime));
        calendarConstraints.setStart(startbound);
        calendarConstraints.setEnd(endTime);



        //Date picker Builder
        MaterialDatePicker.Builder builder = MaterialDatePicker.Builder.datePicker();
        builder.setTitleText("Select Event Date");
        builder.setSelection(today);
        builder.setCalendarConstraints(calendarConstraints.build());
         materialDatePicker = builder.build();

         //date_now = materialDatePicker.getHeaderText().toString();

    }

    private void showDateTime() {
        final Dialog dialog = new Dialog(EventActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottom_date_time_layout);


        dia_date = dialog.findViewById(R.id.tv_date);
         time = dialog.findViewById(R.id.tv_time);

         time.setOnClickListener(view -> {

             Toast.makeText(EventActivity.this, "clicked", Toast.LENGTH_SHORT).show();

             TimePickerDialog.OnTimeSetListener onTimeSetListener = (timePicker, i, i1) -> {

                 hour = i;
                 minute = i1;
                 time.setText(String.format(Locale.getDefault(),"%02d:%02d",hour,minute));
             };

             TimePickerDialog timePickerDialog = new TimePickerDialog(EventActivity.this,onTimeSetListener,hour,minute,true);
             timePickerDialog.setTitle("Select Time Event");
         });



        dia_date.setOnClickListener(v -> materialDatePicker.show(getSupportFragmentManager(),"DATE_PICKER"));

        //Date Picker Positive button on click listner
        materialDatePicker.addOnPositiveButtonClickListener(selection -> {

            dia_date.setText(materialDatePicker.getHeaderText());
            date.setText(materialDatePicker.getHeaderText());
        });



        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.Bottomanim;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }




    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Calendar mCalendar = Calendar.getInstance();
        int year = mCalendar.get(Calendar.YEAR);
        int month = mCalendar.get(Calendar.MONTH);
        int dayOfMonth = mCalendar.get(Calendar.DAY_OF_MONTH);
        return new DatePickerDialog(EventActivity.this, (DatePickerDialog.OnDateSetListener)
                EventActivity.this, year, month, dayOfMonth);
    }


    private void chooseImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/* video/*");
        //intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,PICK_FILE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_FILE && resultCode == RESULT_OK && data!= null && data.getData() != null){

            selectedUri = data.getData();
            if(selectedUri.toString().contains("image")){
                Picasso.get().load(selectedUri).into(imageView);
                imageView.setVisibility(View.VISIBLE);
                videoView.setVisibility(View.INVISIBLE);
                type = "iv";
                closePanel.setVisibility(View.VISIBLE);
            }else if(selectedUri.toString().contains("video")) {
                videoView.setMediaController(mediaController);
                videoView.setVisibility(View.VISIBLE);
                imageView.setVisibility(View.INVISIBLE);
                videoView.setVideoURI(selectedUri);
                videoView.start();
                type = "vv";
                closePanel.setVisibility(View.VISIBLE);
            }else{
                Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String getFileExt(Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType((contentResolver.getType(uri)));
    }

    @Override
    protected void onStart() {
        super.onStart();

        //date.setText(date_now);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference documentReference = db.collection("user").document(currentuid);

        documentReference.get()
                .addOnCompleteListener(task -> {

                    if(task.getResult().exists()){
                        name = task.getResult().getString("name");
                        url = task.getResult().getString("url");

                        Picasso.get().load(url).into(useriv);
                    }else{
                        Toast.makeText(EventActivity.this, "Error", Toast.LENGTH_SHORT).show();
                    }


                });
    }

    private void Dopost() {

        Calendar cdate = Calendar.getInstance();
        SimpleDateFormat currentdate = new SimpleDateFormat("dd-MMMM-yyy");
        final String savedate = currentdate.format(cdate.getTime());

        Calendar ctime = Calendar.getInstance();
        SimpleDateFormat currenttime =new SimpleDateFormat("HH-mm-ss");
        final String savetime = currenttime.format(ctime.getTime());


        String desc = etdesc.getText().toString();
        String title = etTitle.getText().toString();

              GetCurrentTime gc = new GetCurrentTime();
              String time = gc.ctime();

        if(!TextUtils.isEmpty(desc) && selectedUri != null && !TextUtils.isEmpty(title)){

            progressBar.setVisibility(View.VISIBLE);
            final StorageReference reference = storageReference.child(System.currentTimeMillis()+"."+getFileExt(selectedUri));
            uploadTask = reference.putFile(selectedUri);

            Task<Uri> urlTask = uploadTask.continueWithTask(task -> {
                if(!task.isSuccessful()){
                    throw task.getException();

                }
                return reference.getDownloadUrl();
            }).addOnCompleteListener(task -> {

                if(task.isSuccessful()){
                    Uri downloadUri = task.getResult();

                    if(type.equals("iv")){
                        String id1 = db3.push().getKey();
                        eventMember.setTitle(title);
                        eventMember.setDesc(desc);
                        eventMember.setName(name);
                        eventMember.setPostUri(downloadUri.toString());
                        eventMember.setTime(savetime);
                        eventMember.setUid(currentuid);
                        eventMember.setUrl(url);
                        eventMember.setType("iv");
                        eventMember.setDate(savedate);
                        eventMember.setPostkey(id1);

                        //for image
                        String id = db1.push().getKey();
                        db1.child(id).setValue(eventMember);
                        //for both
                        db3.child(id1).setValue(eventMember);

                        progressBar.setVisibility(View.INVISIBLE);

                        Toast.makeText(EventActivity.this, "post uploaded", Toast.LENGTH_SHORT).show();
                        deletedb();

                    }else if(type.equals("vv")){
                        String id1 = db3.push().getKey();
                        eventMember.setTitle(title);
                        eventMember.setDesc(desc);
                        eventMember.setName(name);
                        eventMember.setPostUri(downloadUri.toString());
                        eventMember.setTime(time);
                        eventMember.setUid(currentuid);
                        eventMember.setUrl(url);
                        eventMember.setType("vv");
                        eventMember.setDate(savedate);
                        eventMember.setPostkey(id1);

                        //for image
                        String id = db1.push().getKey();
                        db1.child(id).setValue(eventMember);
                        //for both
                        db3.child(id1).setValue(eventMember);

                        progressBar.setVisibility(View.INVISIBLE);

                        Toast.makeText(EventActivity.this, "post uploaded", Toast.LENGTH_SHORT).show();
                        deletedb();
                    }else{
                        Toast.makeText(EventActivity.this, "error", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }else{
            Toast.makeText(this, "Please fill all Fields", Toast.LENGTH_SHORT).show();
        }


    }

    public void deletedb(){
        event.child(currentuid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                dataSnapshot.getRef().removeValue();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(EventActivity.this, "Data error", Toast.LENGTH_SHORT).show();
            }
        });

        Intent intent = new Intent(EventActivity.this,MainActivity.class);
        startActivity(intent);
    }

}