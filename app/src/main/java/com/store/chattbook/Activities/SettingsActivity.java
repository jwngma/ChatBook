package com.store.chattbook.Activities;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.store.chattbook.R;
import com.tapadoo.alerter.Alerter;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {
    private static final String TAG = "SettingsActivity";
    private DatabaseReference SettingsUsersRef;
    private FirebaseAuth mAuth;
    private Toolbar mtoolbar;
    private CircleImageView profile_image;
    private EditText profile_status, profile_username, country, gender, relationship, profile_email, dob;
    private Button changebtn,saveBtn;
    private Uri imageUri;
    private String currentUserId;
    private  String profile_photo,profile_statuss,profile_usernamee,countryy,email,dobb,genderr,relationshipp;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        SettingsUsersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId);
        SettingsUsersRef.keepSynced(true);
        progressDialog= new ProgressDialog(this);

        initToolbar();
        initAll();
        retrieveAlldata();
        updateSettings();
    }

    private void initToolbar() {
        mtoolbar = findViewById(R.id.settings_toolbar);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle("Settings Activity");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initAll() {
        profile_image = findViewById(R.id.profile_image);
        changebtn = findViewById(R.id.btnChange);
        saveBtn=findViewById(R.id.save_profile_btn);
        profile_status = findViewById(R.id.profile_status);
        profile_username = findViewById(R.id.profile_username);
        country = findViewById(R.id.profile_country);
        gender = findViewById(R.id.profile_gender);
        relationship = findViewById(R.id.profile_relationship);
        profile_email=findViewById(R.id.profile_email);
        dob=findViewById(R.id.dob);

        changebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(500, 500)
                        .start(SettingsActivity.this);
            }
        });

    }

    private void retrieveAlldata() {

        SettingsUsersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {


                    if (dataSnapshot.hasChild("image")){
                        profile_photo = dataSnapshot.child("image").getValue().toString();
                    }
                    if (dataSnapshot.hasChild("status")){
                        profile_statuss = dataSnapshot.child("status").getValue().toString();
                    }
                    if (dataSnapshot.hasChild("fullname")){
                        profile_usernamee = dataSnapshot.child("fullname").getValue().toString();
                    }
                    if (dataSnapshot.hasChild("country")){
                        countryy = dataSnapshot.child("country").getValue().toString();
                    }
                    if (dataSnapshot.hasChild("dob")){
                        dobb = dataSnapshot.child("dob").getValue().toString();
                    }
                    if (dataSnapshot.hasChild("email")){
                        email = dataSnapshot.child("dob").getValue().toString();
                    }

                    if (dataSnapshot.hasChild("gender")) {
                         genderr = dataSnapshot.child("gender").getValue().toString();
                    }
                    if (dataSnapshot.hasChild("relationship")) {
                         relationshipp = dataSnapshot.child("relationship").getValue().toString();
                    }

                    Picasso.get().load(profile_photo).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.profile).into(profile_image, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError(Exception e) {
                            Picasso.get().load(profile_photo).into(profile_image);
                        }
                    });
                    profile_status.setText(profile_statuss);
                    profile_username.setText(profile_usernamee);
                    country.setText(countryy);
                    gender.setText(genderr);
                    relationship.setText(relationshipp);
                    profile_email.setText(email);
                    dob.setText(dobb);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }
    private void updateSettings() {
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String status, username, countryyy,genderrr,relation,email,dobirth;
                status=profile_status.getText().toString();
                username=profile_username.getText().toString();
                countryyy=country.getText().toString();
                genderrr=gender.getText().toString();
                relation=relationship.getText().toString();
                email=profile_email.getText().toString();
                dobirth=dob.getText().toString();
                if (TextUtils.isEmpty(status)){
                    Toast.makeText(SettingsActivity.this, "All the Field are important to fill", Toast.LENGTH_SHORT).show();

                }
                else if (TextUtils.isEmpty(username)){
                    Toast.makeText(SettingsActivity.this, "All the Field are important to fill", Toast.LENGTH_SHORT).show();

                }
                else if (TextUtils.isEmpty(countryyy)){
                    Toast.makeText(SettingsActivity.this, "All the Field are important to fill", Toast.LENGTH_SHORT).show();
                }
                else if (TextUtils.isEmpty(genderrr)){
                    Toast.makeText(SettingsActivity.this, "All the Field are important to fill", Toast.LENGTH_SHORT).show();

                }
                else if (TextUtils.isEmpty(relation)){
                    Toast.makeText(SettingsActivity.this, "All the Field are important to fill", Toast.LENGTH_SHORT).show();

                }
                else if (TextUtils.isEmpty(email)){
                    Toast.makeText(SettingsActivity.this, "All the Field are important to fill", Toast.LENGTH_SHORT).show();


                }
                else if (TextUtils.isEmpty(dobirth)){

                    Toast.makeText(SettingsActivity.this, "All the Field are important to fill", Toast.LENGTH_SHORT).show();

                }
                else {
                    progressDialog.setTitle("Saving Data");
                    progressDialog.setMessage("Please wait!");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();

                    HashMap userMap = new HashMap<>();
                    userMap.put("name", username);
                    userMap.put("fullname", username);
                  /*  userMap.put("image", imageUri);*/
                    userMap.put("thumb_image", "default");
                    userMap.put("status", status);
                    userMap.put("email", email);
                    userMap.put("gender", genderrr);
                    userMap.put("country", countryyy);
                    userMap.put("dob", dobirth);

                    SettingsUsersRef.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if (task.isSuccessful()){
                                Toast.makeText(SettingsActivity.this, "Saved Succesfully", Toast.LENGTH_SHORT).show();
                                SendToMain();

                            }
                            else {
                                String error= task.getException().getMessage();
                                Toast.makeText(SettingsActivity.this, "ailleddddddd", Toast.LENGTH_SHORT).show();

                            }
                        }
                    });


                }

            }
        });
    }

    private void SendToMain() {
        Intent intent= new Intent(SettingsActivity.this,MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                imageUri = result.getUri();
                profile_image.setImageURI(imageUri);

                ///i have to write a code here to upload the image directly to the database
            }

        }
    }

}
