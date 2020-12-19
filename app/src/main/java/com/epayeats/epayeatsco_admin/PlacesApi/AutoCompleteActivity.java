package com.epayeats.epayeatsco_admin.PlacesApi;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.epayeats.epayeatsco_admin.Activity.AddRestaurant_MapsActivity;
import com.epayeats.epayeatsco_admin.Model.RestaurantModel;
import com.epayeats.epayeatsco_admin.R;
import com.google.android.gms.maps.model.LatLng;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class AutoCompleteActivity extends AppCompatActivity
{
    AutoCompleteTextView addline1;
    EditText addline2;
    EditText addcity;
    EditText addstate;
    EditText addcountry;
    EditText addpincode;

    double latitude;
    double longitude;

    Geocoder mGeocoder;

    EditText add_res_name, add_res_user_name, add_res_phoneno, add_res_opentime, add_res_closetime, add_res_licenceno, add_res_gstno, add_res_email, add_res_password, add_res_conpassword;
    ImageView add_res_photo;
    Button restaurant_map_savebtn;
    SwitchCompat switchCompat;
    Spinner add_res_block_spinner;
    private static int image_pic_request = 1;

    private Uri mImageUri;
    private StorageTask mUploadTask;

    public ProgressDialog progressDialog;

    SharedPreferences sharedPreferences;
    String a1;

    String kmdata;
    String AdminName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auto_complete);

        sharedPreferences = getSharedPreferences("data", 0);
        a1 = sharedPreferences.getString("userid", "");

        progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading...");

        add_res_name = findViewById(R.id.add_res_name_auto);
        add_res_user_name = findViewById(R.id.add_res_user_name_auto);
        add_res_phoneno = findViewById(R.id.add_res_phoneno_auto);
        add_res_opentime = findViewById(R.id.add_res_opentime_auto);
        add_res_closetime = findViewById(R.id.add_res_closetime_auto);
        add_res_licenceno = findViewById(R.id.add_res_licenceno_auto);
        add_res_gstno = findViewById(R.id.add_res_gstno_auto);
        add_res_photo = findViewById(R.id.add_res_photo_auto);
        restaurant_map_savebtn = findViewById(R.id.restaurant_map_savebtn_auto);
        add_res_email = findViewById(R.id.add_res_email_auto);
        add_res_password = findViewById(R.id.add_res_password_auto);
        add_res_conpassword = findViewById(R.id.add_res_conpassword_auto);
        switchCompat = findViewById(R.id.add_res_closed_auto);
        add_res_block_spinner = findViewById(R.id.add_res_block_spinner_auto);

        String[] catogries = {"UnBlocked", "Blocked" };
        ArrayAdapter<String> catog = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, catogries);
        add_res_block_spinner.setAdapter(catog);

        addline1 = findViewById(R.id.newaddline1);
        addline2 = findViewById(R.id.newaddline2);
        addcity = findViewById(R.id.newaddcity);
        addstate = findViewById(R.id.newaddstate);
        addcountry = findViewById(R.id.newaddcountry);
        addpincode = findViewById(R.id.newaddpincode);

        addline1.setAdapter(new PlaceAutoSuggestAdapter(this,android.R.layout.simple_list_item_1));

        addline1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                getLatLng(addline1.getText().toString());
            }
        });


        add_res_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                openFileChooser();
            }
        });

        restaurant_map_savebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                addRestaurant();
