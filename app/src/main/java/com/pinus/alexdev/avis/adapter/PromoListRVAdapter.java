package com.pinus.alexdev.avis.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.pinus.alexdev.avis.R;
import com.pinus.alexdev.avis.dto.response.promo_code_response.PromoCodeResponse;
import com.pinus.alexdev.avis.network.apiServices.PromoCodeApiService;
import com.pinus.alexdev.avis.utils.FontManager;
import com.pinus.alexdev.avis.view.promo_code_activity.SendPromoCodeActivity;

import java.util.ArrayList;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class PromoListRVAdapter extends RecyclerView.Adapter<PromoListRVAdapter.MyViewHolder> {
    private static final String TAG = "PromoListRVAdapter";
    private Context context;
    private ArrayList<PromoCodeResponse> promoCodesList;

    public PromoListRVAdapter(Context context, ArrayList<PromoCodeResponse> promoCodesList) {
        this.context = context;
        this.promoCodesList = promoCodesList;
    }

    @NonNull
    @Override
    public PromoListRVAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.promo_code_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PromoListRVAdapter.MyViewHolder holder, int position) {
        PromoCodeResponse promoCode = promoCodesList.get(position);

        holder.imagePromo.setText(promoCode.getIconId());
        holder.tvPromoCode.setText(promoCode.getName());
        holder.tvLifeTime.setText(context.getString(R.string.validPromoTitle) + " " + promoCode.getLifeTime() + " " + context.getString(R.string.daysTitle));

        Typeface iconFont = FontManager.getTypeface(context, FontManager.FONTAWESOME);
        FontManager.markAsIconContainer(holder.imagePromo, iconFont);

    }

    @Override
    public int getItemCount() {
        return promoCodesList.size();
    }

    @SuppressLint("CheckResult")
    public void removeItem(int organizationId, int promoID, int position, PromoCodeApiService promoCodeApiService) {
        promoCodeApiService
                .deletePromo(organizationId, promoID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> {
                    promoCodesList.remove(position);
                    notifyItemRemoved(position);
                }, e -> Log.e(TAG, "onError: " + e.getMessage()));
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public RelativeLayout itemBackground;

        public RelativeLayout itemForeground;
        TextView imagePromo;
        TextView tvPromoCode;
        TextView tvLifeTime;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            itemBackground = itemView.findViewById(R.id.itemBackground);

            itemForeground = itemView.findViewById(R.id.itemForeground);
            imagePromo = itemView.findViewById(R.id.imagePromo);
            tvPromoCode = itemView.findViewById(R.id.tvPromoCode);
            tvLifeTime = itemView.findViewById(R.id.tvLifeTime);

            itemView.setOnClickListener(v -> {
                String jsonPromoCode = new Gson().toJson(promoCodesList.get(getAdapterPosition()));
                Intent intent = new Intent(context, SendPromoCodeActivity.class);
                intent.putExtra("promoCodeJson", jsonPromoCode);
                context.startActivity(intent);
            });
        }
    }
}
