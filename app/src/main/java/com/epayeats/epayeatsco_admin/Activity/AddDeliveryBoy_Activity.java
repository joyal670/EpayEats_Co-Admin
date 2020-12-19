package com.epayeats.epayeatsco_admin.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.epayeats.epayeatsco_admin.Model.DeliveryBoyModel;
import com.epayeats.epayeatsco_admin.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class AddDeliveryBoy_Activity extends AppCompatActivity
{
    EditText name_editext, email_editext, passeord_editext, conpassword_editext, licence_editext, vechileno_editext, mobileno_editext, deliverycharge_editext;
    Spinner new_deliveryboy_block_spinner;
    Button save_button;
    ImageView deliveryboy_image;

    private static int image_pic_request = 1;

    private Uri mImageUri;
    private StorageTask mUploadTask;

    public ProgressDialog progressDialog;

    SharedPreferences sharedPreferences;
    String a1, a2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_delivery_boy_);

        try {
            sharedPreferences = getSharedPreferences("data", 0);
            a1 = sharedPreferences.getString("userid", "");
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("local_admin").child(a1);
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    a2 = snapshot.child("admnName").getValue().toString();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(AddDeliveryBoy_Activity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

            name_editext = findViewById(R.id.add_deliveryboy_name_editext);
            email_editext = findViewById(R.id.add_deliveryboy_email_editext);
            passeord_editext = findViewById(R.id.add_deliveryboy_passeord_editext);
            conpassword_editext = findViewById(R.id.add_deliveryboy_conpassword_editext);
            deliverycharge_editext = findViewById(R.id.add_deliveryboy_deliverycharge_editext);
            save_button = findViewById(R.id.add_deliveryboy_save_button);
            new_deliveryboy_block_spinner = findViewById(R.id.add_deliveryboy_block_spinner);
            licence_editext = findViewById(R.id.add_deliveryboy_licence_editext);
            vechileno_editext = findViewById(R.id.add_deliveryboy_vechileno_editext);
            mobileno_editext = findViewById(R.id.add_deliveryboy_mobileno_editext);
            deliveryboy_image = findViewById(R.id.add_deliveryboy_image_button);

            progressDialog = new ProgressDialog(this);
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Loading...");

            deliveryboy_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openFileChooser();
                }
            });


            String[] catogries = {"UnBlocked", "Blocked"};
            ArrayAdapter<String> catog = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, catogries);

            new_deliveryboy_block_spinner.setAdapter(catog);

            save_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final String ename, aemail, apassword, aconpassword, alicence, avechileno, amobile, ablock, acharge;
                    ename = name_editext.getText().toString();
                    aemail = email_editext.getText().toString();
                    apassword = passeord_editext.getText().toString();
                    aconpassword = conpassword_editext.getText().toString();
                    acharge = deliverycharge_editext.getText().toString();
                    alicence = licence_editext.getText().toString();
                    avechileno = vechileno_editext.getText().toString();
                    amobile = mobileno_editext.getText().toString();
                    ablock = new_deliveryboy_block_spinner.getSelectedItem().toString();

                    if (ename.isEmpty() || aemail.isEmpty() || apassword.isEmpty() || aconpassword.isEmpty() || alicence.isEmpty() || avechileno.isEmpty() || amobile.isEmpty() || mImageUri == null || ablock.isEmpty() || acharge.isEmpty()) {
                        if (ename.isEmpty()) {
                            name_editext.setError("Required");
                        }
                        if (aemail.isEmpty()) {
                            email_editext.setError("Required");
                        }
                        if (apassword.isEmpty()) {
                            passeord_editext.setError("Required");
                        }
                        if (aconpassword.isEmpty()) {
                            conpassword_editext.setError("Required");
                        }
                        if (alicence.isEmpty()) {
                            licence_editext.setError("Required");
                        }
                        if (avechileno.isEmpty()) {
                            vechileno_editext.setError("Required");
                        }
                        if (amobile.isEmpty()) {
                            mobileno_editext.setError("Required");
                        }

                        if (mImageUri == null) {
                            Toast.makeText(AddDeliveryBoy_Activity.this, "Select Image", Toast.LENGTH_SHORT).show();
                        }
                        if (ablock.isEmpty()) {
                            Toast.makeText(AddDeliveryBoy_Activity.this, "Select Status", Toast.LENGTH_SHORT).show();
                        }

                        if (acharge.isEmpty()) {
                            deliverycharge_editext.setError("Required");
                        }
                    } else {
                        if (apassword.equals(aconpassword))
                        {
                            try {
                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("delivery_boy");

                                FirebaseAuth firebaseAuth;
                                firebaseAuth = FirebaseAuth.getInstance();

                                DatabaseReference reference1;
                                reference1 = FirebaseDatabase.getInstance().getReference("user_data");

                                StorageReference mstorageReference;
                                mstorageReference = FirebaseStorage.getInstance().getReference("delivery_boy/image");

                                progressDialog.show();
                                firebaseAuth.createUserWithEmailAndPassword(aemail, aconpassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            final String key = task.getResult().getUser().getUid();
                                            HashMap<String, String> hashMap = new HashMap<>();
                                            hashMap.put("userId", key);
                                            hashMap.put("userName", ename);
                                            hashMap.put("userEmail", aemail);
                                            hashMap.put("type", "delivery_boy");

                                            reference1.child(key).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        if (mUploadTask != null && mUploadTask.isInProgress()) {

                                                            Toast.makeText(AddDeliveryBoy_Activity.this, "Uploading in Progress", Toast.LENGTH_SHORT).show();
                                                        } else {
                                                            if (mImageUri != null) {
                                                                StorageReference fileRef = mstorageReference.child(System.currentTimeMillis() + "." + getFileExtention(mImageUri));
                                                                mUploadTask = fileRef.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                                    @Override
                                                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                                                                        while (!uriTask.isSuccessful())
                                                                            ;

                                                                        Uri downloadUrl = uriTask.getResult();

                                                                        DeliveryBoyModel model;
                                                                        model = new DeliveryBoyModel();

                                                                        model.setDeliveryBoyID(key);
                                                                        model.setDeliveryBoyName(ename);
                                                                        model.setDeliveryBoyEmail(aemail);
                                                                        model.setDeliveryBoyPassword(apassword);

                                                                        model.setDeliveryBoyLocalAdminID(a1);
                                                                        model.setDeliveryBoyDeliveryCharge(acharge);
                                                                        model.setOnlineorOffline("offline");
                                                                        model.setDeliveryBoyLocalAdminName(a2);

                                                                        model.setDeliveyBoyLicence(alicence);
                                                                        model.setDeliveyBoyVechileNo(avechileno);
                                                                        model.setDeliveyBoyMobileNo(amobile);
                                                                        model.setPhoto(downloadUrl.toString());
                                                                        model.setIsBlocked(ablock);
                                                                        model.setTemp1("");
                                                                        model.setTemp2("");

                                                                        ref.child(key).setValue(model);
                                                                        Toast.makeText(AddDeliveryBoy_Activity.this, "New Delivery Boy Added", Toast.LENGTH_SHORT).show();

                                                                        mImageUri = null;
                                                                        deliveryboy_image.setImageResource(R.drawable.ic_baseline_add_photo_alternate_24);

                                                                        name_editext.setText("");
                                                                        email_editext.setText("");
                                                                        passeord_editext.setText("");
                                                                        conpassword_editext.setText("");
                                                                        licence_editext.setText("");
                                                                        vechileno_editext.setText("");
                                                                        mobileno_editext.setText("");
                                                                        deliverycharge_editext.setText("");

                                                                        progressDialog.dismiss();
                                                                    }
                                                                }).addOnFailureListener(new OnFailureListener() {
                                                                    @Override
                                                                    public void onFailure(@NonNull Exception e) {
                                                                        mImageUri = null;
                                                                        progressDialog.dismiss();
                                                                        Toast.makeText(AddDeliveryBoy_Activity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                                    }
                                                                });
                                                            } else {
                                                                progressDialog.dismiss();
                                                                Toast.makeText(AddDeliveryBoy_Activity.this, "No files Selected", Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    }
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    progressDialog.dismiss();
                                                    Toast.makeText(AddDeliveryBoy_Activity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        progressDialog.dismiss();
                                        Toast.makeText(AddDeliveryBoy_Activity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            conpassword_editext.setError("Password Not Matched");
                            Toast.makeText(AddDeliveryBoy_Activity.this, "Password Not Matched", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openFileChooser()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, image_pic_request);
    }

    private String getFileExtention(Uri uri) {
        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(uri));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == image_pic_request && resultCode == RESULT_OK && data != null && data.getData() != null)
        {
            mImageUri = data.getData();
            Picasso.get().load(mImageUri).into(deliveryboy_image);
        }
    }
}