package com.epayeats.epayeatsco_admin.Fragments;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.epayeats.epayeatsco_admin.Activity.AddDeliveryBoy_Activity;
import com.epayeats.epayeatsco_admin.Adapter.DeliveryBoyAdapter;
import com.epayeats.epayeatsco_admin.Model.DeliveryBoyModel;
import com.epayeats.epayeatsco_admin.R;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;


public class DeliveryBoy_Fragment extends Fragment
{

    ListView fragment_deliveryboy_listview;
    List<DeliveryBoyModel> mDeliveryBoyModel;
    DatabaseReference mDeliveryBoyReference;
    DeliveryBoyAdapter mDeliveryBoyAdapter;

    FloatingActionsMenu deliveryBoyFragmentFloatingMenu;
    FloatingActionButton fab_deliveryBoyFragmentAdd;
    SearchView fragment_deliveryBoy_searchView;
    SwipeRefreshLayout refresh_deliveryboy;

    public ProgressDialog progressDialog;

    SharedPreferences sharedPreferences;
    String a1;

    public DeliveryBoy_Fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_delivery_boy_, container, false);

        try {

            sharedPreferences = this.getActivity().getSharedPreferences("data", 0);
            a1 = sharedPreferences.getString("userid", "");

            fragment_deliveryboy_listview = view.findViewById(R.id.fragment_deliveryboy_listview);
            fab_deliveryBoyFragmentAdd = view.findViewById(R.id.fab_deliveryBoyFragmentAdd);
            deliveryBoyFragmentFloatingMenu = view.findViewById(R.id.deliveryBoyFragmentFloatingMenu);
            fragment_deliveryBoy_searchView = view.findViewById(R.id.fragment_deliveryBoy_searchView);
            refresh_deliveryboy = view.findViewById(R.id.refresh_deliveryboy);

            mDeliveryBoyReference = FirebaseDatabase.getInstance().getReference("delivery_boy");

            progressDialog = new ProgressDialog(getContext());
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Loading...");

            loadData();

            refresh_deliveryboy.setRefreshing(true);
            refresh_deliveryboy.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    loadData();
                }
            });


            fab_deliveryBoyFragmentAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deliveryBoyFragmentFloatingMenu.collapse();
                    addDeliveryBoy();
                }
            });

            fragment_deliveryBoy_searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    searchDeliveryBoy(query);
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    searchDeliveryBoy(newText);
                    return false;
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
        return view;
    }

    private void addDeliveryBoy()
    {
        Intent intent = new Intent(getContext(), AddDeliveryBoy_Activity.class);
        startActivity(intent);
    }

    private void loadData()
    {
        try {
            mDeliveryBoyReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    refresh_deliveryboy.setRefreshing(false);
                    mDeliveryBoyModel.clear();
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        if (a1.equals(snapshot1.child("deliveryBoyLocalAdminID").getValue().toString())) {
                            DeliveryBoyModel model = snapshot1.getValue(DeliveryBoyModel.class);
                            mDeliveryBoyModel.add(model);
                        }
                    }
                    mDeliveryBoyAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getContext(), "" + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

            mDeliveryBoyModel = new ArrayList<>();
            mDeliveryBoyAdapter = new DeliveryBoyAdapter(getContext(), mDeliveryBoyModel);
            fragment_deliveryboy_listview.setAdapter(mDeliveryBoyAdapter);
            fragment_deliveryboy_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    editDeliveryBoy(position);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void searchDeliveryBoy(String query)
    {
        ArrayList<DeliveryBoyModel> myList = new ArrayList<>();
        for (DeliveryBoyModel obj : mDeliveryBoyModel)
        {
            if (obj.getDeliveryBoyName().toLowerCase().contains(query.toLowerCase())) {
                myList.add(obj);
            }
        }
        DeliveryBoyAdapter deliveryBoyAdapter = new DeliveryBoyAdapter(getContext(), myList);
        fragment_deliveryboy_listview.setAdapter(deliveryBoyAdapter);
        fragment_deliveryboy_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
    }

    private void editDeliveryBoy(int position)
    {
        try {
            String[] items = {"Edit", "Delete"};
            AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
            dialog.setTitle("Select Options");
            dialog.setItems(items, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (which == 0) {
                        LayoutInflater li = LayoutInflater.from(getContext());
                        View deliveryboy = li.inflate(R.layout.new_deliveryboy_layout, null);
                        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getContext());
                        alertBuilder.setView(deliveryboy);

                        String[] catogries = {"UnBlocked", "Blocked"};
                        ArrayAdapter<String> catog = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, catogries);

                        final EditText name_editext, email_editext, licence_editext, vechileno_editext, mobileno_editext, deliverycharge_editext;
                        final Spinner new_deliveryboy_block_spinner;
                        final Button save_button;

                        name_editext = deliveryboy.findViewById(R.id.new_deliveryboy_name_editext);
                        email_editext = deliveryboy.findViewById(R.id.new_deliveryboy_email_editext);
                        deliverycharge_editext = deliveryboy.findViewById(R.id.new_deliveryboy_deliverycharge_editext);
                        new_deliveryboy_block_spinner = deliveryboy.findViewById(R.id.new_deliveryboy_block_spinner);
                        save_button = deliveryboy.findViewById(R.id.new_deliveryboy_save_button);
                        licence_editext = deliveryboy.findViewById(R.id.new_deliveryboy_licence_editext);
                        vechileno_editext = deliveryboy.findViewById(R.id.new_deliveryboy_vechileno_editext);
                        mobileno_editext = deliveryboy.findViewById(R.id.new_deliveryboy_mobileno_editext);


                        email_editext.setEnabled(false);
                        email_editext.setText(mDeliveryBoyModel.get(position).getDeliveryBoyEmail());
                        name_editext.setText(mDeliveryBoyModel.get(position).getDeliveryBoyName());
                        licence_editext.setText(mDeliveryBoyModel.get(position).getDeliveyBoyLicence());
                        vechileno_editext.setText(mDeliveryBoyModel.get(position).getDeliveyBoyVechileNo());
                        mobileno_editext.setText(mDeliveryBoyModel.get(position).getDeliveyBoyMobileNo());
                        deliverycharge_editext.setText(mDeliveryBoyModel.get(position).getDeliveryBoyDeliveryCharge());

                        new_deliveryboy_block_spinner.setAdapter(catog);

                        final AlertDialog alertDialog = alertBuilder.create();

                        save_button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                final String ename, alocaladmin, licence, vechileno, mobile, block, charge;
                                ename = name_editext.getText().toString();
                                charge = deliverycharge_editext.getText().toString();
                                block = new_deliveryboy_block_spinner.getSelectedItem().toString();
                                licence = licence_editext.getText().toString();
                                vechileno = vechileno_editext.getText().toString();
                                mobile = mobileno_editext.getText().toString();

                                if (ename.isEmpty() || charge.isEmpty() || licence.isEmpty() || vechileno.isEmpty() || mobile.isEmpty() || block.isEmpty()) {
                                    if (ename.isEmpty()) {
                                        name_editext.setError("Required");
                                    }
                                    if (licence.isEmpty()) {
                                        licence_editext.setError("Required");
                                    }
                                    if (vechileno.isEmpty()) {
                                        vechileno_editext.setError("Required");
                                    }
                                    if (mobile.isEmpty()) {
                                        mobileno_editext.setError("Required");
                                    }

                                    if (block.isEmpty()) {
                                        Toast.makeText(getContext(), "Select Status", Toast.LENGTH_SHORT).show();
                                    }
                                    if (charge.isEmpty()) {
                                        deliverycharge_editext.setError("Required");
                                    }
                                } else {
                                    progressDialog.show();
                                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("delivery_boy").child(mDeliveryBoyModel.get(position).getDeliveryBoyID());
                                    ref.child("deliveryBoyName").setValue(ename);
                                    ref.child("deliveyBoyLicence").setValue(licence);
                                    ref.child("deliveyBoyVechileNo").setValue(vechileno);
                                    ref.child("deliveyBoyMobileNo").setValue(mobile);
                                    ref.child("deliveryBoyDeliveryCharge").setValue(charge);
                                    ref.child("isBlocked").setValue(block);

                                    Toast.makeText(getContext(), "Updated Sucessfully", Toast.LENGTH_SHORT).show();
                                    alertDialog.cancel();
                                    progressDialog.dismiss();

                                }
                            }
                        });
                        alertDialog.show();

                    }

                    if (which == 1) {
                        SweetAlertDialog dialog1 = new SweetAlertDialog(Objects.requireNonNull(getContext()), SweetAlertDialog.WARNING_TYPE)
                                .setTitleText("Are you sure?")
                                .setContentText("Won't be able to recover!")
                                .setConfirmText("Yes, delete it!")
                                .setCancelText("No, cancel please")
                                .showCancelButton(true)
                                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                        sweetAlertDialog.cancel();
                                    }
                                })
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                        sweetAlertDialog.setTitleText("Deleted")
                                                .setContentText("Data has been deleted")
                                                .setConfirmText("OK")
                                                .setConfirmClickListener(null)
                                                .showCancelButton(false)
                                                .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);

                                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                                        database.getReference("delivery_boy").child(mDeliveryBoyModel.get(position).getDeliveryBoyID()).removeValue();
                                        database.getReference("user_data").child(mDeliveryBoyModel.get(position).getDeliveryBoyID()).removeValue();
                                    }
                                });
                        dialog1.show();

                    }

                }
            });
            dialog.create().show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}