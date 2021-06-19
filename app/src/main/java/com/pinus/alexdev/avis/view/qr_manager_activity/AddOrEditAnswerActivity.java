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
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.muddzdev.styleabletoast.StyleableToast;
import com.pinus.alexdev.avis.R;
import com.pinus.alexdev.avis.utils.SaveLoadData;
import com.pinus.alexdev.avis.view.BaseToolbarBack;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.disposables.CompositeDisposable;

public class AddOrEditAnswerActivity extends BaseToolbarBack {
    private static final String TAG = AddOrEditAnswerActivity.class.getSimpleName();
    private CompositeDisposable disposable = new CompositeDisposable();

    public static final String ANSWER_KEY = "answer_key";
    public static final String ANSWERS_LIST_KEY = "answers_list_key";

    public static final int ADD_ANSWER = 1;
    public static final int EDIT_ANSWER = 2;

    @BindView(R.id.appbar_back)
    View appbar;

    @BindView(R.id.backLayout)
    LinearLayout backTo;

    @BindView(R.id.scanQrButton)
    ImageButton qrScanButton;

    @BindView(R.id.addOrEditAnswerInput)
    AppCompatEditText addOrEditAnswerInput;

    @BindView(R.id.btnDeleteAnswer)
    MaterialButton btnDeleteAnswer;

    @BindView(R.id.btnSaveAnswer)
    GradientButton btnSaveAnswer;

    Intent intent;

    //Класс для удобной работы с SharedPreferences
    SaveLoadData saveLoadData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_or_edit_answer);
        ButterKnife.bind(this);
        startScan(qrScanButton);
        setToolbarContent(getString(R.string.addAnswer));

        saveLoadData = new SaveLoadData(this);
        intent = getIntent();

        btnDeleteAnswer.setVisibility(View.GONE);
        btnSaveAnswer.getButton().setOnClickListener(v -> {
            String answerInput = addOrEditAnswerInput.getText().toString();
            if (answerInput.isEmpty()) {
                StyleableToast.makeText(getApplicationContext(), getString(R.string.toast_field_can_not_be_empty), Toast.LENGTH_SHORT, R.style.mytoast).show();
            } else {
                Gson gson = new Gson();
                ArrayList<String> answersList;
                if (saveLoadData.loadString(ANSWERS_LIST_KEY) != null && !saveLoadData.loadString(ANSWERS_LIST_KEY).isEmpty())
                    answersList = gson.fromJson(saveLoadData.loadString(ANSWERS_LIST_KEY), new TypeToken<ArrayList<String>>() {}.getType());
                else answersList = new ArrayList<>();
                answersList.add(answerInput);
                saveLoadData.saveString(ANSWERS_LIST_KEY, gson.toJson(answersList));

//                saveLoadData.saveInt("add_or_edit_answer", ADD_ANSWER);
//                saveLoadData.saveString(ANSWER_KEY, answerInput);
                StyleableToast.makeText(getApplicationContext(), getString(R.string.answer_added), Toast.LENGTH_SHORT, R.style.mytoast).show();
                finish();
            }
        });

        if (intent.getIntExtra("add_or_edit_answer", -1) == EDIT_ANSWER) {
            btnDeleteAnswer.setVisibility(View.VISIBLE);
            setToolbarContent(getString(R.string.editAnswer));

            btnDeleteAnswer.setOnClickListener(v -> {
                Gson gson = new Gson();
                ArrayList<String> answersList;
                if (saveLoadData.loadString(ANSWERS_LIST_KEY) != null && !saveLoadData.loadString(ANSWERS_LIST_KEY).isEmpty())
                    answersList = gson.fromJson(saveLoadData.loadString(ANSWERS_LIST_KEY), new TypeToken<ArrayList<String>>() {}.getType());
                else answersList = new ArrayList<>();

                answersList.remove(intent.getIntExtra("answer_position", -1));
                saveLoadData.saveString(ANSWERS_LIST_KEY, gson.toJson(answersList));

//                saveLoadData.saveInt("add_or_edit_answer", EDIT_ANSWER);
//                saveLoadData.saveInt("answer_position", intent.getIntExtra("answer_position", -1));
//                saveLoadData.saveBoolean("isForDelete", true);

                StyleableToast.makeText(getApplicationContext(), getString(R.string.answer_deleted), Toast.LENGTH_SHORT, R.style.mytoast).show();
                finish();
            });

            String answerText = intent.getStringExtra("answer_text");
            addOrEditAnswerInput.setText(answerText);
            btnSaveAnswer.getButton().setOnClickListener(v -> {
                String answerInput = addOrEditAnswerInput.getText().toString();

                if (answerInput.isEmpty()) {
                    StyleableToast.makeText(getApplicationContext(), getString(R.string.toast_field_can_not_be_empty), Toast.LENGTH_SHORT, R.style.mytoast).show();
                } else {
                    Gson gson = new Gson();
                    ArrayList<String> answersList;
                    if (saveLoadData.loadString(ANSWERS_LIST_KEY) != null || !saveLoadData.loadString(ANSWERS_LIST_KEY).isEmpty())
                        answersList = gson.fromJson(saveLoadData.loadString(ANSWERS_LIST_KEY), new TypeToken<ArrayList<String>>() {}.getType());
                    else answersList = new ArrayList<>();
                    answersList.set(intent.getIntExtra("answer_position", -1), answerInput);
                    saveLoadData.saveString(ANSWERS_LIST_KEY, gson.toJson(answersList));

//                    saveLoadData.saveInt("add_or_edit_answer", EDIT_ANSWER);
//                    saveLoadData.saveInt("answer_position", intent.getIntExtra("answer_position", -1));
//                    saveLoadData.saveString(ANSWER_KEY, answerInput);

                    StyleableToast.makeText(getApplicationContext(), getString(R.string.answer_edited), Toast.LENGTH_SHORT, R.style.mytoast).show();
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
