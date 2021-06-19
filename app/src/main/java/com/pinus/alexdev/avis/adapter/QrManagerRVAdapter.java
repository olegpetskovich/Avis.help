package com.pinus.alexdev.avis.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.muddzdev.styleabletoast.StyleableToast;
import com.pinus.alexdev.avis.R;
import com.pinus.alexdev.avis.dto.response.qr_manager_list_response.cta_response.CTAResponse;
import com.pinus.alexdev.avis.model.CtaModel;
import com.pinus.alexdev.avis.model.OpinionModel;
import com.pinus.alexdev.avis.model.QrModel;
import com.pinus.alexdev.avis.network.apiServices.QrManagerApiService;
import com.pinus.alexdev.avis.utils.SaveLoadData;
import com.pinus.alexdev.avis.view.qr_manager_activity.QrInfoActivity;

import java.util.ArrayList;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static com.pinus.alexdev.avis.view.LoginActivity.ORGANIZATION_ID_KEY;

public class QrManagerRVAdapter extends RecyclerView.Adapter<QrManagerRVAdapter.MyViewHolder> {
    private Context context;
    private ArrayList<QrModel> qrList;

    public QrManagerRVAdapter(Context context, ArrayList<QrModel> qrList) {
        this.context = context;
        this.qrList = qrList;
    }

    @NonNull
    @Override
    public QrManagerRVAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_qr_manager, parent, false);
        return new QrManagerRVAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QrManagerRVAdapter.MyViewHolder holder, int position) {
        QrModel qrModel = qrList.get(position);
        if (qrModel instanceof CtaModel) {
            CtaModel model = (CtaModel) qrModel;
            holder.tvQrName.setText(model.getName());
            holder.tvQrType.setText(model.getQrCode().getEndpointType());
        }
        if (qrModel instanceof OpinionModel) {
            OpinionModel model = (OpinionModel) qrModel;
            holder.tvQrName.setText(model.getName());
            holder.tvQrType.setText(model.getQrCode().getEndpointType());
        }
    }

    @Override
    public int getItemCount() {
        return qrList.size();
    }

    @SuppressLint("CheckResult")
    public void removeItem(QrModel deletedItem, int position, QrManagerApiService qrManagerApiService) {
        int organizationID = new SaveLoadData(context).loadInt(ORGANIZATION_ID_KEY);
        if (deletedItem instanceof CtaModel) {
            CtaModel model = (CtaModel) deletedItem;
            qrManagerApiService
                    .deleteQrCta(model.getBranchId(), model.getId(), organizationID)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(s -> {
                        qrList.remove(position);
                        notifyItemRemoved(position);
                        StyleableToast.makeText(context, context.getString(R.string.qrDeleted), Toast.LENGTH_LONG, R.style.mytoast).show();
                    }, e -> Log.e("QrOnError", "OnError: " + e.getMessage()));
        }
        if (deletedItem instanceof OpinionModel) {
            OpinionModel model = (OpinionModel) deletedItem;
            qrManagerApiService
                    .deleteQrOpinion(model.getBranchId(), model.getId(), organizationID)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(s -> {
                        qrList.remove(position);
                        notifyItemRemoved(position);
                        StyleableToast.makeText(context, context.getString(R.string.qrDeleted), Toast.LENGTH_LONG, R.style.mytoast).show();
                    }, e -> Log.e("QrOnError", "OnError: " + e.getMessage()));
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public RelativeLayout itemBackground;
        public RelativeLayout itemForeground;

        TextView tvQrName;
        TextView tvQrType;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            itemBackground = itemView.findViewById(R.id.itemBackground);
            itemForeground = itemView.findViewById(R.id.itemForeground);

            tvQrName = itemView.findViewById(R.id.tvQrName);
            tvQrType = itemView.findViewById(R.id.tvQrCategory);

            itemView.setOnClickListener(v -> {
                CtaModel ctaModel;
                OpinionModel opinionModel;
                String qrManagerJson = null;

                Intent intent = new Intent(context, QrInfoActivity.class);
                if (qrList.get(getAdapterPosition()) instanceof CtaModel) {
                    ctaModel = (CtaModel) qrList.get(getAdapterPosition());
                    qrManagerJson = new Gson().toJson(ctaModel);
                    intent.putExtra("qrType", QrInfoActivity.CTA_QR_TYPE);
                } else if (qrList.get(getAdapterPosition()) instanceof OpinionModel) {
                    opinionModel = (OpinionModel) qrList.get(getAdapterPosition());
                    intent.putExtra("qrType", QrInfoActivity.OPINION_QR_TYPE);
                    qrManagerJson = new Gson().toJson(opinionModel);
                }
                intent.putExtra("qrManagerJson", qrManagerJson);
                context.startActivity(intent);
            });
        }
    }
}
