package com.epayeats.epayeatsco_admin.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.epayeats.epayeatsco_admin.Model.RestaurantModel;
import com.epayeats.epayeatsco_admin.PlacesApi.AutoCompleteActivity;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class AddRestaurantCurrentLocation_Activity extends AppCompatActivity implements LocationListener
{

    TextView text_location;
    Button button_location;
    LocationManager locationManager;
    boolean GpsStatus;
    String lang;
    String lot;

    EditText add_res_name, add_res_user_name, add_res_phoneno, add_res_opentime, add_res_closetime, add_res_licenceno, add_res_gstno, add_res_email, add_res_password, add_res_conpassword;
    ImageView add_res_photo;
    Button restaurant_map_savebtn;
    SwitchCompat switchCompat;
    Spinner add_res_block_spinner;

    SharedPreferences sharedPreferences;
    String a1;

    public ProgressDialog progressDialog;

    String kmdata;
    String AdminName;

    private static int image_pic_request = 1;

    Double lat, lon;
    private Uri mImageUri;
    private StorageTask mUploadTask;

    LinearLayout location_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_restaurant_current_location_);

        try {
            sharedPreferences = getSharedPreferences("data", 0);
            a1 = sharedPreferences.getString("userid", "");

            progressDialog = new ProgressDialog(this);
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Loading...");

            text_location = findViewById(R.id.text_location);
            button_location = findViewById(R.id.button_location);

            add_res_name = findViewById(R.id.add_res_name_crnt);
            add_res_user_name = findViewById(R.id.add_res_user_name_crnt);
            add_res_phoneno = findViewById(R.id.add_res_phoneno_crnt);
            add_res_opentime = findViewById(R.id.add_res_opentime_crnt);
            add_res_closetime = findViewById(R.id.add_res_closetime_crnt);
            add_res_licenceno = findViewById(R.id.add_res_licenceno_crnt);
            add_res_gstno = findViewById(R.id.add_res_gstno_crnt);
            add_res_photo = findViewById(R.id.add_res_photo_crnt);
            restaurant_map_savebtn = findViewById(R.id.restaurant_map_savebtn_crnt);
            add_res_email = findViewById(R.id.add_res_email_crnt);
            add_res_password = findViewById(R.id.add_res_password_crnt);
            add_res_conpassword = findViewById(R.id.add_res_conpassword_crnt);
            switchCompat = findViewById(R.id.add_res_closed_crnt);
            add_res_block_spinner = findViewById(R.id.add_res_block_spinner_crnt);
            location_layout = findViewById(R.id.location_layout);

            String[] catogries = {"UnBlocked", "Blocked"};
            ArrayAdapter<String> catog = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, catogries);
            add_res_block_spinner.setAdapter(catog);

            checkPermission();

            button_location.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    text_location.setVisibility(View.VISIBLE);
                    getCurrentLocation();
                }
            });

            restaurant_map_savebtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addRestaurant();
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

    private void addRestaurant()
    {
        try {
            String name, username, phone, open, close, licence, gstno, location, email, password, conpassword, restClosed, block;

            name = add_res_name.getText().toString();
            username = add_res_user_name.getText().toString();
            phone = add_res_phoneno.getText().toString();
            open = add_res_opentime.getText().toString();
            close = add_res_closetime.getText().toString();
            licence = add_res_licenceno.getText().toString();
            gstno = add_res_gstno.getText().toString();
            location = text_location.getText().toString();
            email = add_res_email.getText().toString();
            password = add_res_password.getText().toString();
            conpassword = add_res_conpassword.getText().toString();
            block = add_res_block_spinner.getSelectedItem().toString();

            if (switchCompat.isChecked()) {
                restClosed = "open";
            } else {
                restClosed = "closed";
            }

            if (name.isEmpty() || username.isEmpty() || phone.isEmpty() || open.isEmpty() || close.isEmpty() || licence.isEmpty() || gstno.isEmpty() || location.isEmpty() || mImageUri == null || email.isEmpty() || password.isEmpty() || conpassword.isEmpty()) {
                if (name.isEmpty()) {
                    add_res_name.setError("Required");
                }
                if (username.isEmpty()) {
                    add_res_user_name.setError("Required");
                }
                if (phone.isEmpty()) {
                    add_res_phoneno.setError("Required");
                }
                if (open.isEmpty()) {
                    add_res_opentime.setError("Required");
                }
                if (close.isEmpty()) {
                    add_res_closetime.setError("Required");
                }
                if (licence.isEmpty()) {
                    add_res_licenceno.setError("Required");
                }
                if (gstno.isEmpty()) {
                    add_res_gstno.setError("Required");
                }
                if (location.isEmpty()) {
                    Toast.makeText(this, "Select Restaurant Location", Toast.LENGTH_SHORT).show();
                }
                if (mImageUri == null) {
                    Toast.makeText(this, "Select Restaurant Image", Toast.LENGTH_SHORT).show();
                }
                if (email.isEmpty()) {
                    add_res_email.setError("Required");
                }
                if (password.isEmpty()) {
                    add_res_password.setError("Required");
                }
                if (conpassword.isEmpty()) {
                    add_res_conpassword.setError("Required");
                }
            } else {
                if (password.equals(conpassword))
                {
                    try {
                        progressDialog.show();
                        Query query = FirebaseDatabase.getInstance().getReference("local_admin").child(a1);
                        query.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                progressDialog.dismiss();
                                kmdata = snapshot.child("admnBusinessKM").getValue().toString();
                                String Loc = snapshot.child("admnBusinessArea").getValue().toString();
                                AdminName = snapshot.child("admnName").getValue().toString();

                                progressDialog.show();
                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("business");
                                reference.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        progressDialog.dismiss();
                                        for (DataSnapshot snapshot2 : snapshot.getChildren()) {
                                            if (Loc.equals(snapshot2.child("location").getValue().toString())) {
                                                String cLat = snapshot2.child("latitude").getValue().toString();
                                                String cLot = snapshot2.child("longitute").getValue().toString();

                                                Double km = distance(Double.parseDouble(cLat), Double.parseDouble(cLot), Double.parseDouble(lang), Double.parseDouble(lot));

                                                String roKm = String.valueOf(Math.round(km));

                                                int tempkmdata = Integer.parseInt(kmdata);
                                                int temproKm = Integer.parseInt(roKm);

                                                if (tempkmdata <= temproKm) {
                                                    Toast.makeText(AddRestaurantCurrentLocation_Activity.this, "Please select a place, within your business kilometer radius", Toast.LENGTH_SHORT).show();
                                                } else {
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
                                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                                            if (task.isSuccessful()) {
                                                                final String key = task.getResult().getUser().getUid();
                                                                HashMap<String, String> hashMap = new HashMap<>();
                                                                hashMap.put("userId", key);
                                                                hashMap.put("userName", name);
                                                                hashMap.put("userEmail", email);
                                                                hashMap.put("type", "restaurant");

                                                                reference1.child(key).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {

                                                                        if (task.isSuccessful()) {
                                                                            if (mUploadTask != null && mUploadTask.isInProgress()) {
                                                                                Toast.makeText(AddRestaurantCurrentLocation_Activity.this, "Uploading in Progress", Toast.LENGTH_SHORT).show();
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

                                                                                            model.setLat(String.valueOf(lang));
                                                                                            model.setLon(String.valueOf(lot));

                                                                                            model.setResOpenTime(open);
                                                                                            model.setResCloseTime(close);

                                                                                            model.setResLicenceNo(licence);
                                                                                            model.setResGstNo(gstno);

                                                                                            model.setResName(name);
                                                                                            model.setResPhone(phone);
                                                                                            model.setResPhoto(downloadUrl.toString());

                                                                                            model.setIsBlocked(block);

                                                                                            ref.child(key).setValue(model);

                                                                                            Toast.makeText(AddRestaurantCurrentLocation_Activity.this, "New Restaurant Added", Toast.LENGTH_SHORT).show();


                                                                                            add_res_name.setText("");
                                                                                            add_res_phoneno.setText("");
                                                                                            add_res_opentime.setText("");
                                                                                            add_res_closetime.setText("");
                                                                                            add_res_licenceno.setText("");
                                                                                            add_res_gstno.setText("");
                                                                                            add_res_user_name.setText("");


                                                                                            add_res_email.setText("");
                                                                                            add_res_password.setText("");
                                                                                            add_res_conpassword.setText("");
                                                                                            kmdata = "";


                                                                                            mImageUri = null;
                                                                                            add_res_photo.setImageResource(R.drawable.ic_baseline_add_photo_alternate_24);


                                                                                            progressDialog.dismiss();


                                                                                        }
                                                                                    }).addOnFailureListener(new OnFailureListener() {
                                                                                        @Override
                                                                                        public void onFailure(@NonNull Exception e) {
                                                                                            progressDialog.dismiss();
                                                                                            mImageUri = null;
                                                                                            Toast.makeText(AddRestaurantCurrentLocation_Activity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                                                        }
                                                                                    });
                                                                                } else {
                                                                                    progressDialog.dismiss();
                                                                                    Toast.makeText(AddRestaurantCurrentLocation_Activity.this, "No files Selected", Toast.LENGTH_SHORT).show();
                                                                                }
                                                                            }
                                                                        }


                                                                    }
                                                                }).addOnFailureListener(new OnFailureListener() {
                                                                    @Override
                                                                    public void onFailure(@NonNull Exception e) {
                                                                        progressDialog.dismiss();
                                                                        Toast.makeText(AddRestaurantCurrentLocation_Activity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                                    }
                                                                });
                                                            }
                                                        }
                                                    }).addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            progressDialog.dismiss();
                                                            Toast.makeText(AddRestaurantCurrentLocation_Activity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                                }
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        progressDialog.dismiss();
                                        Toast.makeText(AddRestaurantCurrentLocation_Activity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                progressDialog.dismiss();
                                Toast.makeText(AddRestaurantCurrentLocation_Activity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    add_res_conpassword.setError("Not Matched");
                    Toast.makeText(this, "Password not matched", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void checkPermission()
    {
        Permissions.check(AddRestaurantCurrentLocation_Activity.this, Manifest.permission.ACCESS_FINE_LOCATION, null, new PermissionHandler() {
            @Override
            public void onGranted()
            {
            }

            @Override
            public void onDenied(Context context, ArrayList<String> deniedPermissions)
            {
                Toast.makeText(AddRestaurantCurrentLocation_Activity.this, "Pemission Denied", Toast.LENGTH_SHORT).show();
            }

            @Override
            public boolean onBlocked(Context context, ArrayList<String> blockedList)
            {
                Toast.makeText(AddRestaurantCurrentLocation_Activity.this, "Permission Blocked", Toast.LENGTH_SHORT).show();
                return super.onBlocked(context, blockedList);

            }
        });
    }

    private void getCurrentLocation()
    {

        if(CheckGpsStatus())
        {
            try {

                locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                {
                    ActivityCompat.requestPermissions(AddRestaurantCurrentLocation_Activity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 100);
                }
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5, AddRestaurantCurrentLocation_Activity.this);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else
        {
            SweetAlertDialog dialog1 = new SweetAlertDialog(AddRestaurantCurrentLocation_Activity.this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Oops....")
                    .setContentText("Location permission not granted, please turn on location")
                    .showCancelButton(false)
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog)
                        {
                            Intent intent1 = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(intent1);
                            sweetAlertDialog.dismiss();
                        }
                    });

            dialog1.show();

        }

    }

    public boolean CheckGpsStatus()
    {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        assert locationManager != null;
        GpsStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        return GpsStatus;
    }

    @Override
    public void onLocationChanged(Location location)
    {
        lang = String.valueOf(location.getLatitude());
        lot = String.valueOf(location.getLongitude());

        try {
            Geocoder geocoder = new Geocoder(AddRestaurantCurrentLocation_Activity.this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(),1);
            String address = addresses.get(0).getAddressLine(0);
            text_location.setText(address);
            location_layout.setVisibility(View.VISIBLE);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onProviderDisabled(String provider) {
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

}