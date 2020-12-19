package com.epayeats.epayeatsco_admin.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.FragmentActivity;

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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.epayeats.epayeatsco_admin.Model.RestaurantModel;
import com.epayeats.epayeatsco_admin.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
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
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class AddRestaurant_MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    EditText add_res_name, add_res_user_name, add_res_phoneno, add_res_opentime, add_res_closetime, add_res_licenceno, add_res_gstno, add_res_email, add_res_password, add_res_conpassword;
    ImageView add_res_photo;
    Button restaurant_map_savebtn;
    TextView restaurantmap_selectedloc;
    BottomSheetBehavior behavior;
    SwitchCompat switchCompat;
    Spinner add_res_block_spinner;

    SharedPreferences sharedPreferences;
    String a1;

    String kmdata;
    String AdminName;

    private static int image_pic_request = 1;

    Double lat, lon;
    private Uri mImageUri;
    private StorageTask mUploadTask;

    public ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_restaurant__maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.restaurantmap);
        mapFragment.getMapAsync(this);

        try {
            sharedPreferences = getSharedPreferences("data", 0);
            a1 = sharedPreferences.getString("userid", "");

            progressDialog = new ProgressDialog(this);
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Loading...");

            add_res_name = findViewById(R.id.add_res_name);
            add_res_user_name = findViewById(R.id.add_res_user_name);
            add_res_phoneno = findViewById(R.id.add_res_phoneno);
            add_res_opentime = findViewById(R.id.add_res_opentime);
            add_res_closetime = findViewById(R.id.add_res_closetime);
            add_res_licenceno = findViewById(R.id.add_res_licenceno);
            add_res_gstno = findViewById(R.id.add_res_gstno);
            add_res_photo = findViewById(R.id.add_res_photo);
            restaurant_map_savebtn = findViewById(R.id.restaurant_map_savebtn);
            restaurantmap_selectedloc = findViewById(R.id.restaurantmap_selectedloc);
            add_res_email = findViewById(R.id.add_res_email);
            add_res_password = findViewById(R.id.add_res_password);
            add_res_conpassword = findViewById(R.id.add_res_conpassword);
            switchCompat = findViewById(R.id.add_res_closed);
            add_res_block_spinner = findViewById(R.id.add_res_block_spinner);

            String[] catogries = {"UnBlocked", "Blocked"};
            ArrayAdapter<String> catog = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, catogries);
            add_res_block_spinner.setAdapter(catog);

            restaurant_map_savebtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addRestaurant();
                }
            });

            View bootom = findViewById(R.id.bottom_sheet);
            behavior = BottomSheetBehavior.from(bootom);
            behavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
                @Override
                public void onStateChanged(@NonNull View bottomSheet, int newState) {
                    switch (newState) {
                        case BottomSheetBehavior.STATE_DRAGGING:
                            break;
                        case BottomSheetBehavior.STATE_SETTLING:
                            break;
                        case BottomSheetBehavior.STATE_EXPANDED:
                            break;
                    }
                }

                @Override
                public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                }
            });

            add_res_photo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openFileChooser();
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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                try {
                    Query query = FirebaseDatabase.getInstance().getReference("local_admin").child(a1);
                    query.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            kmdata = snapshot.child("admnBusinessKM").getValue().toString();
                            String Loc = snapshot.child("admnBusinessArea").getValue().toString();
                            AdminName = snapshot.child("admnName").getValue().toString();

                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("business");
                            reference.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot snapshot2 : snapshot.getChildren()) {
                                        if (Loc.equals(snapshot2.child("location").getValue().toString())) {
                                            String cLat = snapshot2.child("latitude").getValue().toString();
                                            String cLot = snapshot2.child("longitute").getValue().toString();

                                            mMap.clear();
                                            restaurantmap_selectedloc.setText("");
                                            mMap.addMarker(new MarkerOptions().position(latLng).title("New Restaurant Location"));
                                            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

                                            String s = latLng.toString();
                                            String s2 = s.replaceAll("[()lat/lng:]", "");
                                            String[] s3 = s2.split(",");
                                            Double d1 = Double.valueOf(s3[0]);
                                            Double d2 = Double.valueOf(s3[1]);

                                            lat = d1;
                                            lon = d2;

                                            Double km = distance(Double.parseDouble(cLat), Double.parseDouble(cLot), d1, d2);

                                            String roKm = String.valueOf(Math.round(km));

                                            int tempkmdata = Integer.parseInt(kmdata);
                                            int temproKm = Integer.parseInt(roKm);

                                            if (tempkmdata <= temproKm) {
                                                Toast.makeText(AddRestaurant_MapsActivity.this, "Please select a place, within your business kilometer radius", Toast.LENGTH_SHORT).show();
                                            } else {
                                                try {
                                                    Geocoder geocoder = new Geocoder(AddRestaurant_MapsActivity.this, Locale.getDefault());
                                                    List<Address> addresses = geocoder.getFromLocation(d1, d2, 1);
                                                    String address = addresses.get(0).getAddressLine(0);
                                                    restaurantmap_selectedloc.setText(address);

                                                    setBootom();

                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Toast.makeText(AddRestaurant_MapsActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(AddRestaurant_MapsActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

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

    private void setBootom()
    {
        if(behavior.getState() == BottomSheetBehavior.STATE_COLLAPSED)
        {
            behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        }
        else
        {
            behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
    }

    private void addRestaurant()
    {
        String name, username, phone, open, close, licence, gstno, location, email, password, conpassword, restClosed, block;

        name = add_res_name.getText().toString();
        username = add_res_user_name.getText().toString();
        phone = add_res_phoneno.getText().toString();
        open = add_res_opentime.getText().toString();
        close = add_res_closetime.getText().toString();
        licence = add_res_licenceno.getText().toString();
        gstno = add_res_gstno.getText().toString();
        location = restaurantmap_selectedloc.getText().toString();
        email = add_res_email.getText().toString();
        password = add_res_password.getText().toString();
        conpassword = add_res_conpassword.getText().toString();
        block = add_res_block_spinner.getSelectedItem().toString();

        if(switchCompat.isChecked())
        {
            restClosed = "open";
        }
        else
        {
            restClosed = "closed";
        }

        if(name.isEmpty() || username.isEmpty() || phone.isEmpty() || open.isEmpty() || close.isEmpty() || licence.isEmpty() || gstno.isEmpty() || location.isEmpty() || mImageUri == null || email.isEmpty() || password.isEmpty() || conpassword.isEmpty())
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
            if(location.isEmpty())
            {
                Toast.makeText(this, "Select Restaurant Location", Toast.LENGTH_SHORT).show();
            }
            if(mImageUri == null)
            {
                Toast.makeText(this, "Select Restaurant Image", Toast.LENGTH_SHORT).show();
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
        }
        else
        {
            if(password.equals(conpassword))
            {
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("restaurants");
                String pushKey = ref.push().getKey();

                DatabaseReference reference1;
                reference1 = FirebaseDatabase.getInstance().getReference("user_data");

                FirebaseAuth firebaseAuth;
                StorageReference mstorageReference;

                firebaseAuth = FirebaseAuth.getInstance();
                mstorageReference = FirebaseStorage.getInstance().getReference("restaurant/image");


                progressDialog.show();
                firebaseAuth.createUserWithEmailAndPassword(email, conpassword)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>()
                        {
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

                                    reference1.child(key).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>()
                                    {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task)
                                        {
                                            if (task.isSuccessful())
                                            {
                                                if (mUploadTask != null && mUploadTask.isInProgress())
                                                {
                                                    Toast.makeText(AddRestaurant_MapsActivity.this, "Uploading in Progress", Toast.LENGTH_SHORT).show();
                                                }
                                                else
                                                {
                                                    if(mImageUri != null)
                                                    {
                                                        StorageReference fileRef = mstorageReference.child(System.currentTimeMillis() + "." + getFileExtention(mImageUri));
                                                        mUploadTask = fileRef.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>()
                                                        {
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

                                                                model.setResLocation(location);
                                                                model.setIsShopClosed(restClosed);

                                                                model.setLat(String.valueOf(lat));
                                                                model.setLon(String.valueOf(lon));

                                                                model.setResOpenTime(open);
                                                                model.setResCloseTime(close);

                                                                model.setResLicenceNo(licence);
                                                                model.setResGstNo(gstno);

                                                                model.setResName(name);
                                                                model.setResPhone(phone);
                                                                model.setResPhoto(downloadUrl.toString());

                                                                model.setIsBlocked(block);

                                                                ref.child(key).setValue(model);

                                                                Toast.makeText(AddRestaurant_MapsActivity.this, "New Restaurant Added", Toast.LENGTH_SHORT).show();

                                                                add_res_name.setText("");
                                                                add_res_phoneno.setText("");
                                                                add_res_opentime.setText("");
                                                                add_res_closetime.setText("");
                                                                add_res_licenceno.setText("");
                                                                add_res_gstno.setText("");
                                                                add_res_user_name.setText("");

                                                                add_res_photo.setImageResource(R.drawable.ic_baseline_add_photo_alternate_24);

                                                                restaurantmap_selectedloc.setText("");
                                                                add_res_email.setText("");
                                                                add_res_password.setText("");
                                                                add_res_conpassword.setText("");
                                                                kmdata = "";

                                                                mImageUri = null;

                                                                progressDialog.dismiss();

                                                                setBootom();
                                                            }
                                                        }).addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e)
                                                            {
                                                                progressDialog.dismiss();
                                                                mImageUri = null;
                                                                Toast.makeText(AddRestaurant_MapsActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                                            }
                                                        });
                                                    }
                                                    else
                                                    {
                                                        progressDialog.dismiss();
                                                        Toast.makeText(AddRestaurant_MapsActivity.this, "No files Selected", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            }
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            progressDialog.dismiss();
                                            Toast.makeText(AddRestaurant_MapsActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                                Toast.makeText(AddRestaurant_MapsActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
            else
            {
                add_res_conpassword.setError("Not Matched");
                Toast.makeText(this, "Password not matched", Toast.LENGTH_SHORT).show();
            }
        }
    }

}