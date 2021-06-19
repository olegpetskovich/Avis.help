package com.pinus.alexdev.avis.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.pinus.alexdev.avis.R;

public class IconAdapter extends BaseAdapter {
    private final Context mContext;

    private final int[] icons = {
            R.string._0,
            R.string._1,
            R.string._2,
            R.string._3,
            R.string._4,
            R.string._5,
            R.string._6,
            R.string._7,
            R.string._8,
            R.string._9,
            R.string._10,
            R.string._11,
            R.string._12,
            R.string._13,
            R.string._14,
            R.string._15,
            R.string._16,
            R.string._17,
            R.string._18,
            R.string._19,
            R.string._20,
            R.string._21,
            R.string._22,
            R.string._23,
            R.string._24,
            R.string._25,
            R.string._26,
            R.string._27,
            R.string._28,
            R.string._29,
            R.string._30,
            R.string._31,
            R.string._32,
            R.string._33,
            R.string._34,
            R.string._35,
            R.string._36,
            R.string._37,
            R.string._38,
            R.string._39,
            R.string._40,
            R.string._41,
            R.string._42,
            R.string._43,
            R.string._44,
            R.string._45,
            R.string._46,
            R.string._47,
            R.string._48,
            R.string._49,
            R.string._50,
            R.string._51,
            R.string._52,
            R.string._53,
            R.string._54,
            R.string._55,
            R.string._56,
            R.string._57,
            R.string._58,
            R.string._59,
            R.string._60,
            R.string._61,
            R.string._62,
            R.string._63,
            R.string._64,
            R.string._65,
            R.string._66,
            R.string._67,
            R.string._68,
    };

    // 1
    public IconAdapter(Context context) {
        super();
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return icons.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // 2
        if (convertView == null) {
            final LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            convertView = layoutInflater.inflate(R.layout.icons, null);
        }
        // 3


        final TextView imageView = convertView.findViewById(R.id.iconText);
        imageView.setText(icons[position]);
        return convertView;

    }

}
