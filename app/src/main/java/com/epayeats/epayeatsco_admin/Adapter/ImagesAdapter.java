package com.epayeats.epayeatsco_admin.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.epayeats.epayeatsco_admin.Fragments.ImageUpload_Fragment;
import com.epayeats.epayeatsco_admin.Model.ImagesModel;
import com.epayeats.epayeatsco_admin.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ImagesAdapter extends RecyclerView.Adapter<ImagesAdapter.ImageHolder>
{
    Context context;
    List<ImagesModel> menuModel;

    public ImagesAdapter(Context context, List<ImagesModel> menuModel) {
        this.context = context;
        this.menuModel = menuModel;
    }

    @NonNull
    @Override
    public ImagesAdapter.ImageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.images_recycler, parent, false);
        return new ImagesAdapter.ImageHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ImagesAdapter.ImageHolder holder, int position)
    {
        ImagesModel currentUpload = menuModel.get(position);


        Picasso.get().load(currentUpload.getImageUrl()).into(holder.images_recycer_imageview);

        holder.images_recycer_imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                ImageUpload_Fragment.imageInterface.img(menuModel.get(position).getId());
            }
        });

    }

    @Override
    public int getItemCount() {
        return menuModel.size();
    }

    public class ImageHolder extends RecyclerView.ViewHolder
    {
        ImageView images_recycer_imageview;

        public ImageHolder(@NonNull View itemView) {
            super(itemView);

            images_recycer_imageview = itemView.findViewById(R.id.images_recycer_imageview);

        }

    }
}

