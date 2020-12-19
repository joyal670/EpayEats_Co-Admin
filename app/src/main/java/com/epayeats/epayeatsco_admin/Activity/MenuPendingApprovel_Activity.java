package com.epayeats.epayeatsco_admin.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.epayeats.epayeatsco_admin.Adapter.MenuApprovelAdapter;
import com.epayeats.epayeatsco_admin.Model.MenuModel;
import com.epayeats.epayeatsco_admin.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MenuPendingApprovel_Activity extends AppCompatActivity
{
    ListView dashboard_appriving_listview;
    List<MenuModel> menuModel;
    MenuApprovelAdapter menuApprovelAdapter;
    DatabaseReference menuApprovelReference;

    SharedPreferences sharedPreferences;
    String a1;

    public ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_pending_approvel_);
        dashboard_appriving_listview = findViewById(R.id.dashboard_appriving_listview);

        progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading...");

        sharedPreferences = getSharedPreferences("data", 0);
        a1 = sharedPreferences.getString("userid", "");

        loadApprovelData();
    }

    private void loadApprovelData()
    {
        try {
            progressDialog.show();
            String ap = "Not Approved";
            menuApprovelReference = FirebaseDatabase.getInstance().getReference("menu");
            menuApprovelReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    progressDialog.dismiss();
                    menuModel.clear();
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        if (a1.equals(snapshot1.child("menuLocalAdminID").getValue().toString())) {
                            if (ap.equals(snapshot1.child("menuApprovel").getValue().toString())) {
                                MenuModel model = snapshot1.getValue(MenuModel.class);
                                menuModel.add(model);
                            }

                        }
                    }
                    menuApprovelAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    progressDialog.dismiss();
                    Toast.makeText(MenuPendingApprovel_Activity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
            menuModel = new ArrayList<>();
            menuApprovelAdapter = new MenuApprovelAdapter(this, menuModel);
            dashboard_appriving_listview.setAdapter(menuApprovelAdapter);
            dashboard_appriving_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    editMenu(position);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void editMenu(int position)
    {
        try {
            LayoutInflater li = LayoutInflater.from(MenuPendingApprovel_Activity.this);
            View myBusiness = li.inflate(R.layout.new_menu_approving_layout, null);
            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(MenuPendingApprovel_Activity.this);
            alertBuilder.setView(myBusiness);

            EditText item_approvel_selling_price, item_approvel_offer_price, item_menu_gstno;
            Spinner item_approvel_spinner;
            Button item_approvel_save_btn, item_calculate_btn;
            TextView item_final_offer_price;

            item_approvel_selling_price = myBusiness.findViewById(R.id.item_approvel_selling_price);
            item_approvel_offer_price = myBusiness.findViewById(R.id.item_approvel_offer_price);
            item_approvel_spinner = myBusiness.findViewById(R.id.item_approvel_spinner);
            item_approvel_save_btn = myBusiness.findViewById(R.id.item_approvel_save_btn);

            item_calculate_btn = myBusiness.findViewById(R.id.item_calculate_btn);
            item_final_offer_price = myBusiness.findViewById(R.id.item_final_offer_price);
            item_menu_gstno = myBusiness.findViewById(R.id.item_menu_gstno);

            final AlertDialog alertDialog = alertBuilder.create();

            String[] catogries = {"Approved", "Not Approved"};
            ArrayAdapter<String> catog = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, catogries);
            item_approvel_spinner.setAdapter(catog);

            // calculate gst
            item_calculate_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    String off = item_approvel_offer_price.getText().toString();
                    String gst = item_menu_gstno.getText().toString();

                    if( off.isEmpty() || gst.isEmpty())
                    {
                        if(off.isEmpty())
                        {
                            Toast.makeText(MenuPendingApprovel_Activity.this, "Enter offer price", Toast.LENGTH_SHORT).show();
                        }
                        if(gst.isEmpty())
                        {
                            Toast.makeText(MenuPendingApprovel_Activity.this, "Enter gst percentage", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else
                    {
                        Double tempoff = Double.parseDouble(off);
                        Double tempgst = Double.parseDouble(gst);

                        Double temp = (tempgst / 100) * tempoff;

                        Double fin = temp + tempoff;

                        String nw = String.valueOf(Math.round(fin)) ;

                        item_final_offer_price.setText(nw);


                    }
                }
            });


            item_approvel_save_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String aa, a2, a3, a4, a5;
                    aa = item_approvel_selling_price.getText().toString();
                    a2 = item_approvel_offer_price.getText().toString();
                    a3 = item_approvel_spinner.getSelectedItem().toString();
                    a4 = item_final_offer_price.getText().toString();
                    a5 = item_menu_gstno.getText().toString();

                    if (aa.isEmpty() || a2.isEmpty() || a4.isEmpty() || a5.isEmpty())
                    {
                        if (aa.isEmpty()) {
                            item_approvel_selling_price.setError("Required");
                        }
                        if (a2.isEmpty()) {
                            item_approvel_offer_price.setError("Required");
                        }
                        if(a4.isEmpty())
                        {
                            Toast.makeText(MenuPendingApprovel_Activity.this, "Calculate final Offer price", Toast.LENGTH_SHORT).show();
                        }
                        if(a5.isEmpty())
                        {
                            item_menu_gstno.setError("Required");
                        }
                    } else {

                        progressDialog.show();
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("menu").child(menuModel.get(position).getMenuID());
                        ref.child("menuOfferPrice").setValue(a4);
                        ref.child("menuSellingPrice").setValue(aa);
                        ref.child("menuApprovel").setValue(a3);
                        ref.child("gstno").setValue(a5);

                        progressDialog.dismiss();
                        Toast.makeText(MenuPendingApprovel_Activity.this, "Menu Approved", Toast.LENGTH_SHORT).show();
                        alertDialog.cancel();
                    }

                }
            });
            alertDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}