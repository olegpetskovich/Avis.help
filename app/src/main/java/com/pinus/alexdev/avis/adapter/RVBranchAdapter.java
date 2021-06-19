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
import com.pinus.alexdev.avis.dto.response.BranchesItem;
import com.pinus.alexdev.avis.dto.response.BranchesResponse;
import com.pinus.alexdev.avis.model.ChartItemModel;
import com.pinus.alexdev.avis.view.BranchChartListActivity;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.view.Chart;

public class RVBranchAdapter extends RecyclerView.Adapter<RVBranchAdapter.MyViewHolder> {
    private final short MAIN_ITEM_POSITION = 7;

    private Context context;
    private List<BranchesResponse> branchesItems;
    private List<Integer> posItems = new ArrayList<>();
    private int checkedPosition = -1;

    public RVBranchAdapter(Context context, List<BranchesResponse> branchesItems) {
        this.context = context;
        this.branchesItems = branchesItems;
    }

    private DataListener dataListener;

    public void setDataListener(DataListener dataListener) {
        this.dataListener = dataListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == MAIN_ITEM_POSITION)
            view = inflater.inflate(R.layout.item_main, parent, false);
        else view = inflater.inflate(R.layout.item_rest, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.tvItem.setText(branchesItems.get(position).getName());

        if (posItems.size() != 0) holder.bindMultipleSelection();
        else holder.bindSingleSelection();

    }

    @Override
    public int getItemCount() {
        return branchesItems.size();
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
                long id = branchesItems.get(getAdapterPosition()).getId();

                ChartItemModel model = new ChartItemModel();
                model.setName(branchesItems.get(getAdapterPosition()).getName());
                model.setOpinionId(-1);

                dataListener.sendDataToActivity(model, id, BranchChartListActivity.BRANCH);
                if (getItemViewType() == MAIN_ITEM_POSITION) {
                    if (posItems.size() != branchesItems.size()) {
                        for (int i = 0; i < branchesItems.size(); i++) {
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
                tvItem.setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));
            } else {
                if (checkedPosition == getAdapterPosition()) {
                    itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimary));
                    tvItem.setTextColor(ContextCompat.getColor(context, android.R.color.white));
                } else {
                    itemView.setBackgroundColor(ContextCompat.getColor(context, android.R.color.white));
                    tvItem.setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));
                }
            }
        }

        private void bindMultipleSelection() {
            if (posItems.size() == branchesItems.size()) {
                itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimary));
                tvItem.setTextColor(ContextCompat.getColor(context, android.R.color.white));
            } else {
                itemView.setBackgroundColor(ContextCompat.getColor(context, android.R.color.white));
                tvItem.setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));
            }
        }
    }
}
