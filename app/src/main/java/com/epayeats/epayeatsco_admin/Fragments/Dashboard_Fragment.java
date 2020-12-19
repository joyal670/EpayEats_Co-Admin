package com.epayeats.epayeatsco_admin.Fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.epayeats.epayeatsco_admin.Activity.ActiveDeliveryBoys_Activity;
import com.epayeats.epayeatsco_admin.Activity.DeliveryboyReport_Activity;
import com.epayeats.epayeatsco_admin.Activity.MenuPendingApprovel_Activity;
import com.epayeats.epayeatsco_admin.Activity.PendingOrders_Activity;
import com.epayeats.epayeatsco_admin.Activity.RestaurantReport_Activity;
import com.epayeats.epayeatsco_admin.Model.MenuModel;
import com.epayeats.epayeatsco_admin.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;


public class Dashboard_Fragment extends Fragment
{
    TextView total_sale, deliverd_items, pending_items, cancelled_items, total_restaurants, total_deliveryboys, neworders_items;

    int count0 = 0;
    int count1 = 0;
    int count2 = 0;
    int count3 = 0;
    int countRes = 0;
    int countDeli = 0;
    int countnew = 0;

    String status0 = "0";
    String status1 = "1";
    String status2 = "2";
    String status3 = "3";

    DatabaseReference databaseReference;

    SharedPreferences sharedPreferences;
    String a1;

    List<MenuModel> menuModel;

    Button dashboard_avalible_boys_btn, dashboard_pending_menu_approvel_btn;

    public ProgressDialog progressDialog;

    Button deliveryboy_report_btn, restaurant_report_btn, dashboard_pending_order_btn;

    public Dashboard_Fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_dashboard_, container, false);

        try {

            progressDialog = new ProgressDialog(getContext());
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Loading...");

            sharedPreferences = getContext().getSharedPreferences("data", 0);
            a1 = sharedPreferences.getString("userid", "");

            total_sale = view.findViewById(R.id.total_sale);
            deliverd_items = view.findViewById(R.id.deliverd_items);
            pending_items = view.findViewById(R.id.pending_items);
            cancelled_items = view.findViewById(R.id.cancelled_items);
            total_restaurants = view.findViewById(R.id.total_restaurants);
            total_deliveryboys = view.findViewById(R.id.total_deliveryboys);
            dashboard_avalible_boys_btn = view.findViewById(R.id.dashboard_avalible_boys_btn);
            dashboard_pending_menu_approvel_btn = view.findViewById(R.id.dashboard_pending_menu_approvel_btn);
            deliveryboy_report_btn = view.findViewById(R.id.deliveryboy_report_btn);
            restaurant_report_btn = view.findViewById(R.id.restaurant_report_btn);
            dashboard_pending_order_btn = view.findViewById(R.id.dashboard_pending_order_btn);
            neworders_items = view.findViewById(R.id.neworders_items);

            databaseReference = FirebaseDatabase.getInstance().getReference("order_data");

            loadAllOrders();
            loadDeliverdOrders();
            loadPendingOrders();
            loadCancelledOrders();
            loadRestaurants();
            loadDeliveryBoys();
            loanewOrders();


            dashboard_avalible_boys_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), ActiveDeliveryBoys_Activity.class);
                    startActivity(intent);
                }
            });

            dashboard_pending_menu_approvel_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), MenuPendingApprovel_Activity.class);
                    startActivity(intent);
                }
            });

            deliveryboy_report_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    Intent intent = new Intent(getContext(), DeliveryboyReport_Activity.class);
                    startActivity(intent);
                }
            });

            restaurant_report_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    Intent intent = new Intent(getContext(), RestaurantReport_Activity.class);
                    startActivity(intent);
                }
            });

            dashboard_pending_order_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    Intent intent = new Intent(getContext(), PendingOrders_Activity.class);
                    startActivity(intent);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
        return view;
    }

    private void loanewOrders()
    {
        progressDialog.show();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                progressDialog.dismiss();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren())
                {
                    if (a1.equals(dataSnapshot1.child("localAdminID").getValue().toString()))
                    {
                        if (status0.equals(dataSnapshot1.child("orderStatus").getValue().toString()))
                        {
                            try {
                                countnew = countnew + 1;
                                neworders_items.setText(countnew + "");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressDialog.dismiss();
                Toast.makeText(getContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void loadDeliveryBoys()
    {
        try {
            progressDialog.show();
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("delivery_boy");
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    progressDialog.dismiss();
                    for (DataSnapshot dataSnapshot1 : snapshot.getChildren()) {
                        if (a1.equals(dataSnapshot1.child("deliveryBoyLocalAdminID").getValue().toString())) {
                            try {
                                countDeli = countDeli + 1;
                                total_deliveryboys.setText(countDeli + "");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    progressDialog.dismiss();
                    Toast.makeText(getContext(), "" + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadRestaurants()
    {
        try {
            progressDialog.show();
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("restaurants");
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    progressDialog.dismiss();
                    for (DataSnapshot dataSnapshot1 : snapshot.getChildren()) {
                        if (a1.equals(dataSnapshot1.child("resLocalAdminID").getValue().toString())) {
                            try {
                                countRes = countRes + 1;
                                total_restaurants.setText(countRes + "");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    progressDialog.dismiss();
                    Toast.makeText(getContext(), "" + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadAllOrders()
    {
        try {
            progressDialog.show();
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    progressDialog.dismiss();
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        if (a1.equals(dataSnapshot1.child("localAdminID").getValue().toString())) {
                            try {
                                count0 = count0 + 1;
                                total_sale.setText(count0 + "");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    progressDialog.dismiss();
                    Toast.makeText(getContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadDeliverdOrders()
    {
        try {
            progressDialog.show();
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    progressDialog.dismiss();
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        if (status2.equals(dataSnapshot1.child("orderStatus").getValue().toString())) {
                            if (a1.equals(dataSnapshot1.child("localAdminID").getValue().toString())) {
                                try {
                                    count2 = count2 + 1;
                                    deliverd_items.setText(count2 + "");
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    progressDialog.dismiss();
                    Toast.makeText(getContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadPendingOrders()
    {
        try {
            progressDialog.show();
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    progressDialog.dismiss();
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        if (status1.equals(dataSnapshot1.child("orderStatus").getValue().toString())) {
                            if (a1.equals(dataSnapshot1.child("localAdminID").getValue().toString())) {
                                try {
                                    count1 = count1 + 1;
                                    pending_items.setText(count1 + "");
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    progressDialog.dismiss();
                    Toast.makeText(getContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadCancelledOrders()
    {
        try {
            progressDialog.show();
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    progressDialog.dismiss();
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        if (status3.equals(dataSnapshot1.child("orderStatus").getValue().toString())) {
                            if (a1.equals(dataSnapshot1.child("localAdminID").getValue().toString())) {
                                try {
                                    count3 = count3 + 1;
                                    cancelled_items.setText(count3 + "");
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    progressDialog.dismiss();
                    Toast.makeText(getContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}