package com.epayeats.epayeatsco_admin.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.epayeats.epayeatsco_admin.Model.SubCatagoryModel;
import com.epayeats.epayeatsco_admin.R;

import java.util.List;

public class SubCategoryAdapter extends BaseAdapter
{
    Context context;
    List<SubCatagoryModel> model;

    public SubCategoryAdapter(Context context, List<SubCatagoryModel> model) {
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
    public View getView(int position, View convertView, ViewGroup parent)
    {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.sub_category_listview_items, null);

        TextView sub_category_name_textview, sub_category_maincategory_textview;

        sub_category_name_textview = convertView.findViewById(R.id.sub_category_name_textview);
        sub_category_maincategory_textview = convertView.findViewById(R.id.sub_category_maincategory_textview);

        sub_category_name_textview.setText(model.get(position).getSubCatagoryName());
        sub_category_maincategory_textview.setText(model.get(position).getMainCategoryName());

        return convertView;
    }
}
