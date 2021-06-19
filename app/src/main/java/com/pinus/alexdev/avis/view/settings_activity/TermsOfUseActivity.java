package com.pinus.alexdev.avis.view.settings_activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.pinus.alexdev.avis.R;
import com.pinus.alexdev.avis.view.BaseToolbarBack;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TermsOfUseActivity extends BaseToolbarBack {
    @BindView(R.id.appbar_back)
    View appbar;

    @BindView(R.id.backLayout)
    LinearLayout backTo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms_of_use);
        ButterKnife.bind(this);
        setButtonBackClick(
                appbar.findViewById(R.id.appBarTitle),
                getResources().getString(R.string.termsConditions),
                appbar.findViewById(R.id.logoImageBack),
                backTo,
                appbar.findViewById(R.id.toolbarPreviosActivityTitle),
                "",
                null
        );
    }
}
