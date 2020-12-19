package com.epayeats.epayeatsco_admin.Fragments;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.SwitchCompat;
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

import com.epayeats.epayeatsco_admin.Activity.AddRestaurantCurrentLocation_Activity;
import com.epayeats.epayeatsco_admin.Activity.AddRestaurant_MapsActivity;
import com.epayeats.epayeatsco_admin.Adapter.RestaurantAdapter;
import com.epayeats.epayeatsco_admin.Model.RestaurantModel;
import com.epayeats.epayeatsco_admin.PlacesApi.AutoCompleteActivity;
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

public class Restaurant_Fragment extends Fragment
{
    SearchView fragment_restaurant_searchView;
    SwipeRefreshLayout refresh_restaurants;
    ListView fragment_restaurant_listview;
    FloatingActionsMenu restaurantFragmentFloatingMenu;
    FloatingActionButton fab_restaurantFragmentAdd, fab_restaurantFragmentAddAutoSuggest, fab_restaurantFragmentAddCurrentLoc;

    DatabaseReference mRestaurantReference;
    List<RestaurantModel> mRestuarantModel;
    RestaurantAdapter mRestaurantAdapter;

    SharedPreferences sharedPreferences;
    String a1;

    private ProgressDialog progressDialog;

    public Restaurant_Fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_restaurant_, container, false);

        try {

            fragment_restaurant_searchView = view.findViewById(R.id.fragment_restaurant_searchView);
            refresh_restaurants = view.findViewById(R.id.refresh_restaurants);
            fragment_restaurant_listview = view.findViewById(R.id.fragment_restaurant_listview);
            restaurantFragmentFloatingMenu = view.findViewById(R.id.restaurantFragmentFloatingMenu);
            fab_restaurantFragmentAdd = view.findViewById(R.id.fab_restaurantFragmentAdd);
            fab_restaurantFragmentAddAutoSuggest = view.findViewById(R.id.fab_restaurantFragmentAddAutoSuggest);
            fab_restaurantFragmentAddCurrentLoc = view.findViewById(R.id.fab_restaurantFragmentAddCurrentLoc);

            progressDialog = new ProgressDialog(getContext());
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Loading...");

            sharedPreferences = this.getActivity().getSharedPreferences("data", 0);
            a1 = sharedPreferences.getString("userid", "");

            mRestaurantReference = FirebaseDatabase.getInstance().getReference("restaurants");

            loadData();

            refresh_restaurants.setRefreshing(true);
            refresh_restaurants.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    loadData();
                }
            });

            fab_restaurantFragmentAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), AddRestaurant_MapsActivity.class);
                    startActivity(intent);
                }
            });

            fragment_restaurant_searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    searchRestaurant(query);
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    searchRestaurant(newText);
                    return false;
                }
            });

            fab_restaurantFragmentAddAutoSuggest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), AutoCompleteActivity.class);
                    startActivity(intent);
                }
            });

            fab_restaurantFragmentAddCurrentLoc.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(getContext(), AddRestaurantCurrentLocation_Activity.class);
                    startActivity(i);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

        return view;
    }

    private void searchRestaurant(String query)
    {
        ArrayList<RestaurantModel> myList = new ArrayList<>();
        for (RestaurantModel obj : mRestuarantModel)
        {
            if (obj.getResName().toLowerCase().contains(query.toLowerCase())) {
                myList.add(obj);
            }
        }
        RestaurantAdapter localadminAdapter = new RestaurantAdapter(getContext(), myList);
        fragment_restaurant_listview.setAdapter(localadminAdapter);
        fragment_restaurant_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
    }

    private void loadData()
    {
        try {
            mRestaurantReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    refresh_restaurants.setRefreshing(false);
                    mRestuarantModel.clear();
                    for (DataSnapshot dataSnapshot1 : snapshot.getChildren()) {
                        if (a1.equals(dataSnapshot1.child("resLocalAdminID").getValue().toString())) {
                            RestaurantModel model = dataSnapshot1.getValue(RestaurantModel.class);
                            mRestuarantModel.add(model);
                        }
                    }
                    mRestaurantAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getContext(), "" + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

            mRestuarantModel = new ArrayList<>();
            mRestaurantAdapter = new RestaurantAdapter(getContext(), mRestuarantModel);
            fragment_restaurant_listview.setAdapter(mRestaurantAdapter);
            fragment_restaurant_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    editRestaurant(position);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void editRestaurant(int position)
    {
        try {
            String[] items = {"Edit", "Delete"};
            AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
            dialog.setTitle("Select Options");
            dialog.setItems(items, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (which == 0)
                    {
                        try {
                            LayoutInflater li = LayoutInflater.from(getContext());
                            View admin = li.inflate(R.layout.new_restaurant_layout, null);
                            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getContext());
                            alertBuilder.setView(admin);

                            EditText name, phone, open, close, licence, gst;
                            Button save;
                            Spinner blockStatus;
                            SwitchCompat switchCompat;

                            name = admin.findViewById(R.id.new_restaurant_name_editext);
                            phone = admin.findViewById(R.id.new_restaurant_phone_editext);
                            open = admin.findViewById(R.id.new_restaurant_opentime_editext);
                            close = admin.findViewById(R.id.new_restaurant_closetime_editext);
                            licence = admin.findViewById(R.id.new_restaurant_licenceno_editext);
                            gst = admin.findViewById(R.id.new_restaurant_gstno_editext);
                            save = admin.findViewById(R.id.new_restaurant_save_button);
                            switchCompat = admin.findViewById(R.id.new_restaurant_isClosed);
                            blockStatus = admin.findViewById(R.id.new_restautrant_isblocked_spinner);

                            String[] catogries = {"UnBlocked", "Blocked"};
                            ArrayAdapter<String> catog = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, catogries);
                            blockStatus.setAdapter(catog);


                            final AlertDialog alertDialog = alertBuilder.create();

                            name.setText(mRestuarantModel.get(position).getResName());
                            phone.setText(mRestuarantModel.get(position).getResPhone());
                            open.setText(mRestuarantModel.get(position).getResOpenTime());
                            close.setText(mRestuarantModel.get(position).getResCloseTime());
                            licence.setText(mRestuarantModel.get(position).getResLicenceNo());
                            gst.setText(mRestuarantModel.get(position).getResGstNo());

                            String val = mRestuarantModel.get(position).getIsShopClosed();
                            if (val.equals("open")) {
                                switchCompat.setChecked(true);
                            } else {
                                switchCompat.setChecked(false);
                            }


                            save.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    String aname, aphone, aopen, aclose, alicence, agst, block;

                                    aname = name.getText().toString();
                                    aphone = phone.getText().toString();
                                    aopen = open.getText().toString();
                                    aclose = close.getText().toString();
                                    alicence = licence.getText().toString();
                                    agst = gst.getText().toString();
                                    block = blockStatus.getSelectedItem().toString();


                                    if (aname.isEmpty() || aphone.isEmpty() || aopen.isEmpty() || aclose.isEmpty() || alicence.isEmpty() || agst.isEmpty()) {
                                        if (aname.isEmpty()) {
                                            name.setError("Required");
                                        }
                                        if (aphone.isEmpty()) {
                                            phone.setError("Required");
                                        }
                                        if (aopen.isEmpty()) {
                                            open.setError("Required");
                                        }
                                        if (aclose.isEmpty()) {
                                            close.setError("Required");
                                        }
                                        if (alicence.isEmpty()) {
                                            licence.setError("Required");
                                        }
                                        if (agst.isEmpty()) {
                                            gst.setError("Required");
                                        }
                                    } else {
                                        String crtval;
                                        if (switchCompat.isChecked()) {
                                            crtval = "open";
                                        } else {
                                            crtval = "closed";
                                        }

                                        progressDialog.show();
                                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("restaurants").child(mRestuarantModel.get(position).getResID());
                                        ref.child("resName").setValue(aname);
                                        ref.child("resPhone").setValue(aphone);
                                        ref.child("resOpenTime").setValue(aopen);
                                        ref.child("resCloseTime").setValue(aclose);
                                        ref.child("resLicenceNo").setValue(alicence);
                                        ref.child("resGstNo").setValue(agst);
                                        ref.child("isShopClosed").setValue(crtval);
                                        ref.child("isBlocked").setValue(block);

                                        progressDialog.dismiss();
                                        Toast.makeText(getContext(), "Updated Sucessfully", Toast.LENGTH_SHORT).show();
                                        alertDialog.cancel();
                                    }

                                }
                            });
                            alertDialog.show();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
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
                                        database.getReference("restaurants").child(mRestuarantModel.get(position).getResID()).removeValue();
                                        database.getReference("user_data").child(mRestuarantModel.get(position).getResID()).removeValue();
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