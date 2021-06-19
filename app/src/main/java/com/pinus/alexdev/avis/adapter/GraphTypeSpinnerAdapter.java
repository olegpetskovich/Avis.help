package com.pinus.alexdev.avis.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.pinus.alexdev.avis.R;

import java.util.List;

public class GraphTypeSpinnerAdapter extends ArrayAdapter<String> {
    private Context context;

    public GraphTypeSpinnerAdapter(Context context,
                                   List<String> objects) {
        super(context, 0, objects);
        this.context = context;
    }

    @Override
    public View getDropDownView(int position, View convertView,
                                ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    public View getCustomView(int position, View convertView, ViewGroup parent) {

        String item = getItem(position);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.custom_spinner_item, parent, false);

        TextView nameBranch = (TextView) row.findViewById(android.R.id.text1);
        nameBranch.setText(item);
        return row;
    }
}
