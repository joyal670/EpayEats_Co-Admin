package com.epayeats.epayeatsco_admin.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.epayeats.epayeatsco_admin.Model.orderModel;
import com.epayeats.epayeatsco_admin.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class OrderDataAdapter extends BaseAdapter
{
    Context context;
    List<orderModel> model;

    public OrderDataAdapter(Context context, List<orderModel> model) {
        this.context = context;
        this.model = model;
    }

    @Override
    public int getCount() {
        return model.size();
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
        convertView = inflater.inflate(R.layout.order_view, null);

        TextView name, number, date, status, time;
        String temp;
        CircleImageView image;

        name = convertView.findViewById(R.id.order_view_name);
        number = convertView.findViewById(R.id.order_view_number);
        date = convertView.findViewById(R.id.order_view_date);
        status = convertView.findViewById(R.id.order_view_status);
        image = convertView.findViewById(R.id.order_view_image);
        time = convertView.findViewById(R.id.order_view_time);


        name.setText(model.get(position).getcName());
        number.setText(model.get(position).getcPhone());
        date.setText(model.get(position).getOrderDate());
        time.setText(model.get(position).getOrderTime());

        Picasso.get().load(model.get(position).getMenuImage()).into(image);

        temp = model.get(position).getOrderStatus();

        if (temp.equals("0")) {
            status.setText("Pending, Not yet Delivered");
        } else if (temp.equals("1")) {
            status.setText("Order is Picked up by the Delivery Agent");
        } else if (temp.equals("2")) {
            status.setText("Delivered");
        } else {
            status.setText("Cancelled");
        }

        return convertView;
    }
}
