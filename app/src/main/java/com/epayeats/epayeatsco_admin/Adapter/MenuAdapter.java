package com.epayeats.epayeatsco_admin.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.epayeats.epayeatsco_admin.Model.MenuModel;
import com.epayeats.epayeatsco_admin.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MenuAdapter extends BaseAdapter
{
    Context context;
    List<MenuModel> menuModel;

    public MenuAdapter(Context context, List<MenuModel> menuModel) {
        this.context = context;
        this.menuModel = menuModel;
    }

    @Override
    public int getCount() {
        return menuModel.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.menu_listview_items, null);

        TextView listview_menu_name, listview_menu_description, listview_menu_main_catagory, listview_menu_sub_catagory;
        TextView listview_menu_offer_price, listview_menu_selling_price, listview_menu_actual_price, listview_menu_gstno;
        TextView listview_menu_open_time, listview_menu_close_time, listview_menu_onOf, listview_menu_unit, listview_menu_approvel;
        ImageView listview_menu_image1;

        listview_menu_name = convertView.findViewById(R.id.listview_menu_name);
        listview_menu_description = convertView.findViewById(R.id.listview_menu_description);
        listview_menu_main_catagory = convertView.findViewById(R.id.listview_menu_main_catagory);
        listview_menu_sub_catagory = convertView.findViewById(R.id.listview_menu_sub_catagory);

        listview_menu_offer_price = convertView.findViewById(R.id.listview_menu_offer_price);
        listview_menu_selling_price = convertView.findViewById(R.id.listview_menu_selling_price);
        listview_menu_actual_price = convertView.findViewById(R.id.listview_menu_actual_price);

        listview_menu_open_time = convertView.findViewById(R.id.listview_menu_open_time);
        listview_menu_close_time = convertView.findViewById(R.id.listview_menu_close_time);
        listview_menu_onOf = convertView.findViewById(R.id.listview_menu_onOf);
        listview_menu_unit = convertView.findViewById(R.id.listview_menu_unit);
        listview_menu_approvel = convertView.findViewById(R.id.listview_menu_approvel);
        listview_menu_gstno = convertView.findViewById(R.id.listview_menu_gstno);

        listview_menu_image1 = convertView.findViewById(R.id.listview_menu_image1);

        listview_menu_name.setText(menuModel.get(position).getMenuName());
        listview_menu_description.setText(menuModel.get(position).getMenuDescription());
        listview_menu_main_catagory.setText(menuModel.get(position).getMenuMainCatagorey());
        listview_menu_sub_catagory.setText(menuModel.get(position).getMenuSubCatagorey());

        listview_menu_offer_price.setText(menuModel.get(position).getMenuOfferPrice());
        listview_menu_selling_price.setText(menuModel.get(position).getMenuSellingPrice());
        listview_menu_actual_price.setText(menuModel.get(position).getMenuActualPrice());

        listview_menu_open_time.setText(menuModel.get(position).getMenuOpenTime());
        listview_menu_close_time.setText(menuModel.get(position).getMenuCloseTime());
        listview_menu_onOf.setText(menuModel.get(position).getMenuCloseTime());
        listview_menu_unit.setText(menuModel.get(position).getMenuUnit());
        listview_menu_approvel.setText(menuModel.get(position).getMenuApprovel());
        listview_menu_gstno.setText(menuModel.get(position).getGstno());

        Picasso.get().load(menuModel.get(position).getImage1()).into(listview_menu_image1);

        return convertView;
    }
}

