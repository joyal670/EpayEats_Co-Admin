package com.epayeats.epayeatsco_admin.Fragments;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.epayeats.epayeatsco_admin.Activity.AddMenu_Activity;
import com.epayeats.epayeatsco_admin.Adapter.MenuAdapter;
import com.epayeats.epayeatsco_admin.Model.MenuModel;
import com.epayeats.epayeatsco_admin.R;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static android.app.Activity.RESULT_OK;


public class Menu_Fragment extends Fragment
{
    SearchView fragment_menu_searchView;
    SwipeRefreshLayout refresh_menu;
    ListView fragment_menu_listview;
    FloatingActionsMenu menuFragmentFloatingMenu;
    FloatingActionButton fab_menuFragmentAdd;

    DatabaseReference mMenuDatabaseReference;
    List<MenuModel> mMenuModel;
    MenuAdapter mMenuAdapter;
    private ProgressDialog progressDialog;

    SharedPreferences sharedPreferences;
    String a1;

    ImageView add_menu_image1;
    private Uri image1;
    private StorageTask mUploadTask1;
    private static int image_pic_request1 = 1;


    public Menu_Fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_menu_, container, false);

        try {

            sharedPreferences = this.getActivity().getSharedPreferences("data", 0);
            a1 = sharedPreferences.getString("userid", "");

            progressDialog = new ProgressDialog(getContext());
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Loading...");

            mMenuDatabaseReference = FirebaseDatabase.getInstance().getReference("menu");

            fragment_menu_searchView = view.findViewById(R.id.fragment_menu_searchView);
            refresh_menu = view.findViewById(R.id.refresh_menu);
            fragment_menu_listview = view.findViewById(R.id.fragment_menu_listview);
            menuFragmentFloatingMenu = view.findViewById(R.id.menuFragmentFloatingMenu);
            fab_menuFragmentAdd = view.findViewById(R.id.fab_menuFragmentAdd);

            refresh_menu.setRefreshing(true);

            loadData();

            refresh_menu.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    loadData();
                }
            });

            fab_menuFragmentAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    menuFragmentFloatingMenu.collapse();
                    addMenu();
                }
            });

            fragment_menu_searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    searchMenu(query);
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    searchMenu(newText);
                    return false;
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
        return view;
    }

    private void addMenu()
    {
        Intent intent = new Intent(getContext(), AddMenu_Activity.class);
        startActivity(intent);
    }

    private void searchMenu(String query)
    {
        ArrayList<MenuModel> myList = new ArrayList<>();
        for (MenuModel obj : mMenuModel)
        {
            if (obj.getMenuName().toLowerCase().contains(query.toLowerCase())) {
                myList.add(obj);
            }
        }
        MenuAdapter localadminAdapter = new MenuAdapter(getContext(), myList);
        fragment_menu_listview.setAdapter(localadminAdapter);
        fragment_menu_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
    }

    private void loadData()
    {
        try {
            mMenuDatabaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    refresh_menu.setRefreshing(false);
                    mMenuModel.clear();
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        if (a1.equals(snapshot1.child("menuLocalAdminID").getValue().toString())) {
                            MenuModel model = snapshot1.getValue(MenuModel.class);
                            mMenuModel.add(model);
                        }
                    }
                    mMenuAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getContext(), "" + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

            mMenuModel = new ArrayList<>();
            mMenuAdapter = new MenuAdapter(getContext(), mMenuModel);
            fragment_menu_listview.setAdapter(mMenuAdapter);
            fragment_menu_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
            String[] items = {"Edit", "Delete", "Change Image"};
            AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
            dialog.setTitle("Select Options");
            dialog.setItems(items, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (which == 0)
                    {
                        try {
                            LayoutInflater li = LayoutInflater.from(getContext());
                            View menu = li.inflate(R.layout.addmenu_catagory_layout, null);
                            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getContext());
                            alertBuilder.setView(menu);

                            final EditText add_menu_name, add_menu_description, add_menu_actual_price, add_menu_selling_price, add_menu_offer_price;
                            final EditText add_menu_opentime, add_menu_close_time, add_menu_unit_kg;
                            final Spinner add_menu_main_catagory_spinner, add_menu_sub_catagory_spinner, add_menu_restaurant_name_spinner, add_menu_approval_spinner;
                            final Button add_menu_save_btn;
                            final SwitchCompat add_menu_on_off;

                            final TextView add_menu_final_offer_price;
                            final Button add_menu_calculate_btn;
                            final  EditText add_menu_gst_per;

                            add_menu_name = menu.findViewById(R.id.add_menu_name);
                            add_menu_description = menu.findViewById(R.id.add_menu_description);
                            add_menu_actual_price = menu.findViewById(R.id.add_menu_actual_price);
                            add_menu_selling_price = menu.findViewById(R.id.add_menu_selling_price);
                            add_menu_offer_price = menu.findViewById(R.id.add_menu_offer_price);

                            add_menu_opentime = menu.findViewById(R.id.add_menu_opentime);
                            add_menu_close_time = menu.findViewById(R.id.add_menu_close_time);
                            add_menu_unit_kg = menu.findViewById(R.id.add_menu_unit_kg);
                            add_menu_on_off = menu.findViewById(R.id.add_menu_on_off);
                            add_menu_approval_spinner = menu.findViewById(R.id.add_menu_approval_spinner);
                            add_menu_save_btn = menu.findViewById(R.id.add_menu_save_btn);

                            add_menu_restaurant_name_spinner = menu.findViewById(R.id.add_menu_restaurant_name_spinner);
                            add_menu_main_catagory_spinner = menu.findViewById(R.id.add_menu_main_catagory_spinner);
                            add_menu_sub_catagory_spinner = menu.findViewById(R.id.add_menu_sub_catagory_spinner);

                            add_menu_final_offer_price = menu.findViewById(R.id.add_menu_final_offer_price);
                            add_menu_calculate_btn = menu.findViewById(R.id.add_menu_calculate_btn);
                            add_menu_gst_per = menu.findViewById(R.id.add_menu_gst_per);

                            add_menu_name.setText(mMenuModel.get(position).getMenuName());
                            add_menu_description.setText(mMenuModel.get(position).getMenuDescription());
                            add_menu_actual_price.setText(mMenuModel.get(position).getMenuActualPrice());
                            add_menu_selling_price.setText(mMenuModel.get(position).getMenuSellingPrice());
                            add_menu_offer_price.setText(mMenuModel.get(position).getMenuOfferPrice());

                            add_menu_opentime.setText(mMenuModel.get(position).getMenuOpenTime());
                            add_menu_close_time.setText(mMenuModel.get(position).getMenuCloseTime());
                            add_menu_unit_kg.setText(mMenuModel.get(position).getMenuUnit());
                            add_menu_gst_per.setText(mMenuModel.get(position).getGstno());

                            add_menu_calculate_btn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v)
                                {
                                    String off = add_menu_offer_price.getText().toString();
                                    String gst = add_menu_gst_per.getText().toString();

                                    if( off.isEmpty() || gst.isEmpty())
                                    {
                                        if(off.isEmpty())
                                        {
                                            Toast.makeText(getContext(), "Enter offer price", Toast.LENGTH_SHORT).show();
                                        }
                                        if(gst.isEmpty())
                                        {
                                            Toast.makeText(getContext(), "Enter gst percentage", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                    else
                                    {
                                        Double tempoff = Double.parseDouble(off);
                                        Double tempgst = Double.parseDouble(gst);

                                        Double temp = (tempgst / 100) * tempoff;

                                        Double fin = temp + tempoff;

                                        String nw = String.valueOf(Math.round(fin)) ;

                                        add_menu_final_offer_price.setText(nw);
                                    }
                                }
                            });

                            String tem = mMenuModel.get(position).getMenuOnorOff();
                            if (tem.equals("on")) {
                                add_menu_on_off.setChecked(true);
                            } else {
                                add_menu_on_off.setChecked(false);
                            }

                            String msg = "Select --  ";

                            try {
                                // restaurant spinner
                                DatabaseReference restaurant = FirebaseDatabase.getInstance().getReference("restaurants");
                                ArrayList<String> spinnerDatalistRestaturant;
                                ArrayAdapter<String> adapterRestaurant;
                                ValueEventListener listenerResturant;


                                progressDialog.show();
                                spinnerDatalistRestaturant = new ArrayList<>();
                                adapterRestaurant = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, spinnerDatalistRestaturant);
                                listenerResturant = restaurant.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        spinnerDatalistRestaturant.clear();
                                        spinnerDatalistRestaturant.add(mMenuModel.get(position).getRestName());
                                        spinnerDatalistRestaturant.add(msg);
                                        progressDialog.dismiss();
                                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                            spinnerDatalistRestaturant.add(dataSnapshot.child("resName").getValue().toString());
                                            add_menu_restaurant_name_spinner.setAdapter(adapterRestaurant);
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


                            try {
                                // main catagory spinner
                                DatabaseReference businessReference = FirebaseDatabase.getInstance().getReference("main_catagory");
                                ArrayList<String> spinnerDatalist;
                                ArrayAdapter<String> adapter;
                                ValueEventListener listener;

                                progressDialog.show();
                                spinnerDatalist = new ArrayList<>();
                                adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, spinnerDatalist);
                                listener = businessReference.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        spinnerDatalist.clear();
                                        spinnerDatalist.add(mMenuModel.get(position).getMenuMainCatagorey());
                                        spinnerDatalist.add(msg);
                                        progressDialog.dismiss();
                                        for (DataSnapshot dataSnapshot1 : snapshot.getChildren()) {
                                            spinnerDatalist.add(dataSnapshot1.child("foodCatagoreyType").getValue().toString());
                                            add_menu_main_catagory_spinner.setAdapter(adapter);
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

                            try {
                                // sub catagory spinner
                                DatabaseReference catagoryRef = FirebaseDatabase.getInstance().getReference("sub_category");
                                ArrayList<String> spinnerDatalistcat;
                                ArrayAdapter<String> adaptercat;
                                ValueEventListener listenercat;

                                progressDialog.show();
                                spinnerDatalistcat = new ArrayList<>();
                                adaptercat = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, spinnerDatalistcat);
                                listenercat = catagoryRef.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        spinnerDatalistcat.clear();
                                        spinnerDatalistcat.add(mMenuModel.get(position).getMenuSubCatagorey());
                                        spinnerDatalistcat.add(msg);
                                        progressDialog.dismiss();
                                        for (DataSnapshot dataSnapshot1 : snapshot.getChildren()) {
                                            spinnerDatalistcat.add(dataSnapshot1.child("subCatagoryName").getValue().toString());
                                            add_menu_sub_catagory_spinner.setAdapter(adaptercat);
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

                            String[] catogries = {"Approved", "Not Approved"};
                            ArrayAdapter<String> catog = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, catogries);
                            add_menu_approval_spinner.setAdapter(catog);

                            final AlertDialog alertDialog = alertBuilder.create();

                            add_menu_save_btn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    String name, description, actual, selling, offer, open, close, unitKg, mainCat, subCat, img1, img2, img3, rest, finOff, gstFin;

                                    name = add_menu_name.getText().toString();
                                    description = add_menu_description.getText().toString();
                                    actual = add_menu_actual_price.getText().toString();
                                    selling = add_menu_selling_price.getText().toString();
                                    offer = add_menu_offer_price.getText().toString();

                                    open = add_menu_opentime.getText().toString();
                                    close = add_menu_close_time.getText().toString();

                                    unitKg = add_menu_unit_kg.getText().toString();

                                    rest = add_menu_restaurant_name_spinner.getSelectedItem().toString();
                                    mainCat = add_menu_main_catagory_spinner.getSelectedItem().toString();
                                    subCat = add_menu_sub_catagory_spinner.getSelectedItem().toString();

                                    finOff = add_menu_final_offer_price.getText().toString();
                                    gstFin = add_menu_gst_per.getText().toString();


                                    if (name.isEmpty() || description.isEmpty() || actual.isEmpty() || selling.isEmpty() || offer.isEmpty() || open.isEmpty() || close.isEmpty() || unitKg.isEmpty() || finOff.isEmpty() || gstFin.isEmpty()) {
                                        if (name.isEmpty()) {
                                            add_menu_name.setError("Required");
                                        }
                                        if (description.isEmpty()) {
                                            add_menu_description.setError("Required");
                                        }
                                        if (actual.isEmpty()) {
                                            add_menu_actual_price.setError("Required");
                                        }
                                        if (selling.isEmpty()) {
                                            add_menu_selling_price.setError("Required");
                                        }
                                        if (offer.isEmpty()) {
                                            add_menu_offer_price.setError("Required");
                                        }
                                        if (open.isEmpty()) {
                                            add_menu_opentime.setError("Required");
                                        }
                                        if (close.isEmpty()) {
                                            add_menu_close_time.setError("Required");
                                        }
                                        if (unitKg.isEmpty()) {
                                            add_menu_unit_kg.setError("Required");
                                        }
                                        if(gstFin.isEmpty())
                                        {
                                            add_menu_gst_per.setError("Required");
                                        }

                                        if(finOff.isEmpty())
                                        {
                                            Toast.makeText(getContext(), "Calculate final Price including GST", Toast.LENGTH_SHORT).show();
                                        }

                                    } else {
                                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("menu").child(mMenuModel.get(position).getMenuID());


                                        if (mainCat.equals(msg) || subCat.equals(msg) || rest.equals(msg)) {
                                            if (mainCat.equals(msg)) {
                                                Toast.makeText(getContext(), "Select Main Catagory", Toast.LENGTH_SHORT).show();
                                            }
                                            if (subCat.equals(msg)) {
                                                Toast.makeText(getContext(), "Select Sub Catagory", Toast.LENGTH_SHORT).show();
                                            }
                                            if (rest.equals(msg)) {
                                                Toast.makeText(getContext(), "Select Restaurant", Toast.LENGTH_SHORT).show();
                                            }
                                        } else {

                                            String crtval;
                                            if (add_menu_on_off.isChecked()) {
                                                crtval = "on";
                                            } else {
                                                crtval = "off";
                                            }

                                            progressDialog.show();
                                            Query query = FirebaseDatabase.getInstance().getReference("restaurants");
                                            query.addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                                                        if (rest.equals(snapshot1.child("resName").getValue().toString())) {
                                                            String resID = snapshot1.child("resID").getValue().toString();


                                                            ref.child("menuName").setValue(name);
                                                            ref.child("menuDescription").setValue(description);
                                                            ref.child("menuMainCatagorey").setValue(mainCat);
                                                            ref.child("menuSubCatagorey").setValue(subCat);
                                                            ref.child("menuOfferPrice").setValue(finOff);
                                                            ref.child("gstno").setValue(gstFin);
                                                            ref.child("menuSellingPrice").setValue(selling);
                                                            ref.child("menuActualPrice").setValue(actual);
                                                            ref.child("menuOpenTime").setValue(open);
                                                            ref.child("menuCloseTime").setValue(close);
                                                            ref.child("menuOnorOff").setValue(crtval);
                                                            ref.child("menuUnit").setValue(unitKg);
                                                            ref.child("menuApprovel").setValue(add_menu_approval_spinner.getSelectedItem().toString());
                                                            ref.child("restID").setValue(resID);
                                                            ref.child("restName").setValue(rest);


                                                            progressDialog.dismiss();

                                                            Toast.makeText(getContext(), "Updated Sucessfully", Toast.LENGTH_SHORT).show();
                                                            alertDialog.cancel();


                                                        }
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {
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
                                        database.getReference("menu").child(mMenuModel.get(position).getMenuID()).removeValue();
                                    }
                                });
                        dialog1.show();

                    }
                    if (which == 2)
                    {
                        try {
                            LayoutInflater li = LayoutInflater.from(getContext());
                            View menu = li.inflate(R.layout.addmenu_catagory_image_layout, null);
                            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getContext());
                            alertBuilder.setView(menu);

                            Button add_menu_image_uploadbtn;

                            add_menu_image_uploadbtn = menu.findViewById(R.id.add_menu_image_uploadbtn);
                            add_menu_image1 = menu.findViewById(R.id.add_menu_image1);

                            final AlertDialog alertDialog = alertBuilder.create();

                            add_menu_image1.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    openFileChooser();
                                }
                            });

                            add_menu_image_uploadbtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v)
                                {
                                    if (mUploadTask1 != null && mUploadTask1.isInProgress()) {
                                        Toast.makeText(getContext(), "Uploading in Progress", Toast.LENGTH_SHORT).show();
                                    } else {
                                        StorageReference mstorageReference;
                                        mstorageReference = FirebaseStorage.getInstance().getReference("menu/image");

                                        if (image1 != null)
                                        {
                                            progressDialog.show();
                                            StorageReference fileRef = mstorageReference.child(System.currentTimeMillis() + "." + getFileExtention(image1));
                                            mUploadTask1 = fileRef.putFile(image1).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                @Override
                                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                    Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                                                    while (!uriTask.isSuccessful()) ;

                                                    Uri downloadUrl = uriTask.getResult();

                                                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("menu").child(mMenuModel.get(position).getMenuID());
                                                    ref.child("image1").setValue(downloadUrl.toString());

                                                    progressDialog.dismiss();
                                                    Toast.makeText(getContext(), "Image Uploaded", Toast.LENGTH_SHORT).show();
                                                    alertDialog.cancel();

                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    progressDialog.dismiss();
                                                    Toast.makeText(getContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            });

                                        } else {
                                            Toast.makeText(getContext(), "No files Selected", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }
                            });
                            alertDialog.show();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            dialog.create().show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openFileChooser()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, image_pic_request1);
    }

    private String getFileExtention(Uri uri) {
        ContentResolver cr = getContext().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(uri));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == image_pic_request1 && resultCode == RESULT_OK && data != null && data.getData() != null)
        {
            image1 = data.getData();
            Picasso.get().load(image1).into(add_menu_image1);
        }
    }

}