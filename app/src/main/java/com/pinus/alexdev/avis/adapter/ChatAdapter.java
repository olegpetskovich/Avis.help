package com.pinus.alexdev.avis.adapter;

import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pinus.alexdev.avis.R;
import com.pinus.alexdev.avis.enums.SenderChatEnums;
import com.pinus.alexdev.avis.model.ChatLocal;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.SimpleViewHolder> {
    private static final int VIEW_TYPE_0 = 0;
    private static final int VIEW_TYPE_1 = 1;
    private final List<ChatLocal> mDataSet;

    public ChatAdapter(List<ChatLocal> dataSet) {
        mDataSet = dataSet;
    }

    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType != VIEW_TYPE_0) {
            return new SimpleViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.conversation_in, parent, false));
        }
        return new SimpleViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.conversation_out, parent, false));
    }

    @Override
    public void onBindViewHolder(@NotNull SimpleViewHolder holder, int position) {
        holder.mTextView.setText(mDataSet.get(position).getMessage());
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    @Override
    public long getItemId(int position) {
        return mDataSet.get(position).hashCode();
    }

    @Override
    public int getItemViewType(int position) {
        if (mDataSet.get(position).getSender().equals(SenderChatEnums.YOUR)) {
            return VIEW_TYPE_0;
        }
        return VIEW_TYPE_1;
    }

    static class SimpleViewHolder extends RecyclerView.ViewHolder {

        final TextView mTextView;

        public SimpleViewHolder(View itemView) {
            super(itemView);
            mTextView = itemView.findViewById(R.id.msg);
        }
    }
}