//                searchLoc();
            }
        });
    }

    private void searchLoc()
    {
        progressDialog.show();
        Query query = FirebaseDatabase.getInstance().getReference("local_admin").child(a1);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                progressDialog.dismiss();
                kmdata = snapshot.child("admnBusinessKM").getValue().toString();
                String Loc = snapshot.child("admnBusinessArea").getValue().toString();
                AdminName = snapshot.child("admnName").getValue().toString();

                progressDialog.show();
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("business");
                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot)
                    {
                        progressDialog.dismiss();
                        for(DataSnapshot snapshot2 : snapshot.getChildren())
                        {
                            if(Loc.equals(snapshot2.child("location").getValue().toString()))
                            {
                                String cLat = snapshot2.child("latitude").getValue().toString();
                                String cLot = snapshot2.child("longitute").getValue().toString();

                                Double km = distance(Double.parseDouble(cLat), Double.parseDouble(cLot), latitude, longitude);

                                String roKm = String.valueOf(Math.round(km));

                                int tempkmdata = Integer.parseInt(kmdata);
                                int temproKm = Integer.parseInt(roKm);

                                if(tempkmdata <= temproKm)
                                {
                                    Toast.makeText(AutoCompleteActivity.this, "Please select a place, within your business kilometer radius", Toast.LENGTH_SHORT).show();
                                }
                                else
                                {
                                    Toast.makeText(AutoCompleteActivity.this, "good", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error)
                    {
                        Toast.makeText(AutoCompleteActivity.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {
                Toast.makeText(AutoCompleteActivity.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addRestaurant()
    {
        String name, username, phone, open, close, licence, gstno, email, password, conpassword, restClosed, block, loc1, loc2, loc3,loc4, loc5, loc6;

        name = add_res_name.getText().toString();
        username = add_res_user_name.getText().toString();
        phone = add_res_phoneno.getText().toString();
        open = add_res_opentime.getText().toString();
        close = add_res_closetime.getText().toString();
        licence = add_res_licenceno.getText().toString();
        gstno = add_res_gstno.getText().toString();
        email = add_res_email.getText().toString();
        password = add_res_password.getText().toString();
        conpassword = add_res_conpassword.getText().toString();
        block = add_res_block_spinner.getSelectedItem().toString();

        loc1 = addline1.getText().toString();
        loc2 = addline2.getText().toString();
        loc3 = addcity.getText().toString();
        loc4 = addstate.getText().toString();
        loc5 = addcountry.getText().toString();
        loc6 = addpincode.getText().toString();

        String temp = loc1 +","+ loc2+"," + loc3+"," + loc4+"," +loc5+"," + loc6;

        if(switchCompat.isChecked())
        {
            restClosed = "open";
        }
        else
        {
            restClosed = "closed";
        }

        if(name.isEmpty() || username.isEmpty() || phone.isEmpty() || open.isEmpty() || close.isEmpty() || licence.isEmpty() || gstno.isEmpty() || email.isEmpty() || password.isEmpty() || conpassword.isEmpty() || loc1.isEmpty() || mImageUri == null)
        {
            if(name.isEmpty())
            {
                add_res_name.setError("Required");
            }
            if(username.isEmpty())
            {
                add_res_user_name.setError("Required");
            }
            if(phone.isEmpty())
            {
                add_res_phoneno.setError("Required");
            }
            if(open.isEmpty())
            {
                add_res_opentime.setError("Required");
            }
            if(close.isEmpty())
            {
                add_res_closetime.setError("Required");
            }
            if(licence.isEmpty())
            {
                add_res_licenceno.setError("Required");
            }
            if(gstno.isEmpty())
            {
                add_res_gstno.setError("Required");
            }

            if(email.isEmpty())
            {
                add_res_email.setError("Required");
            }
            if(password.isEmpty())
            {
                add_res_password.setError("Required");
            }
            if(conpassword.isEmpty())
            {
                add_res_conpassword.setError("Required");
            }
            if(loc1.isEmpty())
            {
                addline1.setError("Required");
            }
            if(mImageUri == null)
            {
                Toast.makeText(this, "No files Selected", Toast.LENGTH_SHORT).show();
            }
        }
        else
        {
            if(password.equals(conpassword))
            {
                progressDialog.show();
                Query query = FirebaseDatabase.getInstance().getReference("local_admin").child(a1);
                query.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot)
                    {
                        progressDialog.dismiss();
                        kmdata = snapshot.child("admnBusinessKM").getValue().toString();
                        String Loc = snapshot.child("admnBusinessArea").getValue().toString();
                        AdminName = snapshot.child("admnName").getValue().toString();

                        progressDialog.show();
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("business");
                        reference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot)
                            {
                                progressDialog.dismiss();
                                for(DataSnapshot snapshot2 : snapshot.getChildren())
                                {
                                    if(Loc.equals(snapshot2.child("location").getValue().toString()))
                                    {
                                        String cLat = snapshot2.child("latitude").getValue().toString();
                                        String cLot = snapshot2.child("longitute").getValue().toString();

                                        Double km = distance(Double.parseDouble(cLat), Double.parseDouble(cLot), latitude, longitude);

                                        String roKm = String.valueOf(Math.round(km));

                                        int tempkmdata = Integer.parseInt(kmdata);
                                        int temproKm = Integer.parseInt(roKm);

                                        if(tempkmdata <= temproKm)
                                        {
                                            addline1.setError("Change place");
                                            Toast.makeText(AutoCompleteActivity.this, "Please select a place, within your business kilometer radius", Toast.LENGTH_SHORT).show();
                                        }
                                        else
                                        {


                                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("restaurants");
                                            DatabaseReference reference1;
                                            reference1 = FirebaseDatabase.getInstance().getReference("user_data");

                                            FirebaseAuth firebaseAuth;
                                            StorageReference mstorageReference;

                                            firebaseAuth = FirebaseAuth.getInstance();
                                            mstorageReference = FirebaseStorage.getInstance().getReference("restaurant/image");

                                            progressDialog.show();
                                            firebaseAuth.createUserWithEmailAndPassword(email, conpassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                                @Override
                                                public void onComplete(@NonNull Task<AuthResult> task)
                                                {
                                                    if(task.isSuccessful())
                                                    {
                                                        final String key = task.getResult().getUser().getUid();
                                                        HashMap<String, String> hashMap = new HashMap<>();
                                                        hashMap.put("userId", key);
                                                        hashMap.put("userName", name);
                                                        hashMap.put("userEmail", email);
                                                        hashMap.put("type", "restaurant");

                                                        reference1.child(key).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task)
                                                            {

                                                                if (task.isSuccessful())
                                                                {
                                                                    if (mUploadTask != null && mUploadTask.isInProgress())
                                                                    {
                                                                        Toast.makeText(AutoCompleteActivity.this, "Uploading in Progress", Toast.LENGTH_SHORT).show();
                                                                    }
                                                                    else
                                                                    {
                                                                        if(mImageUri != null)
                                                                        {
                                                                            StorageReference fileRef = mstorageReference.child(System.currentTimeMillis() + "." + getFileExtention(mImageUri));
                                                                            mUploadTask = fileRef.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                                                @Override
                                                                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
                                                                                {
                                                                                    Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                                                                                    while (!uriTask.isSuccessful());

                                                                                    Uri downloadUrl = uriTask.getResult();

                                                                                    RestaurantModel model;
                                                                                    model = new RestaurantModel();

                                                                                    model.setResID(key);
                                                                                    model.setResName(username);
                                                                                    model.setResEmail(email);
                                                                                    model.setResPassword(conpassword);

                                                                                    model.setResLocalAdminID(a1);
                                                                                    model.setResLocalAdminName(AdminName);

                                                                                    model.setResLocation(temp);
                                                                                    model.setIsShopClosed(restClosed);

                                                                                    model.setLat(String.valueOf(latitude));
                                                                                    model.setLon(String.valueOf(longitude));

                                                                                    model.setResOpenTime(open);
                                                                                    model.setResCloseTime(close);

                                                                                    model.setResLicenceNo(licence);
                                                                                    model.setResGstNo(gstno);

                                                                                    model.setResName(name);
                                                                                    model.setResPhone(phone);
                                                                                    model.setResPhoto(downloadUrl.toString());

                                                                                    model.setIsBlocked(block);

                                                                                    ref.child(key).setValue(model);

                                                                                    Toast.makeText(AutoCompleteActivity.this, "New Restaurant Added", Toast.LENGTH_SHORT).show();


                                                                                    add_res_name.setText("");
                                                                                    add_res_phoneno.setText("");
                                                                                    add_res_opentime.setText("");
                                                                                    add_res_closetime.setText("");
                                                                                    add_res_licenceno.setText("");
                                                                                    add_res_gstno.setText("");
                                                                                    add_res_user_name.setText("");

                                                                                    add_res_photo.setImageResource(R.drawable.ic_baseline_add_photo_alternate_24);

                                                                                    add_res_email.setText("");
                                                                                    add_res_password.setText("");
                                                                                    add_res_conpassword.setText("");
                                                                                    kmdata = "";

                                                                                    addline1.setText("");
                                                                                    addline2.setText("");
                                                                                    addcity.setText("");
                                                                                    addstate.setText("");
                                                                                    addcountry.setText("");
                                                                                    addpincode.setText("");

                                                                                    mImageUri = null;

                                                                                    progressDialog.dismiss();
                                                                                }
                                                                            }).addOnFailureListener(new OnFailureListener() {
                                                                                @Override
                                                                                public void onFailure(@NonNull Exception e)
                                                                                {
                                                                                    progressDialog.dismiss();
                                                                                    mImageUri = null;
                                                                                    Toast.makeText(AutoCompleteActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                                                                }
                                                                            });
                                                                        }
                                                                        else
                                                                        {
                                                                            progressDialog.dismiss();
                                                                            Toast.makeText(AutoCompleteActivity.this, "No files Selected", Toast.LENGTH_SHORT).show();
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }).addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                progressDialog.dismiss();
                                                                Toast.makeText(AutoCompleteActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                                            }
                                                        });
                                                    }
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    progressDialog.dismiss();
                                                    Toast.makeText(AutoCompleteActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            });


                                        }
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error)
                            {
                                progressDialog.dismiss();
                                Toast.makeText(AutoCompleteActivity.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        progressDialog.dismiss();
                        Toast.makeText(AutoCompleteActivity.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
            else
            {
                add_res_conpassword.setError("Not Matched");
                Toast.makeText(AutoCompleteActivity.this, "Password not Matched", Toast.LENGTH_SHORT).show();
            }


        }

    }

//    private void addRestaurant()
//    {
//        Query query = FirebaseDatabase.getInstance().getReference("local_admin").child(a1);
//        query.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot)
//            {
//                kmdata = snapshot.child("admnBusinessKM").getValue().toString();
//                String Loc = snapshot.child("admnBusinessArea").getValue().toString();
//                AdminName = snapshot.child("admnName").getValue().toString();
//
//                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("business");
//                reference.addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot)
//                    {
//                        for(DataSnapshot snapshot2 : snapshot.getChildren())
//                        {
//                            if(Loc.equals(snapshot2.child("location").getValue().toString()))
//                            {
//                                String cLat = snapshot2.child("latitude").getValue().toString();
//                                String cLot = snapshot2.child("longitute").getValue().toString();
//
//                                Double km = distance(Double.parseDouble(cLat), Double.parseDouble(cLot), latitude, longitude);
//
//                                String roKm = String.valueOf(Math.round(km));
//
//                                int tempkmdata = Integer.parseInt(kmdata);
//                                int temproKm = Integer.parseInt(roKm);
//
//                                if(tempkmdata <= temproKm)
//                                {
//                                    Toast.makeText(AutoCompleteActivity.this, "Please select a place, within your business kilometer radius", Toast.LENGTH_SHORT).show();
//                                }
//                                else
//                                {
//                                    String name, username, phone, open, close, licence, gstno, email, password, conpassword, restClosed, block, loc1, loc2, loc3,loc4, loc5, loc6;
//
//                                    name = add_res_name.getText().toString();
//                                    username = add_res_user_name.getText().toString();
//                                    phone = add_res_phoneno.getText().toString();
//                                    open = add_res_opentime.getText().toString();
//                                    close = add_res_closetime.getText().toString();
//                                    licence = add_res_licenceno.getText().toString();
//                                    gstno = add_res_gstno.getText().toString();
//                                    email = add_res_email.getText().toString();
//                                    password = add_res_password.getText().toString();
//                                    conpassword = add_res_conpassword.getText().toString();
//                                    block = add_res_block_spinner.getSelectedItem().toString();
//
//                                    loc1 = addline1.getText().toString();
//                                    loc2 = addline2.getText().toString();
//                                    loc3 = addcity.getText().toString();
//                                    loc4 = addstate.getText().toString();
//                                    loc5 = addcountry.getText().toString();
//                                    loc6 = addpincode.getText().toString();
//
//                                    String temp = loc1 +","+ loc2+"," + loc3+"," + loc4+"," +loc5+"," + loc6;
//
//                                    if(switchCompat.isChecked())
//                                    {
//                                        restClosed = "open";
//                                    }
//                                    else
//                                    {
//                                        restClosed = "closed";
//                                    }
//
//                                    if(name.isEmpty() || username.isEmpty() || phone.isEmpty() || open.isEmpty() || close.isEmpty() || licence.isEmpty() || gstno.isEmpty() || email.isEmpty() || password.isEmpty() || conpassword.isEmpty() || loc1.isEmpty())
//                                    {
//                                        if(name.isEmpty())
//                                        {
//                                            add_res_name.setError("Required");
//                                        }
//                                        if(username.isEmpty())
//                                        {
//                                            add_res_user_name.setError("Required");
//                                        }
//                                        if(phone.isEmpty())
//                                        {
//                                            add_res_phoneno.setError("Required");
//                                        }
//                                        if(open.isEmpty())
//                                        {
//                                            add_res_opentime.setError("Required");
//                                        }
//                                        if(close.isEmpty())
//                                        {
//                                            add_res_closetime.setError("Required");
//                                        }
//                                        if(licence.isEmpty())
//                                        {
//                                            add_res_licenceno.setError("Required");
//                                        }
//                                        if(gstno.isEmpty())
//                                        {
//                                            add_res_gstno.setError("Required");
//                                        }
//
//                                        if(email.isEmpty())
//                                        {
//                                            add_res_email.setError("Required");
//                                        }
//                                        if(password.isEmpty())
//                                        {
//                                            add_res_password.setError("Required");
//                                        }
//                                        if(conpassword.isEmpty())
//                                        {
//                                            add_res_conpassword.setError("Required");
//                                        }
//                                        if(loc1.isEmpty())
//                                        {
//                                            addline1.setError("Required");
//                                        }
//                                    }
//                                    else
//                                    {
//                                        if(password.equals(conpassword))
//                                        {
//                                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("restaurants");
//                                            DatabaseReference reference1;
//                                            reference1 = FirebaseDatabase.getInstance().getReference("user_data");
//
//                                            FirebaseAuth firebaseAuth;
//                                            StorageReference mstorageReference;
//
//                                            firebaseAuth = FirebaseAuth.getInstance();
//                                            mstorageReference = FirebaseStorage.getInstance().getReference("restaurant/image");
//
//                                            progressDialog.show();
//                                            firebaseAuth.createUserWithEmailAndPassword(email, conpassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//                                                @Override
//                                                public void onComplete(@NonNull Task<AuthResult> task)
//                                                {
//                                                    if(task.isSuccessful())
//                                                    {
//                                                        final String key = task.getResult().getUser().getUid();
//                                                        HashMap<String, String> hashMap = new HashMap<>();
//                                                        hashMap.put("userId", key);
//                                                        hashMap.put("userName", name);
//                                                        hashMap.put("userEmail", email);
//                                                        hashMap.put("type", "restaurant");
//
//                                                        reference1.child(key).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
//                                                            @Override
//                                                            public void onComplete(@NonNull Task<Void> task)
//                                                            {
//
//                                                                if (task.isSuccessful())
//                                                                {
//                                                                    if (mUploadTask != null && mUploadTask.isInProgress())
//                                                                    {
//                                                                        Toast.makeText(AutoCompleteActivity.this, "Uploading in Progress", Toast.LENGTH_SHORT).show();
//                                                                    }
//                                                                    else
//                                                                    {
//                                                                        if(mImageUri != null)
//                                                                        {
//                                                                            StorageReference fileRef = mstorageReference.child(System.currentTimeMillis() + "." + getFileExtention(mImageUri));
//                                                                            mUploadTask = fileRef.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                                                                                @Override
//                                                                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
//                                                                                {
//                                                                                    Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
//                                                                                    while (!uriTask.isSuccessful());
//
//                                                                                    Uri downloadUrl = uriTask.getResult();
//
//                                                                                    RestaurantModel model;
//                                                                                    model = new RestaurantModel();
//
//                                                                                    model.setResID(key);
//                                                                                    model.setResName(username);
//                                                                                    model.setResEmail(email);
//                                                                                    model.setResPassword(conpassword);
//
//                                                                                    model.setResLocalAdminID(a1);
//                                                                                    model.setResLocalAdminName(AdminName);
//
//                                                                                    model.setResLocation(temp);
//                                                                                    model.setIsShopClosed(restClosed);
//
//                                                                                    model.setLat(String.valueOf(latitude));
//                                                                                    model.setLon(String.valueOf(longitude));
//
//                                                                                    model.setResOpenTime(open);
//                                                                                    model.setResCloseTime(close);
//
//                                                                                    model.setResLicenceNo(licence);
//                                                                                    model.setResGstNo(gstno);
//
//                                                                                    model.setResName(name);
//                                                                                    model.setResPhone(phone);
//                                                                                    model.setResPhoto(downloadUrl.toString());
//
//                                                                                    model.setIsBlocked(block);
//
//                                                                                    ref.child(key).setValue(model);
//
//                                                                                    Toast.makeText(AutoCompleteActivity.this, "New Restaurant Added", Toast.LENGTH_SHORT).show();
//
//
//                                                                                    add_res_name.setText("");
//                                                                                    add_res_phoneno.setText("");
//                                                                                    add_res_opentime.setText("");
//                                                                                    add_res_closetime.setText("");
//                                                                                    add_res_licenceno.setText("");
//                                                                                    add_res_gstno.setText("");
//                                                                                    add_res_user_name.setText("");
//
//                                                                                    Picasso.get().load(R.drawable.ic_baseline_add_photo_alternate_24).into(add_res_photo);
//
//                                                                                    add_res_email.setText("");
//                                                                                    add_res_password.setText("");
//                                                                                    add_res_conpassword.setText("");
//                                                                                    kmdata = "";
//
//                                                                                    addline1.setText("");
//                                                                                    addline2.setText("");
//                                                                                    addcity.setText("");
//                                                                                    addstate.setText("");
//                                                                                    addcountry.setText("");
//                                                                                    addpincode.setText("");
//
//                                                                                    mImageUri = null;
//
//                                                                                    progressDialog.dismiss();
//
//
//                                                                                }
//                                                                            }).addOnFailureListener(new OnFailureListener() {
//                                                                                @Override
//                                                                                public void onFailure(@NonNull Exception e)
//                                                                                {
//                                                                                    progressDialog.dismiss();
//                                                                                    mImageUri = null;
//                                                                                    Toast.makeText(AutoCompleteActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
//                                                                                }
//                                                                            });
//
//
//
//                                                                        }
//                                                                        else
//                                                                        {
//                                                                            progressDialog.dismiss();
//                                                                            Toast.makeText(AutoCompleteActivity.this, "No files Selected", Toast.LENGTH_SHORT).show();
//                                                                        }
//                                                                    }
//                                                                }
//
//
//
//
//                                                            }
//                                                        }).addOnFailureListener(new OnFailureListener() {
//                                                            @Override
//                                                            public void onFailure(@NonNull Exception e) {
//                                                                progressDialog.dismiss();
//                                                                Toast.makeText(AutoCompleteActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
//                                                            }
//                                                        });
//
//
//
//                                                    }
//
//
//
//
//
//                                                }
//                                            }).addOnFailureListener(new OnFailureListener() {
//                                                @Override
//                                                public void onFailure(@NonNull Exception e) {
//                                                    progressDialog.dismiss();
//                                                    Toast.makeText(AutoCompleteActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
//                                                }
//                                            });
//
//
//
//
//
//
//                                        }
//                                        else
//                                        {
//                                            add_res_conpassword.setError("Not Matched");
//                                            Toast.makeText(AutoCompleteActivity.this, "Password not Matched", Toast.LENGTH_SHORT).show();
//                                        }
//                                    }
//
//                                }
//                            }
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                    }
//                });
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//    }

    private double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;

        double km = dist / 0.62137;

        return (dist);
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

    private void openFileChooser()
    {
    Intent intent = new Intent();
    intent.setType("image/*");
    intent.setAction(Intent.ACTION_GET_CONTENT);
    startActivityForResult(intent, image_pic_request);
    }

    private String getFileExtention(Uri uri)
    {
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
            Picasso.get().load(mImageUri).into(add_res_photo);
        }
    }

    public void getLatLng(String location) {
        if (Geocoder.isPresent()) {
            try {
                Geocoder gc = new Geocoder(this);
                List<Address> addresses = gc.getFromLocationName(location, 5); // get the found Address Objects
                List<LatLng> ll = new ArrayList<LatLng>(addresses.size()); // A list to save the coordinates if they are available
                for (Address a : addresses) {
                    if (a.hasLatitude() && a.hasLongitude()) {
                        ll.add(new LatLng(a.getLatitude(), a.getLongitude()));
                        System.out.println("HAHAHA  loc lat: " + a.getLatitude() + " " + a.getLongitude());
                        latitude = a.getLatitude();
                        longitude = a.getLongitude();

                        try {
                            getCityNameByCoordinates(a.getLatitude(), a.getLongitude());
                        } catch (Exception e) {
                            e.printStackTrace();
                            getLatLng(location);
                        }
                    }
                }
                if (addresses.size() < 1) {
                    getLatLng(location);
                }
            } catch (Exception e) {
                getLatLng(location);
            }
        }
    }

    private String getCityNameByCoordinates(double lat, double lon) throws IOException {
        mGeocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses = mGeocoder.getFromLocation(lat, lon, 1);
        if (addresses != null && addresses.size() > 0) {
            System.out.println("HAHAHA place details: country " + addresses.get(0).getCountryName() + " PostalCode " + addresses.get(0).getPostalCode() + " getLocality " + addresses.get(0).getLocality() + " getLocale " + addresses.get(0).getLocale() + " getSubLocality " + addresses.get(0).getAdminArea());
            try {
                addline2.setText(addline1.getText().toString().split(", ")[1]);
                addline1.setText(addline1.getText().toString().split(", ")[0]);
            } catch (Exception e) {
                e.printStackTrace();
            }
            addcity.setText(addresses.get(0).getLocality());
            addcity.setSelection(addcity.getText().toString().length());
            addstate.setText(addresses.get(0).getAdminArea());
            addstate.setSelection(addstate.getText().toString().length());
            addpincode.setText(addresses.get(0).getPostalCode());
            addpincode.setSelection(addpincode.getText().toString().length());
            addcountry.setText(addresses.get(0).getCountryName());
            addcountry.setSelection(addcountry.getText().toString().length());
            addcity.setError(null);
            addstate.setError(null);
            addpincode.setError(null);
            addcountry.setError(null);

            return addresses.get(0).getLocality();
        }
        return null;
    }
}