package com.pinus.alexdev.avis.view.qr_manager_activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatEditText;

import com.dagang.library.GradientButton;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.muddzdev.styleabletoast.StyleableToast;
import com.pinus.alexdev.avis.R;
import com.pinus.alexdev.avis.utils.SaveLoadData;
import com.pinus.alexdev.avis.view.BaseToolbarBack;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.disposables.CompositeDisposable;

public class AddOrEditCategoryActivity extends BaseToolbarBack {
    private static final String TAG = AddOrEditCategoryActivity.class.getSimpleName();
    private CompositeDisposable disposable = new CompositeDisposable();

    public static final String CATEGORY_KEY = "category_key";

    public static final int ADD_CATEGORY= 1;
    public static final int EDIT_CATEGORY = 2;

    @BindView(R.id.appbar_back)
    View appbar;

    @BindView(R.id.backLayout)
    LinearLayout backTo;

    @BindView(R.id.scanQrButton)
    ImageButton qrScanButton;

    @BindView(R.id.addOrEditCategoryInput)
    AppCompatEditText addOrEditCategoryInput;

    @BindView(R.id.btnDeleteCategory)
    MaterialButton btnDeleteCategory;

    @BindView(R.id.btnSaveCategory)
    GradientButton btnSaveCategory;

    Intent intent;

    //Класс для удобной работы с SharedPreferences
    SaveLoadData saveLoadData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_or_edit_category);
        ButterKnife.bind(this);
        startScan(qrScanButton);
        setToolbarContent(getString(R.string.addCategory));

        saveLoadData = new SaveLoadData(this);
        intent = getIntent();

        btnDeleteCategory.setVisibility(View.GONE);
        btnSaveCategory.getButton().setOnClickListener(v -> {
            String answerInput = addOrEditCategoryInput.getText().toString();
            if (answerInput.isEmpty()) {
                StyleableToast.makeText(getApplicationContext(), getString(R.string.toast_field_can_not_be_empty), Toast.LENGTH_SHORT, R.style.mytoast).show();
            } else {
                saveLoadData.saveInt("add_or_edit_category", ADD_CATEGORY);
                saveLoadData.saveString(CATEGORY_KEY, answerInput);

                finish();
            }
        });

        if (intent.getIntExtra("add_or_edit_category", -1) == EDIT_CATEGORY) {
            btnDeleteCategory.setVisibility(View.VISIBLE);
            setToolbarContent(getString(R.string.editCategory));

            btnDeleteCategory.setOnClickListener(v -> {
                saveLoadData.saveInt("add_or_edit_category", EDIT_CATEGORY);
                saveLoadData.saveBoolean("isForDelete", true);
                finish();
            });

            String answerText = intent.getStringExtra("category_text");
            addOrEditCategoryInput.setText(answerText);
            btnSaveCategory.getButton().setOnClickListener(v -> {
                String answerInput = addOrEditCategoryInput.getText().toString();
                if (answerInput.isEmpty()) {
                    StyleableToast.makeText(getApplicationContext(), getString(R.string.toast_field_can_not_be_empty), Toast.LENGTH_SHORT, R.style.mytoast).show();
                } else {
                    saveLoadData.saveInt("add_or_edit_category", EDIT_CATEGORY);
                    saveLoadData.saveInt("category_position", intent.getIntExtra("category_position", -1));
                    saveLoadData.saveString(CATEGORY_KEY, answerInput);
                    finish();
                }
            });
        }
    }

    private void setToolbarContent(String titleText) {
        setButtonBackClick(
                appbar.findViewById(R.id.appBarTitle),
                titleText,
                appbar.findViewById(R.id.logoImageBack),
                backTo,
                appbar.findViewById(R.id.toolbarPreviosActivityTitle),
                "",
                null
        );
    }
}
