package com.epayeats.epayeatsco_admin.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;

import com.epayeats.epayeatsco_admin.Model.MenuModel;
import com.epayeats.epayeatsco_admin.R;

import java.util.List;

public class MenuApprovelAdapter extends BaseAdapter
{
    Context context;
    List<MenuModel> menuModel;

    public MenuApprovelAdapter(Context context, List<MenuModel> menuModel) {
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
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.approvel_list_items, null);

        TextView approvel_item_menu_name, approvel_item_rest_name, approvel_item_spinner, approvel_item_actual_price;

        approvel_item_menu_name = convertView.findViewById(R.id.approvel_item_menu_name);
        approvel_item_rest_name = convertView.findViewById(R.id.approvel_item_rest_name);
        approvel_item_spinner = convertView.findViewById(R.id.approvel_item_spinner);
        approvel_item_actual_price = convertView.findViewById(R.id.approvel_item_actual_price);

        approvel_item_menu_name.setText(menuModel.get(position).getMenuName());
        approvel_item_rest_name.setText(menuModel.get(position).getRestName());
        approvel_item_spinner.setText(menuModel.get(position).getMenuApprovel());
        approvel_item_actual_price.setText(menuModel.get(position).getMenuActualPrice());


        return convertView;
    }
}
