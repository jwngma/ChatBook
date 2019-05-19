package com.store.chattbook.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.store.chattbook.R;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SetupActivity extends AppCompatActivity {
    private static final String TAG = "SetupActivity";
    private FirebaseAuth mAuth;
    private DatabaseReference mUserDatabase;
    private StorageReference mProfileStorage;
    private static final int Gallery_Pick = 1;

    private Button saveBtn, changeImageBtn;
    private CircleImageView profile_image;
    private EditText edt_username, edt_fullname, edt_gender, edt_country, edt_dob;
    private String username, fullname, gender, country, dob, email;
    private Uri resultUri;
    private ProgressDialog progressDialog;
    private Toolbar mtoolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);
        Log.d(TAG, "onCreate: setup Created");
        mAuth = FirebaseAuth.getInstance();
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mUserDatabase.keepSynced(true);
        mProfileStorage = FirebaseStorage.getInstance().getReference().child("Profile_Images");
        progressDialog = new ProgressDialog(this);

        initAll();
        initToolbar();
        getIncomingIntent();
        setupProfile_image();
        SaveAllData();
        retrieveDataFromFirebase();


    }

    private void initToolbar() {
        mtoolbar=findViewById(R.id.setup_toolbar);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle("Setup Activity");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    private void retrieveDataFromFirebase() {
        mUserDatabase.child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (dataSnapshot.hasChild("image")){
                        String image = dataSnapshot.child("image").getValue().toString();
                        Glide.with(getApplicationContext()).load(image).placeholder(R.drawable.profile_icon).into(profile_image);

                    }
                    if (dataSnapshot.hasChild("name")){
                        String user_name = dataSnapshot.child("name").getValue().toString();
                        edt_username.setText(user_name);
                    }
                    if (dataSnapshot.hasChild("fullname")){
                        String full_name = dataSnapshot.child("fullname").getValue().toString();
                        edt_fullname.setText(full_name);
                    }
                    if (dataSnapshot.hasChild("country")){
                        String countri = dataSnapshot.child("country").getValue().toString();
                        edt_country.setText(countri);
                    }
                    if (dataSnapshot.hasChild("gender")){
                        String genderr = dataSnapshot.child("gender").getValue().toString();
                        edt_gender.setText(genderr);
                    }
                    if (dataSnapshot.hasChild("dob")){
                        String d_o_b = dataSnapshot.child("dob").getValue().toString();
                        edt_dob.setText(d_o_b);
                    }
                    else {
                        Toast.makeText(SetupActivity.this, "Error in loading data", Toast.LENGTH_SHORT).show();
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setupProfile_image() {
        changeImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent galleryintent= new Intent();
                galleryintent.setAction(Intent.ACTION_GET_CONTENT);
                galleryintent.setType("image/*");
                startActivityForResult(Intent.createChooser(galleryintent,"Select Image"),Gallery_Pick);*/

                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1, 1)
                        .start(SetupActivity.this);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        /*if (requestCode==Gallery_Pick && resultCode==RESULT_OK && data!=null){
           // Uri image_uri=data.getData();
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(this);

        }*/

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                resultUri = result.getUri();
                profile_image.setImageURI(resultUri);
            }

        }


    }

    private void getIncomingIntent() {
        if (getIntent().hasExtra("email")) {
            email = getIntent().getStringExtra("email");
        }
    }

    private void SaveAllData() {
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progressDialog.setTitle("Saving Data");
                progressDialog.setMessage("Please wait! while We save your data");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();
                username = edt_username.getText().toString();
                fullname = edt_fullname.getText().toString();
                country = edt_country.getText().toString();
                dob = edt_dob.getText().toString();

                final StorageReference filepath = mProfileStorage.child(mAuth.getUid()).child(mAuth.getUid() + ".jpg");
                final UploadTask uploadTask = filepath.putFile(resultUri);

                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                })
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                Task<Uri> uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                                    @Override
                                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                        if (!task.isSuccessful()) {
                                            throw task.getException();
                                        }
                                        return filepath.getDownloadUrl();

                                    }
                                })
                                        .addOnCompleteListener(new OnCompleteListener<Uri>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Uri> task) {
                                                if (task.isSuccessful()) {
                                                    String downloadurl = task.getResult().toString();

                                                    HashMap userMap = new HashMap<>();
                                                    userMap.put("name", username);
                                                    userMap.put("fullname", fullname);
                                                    userMap.put("image", downloadurl);
                                                    userMap.put("thumb_image", "default");
                                                    userMap.put("status", "Hi! i am using ChatTBook");
                                                    userMap.put("email", email);
                                                    userMap.put("gender", gender);
                                                    userMap.put("country", country);
                                                    userMap.put("dob", dob);

                                                    mUserDatabase.child(mAuth.getUid()).updateChildren(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                               // sendToMain();
                                                                progressDialog.dismiss();
                                                            }
                                                        }
                                                    });

                                                } else {
                                                    String error = task.getException().getMessage();
                                                    Toast.makeText(SetupActivity.this, error, Toast.LENGTH_SHORT).show();
                                                    progressDialog.dismiss();
                                                }
                                            }
                                        });

                            }
                        });


            }
        });
    }

    private void sendToMain() {

        Intent setupIntent = new Intent(SetupActivity.this, MainActivity.class);
        startActivity(setupIntent);
    }

    private void initAll() {

        saveBtn = findViewById(R.id.setup_saveBtn);
        profile_image = findViewById(R.id.setup_profile_image);
        edt_username = findViewById(R.id.setup_profile_username);
        edt_fullname = findViewById(R.id.setup_profile_fullname);
        edt_gender = findViewById(R.id.setup_profile_gender);
        edt_country = findViewById(R.id.setup_profile_country);
        edt_dob = findViewById(R.id.setup_profile_dob);
        changeImageBtn = findViewById(R.id.change_imageBtn);

    }
}
