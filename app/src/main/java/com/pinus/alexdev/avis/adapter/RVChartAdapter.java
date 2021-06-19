package com.pinus.alexdev.avis.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.pinus.alexdev.avis.R;
import com.pinus.alexdev.avis.callback.DataListener;
import com.pinus.alexdev.avis.model.ChartItemModel;
import com.pinus.alexdev.avis.view.BranchChartListActivity;

import java.util.ArrayList;
import java.util.List;

public class RVChartAdapter extends RecyclerView.Adapter<RVChartAdapter.MyViewHolder> {
    private final short MAIN_ITEM_POSITION = 7;

    private Context context;
    private List<ChartItemModel> chartItems;

    private List<Integer> posItems = new ArrayList<>();
    private int checkedPosition = -1;

    public RVChartAdapter(Context context, List<ChartItemModel> chartItems) {
        this.context = context;
        this.chartItems = chartItems;
    }

    private DataListener dataListener;

    public void setDataListener(DataListener dataListener) {
        this.dataListener = dataListener;
    }

    @NonNull
    @Override
    public RVChartAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == MAIN_ITEM_POSITION)
            view = inflater.inflate(R.layout.item_main, parent, false);
        else
            view = inflater.inflate(R.layout.item_rest, parent, false);

        return new RVChartAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RVChartAdapter.MyViewHolder holder, int position) {
        holder.tvItem.setText(chartItems.get(position).getName());

        if (posItems.size() != 0) holder.bindMultipleSelection();
        else holder.bindSingleSelection();

        String name = chartItems.get(position).getName();
        if (name.equals(context.getString(R.string.categories)) || name.equals(context.getString(R.string.qrs))) {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorGrayLight));
            holder.itemView.setClickable(false);
        }
    }

    @Override
    public int getItemCount() {
        return chartItems.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) return MAIN_ITEM_POSITION;
        return 0;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvItem;

        MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvItem = itemView.findViewById(R.id.tvItem);
            itemView.setOnClickListener(view -> {
                dataListener.sendDataToActivity(chartItems.get(getAdapterPosition()), -1, BranchChartListActivity.CHART);

                if (getItemViewType() == MAIN_ITEM_POSITION) {
                    if (posItems.size() != chartItems.size()) {
                        for (int i = 0; i < chartItems.size(); i++) {
                            posItems.add(i);
                        }
                    } else {
                        posItems.clear();
                    }
                } else if (checkedPosition != getAdapterPosition()) {
//                    notifyItemChanged(checkedPosition);
                    checkedPosition = getAdapterPosition();
                }
                notifyDataSetChanged();
            });
        }

        private void bindSingleSelection() {
            if (checkedPosition == -1) {
                itemView.setBackgroundColor(ContextCompat.getColor(context, android.R.color.white));
                tvItem.setTextColor(ContextCompat.getColor(context, R.color.colorDark));
            } else {
                if (checkedPosition == getAdapterPosition()) {
                    itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimary));
                    tvItem.setTextColor(ContextCompat.getColor(context, android.R.color.white));
                } else {
                    itemView.setBackgroundColor(ContextCompat.getColor(context, android.R.color.white));
                    tvItem.setTextColor(ContextCompat.getColor(context, R.color.colorDark));
                }
            }
        }

        private void bindMultipleSelection() {
            if (posItems.size() == chartItems.size()) {
                itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimary));
                tvItem.setTextColor(ContextCompat.getColor(context, android.R.color.white));
            } else {
                itemView.setBackgroundColor(ContextCompat.getColor(context, android.R.color.white));
                tvItem.setTextColor(ContextCompat.getColor(context, R.color.colorDark));
            }
        }
    }
}

