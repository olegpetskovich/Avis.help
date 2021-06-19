package com.pinus.alexdev.avis.view.review_list_activity;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.google.android.material.button.MaterialButton;
import com.pinus.alexdev.avis.R;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class SortReviewDialog extends AppCompatDialogFragment {
    public static final int SORT_BY_NUMBER = 1;
    public static final int SORT_BY_DATE = 2;
    public static final int SORT_BY_RATING = 3;
    public static final int SORT_BY_AUTHORIZATION = 4;

    private SortReviewDialogListener listener;

    public interface SortReviewDialogListener {
        void sendChosenBtnData(int typeOfSort);
    }

    @NotNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getActivity()));

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.sort_review_dialog, null);

        MaterialButton btnNumber = view.findViewById(R.id.btnNumber);
        btnNumber.setOnClickListener(v -> listener.sendChosenBtnData(SORT_BY_NUMBER));

        MaterialButton btnDate = view.findViewById(R.id.btnDate);
        btnDate.setOnClickListener(v -> listener.sendChosenBtnData(SORT_BY_DATE));

        MaterialButton btnRating = view.findViewById(R.id.btnRating);
        btnRating.setOnClickListener(v -> listener.sendChosenBtnData(SORT_BY_RATING));

        MaterialButton btnAuthorization = view.findViewById(R.id.btnAuthorization);
        btnAuthorization.setOnClickListener(v -> listener.sendChosenBtnData(SORT_BY_AUTHORIZATION));

        builder.setView(view);
        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (SortReviewDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "must implement SortReviewDialogListener");
        }
    }
}
