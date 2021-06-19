package com.pinus.alexdev.avis.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.pinus.alexdev.avis.R;
import com.pinus.alexdev.avis.view.qr_manager_activity.AddOrEditAnswerActivity;
import com.pinus.alexdev.avis.view.qr_manager_activity.AddOrEditCategoryActivity;

import java.util.ArrayList;

import static com.pinus.alexdev.avis.view.qr_manager_activity.AddOrEditAnswerActivity.ADD_ANSWER;
import static com.pinus.alexdev.avis.view.qr_manager_activity.AddOrEditAnswerActivity.EDIT_ANSWER;
import static com.pinus.alexdev.avis.view.qr_manager_activity.AddOrEditCategoryActivity.ADD_CATEGORY;
import static com.pinus.alexdev.avis.view.qr_manager_activity.AddOrEditCategoryActivity.EDIT_CATEGORY;

public class QrSpinnerListAdapter extends ArrayAdapter {
    public boolean isCategorySpinner;

    private Context context;

    private AnswerOrCategoryDataListener answerOrCategoryDataListener;

    public void setAnswerOrCategoryDataListener(AnswerOrCategoryDataListener answerOrCategoryDataListener) {
        this.answerOrCategoryDataListener = answerOrCategoryDataListener;
    }

    public interface AnswerOrCategoryDataListener {
        void listCollapsed();
    }

    public QrSpinnerListAdapter(Context context, int resourceId, ArrayList<String> answers) {
        super(context, resourceId, answers);
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
        String model = (String) getItem(position);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_answer, parent, false);

        if (position != 0) {
            ImageView imageView = view.findViewById(R.id.btnArrow);
            imageView.setOnClickListener(v -> {
                Intent intent;
                if (isCategorySpinner) {
                    intent = new Intent(context, AddOrEditCategoryActivity.class);
                    intent.putExtra("add_or_edit_category", EDIT_CATEGORY);
                    intent.putExtra("category_text", model);
                    intent.putExtra("category_position", position);
                } else {
                    intent = new Intent(context, AddOrEditAnswerActivity.class);
                    intent.putExtra("add_or_edit_answer", EDIT_ANSWER);
                    intent.putExtra("answer_text", model);
                    intent.putExtra("answer_position", position);
                }
                context.startActivity(intent);
                answerOrCategoryDataListener.listCollapsed();
            });
        } else {
            view.setOnClickListener(v -> {
                Intent intent;
                if (isCategorySpinner) {
                    intent = new Intent(context, AddOrEditCategoryActivity.class);
                    intent.putExtra("add_or_edit_category", ADD_CATEGORY);
                } else {
                    intent = new Intent(context, AddOrEditAnswerActivity.class);
                    intent.putExtra("add_or_edit_answer", ADD_ANSWER);
                }
                context.startActivity(intent);
                answerOrCategoryDataListener.listCollapsed();
            });
        }

        TextView name = view.findViewById(R.id.name);
        name.setText(model);
        return view;
    }

}
