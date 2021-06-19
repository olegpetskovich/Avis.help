package com.pinus.alexdev.avis.view.cta_activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.vision.text.Text;
import com.pinus.alexdev.avis.R;
import com.pinus.alexdev.avis.view.BaseToolbarBack;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CTAInfoActivity extends BaseToolbarBack {
    @BindView(R.id.appbar_back)
    View appbar;
    @BindView(R.id.backLayout)
    LinearLayout backTo;

    @BindView(R.id.scanQrButton)
    ImageButton qrScanButton;

    Intent intent;

    @BindView(R.id.tvQuestion)
    TextView tvQuestion;
    @BindView(R.id.tvAnswer)
    TextView tvAnswer;
    @BindView(R.id.tvClickedCount)
    TextView tvClickedCount;
    @BindView(R.id.tvReactionTime)
    TextView tvReactionTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ctainfo);
        ButterKnife.bind(this);
        startScan(qrScanButton);

        intent = getIntent();
        String qrName = intent.getStringExtra("cta_qr_name");

        String qrQuestionText = intent.getStringExtra("cta_qr_question_text");
        tvQuestion.setText(qrQuestionText);

        String qrAnswerName = intent.getStringExtra("cta_qr_answer_name");
        tvAnswer.setText(qrAnswerName);

        String qrClickedCount = intent.getStringExtra("cta_qr_clicked_count");
        tvClickedCount.setText(qrClickedCount);

        String qrReactionTime = intent.getStringExtra("cta_qr_reaction_time");
        tvReactionTime.setText(qrReactionTime);

        setButtonBackClick(
                appbar.findViewById(R.id.appBarTitle),
                qrName,
                appbar.findViewById(R.id.logoImageBack),
                backTo,
                appbar.findViewById(R.id.toolbarPreviosActivityTitle),
                "",
                null
        );
    }
}
