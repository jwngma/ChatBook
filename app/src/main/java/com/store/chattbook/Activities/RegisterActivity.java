package com.store.chattbook.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.store.chattbook.R;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";
    private FirebaseAuth mAuth;
    private DatabaseReference mUserDatabase;

    private Button regBtn;
    private EditText edt_email, edt_password, edt_con_password;
    private ImageView gmail_reg, facebook_reg, twitter_reg;

    private String  email, password, con_password;
    private TextView to_log;

    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Log.d(TAG, "onCreate: Reg Created");
        mAuth=FirebaseAuth.getInstance();
        mUserDatabase=FirebaseDatabase.getInstance().getReference().child("Users");


        progressDialog= new ProgressDialog(this);

        initAll();
        sendToLogin();
        registerUser();

    }

    private void registerUser() {
        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email=edt_email.getText().toString();
                password=edt_password.getText().toString();
                con_password=edt_con_password.getText().toString();
                if (TextUtils.isEmpty(email)){
                    edt_email.setError("Email is Required");
                }
                else if (TextUtils.isEmpty(password)){
                    edt_password.setError("Password is Required");
                }
                else if (TextUtils.isEmpty(con_password)){
                    edt_con_password.setError("Confirm password is Required");
                }
                else if (!password.equals(con_password)){
                    Toast.makeText(RegisterActivity.this, "Password does not match with confirm password", Toast.LENGTH_SHORT).show();
                }
                else {
                    progressDialog.setTitle("Registering User");
                    progressDialog.setMessage("Please wait! While we check ypur credentails");
                    progressDialog.show();

                    mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                Intent intent= new Intent(RegisterActivity.this,SetupActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.putExtra("email",email);
                                startActivity(intent);
                                finish();
                                progressDialog.dismiss();

                               /* HashMap<String, String> userMap= new HashMap<>();
                                userMap.put("name","default");
                                userMap.put("image","default");
                                userMap.put("thumb_image","default");
                                userMap.put("status","Hi! i am using ChatTBook");
                                userMap.put("email",email);
                                userMap.put("gender","default");

                                mUserDatabase.child(mAuth.getUid()).setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){
                                            Intent intent= new Intent(RegisterActivity.this,SetupActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);
                                            finish();
                                            progressDialog.dismiss();
                                        }
                                        else {
                                            String error= task.getException().getMessage();
                                            Toast.makeText(RegisterActivity.this, error, Toast.LENGTH_SHORT).show();
                                            progressDialog.dismiss();
                                        }
                                    }
                                });*/

                            }
                            else {
                                Toast.makeText(RegisterActivity.this, "Error", Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                            }
                        }
                    });
                }
            }
        });
    }

    private void initAll() {
        regBtn=findViewById(R.id.reg_btn);
        edt_email=findViewById(R.id.reg_email);
        edt_password=findViewById(R.id.reg_password);
        edt_con_password=findViewById(R.id.reg_con_password);
        gmail_reg=findViewById(R.id.gmail_reg);
        facebook_reg=findViewById(R.id.facebook_reg);
        twitter_reg=findViewById(R.id.twitter_reg);
        to_log=findViewById(R.id.to_login);


    }

    private void sendToLogin(){
        to_log.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(RegisterActivity.this,LoginActivity.class);
                startActivity(intent);
            }
        });
    }
}
