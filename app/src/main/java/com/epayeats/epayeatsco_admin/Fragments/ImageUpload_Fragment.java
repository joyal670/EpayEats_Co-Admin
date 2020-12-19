package com.epayeats.epayeatsco_admin.Fragments;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.epayeats.epayeatsco_admin.Adapter.ImagesAdapter;
import com.epayeats.epayeatsco_admin.Interface.ImageInterface;
import com.epayeats.epayeatsco_admin.Model.ImagesModel;
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


public class ImageUpload_Fragment extends Fragment implements ImageInterface
{
    RecyclerView images_recyclerview;
    public ProgressDialog progressDialog;

    SharedPreferences sharedPreferences;
    String a1;

    DatabaseReference reference;
    List<ImagesModel> model;
    ImagesAdapter imagesAdapter;

    public static ImageInterface imageInterface;

    FloatingActionsMenu ImageFragmentFloatingMenu;
    FloatingActionButton fab_imageFragmentAdd;

    ImageView add_banner_image1;
    private Uri image;
    private StorageTask mUploadTask;
    private static int image_pic_request = 1;

    public ImageUpload_Fragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_image_upload_, container, false);

        imageInterface = this;

        images_recyclerview = view.findViewById(R.id.images_recyclerview);
        ImageFragmentFloatingMenu = view.findViewById(R.id.ImageFragmentFloatingMenu);
        fab_imageFragmentAdd = view.findViewById(R.id.fab_imageFragmentAdd);

        sharedPreferences = getContext().getSharedPreferences("data", 0);
        a1 = sharedPreferences.getString("userid", "");

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading...");

        reference = FirebaseDatabase.getInstance().getReference("banner_images");

        loadImages();

        fab_imageFragmentAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                ImageFragmentFloatingMenu.collapse();
                uploadImg();
            }
        });

        return view;
    }

    private void uploadImg()
    {
        LayoutInflater li = LayoutInflater.from(getContext());
        View myBusiness = li.inflate(R.layout.new_image_layout, null);
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getContext());
        alertBuilder.setView(myBusiness);

        Button add_banner_image_uploadbtn;

        add_banner_image_uploadbtn = myBusiness.findViewById(R.id.add_banner_image_uploadbtn);

        add_banner_image1 = myBusiness.findViewById(R.id.add_banner_image1);

        final AlertDialog alertDialog = alertBuilder.create();

        add_banner_image1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        add_banner_image_uploadbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if (mUploadTask != null && mUploadTask.isInProgress())
                {
                    Toast.makeText(getContext(), "Uploading in Progress", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    StorageReference mstorageReference;
                    mstorageReference = FirebaseStorage.getInstance().getReference("banner/image");

                    DatabaseReference databaseReference =  FirebaseDatabase.getInstance().getReference("banner_images");
                    String pushkey = databaseReference.push().getKey();

                    if (image != null)
                    {
                        progressDialog.show();
                        StorageReference fileRef = mstorageReference.child(System.currentTimeMillis() + "." + getFileExtention(image));
                        mUploadTask = fileRef.putFile(image).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
                            {
                                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                                while (!uriTask.isSuccessful()) ;

                                Uri downloadUrl = uriTask.getResult();


                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("local_admin").child(a1);
                                ref.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot)
                                    {

                                        String name = snapshot.child("admnName").getValue().toString();
                                        String km = snapshot.child("admnBusinessKM").getValue().toString();
                                        String bname = snapshot.child("admnBusinessArea").getValue().toString();

                                        DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference("business");
                                        ref2.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot)
                                            {
                                                for (DataSnapshot snapbus : snapshot.getChildren())
                                                {
                                                    if(bname.equals(snapbus.child("location").getValue().toString()))
                                                    {
                                                        ImagesModel model;
                                                        model = new ImagesModel();

                                                        model.setId(pushkey);
                                                        model.setLocalAdminID(a1);
                                                        model.setImageUrl(downloadUrl.toString());
                                                        model.setLocalAdminName(name);
                                                        model.setBusinessArea(bname);
                                                        model.setBusinessKM(km);
                                                        model.setLat(snapbus.child("latitude").getValue().toString());
                                                        model.setLon(snapbus.child("longitute").getValue().toString());

                                                        databaseReference.child(pushkey).setValue(model);
                                                        progressDialog.dismiss();
                                                        Toast.makeText(getContext(), "Image Uploaded", Toast.LENGTH_SHORT).show();
                                                        alertDialog.cancel();
                                                    }
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error)
                                            {
                                                Toast.makeText(getContext(), ""+error.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });


                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error)
                                    {
                                        Toast.makeText(getContext(), ""+error.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });



                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e)
                            {
                                Toast.makeText(getContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    else
                    {
                        Toast.makeText(getContext(), "No files Selected", Toast.LENGTH_SHORT).show();
                    }


                }
            }
        });
        alertDialog.show();
    }

    private void loadImages()
    {
        progressDialog.show();
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                model.clear();
                progressDialog.dismiss();
                for (DataSnapshot snapshot1 : snapshot.getChildren())
                {
                    if(a1.equals(snapshot1.child("localAdminID").getValue().toString()))
                    {
                        ImagesModel imagesModel = snapshot1.getValue(ImagesModel.class);
                        model.add(imagesModel);
                    }
                }
                imagesAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {
                progressDialog.dismiss();
                Toast.makeText(getContext(), ""+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        model = new ArrayList<>();
        imagesAdapter = new ImagesAdapter(getContext(), model);
        images_recyclerview.setAdapter(imagesAdapter);
    }

    private void openFileChooser()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, image_pic_request);
    }

    private String getFileExtention(Uri uri) {
        ContentResolver cr = getContext().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(uri));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == image_pic_request && resultCode == RESULT_OK && data != null && data.getData() != null)
        {
            image = data.getData();
            Picasso.get().load(image).into(add_banner_image1);
        }
    }

    @Override
    public void img(String id)
    {
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
                                .setContentText("Image has been deleted")
                                .setConfirmText("OK")
                                .setConfirmClickListener(null)
                                .showCancelButton(false)
                                .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);

                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        database.getReference("banner_images").child(id).removeValue();
                    }
                });
        dialog1.show();
    }
}