package com.epayeats.epayeatsco_admin.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.epayeats.epayeatsco_admin.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Login_Activity extends AppCompatActivity
{
    EditText login_activity_email, login_activity_password;
    Button login_activity_btn;
    TextView login_forgot_btn;

    public ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_);

        try {

            firebaseAuth = FirebaseAuth.getInstance();

            progressDialog = new ProgressDialog(this);
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Loging...");

            login_activity_email = findViewById(R.id.login_activity_email);
            login_activity_password = findViewById(R.id.login_activity_password);
            login_activity_btn = findViewById(R.id.login_activity_btn);
            login_forgot_btn = findViewById(R.id.login_forgot_btn);

            login_activity_btn.setOnClickListener(v -> {
                String email = login_activity_email.getText().toString();
                String password = login_activity_password.getText().toString();

                if (email.isEmpty() || password.isEmpty()) {
                    if (email.isEmpty()) {
                        login_activity_email.setError("Required");
                    }
                    if (password.isEmpty()) {
                        login_activity_password.setError("Required");
                    }
                } else {
                    loginFn(email, password);
                }
            });

            login_forgot_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    Intent intent = new Intent(Login_Activity.this, ForgotPassword_Activity.class);
                    startActivity(intent);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loginFn(String email, String password)
    {
        try {
            progressDialog.show();
            firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {

                if (task.isSuccessful()) {
                    String userId = task.getResult().getUser().getUid();
                    String type = "local_admin";

                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("user_data").child(userId);
                    reference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (type.equals(snapshot.child("type").getValue().toString())) {
                                progressDialog.dismiss();

                                sharedPreferences = getSharedPreferences("data", 0);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("userid", task.getResult().getUser().getUid());
                                editor.putString("useremail", email);
                                editor.putBoolean("login_status", true);
                                editor.apply();

                                Intent intent = new Intent(Login_Activity.this, MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                            } else {
                                progressDialog.dismiss();
                                Toast.makeText(Login_Activity.this, "This email is not registered as co-admin account, please contact Admin", Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            progressDialog.dismiss();
                            Toast.makeText(Login_Activity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });


                } else {
                    progressDialog.dismiss();
                    Toast.makeText(Login_Activity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }

            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(Login_Activity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        try {
            SharedPreferences sharedPreferences = getSharedPreferences("data", 0);
            boolean logg = sharedPreferences.getBoolean("login_status", false);
            if (logg) {
                Intent intent = new Intent(Login_Activity.this, MainActivity.class);
                startActivity(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onBackPressed()
    {
        try {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(false);
            builder.setTitle("Exit");
            builder.setMessage("Are you sure want to Exit?");


            builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    moveTaskToBack(true);
                    android.os.Process.killProcess(android.os.Process.myPid());
                    System.exit(1);
                }
            });
            builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            builder.create().show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}