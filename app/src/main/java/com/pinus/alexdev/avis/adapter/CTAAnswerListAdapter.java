package com.pinus.alexdev.avis.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.pinus.alexdev.avis.R;
import com.pinus.alexdev.avis.model.AnswerModel;

import java.util.ArrayList;
import java.util.Objects;

public class CTAAnswerListAdapter extends ArrayAdapter<AnswerModel> {
    private Context context;

    public CTAAnswerListAdapter(Context context, int resourceId, ArrayList<AnswerModel> objects) {
        super(context, resourceId, objects);
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
        AnswerModel model = getItem(position);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_answer, parent, false);

        TextView answerName = view.findViewById(R.id.name);
        answerName.setText(Objects.requireNonNull(model).getAnswerName());
        return view;
    }
}
