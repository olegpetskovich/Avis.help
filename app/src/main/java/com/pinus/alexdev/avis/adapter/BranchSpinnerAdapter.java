package com.pinus.alexdev.avis.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.pinus.alexdev.avis.R;
import com.pinus.alexdev.avis.dto.response.BranchesItem;

import java.util.List;

public class BranchSpinnerAdapter extends ArrayAdapter<BranchesItem> {
    private Context context;

    public BranchSpinnerAdapter(Context context, List<BranchesItem> objects) {
        super(context, 0, objects);
        this.context = context;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    public View getCustomView(int position, View convertView, ViewGroup parent) {
        if (position == 0) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.spinner_top_empty_item, parent, false);
            return convertView;
        } else {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.custom_spinner_item, parent, false);
        }

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.custom_spinner_item, parent, false);
        }

        TextView nameBranch = convertView.findViewById(R.id.text1);

        BranchesItem branch = getItem(position);
        if (branch != null) {
            nameBranch.setText(branch.getName());
        }
        return convertView;
    }
}
