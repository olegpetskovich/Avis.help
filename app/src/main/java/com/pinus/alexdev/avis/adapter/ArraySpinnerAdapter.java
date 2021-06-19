package com.pinus.alexdev.avis.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.pinus.alexdev.avis.R;
import com.pinus.alexdev.avis.dto.response.promo_code_response.PromoCodeResponse;
import com.pinus.alexdev.avis.utils.FontManager;

import java.util.ArrayList;

public class ArraySpinnerAdapter extends ArrayAdapter<PromoCodeResponse> {
    private Context context;

    public ArraySpinnerAdapter(Context context, int resourceId,
                               ArrayList<PromoCodeResponse> objects) {
        super(context, resourceId, objects);
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

        PromoCodeResponse response = getItem(position);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.promo_item_message, parent, false);

        TextView labelIcon = row.findViewById(R.id.itemImgPromo);
        TextView namePromoTitle = row.findViewById(R.id.namePromoTitle);

        Typeface iconFont = FontManager.getTypeface(context, FontManager.FONTAWESOME);
        FontManager.markAsIconContainer(labelIcon, iconFont);

        labelIcon.setText(response.getIconId());
        namePromoTitle.setText(response.getName());
        return row;
    }
}

