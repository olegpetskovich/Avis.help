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
import com.pinus.alexdev.avis.callback.IBranchFilterListener;
import com.pinus.alexdev.avis.dto.response.BranchesResponse;
import com.pinus.alexdev.avis.model.BranchModel;

import java.util.ArrayList;
import java.util.List;

public class BranchFilterRVAdapter extends RecyclerView.Adapter<BranchFilterRVAdapter.MyViewHolder> {
    private final short MAIN_ITEM_POSITION = 7;

    private final Context context;
    private final List<BranchesResponse> branches;
    private List<Integer> posAllSelectedItems = new ArrayList<>();

    private ArrayList<BranchModel> selectedBranches;

    public BranchFilterRVAdapter(Context context, List<BranchesResponse> branches) {
        this.context = context;
        this.branches = branches;
        selectedBranches = new ArrayList<>();
    }

    private IBranchFilterListener branchFilterListener;

    public void setBranchFilterListener(IBranchFilterListener branchFilterListener) {
        this.branchFilterListener = branchFilterListener;
    }

    @NonNull
    @Override
    public BranchFilterRVAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == MAIN_ITEM_POSITION)
            view = inflater.inflate(R.layout.item_main, parent, false);
        else view = inflater.inflate(R.layout.item_rest, parent, false);

        return new BranchFilterRVAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BranchFilterRVAdapter.MyViewHolder holder, int position) {
        holder.tvItem.setText(branches.get(position).getName());

        if (posAllSelectedItems.size() != 0) holder.bindAllItemsSelected();
        else holder.bindMultipleSelection();
    }

    @Override
    public int getItemCount() {
        return branches.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) return MAIN_ITEM_POSITION;
        return 0;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvItem;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvItem = itemView.findViewById(R.id.tvItem);
            itemView.setOnClickListener(v -> {
                if (!branches.get(getAdapterPosition()).isClicked()) {
                    branches.get(getAdapterPosition()).setClicked(true);
                    if (getItemViewType() == MAIN_ITEM_POSITION) {
                        selectedBranches.clear(); //Очищаем, чтобы в этот список не были добавлены ранее нажатые отдельные айтемы
                        //Добавляем все бранчи в список если выбран пункт "Все филлиалы"
                        for (int i = 0; i < branches.size(); i++) {
                            if (i == 0) {
                                String name = branches.get(i).getName();
                                continue; //Пропускаем добавление первого айтема, потому что первый айтем это кнопка "Все филиалы", а не какой-то конкретный бранч.
                            }
                            BranchModel model = new BranchModel();
                            model.setBranchName(branches.get(i).getName());
                            model.setBranchID(branches.get(i).getId());
                            selectedBranches.add(model);
                        }
                        if (posAllSelectedItems.size() != branches.size()) {
                            for (int i = 0; i < branches.size(); i++) {
                                posAllSelectedItems.add(i);
                            }
                        }
                    } else {
                        branches.get(getAdapterPosition()).setItemPosition(getAdapterPosition());
                        BranchModel model = new BranchModel();
                        model.setBranchName(branches.get(getAdapterPosition()).getName());
                        model.setBranchID(branches.get(getAdapterPosition()).getId());
                        selectedBranches.add(model);
                    }
                } else {
                    branches.get(getAdapterPosition()).setClicked(false);
                    if (getItemViewType() == MAIN_ITEM_POSITION) {
                        selectedBranches.clear();
                        posAllSelectedItems.clear();
                        for (BranchesResponse model : branches) {
                            model.setClicked(false);
                        }
                    } else {
                        branches.get(getAdapterPosition()).setItemPosition(-1); // -1 выступает выражением, означающим, что айтем не выбран. То есть при повторном нажатии на него выделение убирается и устанавливается это значение
//                        ПО КАКОЙ_ТО ПРИЧИНЕ НЕ УДАЛЯЕТСЯ АЙТЕМ ИЗ СПИСКА, КОГДА ЕГО ОТКЛЮЧАЮТ, Я МОГУ НЕСКОЛЬКО РАЗ ДОБАВИТЬ И ОТКЛЮЧИТЬ ЕГО, А ОН НЕ УДАЛИТСЯ ИЗ СПИСКА
                        BranchModel model = new BranchModel();
                        model.setBranchName(branches.get(getAdapterPosition()).getName());
                        model.setBranchID(branches.get(getAdapterPosition()).getId());
                        selectedBranches.remove(model);
                    }
                }
                branchFilterListener.sendDataToActivity(selectedBranches);
                notifyDataSetChanged();
            });
        }

        private void bindAllItemsSelected() {
            if (posAllSelectedItems.size() == branches.size()) {
                itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimary));
                tvItem.setTextColor(ContextCompat.getColor(context, android.R.color.white));
            } else {
                itemView.setBackgroundColor(ContextCompat.getColor(context, android.R.color.white));
                tvItem.setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));
            }
        }

        private void bindMultipleSelection() {
            if (branches.get(getAdapterPosition()).getItemPosition() == getAdapterPosition() && branches.get(getAdapterPosition()).isClicked()) {
                itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimary));
                tvItem.setTextColor(ContextCompat.getColor(context, android.R.color.white));
            } else {
                itemView.setBackgroundColor(ContextCompat.getColor(context, android.R.color.white));
                tvItem.setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));
            }
        }
    }
}
