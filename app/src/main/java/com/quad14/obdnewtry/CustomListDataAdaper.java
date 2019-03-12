package com.quad14.obdnewtry;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.quad14.obdnewtry.activity.KmListData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CustomListDataAdaper extends ArrayAdapter<CustomDataListModel> {

    ArrayList<CustomDataListModel> customDataListModels;
    Context context;
    int resource;

    public CustomListDataAdaper(@NonNull Context context, int resource,@NonNull List<CustomDataListModel> objects) {
        super(context, resource,objects);
        this.customDataListModels = customDataListModels;
        this.context = context;
        this.resource = resource;
    }

    public CustomListDataAdaper(@NonNull Context context, int resource, Context context1) {
        super(context, resource);
        this.context = context1;
    }

    @Nullable
    @Override
    public CustomDataListModel getItem(int position) {
        return super.getItem(position);
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(super.getCount() - position - 1);
    }

    @NonNull
    @Override
    public View getView(int position,  @Nullable View convertView, @NonNull ViewGroup parent) {

        if (convertView == null){
            LayoutInflater layoutInflater = (LayoutInflater) getContext()
                    .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.customkmlist, null, true);
        }

        final CustomDataListModel customDataListModel=getItem(position);

        TextView Date=(TextView) convertView.findViewById(R.id.listDatetxt);
        TextView Time=(TextView) convertView.findViewById(R.id.listTimetxt);
        TextView Km=(TextView) convertView.findViewById(R.id.listKmtxt);
        TextView Dffkm=(TextView) convertView.findViewById(R.id.listdiffkmtxt);

        Date.setText(customDataListModel.getDate());
        Time.setText(customDataListModel.getTime());
        Km.setText(String.valueOf(customDataListModel.getKm()));
        Dffkm.setText(String.valueOf(customDataListModel.getDfKm()));

        return convertView;
    }
}
