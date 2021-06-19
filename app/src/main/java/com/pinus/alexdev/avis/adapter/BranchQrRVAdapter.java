package com.pinus.alexdev.avis.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dagang.library.GradientButton;
import com.pinus.alexdev.avis.R;
import com.pinus.alexdev.avis.model.CtaModel;
import com.pinus.alexdev.avis.model.OpinionModel;
import com.pinus.alexdev.avis.model.QrModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class BranchQrRVAdapter extends RecyclerView.Adapter<BranchQrRVAdapter.MyViewHolder> {
    private Context context;
    private ArrayList<QrModel> qrList;

    public BranchQrRVAdapter(Context context, ArrayList<QrModel> qrList) {
        this.context = context;
        this.qrList = qrList;
    }

    private SaveImageListener saveImageListener;

    public interface SaveImageListener{
        void downloadImage(String imageUrl, String imageName);
    }

    public void setSaveImageListener(SaveImageListener saveImageListener) {
        this.saveImageListener = saveImageListener;
    }

    @NonNull
    @Override
    public BranchQrRVAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_branch_qr, parent, false);
        return new BranchQrRVAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BranchQrRVAdapter.MyViewHolder holder, int position) {
        QrModel qrModel = qrList.get(position);
        if (qrModel instanceof CtaModel) {
            CtaModel model = (CtaModel) qrModel;
            holder.tvQrType.setText(model.getQrCode().getEndpointType());
            Picasso.get().load(model.getQrCode().getImageUrl()).placeholder(R.drawable.qr).error(R.drawable.qr).into(holder.qrImage);
            holder.tvQrCode.setText(model.getQrCode().getHumanReadableId());

            if (model.getQrCode().getImageUrl() == null || model.getQrCode().getImageUrl().isEmpty()) {
                holder.btnDownload.getButton().setClickable(false);
            }
        }
        if (qrModel instanceof OpinionModel) {
            OpinionModel model = (OpinionModel) qrModel;
            holder.tvQrType.setText(model.getQrCode().getEndpointType());
            Picasso.get().load(model.getQrCode().getImageUrl()).placeholder(R.drawable.qr).error(R.drawable.qr).into(holder.qrImage);
            holder.tvQrCode.setText(model.getQrCode().getHumanReadableId());

            if (model.getQrCode().getImageUrl() == null || model.getQrCode().getImageUrl().isEmpty()) {
                holder.btnDownload.getButton().setClickable(false);
            }
        }
    }

    @Override
    public int getItemCount() {
        return qrList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvQrType;
        ImageView qrImage;
        TextView tvQrCode;
        GradientButton btnDownload;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvQrType = itemView.findViewById(R.id.tvQrType);
            qrImage = itemView.findViewById(R.id.qrImage);
            tvQrCode = itemView.findViewById(R.id.tvQrCode);

            btnDownload = itemView.findViewById(R.id.btnDownload);
            btnDownload.getButton().setOnClickListener(v -> {
                if (qrList.get(getAdapterPosition()) instanceof CtaModel) {
                    String imageUrl = ((CtaModel) qrList.get(getAdapterPosition())).getQrCode().getImageUrl();
                    String imageName = "image_qr_" + ((CtaModel) qrList.get(getAdapterPosition())).getQrCode().getEndpointId();
                    if (imageUrl != null && !imageUrl.isEmpty()) saveImageListener.downloadImage(imageUrl, imageName);
                    else return;
                }
                if (qrList.get(getAdapterPosition()) instanceof OpinionModel) {
                    String imageUrl = ((OpinionModel) qrList.get(getAdapterPosition())).getQrCode().getImageUrl();
                    String imageName = "image_qr_" + ((OpinionModel) qrList.get(getAdapterPosition())).getQrCode().getEndpointId();
                    if (imageUrl != null && !imageUrl.isEmpty()) saveImageListener.downloadImage(imageUrl, imageName);
                    else return;
                }
            });
        }
    }
}
