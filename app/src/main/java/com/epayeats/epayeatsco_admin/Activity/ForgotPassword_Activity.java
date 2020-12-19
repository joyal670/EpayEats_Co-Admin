package com.epayeats.epayeatsco_admin.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.epayeats.epayeatsco_admin.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.SignInMethodQueryResult;

public class ForgotPassword_Activity extends AppCompatActivity {
    private EditText forgotemailaddress;
    private Button forgotbtn;
    private FirebaseAuth mAuth;
    public ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password_);

        progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Sending Password Reset Request...");

        forgotemailaddress = findViewById(R.id.forgotemailaddress);
        forgotbtn = findViewById(R.id.forgotbtn);

        mAuth = FirebaseAuth.getInstance();

        forgotbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                forgotPassword();
            }
        });
    }

    private void forgotPassword()
    {
        try {

            String temp = forgotemailaddress.getText().toString();
            if (temp.isEmpty()) {
                forgotemailaddress.setError("Email Address Required");
            } else {
                progressDialog.show();
                mAuth.fetchSignInMethodsForEmail(forgotemailaddress.getText().toString()).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                    @Override
                    public void onComplete(@NonNull Task<SignInMethodQueryResult> task)
                    {
                        try {
                            if (task.getResult().getSignInMethods().isEmpty()) {
                                progressDialog.dismiss();
                                Toast.makeText(ForgotPassword_Activity.this, "Not an Registerd Email Address", Toast.LENGTH_SHORT).show();
                            } else {

                                mAuth.sendPasswordResetEmail(forgotemailaddress.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            progressDialog.dismiss();
                                            Toast.makeText(ForgotPassword_Activity.this, "A Password Reset Link is sent to " + forgotemailaddress.getText().toString(), Toast.LENGTH_LONG).show();
                                            Intent intent = new Intent(ForgotPassword_Activity.this, Login_Activity.class);
                                            startActivity(intent);
                                            finish();
                                        } else {
                                            progressDialog.dismiss();
                                            Toast.makeText(ForgotPassword_Activity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        progressDialog.dismiss();
                                        Toast.makeText(ForgotPassword_Activity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e)
                    {
                        progressDialog.dismiss();
                        Toast.makeText(ForgotPassword_Activity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}