package com.pinus.alexdev.avis.adapter;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.pinus.alexdev.avis.R;
import com.pinus.alexdev.avis.dto.response.ConversationResponse;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

public class ConversationsAdapter extends ArrayAdapter<ConversationResponse> {
    private static final String TAG = ConversationsAdapter.class.getSimpleName();
    private Context mContext;

    public ConversationsAdapter(@NonNull Context context, @NonNull ArrayList<ConversationResponse> review) {
        super(context, 0, review);
        this.mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        ConversationResponse chatData = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.review_item, parent, false);
        }
        // Lookup view for data population
        TextView tvReviewNumber = convertView.findViewById(R.id.tvReviewNumber);
        RatingBar ratingReview = convertView.findViewById(R.id.ratingReview);
        if (chatData.getInvitationPhoneNumber() == null) {
            tvReviewNumber.setText(mContext.getString(R.string.reviewIdTitle) + Objects.requireNonNull(chatData).getReviewId());

            LayerDrawable stars = (LayerDrawable) ratingReview.getProgressDrawable();
            stars.getDrawable(2).setColorFilter(ContextCompat.getColor(mContext, R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
            ratingReview.setRating((float) chatData.getImpression());
        } else {
            tvReviewNumber.setText(mContext.getString(R.string.chat_with) + " " + Objects.requireNonNull(chatData).getInvitationPhoneNumber());
            ratingReview.setVisibility(View.INVISIBLE);
        }

        String dataCreated = dateStringParseAndFormat(chatData.getCreationDate());
        TextView tvDateCreated = convertView.findViewById(R.id.tvDateCreated);
        tvDateCreated.setText(mContext.getString(R.string.createdTitle) + " " + dataCreated);

        ImageView imageAuthReview = convertView.findViewById(R.id.imageAuthReview);
        imageAuthReview.setVisibility(View.INVISIBLE);

        ImageView imageIsRead = convertView.findViewById(R.id.imageIsRead);

        if (chatData.isHasNewMessages()) imageIsRead.setVisibility(View.VISIBLE);
        else imageIsRead.setVisibility(View.INVISIBLE);

        return convertView;
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
}
