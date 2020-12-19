package com.epayeats.epayeatsco_admin.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.epayeats.epayeatsco_admin.Fragments.Account_Fragment;
import com.epayeats.epayeatsco_admin.Fragments.Cancelled_Fragment;
import com.epayeats.epayeatsco_admin.Fragments.Dashboard_Fragment;
import com.epayeats.epayeatsco_admin.Fragments.DeliveryBoy_Fragment;
import com.epayeats.epayeatsco_admin.Fragments.ImageUpload_Fragment;
import com.epayeats.epayeatsco_admin.Fragments.Logout_Fragment;
import com.epayeats.epayeatsco_admin.Fragments.Menu_Fragment;
import com.epayeats.epayeatsco_admin.Fragments.Myorders_Fragment;
import com.epayeats.epayeatsco_admin.Fragments.OnGoing_Fragment;
import com.epayeats.epayeatsco_admin.Fragments.Restaurant_Fragment;
import com.epayeats.epayeatsco_admin.Fragments.SubCategory_Fragment;
import com.epayeats.epayeatsco_admin.R;
import com.epayeats.epayeatsco_admin.Service.MyService;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener
{
    DrawerLayout mDrawerLayout;
    ActionBarDrawerToggle mDrawerToogle;
    String currentFragment = "other";
    String isBlocked = "Blocked";
    SharedPreferences sharedPreferences;
    String a1;
    String a2;
    String businessKM, businessLoc;
    String cLat, cLot;
    TextView headerEmail;
    String status1 = "0";


    public ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
            }

            progressDialog = new ProgressDialog(this);
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Loading...");

            mDrawerLayout = findViewById(R.id.drawerLayout);
            mDrawerToogle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.open, R.string.close);
            mDrawerToogle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.colorBlack));
            mDrawerLayout.addDrawerListener(mDrawerToogle);
            mDrawerToogle.syncState();

            sharedPreferences = getSharedPreferences("data", 0);
            a1 = sharedPreferences.getString("userid", "");

            NavigationView navigationView = findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(this);

            headerEmail = navigationView.getHeaderView(0).findViewById(R.id.header_email);
            sharedPreferences = getSharedPreferences("data", 0);
            a2 = sharedPreferences.getString("useremail", "");
            headerEmail.setText(a2);

            Dashboard_Fragment fragment = new Dashboard_Fragment();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.frame_layout, fragment, "Dashboard");
            fragmentTransaction.commit();

            DatabaseReference reference =  FirebaseDatabase.getInstance().getReference("order_data");
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot)
                {
                    for(DataSnapshot dataSnapshot : snapshot.getChildren())
                    {
                        if(a1.equals(dataSnapshot.child("localAdminID").getValue().toString()))
                        {
                            if(status1.equals(dataSnapshot.child("orderStatus").getValue().toString()))
                            {
                                startService(new Intent(MainActivity.this, MyService.class));
                            }
                        }
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error)
                {
                    Toast.makeText(MainActivity.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
            
            checkStatus();

//        TOPIC = "/topics/userABC"; //topic must match with what the receiver subscribed to
//        NOTIFICATION_TITLE = "New Order";
//        NOTIFICATION_MESSAGE = "Please check out your app";
//
//        JSONObject notification = new JSONObject();
//        JSONObject notifcationBody = new JSONObject();
//        try {
//            notifcationBody.put("title", NOTIFICATION_TITLE);
//            notifcationBody.put("message", NOTIFICATION_MESSAGE);
//
//            notification.put("to", TOPIC);
//            notification.put("data", notifcationBody);
//        } catch (JSONException e) {
//            Log.e(TAG, "onCreate: " + e.getMessage() );
//        }
//        sendNotification(notification);

        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }
    }


    private void checkStatus()
    {
        try {
            progressDialog.show();
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("local_admin").child(a1);
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    progressDialog.dismiss();
                    String status = "UnBlocked";
                    if (status.equals(snapshot.child("isBlocked").getValue().toString())) {

                    } else {
                        SweetAlertDialog dialog1 = new SweetAlertDialog(MainActivity.this, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Oops...")
                                .setContentText("Your account is Blocked")
                                .setConfirmText("Ok")
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                        FirebaseAuth.getInstance().signOut();
                                        sharedPreferences = getSharedPreferences("data", 0);
                                        SharedPreferences.Editor editor = sharedPreferences.edit();
                                        editor.clear();
                                        editor.apply();
                                        Intent intent = new Intent(MainActivity.this, Login_Activity.class);
                                        startActivity(intent);

                                    }
                                });
                        dialog1.setCancelable(false);
                        dialog1.show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    progressDialog.dismiss();
                    Toast.makeText(MainActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item)
    {
        int id = item.getItemId();
        if (id == R.id.dashboard)
        {
            Dashboard_Fragment fragment = new Dashboard_Fragment();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.frame_layout, fragment,"Dashboard");
            fragmentTransaction.commit();
        }
        else if (id == R.id.myorders)
        {
            currentFragment = "other";
            Myorders_Fragment fragment = new Myorders_Fragment();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.frame_layout, fragment,"My Orders");
            fragmentTransaction.commit();
        }
        else if (id == R.id.myordersongoing)
        {
            currentFragment = "other";
            OnGoing_Fragment fragment = new OnGoing_Fragment();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.frame_layout, fragment,"On going");
            fragmentTransaction.commit();
        }
        else if (id == R.id.myorderscancelled)
        {
            currentFragment = "other";
            Cancelled_Fragment fragment = new Cancelled_Fragment();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.frame_layout, fragment,"Cancelled");
            fragmentTransaction.commit();
        }
        else if (id == R.id.restaurant)
        {
            currentFragment = "other";
            Restaurant_Fragment fragment = new Restaurant_Fragment();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.frame_layout, fragment,"Restaurant");
            fragmentTransaction.commit();
        }
        else if(id == R.id.subcatagory)
        {
            currentFragment = "other";
            SubCategory_Fragment fragment = new SubCategory_Fragment();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.frame_layout, fragment,"Sub Category");
            fragmentTransaction.commit();
        }

        else if (id == R.id.deliveryboy)
        {
            currentFragment = "other";
            DeliveryBoy_Fragment fragment = new DeliveryBoy_Fragment();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.frame_layout, fragment,"Delivery Boy");
            fragmentTransaction.commit();
        }
        else if (id == R.id.resmenu)
        {
            currentFragment = "other";
            Menu_Fragment fragment = new Menu_Fragment();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.frame_layout, fragment,"Menu");
            fragmentTransaction.commit();
        }
        else if (id == R.id.accout)
        {
            currentFragment = "other";
            Account_Fragment fragment = new Account_Fragment();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.frame_layout, fragment,"Account");
            fragmentTransaction.commit();
        }
        else if (id == R.id.upload)
        {
            currentFragment = "other";
            ImageUpload_Fragment fragment = new ImageUpload_Fragment();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.frame_layout, fragment,"Image upload");
            fragmentTransaction.commit();
        }
        else if (id == R.id.logout)
        {
            currentFragment = "other";
            Logout_Fragment fragment = new Logout_Fragment();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.frame_layout, fragment,"Logout");
            fragmentTransaction.commit();
        }
        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {

            if (currentFragment != "home")
            {
                Dashboard_Fragment fragment = new Dashboard_Fragment();
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.frame_layout, fragment,"Dashboard");
                fragmentTransaction.commit();
            } else {
                super.onBackPressed();
            }
        }
    }
}