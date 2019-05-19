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
import com.store.chattbook.R;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private FirebaseAuth mAuth;

    private Button logBtn;
    private EditText edt_email, edt_password;
    private ImageView google_log, facebook_log, twitter_log;
    private String email,password;
    private ProgressDialog progressDialog;
    private TextView toReg;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Log.d(TAG, "onCreate: Login Activity is created");
        mAuth=FirebaseAuth.getInstance();
        progressDialog= new ProgressDialog(this);


        initAll();
        LoginUser();
        toreg();
    }

    private void toreg() {
        toReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    private void LoginUser() {
        logBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                email=edt_email.getText().toString();
                password=edt_password.getText().toString();

                if (TextUtils.isEmpty(email)){
                    edt_email.setError("Email is required");
                }
                else if (TextUtils.isEmpty(password)){
                    edt_password.setError("Password is Required");
                }
                else {
                    progressDialog.setTitle("Loginning in");
                    progressDialog.setMessage("Please wait");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();

                    mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                sendToMain();
                                progressDialog.dismiss();
                            }
                            else {
                                String error= task.getException().getMessage();
                                Toast.makeText(LoginActivity.this, error, Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                            }
                        }
                    });
                }
            }
        });

    }

    private void sendToMain() {

        Intent intent= new Intent(LoginActivity.this,MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void initAll() {

        logBtn=findViewById(R.id.log_btn);
        edt_email=findViewById(R.id.edt_log_email);
        edt_password=findViewById(R.id.edt_log_password);
        google_log=findViewById(R.id.google_log);
        facebook_log=findViewById(R.id.facebook_log);
        twitter_log=findViewById(R.id.twitter_log);
        toReg=findViewById(R.id.to_reg);



    }
}
