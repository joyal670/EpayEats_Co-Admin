package com.epayeats.epayeatsco_admin.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import com.epayeats.epayeatsco_admin.Adapter.ActiveDeliveryBoysAdapter;
import com.epayeats.epayeatsco_admin.Adapter.DeliveryBoyAdapter;
import com.epayeats.epayeatsco_admin.Model.DeliveryBoyModel;
import com.epayeats.epayeatsco_admin.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ActiveDeliveryBoys_Activity extends AppCompatActivity
{
    ListView active_delivery_boy_listview;
    List<DeliveryBoyModel> deliveryBoyModel;
    ActiveDeliveryBoysAdapter deliveryBoysAdapter;
    DatabaseReference mDeliveryBoyReference;

    SharedPreferences sharedPreferences;
    String a1;

    String sta = "online";
    String blok = "UnBlocked";

    public ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_active_delivery_boys_);

        try {
            sharedPreferences = getSharedPreferences("data", 0);
            a1 = sharedPreferences.getString("userid", "");

            active_delivery_boy_listview = findViewById(R.id.active_delivery_boy_listview);
            mDeliveryBoyReference = FirebaseDatabase.getInstance().getReference("delivery_boy");

            progressDialog = new ProgressDialog(this);
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Loading...");

            loadData();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadData()
    {
        try {
            progressDialog.show();
            mDeliveryBoyReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    progressDialog.dismiss();
                    deliveryBoyModel.clear();
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        if (a1.equals(snapshot1.child("deliveryBoyLocalAdminID").getValue().toString())) {
                            if (sta.equals(snapshot1.child("onlineorOffline").getValue().toString())) {
                                if (blok.equals(snapshot1.child("isBlocked").getValue().toString())) {
                                    DeliveryBoyModel model = snapshot1.getValue(DeliveryBoyModel.class);
                                    deliveryBoyModel.add(model);
                                }
                            }
                        }
                    }
                    deliveryBoysAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    progressDialog.dismiss();
                    Toast.makeText(ActiveDeliveryBoys_Activity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

            deliveryBoyModel = new ArrayList<>();
            deliveryBoysAdapter = new ActiveDeliveryBoysAdapter(this, deliveryBoyModel);
            active_delivery_boy_listview.setAdapter(deliveryBoysAdapter);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}