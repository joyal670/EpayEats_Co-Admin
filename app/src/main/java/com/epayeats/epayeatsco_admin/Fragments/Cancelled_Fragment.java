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
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.epayeats.epayeatsco_admin.Activity.DeliveryBoySelection_Activity;
import com.epayeats.epayeatsco_admin.Adapter.OrderDataAdapter;
import com.epayeats.epayeatsco_admin.Model.orderModel;
import com.epayeats.epayeatsco_admin.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class Cancelled_Fragment extends Fragment
{
    DatabaseReference databaseReference;
    ListView orderrecyclerview;
    SharedPreferences sharedPreferences;
    List<orderModel> mOrderModel;
    OrderDataAdapter mOrderDataAdapter;
    String status = "3";
    public ProgressDialog progressDialog;
    String a1;

    public Cancelled_Fragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_cancelled_, container, false);

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading...");

        sharedPreferences = this.getActivity().getSharedPreferences("data", 0);
        a1 = sharedPreferences.getString("userid", "");

        orderrecyclerview = view.findViewById(R.id.cancelledorderrecyclerview);
        databaseReference = FirebaseDatabase.getInstance().getReference("order_data");


        loadOrders();

        return view;
    }

    private void loadOrders()
    {
        progressDialog.show();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                progressDialog.dismiss();
                mOrderModel.clear();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    if (a1.equals(dataSnapshot1.child("localAdminID").getValue().toString())) {
                        if (status.equals(dataSnapshot1.child("orderStatus").getValue().toString())) {
                            orderModel upload = dataSnapshot1.getValue(orderModel.class);
                            mOrderModel.add(upload);
                        }

                    }
                }
                mOrderDataAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressDialog.dismiss();
                Toast.makeText(getContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        mOrderModel = new ArrayList<>();
        mOrderDataAdapter = new OrderDataAdapter(getContext(), mOrderModel);
        orderrecyclerview.setAdapter(mOrderDataAdapter);
        orderrecyclerview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(getContext(), DeliveryBoySelection_Activity.class);
                intent.putExtra("orderID", mOrderModel.get(position).getOrderID());
                intent.putExtra("menuID", mOrderModel.get(position).getMenuID());
                intent.putExtra("menuName", mOrderModel.get(position).getMenuName());
                intent.putExtra("menuImage", mOrderModel.get(position).getMenuImage());

                intent.putExtra("mainCatagoryID", mOrderModel.get(position).getMainCatagoryID());
                intent.putExtra("mainCatagoryName", mOrderModel.get(position).getMainCatagoryName());
                intent.putExtra("subCatagoryID", mOrderModel.get(position).getSubCatagoryID());
                intent.putExtra("subCatagoryName", mOrderModel.get(position).getSubCatagoryName());

                intent.putExtra("localAdminID", mOrderModel.get(position).getLocalAdminID());

                intent.putExtra("offerPrice", mOrderModel.get(position).getOfferPrice());
                intent.putExtra("sellingPrice", mOrderModel.get(position).getSellingPrice());
                intent.putExtra("actualPrice", mOrderModel.get(position).getActualPrice());


                intent.putExtra("orderDate", mOrderModel.get(position).getOrderDate());
                intent.putExtra("orderTime", mOrderModel.get(position).getOrderTime());

                intent.putExtra("qty", mOrderModel.get(position).getQty());
                intent.putExtra("totalPrice", mOrderModel.get(position).getTotalPrice());

                intent.putExtra("house", mOrderModel.get(position).getHouse());
                intent.putExtra("area", mOrderModel.get(position).getArea());
                intent.putExtra("city", mOrderModel.get(position).getCity());
                intent.putExtra("pincode", mOrderModel.get(position).getPincode());
                intent.putExtra("cName", mOrderModel.get(position).getcName());
                intent.putExtra("cPhone", mOrderModel.get(position).getcPhone());
                intent.putExtra("cAltPhone", mOrderModel.get(position).getcAltPhone());

                intent.putExtra("userID", mOrderModel.get(position).getUserID());
                intent.putExtra("userLocation", mOrderModel.get(position).getUserLocation());

                intent.putExtra("status", mOrderModel.get(position).getOrderStatus());

                startActivity(intent);
            }
        });
    }
}