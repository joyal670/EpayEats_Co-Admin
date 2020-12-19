package com.epayeats.epayeatsco_admin.Fragments;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.epayeats.epayeatsco_admin.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class Account_Fragment extends Fragment
{

    SharedPreferences sharedPreferences;
    String a1;
    public ProgressDialog progressDialog;
    DatabaseReference reference;

    TextView account_name, account_email, account_id, account_phone, account_address, account_business_area, account_business_km, account_gst;
    TextView account_pancard, account_aadhar, account_dateofjoin;

    public Account_Fragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_account_, container, false);

        sharedPreferences = getContext().getSharedPreferences("data", 0);
        a1 = sharedPreferences.getString("userid", "");

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading...");

        account_name = view.findViewById(R.id.account_name);
        account_email = view.findViewById(R.id.account_email);
        account_id = view.findViewById(R.id.account_id);
        account_phone = view.findViewById(R.id.account_phone);
        account_address = view.findViewById(R.id.account_address);
        account_business_area = view.findViewById(R.id.account_business_area);
        account_business_km = view.findViewById(R.id.account_business_km);
        account_gst = view.findViewById(R.id.account_gst);
        account_pancard = view.findViewById(R.id.account_pancard);
        account_aadhar = view.findViewById(R.id.account_aadhar);
        account_dateofjoin = view.findViewById(R.id.account_dateofjoin);

        progressDialog.show();
        reference = FirebaseDatabase.getInstance().getReference("local_admin").child(a1);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                progressDialog.dismiss();
                account_name.setText(snapshot.child("admnName").getValue().toString());
                account_email.setText(snapshot.child("admnEmail").getValue().toString());
                account_id.setText(snapshot.child("admnID").getValue().toString());
                account_phone.setText(snapshot.child("admnPhone").getValue().toString());
                account_address.setText(snapshot.child("admnAddress").getValue().toString());
                account_business_area.setText(snapshot.child("admnBusinessArea").getValue().toString());
                account_business_km.setText(snapshot.child("admnBusinessKM").getValue().toString());
                account_gst.setText(snapshot.child("admnGSTNo").getValue().toString());
                account_pancard.setText(snapshot.child("admnPancardNo").getValue().toString());
                account_aadhar.setText(snapshot.child("admnaadharNo").getValue().toString());
                account_dateofjoin.setText(snapshot.child("date").getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {
                progressDialog.dismiss();
                Toast.makeText(getContext(), ""+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }
}