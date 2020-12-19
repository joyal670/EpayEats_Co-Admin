package com.epayeats.epayeatsco_admin.Fragments;

import android.app.ProgressDialog;
import android.content.DialogInterface;
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

import com.epayeats.epayeatsco_admin.Adapter.SubCategoryAdapter;
import com.epayeats.epayeatsco_admin.Model.SubCatagoryModel;
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


public class SubCategory_Fragment extends Fragment
{
    SearchView fragment_subcategory_searchView;
    SwipeRefreshLayout refresh_subcategory;
    ListView fragment_subcategory_listview;
    List<SubCatagoryModel> subCatagoryModel;
    SubCategoryAdapter subCategoryAdapter;

    FloatingActionsMenu subcategoryFragmentFloatingMenu;
    FloatingActionButton fab_subcategoryFragmentAdd;

    DatabaseReference databaseReference;

    public ProgressDialog progressDialog;

    public SubCategory_Fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sub_category_, container, false);

        try {

            progressDialog = new ProgressDialog(getContext());
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Loading...");

            fragment_subcategory_searchView = view.findViewById(R.id.fragment_subcategory_searchView);
            refresh_subcategory = view.findViewById(R.id.refresh_subcategory);
            fragment_subcategory_listview = view.findViewById(R.id.fragment_subcategory_listview);
            subcategoryFragmentFloatingMenu = view.findViewById(R.id.subcategoryFragmentFloatingMenu);
            fab_subcategoryFragmentAdd = view.findViewById(R.id.fab_subcategoryFragmentAdd);

            databaseReference = FirebaseDatabase.getInstance().getReference("sub_category");

            refresh_subcategory.setRefreshing(true);
            loadData();

            fab_subcategoryFragmentAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    subcategoryFragmentFloatingMenu.collapse();
                    addSubCategory();
                }
            });

            refresh_subcategory.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    loadData();
                }
            });

            fragment_subcategory_searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    searchSubCategory(query);
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    searchSubCategory(newText);
                    return false;
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
        return view;
    }

    private void searchSubCategory(String query)
    {
        ArrayList<SubCatagoryModel> myList = new ArrayList<>();
        for (SubCatagoryModel obj : subCatagoryModel)
        {
            if (obj.getSubCatagoryName().toLowerCase().contains(query.toLowerCase())) {
                myList.add(obj);
            }
        }
        SubCategoryAdapter localadminAdapter = new SubCategoryAdapter(getContext(), myList);
        fragment_subcategory_listview.setAdapter(localadminAdapter);
        fragment_subcategory_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
    }

    private void addSubCategory()
    {
        try {
            LayoutInflater li = LayoutInflater.from(getContext());
            View admin = li.inflate(R.layout.addsub_catagory_layout, null);
            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getContext());
            alertBuilder.setView(admin);

            final EditText addsub_subcatagory_edittext;
            final Button addsub_save_subcatagory_btn;
            final Spinner addsub_maincatagory_spinner;

            addsub_subcatagory_edittext = admin.findViewById(R.id.addsub_subcatagory_edittext);
            addsub_save_subcatagory_btn = admin.findViewById(R.id.addsub_save_subcatagory_btn);
            addsub_maincatagory_spinner = admin.findViewById(R.id.addsub_maincatagory_spinner);

            DatabaseReference main_catagoryReference = FirebaseDatabase.getInstance().getReference("main_catagory");
            ArrayList<String> spinnerDatalist;
            ArrayAdapter<String> adapter;
            ValueEventListener listener;
            String msg = "Select --  ";

            try {
                progressDialog.show();
                spinnerDatalist = new ArrayList<>();
                adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, spinnerDatalist);
                listener = main_catagoryReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        progressDialog.dismiss();
                        spinnerDatalist.clear();
                        spinnerDatalist.add(msg);
                        for (DataSnapshot dataSnapshot1 : snapshot.getChildren()) {
                            spinnerDatalist.add(dataSnapshot1.child("foodCatagoreyType").getValue().toString());
                            addsub_maincatagory_spinner.setAdapter(adapter);
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

            final AlertDialog alertDialog = alertBuilder.create();

            addsub_save_subcatagory_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String aname, asub;
                    aname = addsub_subcatagory_edittext.getText().toString();
                    asub = addsub_maincatagory_spinner.getSelectedItem().toString();

                    if (aname.isEmpty()) {
                        if (aname.isEmpty()) {
                            addsub_subcatagory_edittext.setError("Required");
                        }
                    } else {
                        if (asub.equals(msg)) {
                            Toast.makeText(getContext(), "Select Main Catagory", Toast.LENGTH_SHORT).show();
                        } else {
                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("sub_category");
                            String pushKey = ref.push().getKey();

                            progressDialog.show();
                            DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("main_catagory");
                            reference1.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                                        if (asub.equals(snapshot1.child("foodCatagoreyType").getValue().toString())) {
                                            String id = snapshot1.child("foodCatagoreyID").getValue().toString();

                                            SubCatagoryModel model;
                                            model = new SubCatagoryModel();

                                            model.setSubCatagoryID(pushKey);
                                            model.setSubCatagoryName(aname);
                                            model.setMainCategoryID(id);
                                            model.setMainCategoryName(asub);

                                            ref.child(pushKey).setValue(model);

                                            Toast.makeText(getContext(), "New Sub Catagory Added", Toast.LENGTH_SHORT).show();
                                            alertDialog.cancel();

                                            progressDialog.dismiss();
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    progressDialog.dismiss();
                                    Toast.makeText(getContext(), "" + error.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                }
            });
            alertDialog.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadData()
    {
        try {
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    refresh_subcategory.setRefreshing(false);
                    subCatagoryModel.clear();
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        SubCatagoryModel model = snapshot1.getValue(SubCatagoryModel.class);
                        subCatagoryModel.add(model);
                    }
                    subCategoryAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getContext(), "" + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

            subCatagoryModel = new ArrayList<>();
            subCategoryAdapter = new SubCategoryAdapter(getContext(), subCatagoryModel);
            fragment_subcategory_listview.setAdapter(subCategoryAdapter);
            fragment_subcategory_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    editData(position);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void editData(int position)
    {
        try {
            String[] items = {"Edit", "Delete"};
            AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
            dialog.setTitle("Select Options");
            dialog.setItems(items, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    if (which == 0)
                    {
                        try {
                            LayoutInflater li = LayoutInflater.from(getContext());
                            View admin = li.inflate(R.layout.addsub_catagory_layout, null);
                            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getContext());
                            alertBuilder.setView(admin);

                            final EditText addsub_subcatagory_edittext;
                            final Button addsub_save_subcatagory_btn;
                            final Spinner addsub_maincatagory_spinner;

                            addsub_subcatagory_edittext = admin.findViewById(R.id.addsub_subcatagory_edittext);
                            addsub_save_subcatagory_btn = admin.findViewById(R.id.addsub_save_subcatagory_btn);
                            addsub_maincatagory_spinner = admin.findViewById(R.id.addsub_maincatagory_spinner);

                            addsub_subcatagory_edittext.setText(subCatagoryModel.get(position).getSubCatagoryName());

                            DatabaseReference main_catagoryReference = FirebaseDatabase.getInstance().getReference("main_catagory");
                            ArrayList<String> spinnerDatalist;
                            ArrayAdapter<String> adapter;
                            ValueEventListener listener;
                            String msg = "Select --  ";

                            try {
                                spinnerDatalist = new ArrayList<>();
                                adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, spinnerDatalist);
                                listener = main_catagoryReference.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        spinnerDatalist.clear();
                                        spinnerDatalist.add(subCatagoryModel.get(position).getMainCategoryName());
                                        spinnerDatalist.add(msg);
                                        for (DataSnapshot dataSnapshot1 : snapshot.getChildren()) {
                                            spinnerDatalist.add(dataSnapshot1.child("foodCatagoreyType").getValue().toString());
                                            addsub_maincatagory_spinner.setAdapter(adapter);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Toast.makeText(getContext(), "" + error.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            final AlertDialog alertDialog = alertBuilder.create();

                            addsub_save_subcatagory_btn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    String aname, asub;
                                    aname = addsub_subcatagory_edittext.getText().toString();
                                    asub = addsub_maincatagory_spinner.getSelectedItem().toString();

                                    if (aname.isEmpty()) {
                                        if (aname.isEmpty()) {
                                            addsub_subcatagory_edittext.setError("Required");
                                        }
                                    } else {
                                        if (asub.equals(msg)) {
                                            Toast.makeText(getContext(), "Select Main Catagory", Toast.LENGTH_SHORT).show();
                                        } else {
                                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("sub_category").child(subCatagoryModel.get(position).getSubCatagoryID());
                                            String pushKey = ref.push().getKey();

                                            progressDialog.show();
                                            DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("main_catagory");
                                            reference1.addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                                                        if (asub.equals(snapshot1.child("foodCatagoreyType").getValue().toString())) {
                                                            String id = snapshot1.child("foodCatagoreyID").getValue().toString();

                                                            ref.child("subCatagoryName").setValue(aname);
                                                            ref.child("mainCategoryID").setValue(id);
                                                            ref.child("mainCategoryName").setValue(asub);

                                                            alertDialog.cancel();

                                                            progressDialog.dismiss();
                                                            Toast.makeText(getContext(), "Updated Sucessfully", Toast.LENGTH_SHORT).show();

                                                        }
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {
                                                    progressDialog.dismiss();
                                                    Toast.makeText(getContext(), "" + error.getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            });

                                        }
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
                                        database.getReference("sub_category").child(subCatagoryModel.get(position).getSubCatagoryID()).removeValue();
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