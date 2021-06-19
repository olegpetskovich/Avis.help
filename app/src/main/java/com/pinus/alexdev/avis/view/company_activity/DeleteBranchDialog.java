package com.pinus.alexdev.avis.view.company_activity;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.pinus.alexdev.avis.R;
import com.pinus.alexdev.avis.utils.SaveLoadData;

import java.util.Objects;

public class DeleteBranchDialog extends AppCompatDialogFragment {
    private SaveLoadData saveLoadData;

    private DeleteBranchDialogListener listener;

    public interface DeleteBranchDialogListener {
        void deleteBranch(int branchId);
        void dismissDialog();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        saveLoadData = new SaveLoadData(getContext());

        AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getActivity()));

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_delete_company, null);

        TextView tvDeletingText = view.findViewById(R.id.tvDeletingText);
        tvDeletingText.setText(tvDeletingText.getText() + " " + saveLoadData.loadString("branchNameForDeleting")); //Отображаем имя компании, ранее сохранённое в CompanyRegistrationActivity и CompanyActivity

        TextInputEditText deleteInput = view.findViewById(R.id.deleteInput);
        MaterialButton btnDismissDialog = view.findViewById(R.id.btnDismissDialog);
        btnDismissDialog.setOnClickListener(v -> listener.dismissDialog());

        MaterialButton btnConfirmDelete = view.findViewById(R.id.btnConfirmDelete);
        btnConfirmDelete.setOnClickListener(v -> {
            if (deleteInput.getText().toString().equals(getString(R.string.DELETE))) {
                listener.deleteBranch(saveLoadData.loadInt("branchIdForDeleting"));
            }
        });

        builder.setView(view);
        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (DeleteBranchDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "must implement DeleteBranchDialogListener");
        }
    }
}
