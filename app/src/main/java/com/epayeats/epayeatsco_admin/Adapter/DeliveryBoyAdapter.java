package com.epayeats.epayeatsco_admin.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.epayeats.epayeatsco_admin.Model.DeliveryBoyModel;
import com.epayeats.epayeatsco_admin.R;

import java.util.List;

public class DeliveryBoyAdapter extends BaseAdapter
{
    Context context;
    List<DeliveryBoyModel> mDeliveryBoyModel;

    public DeliveryBoyAdapter(Context context, List<DeliveryBoyModel> mDeliveryBoyModel) {
        this.context = context;
        this.mDeliveryBoyModel = mDeliveryBoyModel;
    }

    @Override
    public int getCount() {
        return mDeliveryBoyModel.size();
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
        convertView = inflater.inflate(R.layout.deliveryboy_listview_items, null);

        TextView deliveryboy_name, deliveryboy_status, deliveryboy_mobile, deliveryboy_blockstatus;

        deliveryboy_name = convertView.findViewById(R.id.deliveryboy_name);
        deliveryboy_status = convertView.findViewById(R.id.deliveryboy_status);
        deliveryboy_mobile = convertView.findViewById(R.id.deliveryboy_mobile);
        deliveryboy_blockstatus = convertView.findViewById(R.id.deliveryboy_blockstatus);

        deliveryboy_name.setText(mDeliveryBoyModel.get(position).getDeliveryBoyName());
        deliveryboy_status.setText(mDeliveryBoyModel.get(position).getOnlineorOffline());
        deliveryboy_mobile.setText(mDeliveryBoyModel.get(position).getDeliveyBoyMobileNo());
        deliveryboy_blockstatus.setText(mDeliveryBoyModel.get(position).getIsBlocked());

        return convertView;
    }
}


