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
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.epayeats.epayeatsco_admin.Adapter.DeliveryBoyAdapter;
import com.epayeats.epayeatsco_admin.Adapter.DeliveryBoyReportAdapter;
import com.epayeats.epayeatsco_admin.Interface.DeliveryInterface;
import com.epayeats.epayeatsco_admin.Model.DeliveryBoyModel;
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

public class DeliveryboyReport_Activity extends AppCompatActivity implements DeliveryInterface
{
    ListView deliveryboy_report_listview;
    List<DeliveryBoyModel> deliveryBoyModel;
    DeliveryBoyReportAdapter deliveryBoyAdapter;
    DatabaseReference reference;
    public ProgressDialog progressDialog;

    SharedPreferences sharedPreferences;
    String a1;

    SearchView fragment_deliveryBoy_searchView;
    public static DeliveryInterface deliveryInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deliveryboy_report_);

        sharedPreferences = getSharedPreferences("data", 0);
        a1 = sharedPreferences.getString("userid", "");

        deliveryInterface = this;

        deliveryboy_report_listview = findViewById(R.id.deliveryboy_report_listview);
        fragment_deliveryBoy_searchView = findViewById(R.id.deliveryBoyreport_searchView);

        progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading...");

        progressDialog.show();
        reference = FirebaseDatabase.getInstance().getReference("delivery_boy");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                deliveryBoyModel.clear();
                progressDialog.dismiss();
                for (DataSnapshot snapshot1 : snapshot.getChildren())
                {
                    if(a1.equals(snapshot1.child("deliveryBoyLocalAdminID").getValue().toString()))
                    {
                        DeliveryBoyModel model = snapshot1.getValue(DeliveryBoyModel.class);
                        deliveryBoyModel.add(model);
                    }
                }
                deliveryBoyAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.dismiss();
                Toast.makeText(DeliveryboyReport_Activity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        deliveryBoyModel = new ArrayList<>();
        deliveryBoyAdapter = new DeliveryBoyReportAdapter(this, deliveryBoyModel);
        deliveryboy_report_listview.setAdapter(deliveryBoyAdapter);


        fragment_deliveryBoy_searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
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
        ArrayList<DeliveryBoyModel> myList = new ArrayList<>();
        for (DeliveryBoyModel obj : deliveryBoyModel)
        {
            if (obj.getDeliveryBoyName().toLowerCase().contains(query.toLowerCase())) {
                myList.add(obj);
            }
        }
        DeliveryBoyReportAdapter deliveryBoyAdapter = new DeliveryBoyReportAdapter(DeliveryboyReport_Activity.this, myList);
        deliveryboy_report_listview.setAdapter(deliveryBoyAdapter);
    }

    @Override
    public void delivery(int postion, String deliveryBoyID, String deliveryBoyName, String deliveryBoyDeliveryCharge, String deliveyBoyMobileNo)
    {
        String[] items = {"Report", "Call"};
        AlertDialog.Builder dialog = new AlertDialog.Builder(DeliveryboyReport_Activity.this);
        dialog.setTitle("Select Options");
        dialog.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                if(which == 0)
                {
                    Intent intent = new Intent(DeliveryboyReport_Activity.this, deliveryBoyReportdetails_Activity.class);
                    intent.putExtra("id", deliveryBoyID);
                    intent.putExtra("name", deliveryBoyName);
                    intent.putExtra("charge", deliveryBoyDeliveryCharge);
                    intent.putExtra("phone", deliveyBoyMobileNo);
                    startActivity(intent);
                }
                if(which == 1)
                {
                    Permissions.check(DeliveryboyReport_Activity.this, Manifest.permission.CALL_PHONE, null, new PermissionHandler() {
                        @Override
                        public void onGranted()
                        {
                            String number = deliveyBoyMobileNo;

                            Intent intent = new Intent(Intent.ACTION_CALL);
                            if (number.isEmpty())
                            {
                                Toast.makeText(DeliveryboyReport_Activity.this, "Unable To make Call", Toast.LENGTH_SHORT).show();
                            } else {
                                intent.setData(Uri.parse("tel:" + number));
                                startActivity(intent);
                            }
                        }

                        @Override
                        public void onDenied(Context context, ArrayList<String> deniedPermissions)
                        {
                            Toast.makeText(DeliveryboyReport_Activity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
                            super.onDenied(context, deniedPermissions);
                        }
                        @Override
                        public boolean onBlocked(Context context, ArrayList<String> blockedList)
                        {
                            Toast.makeText(DeliveryboyReport_Activity.this, "Permission blocked", Toast.LENGTH_SHORT).show();
                            return super.onBlocked(context, blockedList);
                        }
                    });
                }
            }
        });
        dialog.create().show();
    }
}