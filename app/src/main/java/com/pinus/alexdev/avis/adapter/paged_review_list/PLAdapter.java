package com.pinus.alexdev.avis.adapter.paged_review_list;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.pinus.alexdev.avis.R;
import com.pinus.alexdev.avis.dto.response.review_response.ReviewResponse;
import com.pinus.alexdev.avis.view.review_list_activity.InfoReviewActivity;
import com.pinus.alexdev.avis.view.review_list_activity.ReviewsListActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public class PLAdapter extends androidx.paging.PagedListAdapter<ReviewResponse, PLAdapter.MyViewHolder> {
    private Context context;

    public PLAdapter(Context context) {
        super(DIFF_CALLBACK);
        this.context = context;
    }

    private static DiffUtil.ItemCallback<ReviewResponse> DIFF_CALLBACK = new DiffUtil.ItemCallback<ReviewResponse>() {
        @Override
        public boolean areItemsTheSame(@NonNull ReviewResponse oldItem, @NonNull ReviewResponse newItem) {
            return oldItem.getBranchId() == newItem.getBranchId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull ReviewResponse oldItem, @NonNull ReviewResponse newItem) {
            return oldItem.equals(newItem);
        }
    };

    @NonNull
    @Override
    public PLAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.review_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PLAdapter.MyViewHolder holder, int position) {
        ReviewResponse review = getItem(position);
        holder.tvReviewNumber.setText(context.getString(R.string.reviewIdTitle) + Objects.requireNonNull(review).getReview().getId());

        String dataCreated = dateStringParseAndFormat(review.getReview().getDateCreated());

        holder.tvDateCreated.setText(context.getString(R.string.createdTitle) + " " + dataCreated);

        if (!review.getReview().isAnonymous()) holder.imageAuthReview.setVisibility(View.VISIBLE);
        else holder.imageAuthReview.setVisibility(View.INVISIBLE);

        //Для списка получения конверсейшенов
        //скорее всего, нужно будет вызывать isChatViewed
        if (review.getReview().isViewed()) holder.imageIsRead.setVisibility(View.INVISIBLE);
        else holder.imageIsRead.setVisibility(View.VISIBLE);

        LayerDrawable stars = (LayerDrawable) holder.ratingReview.getProgressDrawable();
        stars.getDrawable(2).setColorFilter(ContextCompat.getColor(context, R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
        holder.ratingReview.setRating((float) review.getReview().getImpression());
    }

    private String dateStringParseAndFormat(String dateString) {
        SimpleDateFormat sd1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        Date dt = null;
        try {
            dt = sd1.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        SimpleDateFormat sd2 = new SimpleDateFormat("dd/MM/yy");

        String date = sd2.format(dt);
        return date;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvReviewNumber;
        TextView tvDateCreated;
        ImageView imageAuthReview;
        ImageView imageIsRead;
        RatingBar ratingReview;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvReviewNumber = itemView.findViewById(R.id.tvReviewNumber);
            tvDateCreated = itemView.findViewById(R.id.tvDateCreated);
            imageAuthReview = itemView.findViewById(R.id.imageAuthReview);
            imageIsRead = itemView.findViewById(R.id.imageIsRead);
            ratingReview = itemView.findViewById(R.id.ratingReview);

            itemView.setOnClickListener(v -> {
                Intent intentInfo = new Intent(context, InfoReviewActivity.class);
                intentInfo.putExtra("reviewId", Objects.requireNonNull(getItem(getAdapterPosition())).getReview().getId());
                context.startActivity(intentInfo);
            });
        }
    }
}
