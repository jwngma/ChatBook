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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

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
import com.store.chattbook.R;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class PostActivity extends AppCompatActivity {
    private static final String TAG = "PostActivity";

    private FirebaseAuth mAuth;
    private StorageReference mStorageRef;
    private DatabaseReference mUsersRef;
    private DatabaseReference mPostsRef;

    private Toolbar mtoolbar;
    private Button addBtn;
    private ImageView add_image;
    private EditText edt_add_des;
    private EditText edit_description;
    private static final int Gallery_pick = 22;
    private Uri imageUri;


    private ProgressDialog progressDialog;
    private String saveCurrentTime, saveCurrentDate, downloadUrl, random_name;
    private String fullname, profile_iamge, desc;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        Log.d(TAG, "onCreate: Post Activity is created");
        progressDialog = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();
        mUsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        mStorageRef = FirebaseStorage.getInstance().getReference().child("Posts_Image");
        mPostsRef = FirebaseDatabase.getInstance().getReference().child("Posts");

        initAll();

        initToolbar();
        addImage();
        AddNewPost();
    }

    private void initToolbar() {
        mtoolbar = findViewById(R.id.post_toolbar);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle("Add Post");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initAll() {
        addBtn = findViewById(R.id.addBtn);
        add_image = findViewById(R.id.add_image);
        edit_description=findViewById(R.id.edit_description);


    }

    private void addImage() {
        add_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(Intent.createChooser(galleryIntent, "Select Image"), Gallery_pick);*/

                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(500, 500)
                        .start(PostActivity.this);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
       /* if (requestCode == Gallery_pick && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            add_image.setImageURI(imageUri);

        }*/


        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                imageUri = result.getUri();
                add_image.setImageURI(imageUri);
            }

        }
    }

    private void AddNewPost() {


        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                desc=edit_description.getText().toString();
                if (imageUri == null   ) {
                    Toast.makeText(PostActivity.this, "Select an Image to Upload and description for the image", Toast.LENGTH_SHORT).show();
                }
                else if (TextUtils.isEmpty(desc)){
                    Toast.makeText(PostActivity.this, "nulllllll", Toast.LENGTH_SHORT).show();
                }
               /* else  if (latest_desc.equals("")){
                    Toast.makeText(PostActivity.this, "desc is null", Toast.LENGTH_SHORT).show();

                }*/
                else {
                    progressDialog.setTitle("Adding new Post");
                    progressDialog.setMessage("Please wait! While we upload your Image");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();

                    Calendar calendar = Calendar.getInstance();
                    SimpleDateFormat currentdate = new SimpleDateFormat("dd-MMMM-yyyy");
                    saveCurrentDate = currentdate.format(calendar.getTime());

                    Calendar calforTime = Calendar.getInstance();
                    SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss");
                    saveCurrentTime = currentTime.format(calforTime.getTime());

                    random_name = saveCurrentDate + saveCurrentTime;

                    final StorageReference filePath = mStorageRef.child(mAuth.getCurrentUser().getUid()).child(random_name + ".jpg");
                    final UploadTask uploadTask = filePath.putFile(imageUri);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Task<Uri> uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                                @Override
                                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                    if (!task.isSuccessful()) {
                                        throw task.getException();
                                    }
                                    return filePath.getDownloadUrl();
                                }
                            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Uri> task) {
                                            if (task.isSuccessful()) {
                                                downloadUrl = task.getResult().toString();
                                                SaveInformation();

                                            } else {
                                                String error = task.getException().getMessage();
                                                Toast.makeText(PostActivity.this, error, Toast.LENGTH_SHORT).show();
                                                progressDialog.dismiss();
                                            }
                                        }
                                    });
                        }
                    });
                }
            }
        });

    }
    private void SaveInformation() {

        mUsersRef.child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                fullname = dataSnapshot.child("fullname").getValue().toString();
                profile_iamge = dataSnapshot.child("image").getValue().toString();
                String  desccc=getString(R.string.dummy_text);

                HashMap postMap = new HashMap();
                postMap.put("fullname", fullname);
                postMap.put("profile_image", profile_iamge);
                postMap.put("date", "Updated At:"+saveCurrentDate);
                postMap.put("Date",saveCurrentDate);
                postMap.put("description",desc);
                postMap.put("post_image", downloadUrl);
                postMap.put("time", saveCurrentTime);
                postMap.put("uid", mAuth.getCurrentUser().getUid());
                mPostsRef.child(mAuth.getCurrentUser().getUid() + saveCurrentDate + saveCurrentTime).updateChildren(postMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            sendToMain();
                            progressDialog.dismiss();
                        } else {
                            String error = task.getException().getMessage();
                            Toast.makeText(PostActivity.this, error, Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void sendToMain() {
        Intent intent = new Intent(PostActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }


}
