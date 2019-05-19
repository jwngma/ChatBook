package com.store.chattbook.Activities;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.store.chattbook.R;

public class ClickPostActivity extends AppCompatActivity {

    private static final String TAG = "ClickPostActivity";
    private DatabaseReference ClickPostRef;

    private ImageView post_image;
    private Button deleteBtn, editBtn;
    private FirebaseAuth mAuth;
    private TextView post_description;
    private String postkey;
    private Toolbar mtoolbar;
    private String description, post_imagee, databaseUserId, currentUserId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_click_post);

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();

        initAll();
        initToolbar();
        getInComingIntent();
        retriveAlldata();

    }

    private void initToolbar() {
        mtoolbar = findViewById(R.id.post_click_toolbar);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle("Edit Post");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    private void retriveAlldata() {
        ClickPostRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {

                    description = dataSnapshot.child("description").getValue().toString();
                    post_imagee = dataSnapshot.child("post_image").getValue().toString();
                    databaseUserId = dataSnapshot.child("uid").getValue().toString();

                    Picasso.get().load(post_imagee).networkPolicy(NetworkPolicy.OFFLINE).
                            placeholder(R.drawable.profile).into(post_image, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError(Exception e) {
                            Picasso.get().load(post_imagee).into(post_image);
                        }
                    });
                    post_description.setText(description);
                    if (currentUserId.equals(databaseUserId)) {
                        editBtn.setVisibility(View.VISIBLE);
                        deleteBtn.setVisibility(View.VISIBLE);
                    }

                    editBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            editPostDescription(description);


                        }
                    });
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getInComingIntent() {
        if (getIntent().hasExtra("postkey")) {
            postkey = getIntent().getStringExtra("postkey");
        }
        ClickPostRef = FirebaseDatabase.getInstance().getReference().child("Posts").child(postkey);
    }

    private void initAll() {
        post_description = findViewById(R.id.post_description);
        post_image = findViewById(R.id.post_image);

        deleteBtn = findViewById(R.id.deleteBtn);
        deleteBtn.setVisibility(View.INVISIBLE);

        editBtn = findViewById(R.id.editBtn);
        editBtn.setVisibility(View.INVISIBLE);

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClickPostRef.removeValue();
                sendToMain();

            }
        });


    }

    private void editPostDescription(String description) {
        AlertDialog.Builder builder= new AlertDialog.Builder(this);
        builder.setTitle("Edit Post Description");

         final  EditText editText= new EditText(this);
         editText.setText(description);
         builder.setView(editText);

         builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
             @Override
             public void onClick(DialogInterface dialog, int which) {
                 ClickPostRef.child("description").setValue(editText.getText().toString());

             }
         }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
             @Override
             public void onClick(DialogInterface dialog, int which) {
                 dialog.cancel();
             }
         });

        Dialog dialog=builder.create();
        dialog.show();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.holo_green_dark);
    }

    private void sendToMain() {
        Intent intent= new Intent(ClickPostActivity.this,MainActivity.class);
        startActivity(intent);
        finish();
    }
}
