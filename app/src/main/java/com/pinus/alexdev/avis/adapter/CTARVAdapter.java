package com.pinus.alexdev.avis.adapter;

import android.animation.LayoutTransition;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.pinus.alexdev.avis.R;
import com.pinus.alexdev.avis.model.AnswerModel;
import com.pinus.alexdev.avis.model.CTA_QRModel;
import com.pinus.alexdev.avis.view.cta_activity.CTAInfoActivity;

import java.util.ArrayList;
import java.util.List;

public class CTARVAdapter extends RecyclerView.Adapter<CTARVAdapter.MyViewHolder> {
    private Context context;
    private ArrayList<CTA_QRModel> qrModels;

    public CTARVAdapter(Context context, ArrayList<CTA_QRModel> qrModels) {
        this.context = context;
        this.qrModels = qrModels;
    }

    @NonNull
    @Override
    public CTARVAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        view = inflater.inflate(R.layout.item_cta, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CTARVAdapter.MyViewHolder holder, int position) {
        holder.btnQRSpinner.setText(qrModels.get(position).getQRName());
    }

    @Override
    public int getItemCount() {
        return qrModels.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView layoutCtaSpinner;
        RelativeLayout layoutWithAnswersList;
        ListView answersList;
        MaterialButton btnQRSpinner;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            layoutCtaSpinner = itemView.findViewById(R.id.layoutCtaSpinner);
            layoutWithAnswersList = itemView.findViewById(R.id.layoutWithAnswersList);

            btnQRSpinner = itemView.findViewById(R.id.btnQRSpinner);
            btnQRSpinner.setOnClickListener(v -> {
                List<AnswerModel> answerModels = qrModels.get(getAdapterPosition()).getAnswerModels();

                CTAAnswerListAdapter adapter = new CTAAnswerListAdapter(context, R.layout.item_answer, (ArrayList<AnswerModel>) answerModels);
                answersList = itemView.findViewById(R.id.answersList);
                answersList.setAdapter(adapter);
                answersList.setOnItemClickListener((parent, view, position, id) -> {
                    AnswerModel item = (AnswerModel) answersList.getAdapter().getItem(position);

                    String qrName = qrModels.get(getAdapterPosition()).getQRName();
                    String qrQuestionText = item.getQuestionText();
                    String qrAnswerName = item.getAnswerName();
                    String qrClickedCount = item.getClickedCount();
                    String qrReactionTime = item.getReactionTime();

                    Intent intent = new Intent(context, CTAInfoActivity.class);
                    intent.putExtra("cta_qr_name", qrName);
                    intent.putExtra("cta_qr_question_text", qrQuestionText);
                    intent.putExtra("cta_qr_answer_name", qrAnswerName);
                    intent.putExtra("cta_qr_clicked_count", qrClickedCount);
                    intent.putExtra("cta_qr_reaction_time", qrReactionTime);
                    context.startActivity(intent);

                    listCollapseExpand();
                });

                listCollapseExpand();
            });
        }

        int getTotalHeightOfListView(ListView listView) {
            ListAdapter mAdapter = listView.getAdapter();
            int totalHeight = 0;

            for (int i = 0; i < mAdapter.getCount(); i++) {
                View mView = mAdapter.getView(i, null, listView);

                mView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

                totalHeight += mView.getMeasuredHeight();
                Log.w("HEIGHT" + i, String.valueOf(totalHeight));

            }

            ViewGroup.LayoutParams params = listView.getLayoutParams();
            params.height = totalHeight + (listView.getDividerHeight() * (mAdapter.getCount() - 1));

            //На случай, если нужно сразу установить параметры для ListView
//            listView.setLayoutParams(params);
//            listView.requestLayout();

            return params.height;
        }

        private void listCollapseExpand() {
            layoutCtaSpinner
                    .getLayoutTransition()
                    .enableTransitionType(LayoutTransition.CHANGING);

            ViewGroup.LayoutParams params = layoutWithAnswersList.getLayoutParams();
            params.height = params.height == 0 ? getTotalHeightOfListView(answersList) : 0;
            getTotalHeightOfListView(answersList);
            layoutWithAnswersList.setLayoutParams(params);
            answersList.requestLayout();
        }
    }
}
