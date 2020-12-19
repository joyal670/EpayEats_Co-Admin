package com.epayeats.epayeatsco_admin.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.epayeats.epayeatsco_admin.Activity.RestaurantReport_Activity;
import com.epayeats.epayeatsco_admin.Model.RestaurantModel;
import com.epayeats.epayeatsco_admin.R;

import java.util.List;

public class RestaurantReportAdapter extends BaseAdapter
{
    Context context;
    List<RestaurantModel> restaurantModel;

    public RestaurantReportAdapter(Context context, List<RestaurantModel> restaurantModel) {
        this.context = context;
        this.restaurantModel = restaurantModel;
    }

    @Override
    public int getCount() {
        return restaurantModel.size();
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
        convertView = inflater.inflate(R.layout.restaurant_listview_items, null);

        TextView restaurant__item_name, restaurant__item_phone, restaurant__item_location, restaurant__item_open_time;
        TextView restaurant__item_close_time, restaurant__item_licenceno, restaurant__item_Gstno, restaurant__item_Closed, restaurant__item_isblocked;
        LinearLayout rest_report_layout;

        rest_report_layout = convertView.findViewById(R.id.rest_report_layout);
        restaurant__item_name = convertView.findViewById(R.id.restaurant__item_name);
        restaurant__item_phone = convertView.findViewById(R.id.restaurant__item_phone);
        restaurant__item_location = convertView.findViewById(R.id.restaurant__item_location);
        restaurant__item_open_time = convertView.findViewById(R.id.restaurant__item_open_time);
        restaurant__item_close_time = convertView.findViewById(R.id.restaurant__item_close_time);
        restaurant__item_licenceno = convertView.findViewById(R.id.restaurant__item_licenceno);
        restaurant__item_Gstno = convertView.findViewById(R.id.restaurant__item_Gstno);
        restaurant__item_Closed = convertView.findViewById(R.id.restaurant__item_Closed);
        restaurant__item_isblocked = convertView.findViewById(R.id.restaurant__item_isblocked);

        restaurant__item_name.setText(restaurantModel.get(position).getResName());
        restaurant__item_phone.setText(restaurantModel.get(position).getResPhone());
        restaurant__item_location.setText(restaurantModel.get(position).getResLocation());
        restaurant__item_open_time.setText(restaurantModel.get(position).getResOpenTime());
        restaurant__item_close_time.setText(restaurantModel.get(position).getResCloseTime());
        restaurant__item_licenceno.setText(restaurantModel.get(position).getResLicenceNo());
        restaurant__item_Gstno.setText(restaurantModel.get(position).getResGstNo());
        restaurant__item_Closed.setText(restaurantModel.get(position).getIsShopClosed());
        restaurant__item_isblocked.setText(restaurantModel.get(position).getIsBlocked());

        rest_report_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                RestaurantReport_Activity.restaurantInterface.rest(position, restaurantModel.get(position).getResID(), restaurantModel.get(position).getResName(), restaurantModel.get(position).getResPhone(), restaurantModel.get(position).getResLocalAdminName(),  restaurantModel.get(position).getResLocation());
            }
        });
        return convertView;
    }
}

