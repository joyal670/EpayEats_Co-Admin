package com.epayeats.epayeatsco_admin.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.epayeats.epayeatsco_admin.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class DeliveryBoySelection_Activity extends AppCompatActivity
{
    String orderID;
    String menuID;
    String menuName;
    String menuImage;

    String mainCatagoryID;
    String mainCatagoryName;
    String subCatagoryID;
    String subCatagoryName;

    String localAdminID;

    String offerPrice;
    String sellingPrice;
    String actualPrice;

    String orderDate;
    String orderTime;
    String qty;
    String totalPrice;

    String house;
    String area;
    String city;
    String pincode;
    String cName;
    String cPhone;
    String cAltPhone;

    String userID;
    String userLocation;

    String status;

    ImageView current_image;
    TextView current_name, current_price, current_qty, current_house, current_area, current_pincode, current_cname, current_cnumber, current_status;
    Spinner current_deliveryboy;
    Button current_button;

    DatabaseReference reference;
    SharedPreferences sharedPreferences;
    String a1;

   public static String Boyid;

   TextView current_totalamt;
    int total = 0;

    public ProgressDialog progressDialog;

    TextView current_ordredTime, current_deliveredTime, current_ordredDate, current_deliveredDate;
    String deliverytime, deliverydate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_boy_selection_);

        try {

            progressDialog = new ProgressDialog(this);
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Loading...");

            sharedPreferences = getSharedPreferences("data", 0);
            a1 = sharedPreferences.getString("userid", "");

            orderID = getIntent().getExtras().getString("orderID");
            menuID = getIntent().getExtras().getString("menuID");
            menuName = getIntent().getExtras().getString("menuName");
            menuImage = getIntent().getExtras().getString("menuImage");

            mainCatagoryID = getIntent().getExtras().getString("mainCatagoryID");
            mainCatagoryName = getIntent().getExtras().getString("mainCatagoryName");
            subCatagoryID = getIntent().getExtras().getString("subCatagoryID");
            subCatagoryName = getIntent().getExtras().getString("subCatagoryName");

            localAdminID = getIntent().getExtras().getString("localAdminID");

            offerPrice = getIntent().getExtras().getString("offerPrice");
            sellingPrice = getIntent().getExtras().getString("sellingPrice");
            actualPrice = getIntent().getExtras().getString("actualPrice");

            orderDate = getIntent().getExtras().getString("orderDate");
            orderTime = getIntent().getExtras().getString("orderTime");
            qty = getIntent().getExtras().getString("qty");
            totalPrice = getIntent().getExtras().getString("totalPrice");

            house = getIntent().getExtras().getString("house");
            area = getIntent().getExtras().getString("area");
            city = getIntent().getExtras().getString("city");
            pincode = getIntent().getExtras().getString("pincode");
            cName = getIntent().getExtras().getString("cName");
            cPhone = getIntent().getExtras().getString("cPhone");
            cAltPhone = getIntent().getExtras().getString("cAltPhone");

            userID = getIntent().getExtras().getString("userID");
            userLocation = getIntent().getExtras().getString("userLocation");

            status = getIntent().getExtras().getString("status");
            deliverytime = getIntent().getExtras().getString("deliverytime");
            deliverydate = getIntent().getExtras().getString("deliverydate");

            current_image = findViewById(R.id.current_image);
            current_name = findViewById(R.id.current_name);
            current_price = findViewById(R.id.current_price);
            current_qty = findViewById(R.id.current_qty);
            current_house = findViewById(R.id.current_house);
            current_area = findViewById(R.id.current_area);
            current_pincode = findViewById(R.id.current_pincode);
            current_cname = findViewById(R.id.current_cname);
            current_cnumber = findViewById(R.id.current_cnumber);
            current_deliveryboy = findViewById(R.id.current_deliveryboy);
            current_button = findViewById(R.id.current_button);
            current_status = findViewById(R.id.current_status);
            current_totalamt = findViewById(R.id.current_totalamt);
            current_ordredTime = findViewById(R.id.current_ordredTime);
            current_deliveredTime = findViewById(R.id.current_deliveredTime);
            current_ordredDate = findViewById(R.id.current_ordredDate);
            current_deliveredDate = findViewById(R.id.current_deliveredDate);


            Picasso.get().load(menuImage).into(current_image);

            current_name.setText(menuName);
            current_price.setText(offerPrice);
            current_qty.setText(qty);
            current_house.setText(house);
            current_area.setText(area);
            current_pincode.setText(pincode);
            current_cname.setText(cName);
            current_cnumber.setText(cPhone);
            current_ordredTime.setText(orderTime);
            current_deliveredTime.setText(deliverytime);
            current_ordredDate.setText(orderDate);
            current_deliveredDate.setText(deliverydate);

            if (status.equals("0")) {
                current_status.setText("Pending, Not yet Delivered");
            } else if (status.equals("1")) {
                current_status.setText("Order is Picked up by the Delivery Agent");
                current_button.setEnabled(false);
                current_button.setText("Ongoing Order");
            } else if (status.equals("2")) {
                current_status.setText("Delivered");
                current_button.setEnabled(false);
                current_button.setText("Already Delivered");
            } else {
                current_status.setText("Cancelled");
                current_button.setEnabled(false);
                current_button.setText("Cancelled Order");
            }

            int qty = 0;
            int price = 0;
            qty = qty + Integer.parseInt(current_qty.getText().toString());
            price = price + Integer.parseInt(current_price.getText().toString());
            int temp = qty * price;
            total = total + temp;
            current_totalamt.setText(total + "");

            String msg = "Select --  ";

            try {
                // delivery boy spinner
                DatabaseReference deliveryBoyReference = FirebaseDatabase.getInstance().getReference("delivery_boy");
                ArrayList<String> spinnerDatalist;
                ArrayAdapter<String> adapter;
                ValueEventListener listener;


                progressDialog.show();
                spinnerDatalist = new ArrayList<>();
                adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, spinnerDatalist);
                listener = deliveryBoyReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        progressDialog.show();
                        spinnerDatalist.clear();
                        spinnerDatalist.add(msg);
                        for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                            if (a1.equals(snapshot1.child("deliveryBoyLocalAdminID").getValue().toString())) {
                                if ("UnBlocked".equals(snapshot1.child("isBlocked").getValue().toString())) {
                                    String on = "online";
                                    if (on.equals(snapshot1.child("onlineorOffline").getValue().toString())) {
                                        spinnerDatalist.add(snapshot1.child("deliveryBoyName").getValue().toString());
                                        current_deliveryboy.setAdapter(adapter);
                                    }

                                }

                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        progressDialog.dismiss();
                        Toast.makeText(DeliveryBoySelection_Activity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }

            current_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if (current_deliveryboy.getSelectedItem().toString().equalsIgnoreCase(msg)) {
                            Toast.makeText(DeliveryBoySelection_Activity.this, "Please select a delivery boy", Toast.LENGTH_SHORT).show();
                        } else {
                            pickupDeliveryBoy();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                }
            });

            current_deliveryboy.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String temp = current_deliveryboy.getSelectedItem().toString();
                    progressDialog.show();
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("delivery_boy");
                    reference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            progressDialog.dismiss();
                            for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                                if (temp.equals(snapshot1.child("deliveryBoyName").getValue().toString())) {
                                    Boyid = snapshot1.child("deliveryBoyID").getValue().toString();
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            progressDialog.dismiss();
                            Toast.makeText(DeliveryBoySelection_Activity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void pickupDeliveryBoy()
    {
        String temp = current_deliveryboy.getSelectedItem().toString();

        progressDialog.show();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("order_data").child(orderID);
        ref.child("deliveryBodID").setValue(Boyid);
        ref.child("deliveryBoyName").setValue(temp);
        ref.child("orderStatus").setValue("1");

        progressDialog.dismiss();
        Toast.makeText(this, "Order status updated, Please return to Previous Page", Toast.LENGTH_SHORT).show();

    }


}