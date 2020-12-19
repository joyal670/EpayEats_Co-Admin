package com.epayeats.epayeatsco_admin.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import com.epayeats.epayeatsco_admin.Adapter.RestaurantAdapter;
import com.epayeats.epayeatsco_admin.Adapter.RestaurantReportAdapter;
import com.epayeats.epayeatsco_admin.Interface.RestaurantInterface;
import com.epayeats.epayeatsco_admin.Model.RestaurantModel;
import com.epayeats.epayeatsco_admin.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;

import java.util.ArrayList;
import java.util.List;

public class RestaurantReport_Activity extends AppCompatActivity implements RestaurantInterface
{
    ListView restaurant_report_listview;
    List<RestaurantModel> restaurantModel;
    RestaurantReportAdapter restaurantAdapter;
    DatabaseReference reference;

    public ProgressDialog progressDialog;
    SharedPreferences sharedPreferences;
    String a1;

    SearchView restaurantreport_searchView;
    public static RestaurantInterface restaurantInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_report_);

        sharedPreferences = getSharedPreferences("data", 0);
        a1 = sharedPreferences.getString("userid", "");

        restaurantInterface = this;

        progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading...");

        restaurant_report_listview = findViewById(R.id.restaurant_report_listview);
        restaurantreport_searchView = findViewById(R.id.restaurantreport_searchView);

        progressDialog.show();
        reference = FirebaseDatabase.getInstance().getReference("restaurants");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                restaurantModel.clear();
                progressDialog.dismiss();
                for (DataSnapshot snapshot1 : snapshot.getChildren())
                {
                    if(a1.equals(snapshot1.child("resLocalAdminID").getValue().toString()))
                    {
                        RestaurantModel model = snapshot1.getValue(RestaurantModel.class);
                        restaurantModel.add(model);
                    }

                }
                restaurantAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.dismiss();
                Toast.makeText(RestaurantReport_Activity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        restaurantModel = new ArrayList<>();
        restaurantAdapter = new RestaurantReportAdapter(this, restaurantModel);
        restaurant_report_listview.setAdapter(restaurantAdapter);


        restaurantreport_searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query)
            {
                searchRest(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchRest(newText);
                return false;
            }
        });
    }

    private void searchRest(String query)
    {
        ArrayList<RestaurantModel> myList = new ArrayList<>();
        for (RestaurantModel obj : restaurantModel) {
            if (obj.getResName().toLowerCase().contains(query.toLowerCase())) {
                myList.add(obj);
            }
        }
        RestaurantReportAdapter restaurantAdapter = new RestaurantReportAdapter(RestaurantReport_Activity.this, myList);
        restaurant_report_listview.setAdapter(restaurantAdapter);

    }


    @Override
    public void rest(int position, String resID, String resName, String resPhone, String resLocalAdminName, String resLocation)
    {
        String[] items = {"Report", "Call"};
        AlertDialog.Builder dialog = new AlertDialog.Builder(RestaurantReport_Activity.this);
        dialog.setTitle("Select Options");
        dialog.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                if(which == 0)
                {
                    Intent intent = new Intent(RestaurantReport_Activity.this, resaurantReportDetails_Activity.class);
                    intent.putExtra("id", resID);
                    intent.putExtra("name", resName);
                    intent.putExtra("phone", resPhone);
                    intent.putExtra("localadmin", resLocalAdminName);
                    intent.putExtra("location", resLocation);
                    startActivity(intent);
                }
                if(which == 1)
                {
                    Permissions.check(RestaurantReport_Activity.this, Manifest.permission.CALL_PHONE, null, new PermissionHandler() {
                        @Override
                        public void onGranted()
                        {
                            String number = resPhone;

                            Intent intent = new Intent(Intent.ACTION_CALL);
                            if (number.isEmpty())
                            {
                                Toast.makeText(RestaurantReport_Activity.this, "Unable To make Call", Toast.LENGTH_SHORT).show();
                            } else {
                                intent.setData(Uri.parse("tel:" + number));
                                startActivity(intent);
                            }
                        }

                        @Override
                        public void onDenied(Context context, ArrayList<String> deniedPermissions)
                        {
                            Toast.makeText(RestaurantReport_Activity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
                            super.onDenied(context, deniedPermissions);
                        }
                        @Override
                        public boolean onBlocked(Context context, ArrayList<String> blockedList)
                        {
                            Toast.makeText(RestaurantReport_Activity.this, "Permission blocked", Toast.LENGTH_SHORT).show();
                            return super.onBlocked(context, blockedList);
                        }
                    });
                }
            }
        });
        dialog.create().show();
    }
}