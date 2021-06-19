package com.pinus.alexdev.avis.view.promo_code_activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.core.content.ContextCompat;

import android.view.View;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dagang.library.GradientButton;
import com.pinus.alexdev.avis.R;
import com.pinus.alexdev.avis.adapter.IconAdapter;
import com.pinus.alexdev.avis.view.BaseToolbarBack;

import butterknife.BindView;
import butterknife.ButterKnife;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class IconsActivity extends BaseToolbarBack {
    private static final String TAG = IconsActivity.class.getSimpleName();

    @BindView(R.id.appbar_back)
    View appbar;

    @BindView(R.id.scanQrButton)
    ImageButton qrScanButton;

  /*  @BindView(R.id.scanQrButton)
    ImageButton qrScanButton;*/

    @BindView(R.id.backLayout)
    LinearLayout backTo;

    @BindView(R.id.btnSaveIcon)
    GradientButton btnSaveIcon;

    TextView icon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_icons);
        ButterKnife.bind(this);
        startScan(qrScanButton);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Roboto-RobotoRegular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        setButtonBackClick(
                appbar.findViewById(R.id.appBarTitle),
                getResources().getString(R.string.selectIcon),
                appbar.findViewById(R.id.logoImageBack),
                backTo,
                appbar.findViewById(R.id.toolbarPreviosActivityTitle),
                "",
                null
        );

        IconAdapter iconsAdapter = new IconAdapter(this);
        GridView gridView = findViewById(R.id.iconGridView);
        gridView.setAdapter(iconsAdapter);
        gridView.setOnItemClickListener((parent, v, position, id) -> {
            if (icon == null) {
                icon = v.findViewById(R.id.iconText);
            } else {
                if (!icon.getText().toString().equals(((TextView) v.findViewById(R.id.iconText)).getText().toString())) {
                    icon.setTextColor(ContextCompat.getColor(this, R.color.colorDark));
                    icon = v.findViewById(R.id.iconText);
                }
            }
            icon.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));

        });

        btnSaveIcon.getButton().setOnClickListener(v -> {
            String iconText = getString(R.string._23);
            if (icon != null) iconText = icon.getText().toString();

            Intent returnIntent = new Intent();
            Toast.makeText(this, "" + iconText, Toast.LENGTH_SHORT).show();
            returnIntent.putExtra("resultIconId", iconText);
            setResult(Activity.RESULT_OK, returnIntent);
            finish();
        });
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
