package com.pinus.alexdev.avis.view.settings_activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.pinus.alexdev.avis.Helper.LocaleHelper;
import com.pinus.alexdev.avis.R;
import com.pinus.alexdev.avis.utils.SaveLoadData;
import com.pinus.alexdev.avis.view.BaseToolbarBack;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.paperdb.Paper;

public class LanguagesActivity extends BaseToolbarBack {
    @BindView(R.id.appbar_back)
    View appbar;
    @BindView(R.id.backLayout)
    LinearLayout backTo;

    @BindView(R.id.scanQrButton)
    ImageButton qrScanButton;

    @BindView(R.id.englishLanguageLayout)
    LinearLayout englishLayout;

    @BindView(R.id.frenchLanguageLayout)
    LinearLayout frenchLayout;

    @BindView(R.id.ukraineLanguageLayout)
    LinearLayout ukraineLayout;

    @BindView(R.id.rusLanguageLayout)
    LinearLayout rusLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_languages);
        ButterKnife.bind(this);
        setUnderline();
        startScan(qrScanButton);
        setButtonBackClick(
                appbar.findViewById(R.id.appBarTitle),
                getResources().getString(R.string.languageTitle),
                appbar.findViewById(R.id.logoImageBack),
                backTo,
                appbar.findViewById(R.id.toolbarPreviosActivityTitle),
                "",
                null
        );

        Paper.init(this);

        String language = Paper.book().read(("language"));
        if (language == null)
            Paper.book().write("language", "en");

        englishLayout.setOnClickListener(view -> {
            Paper.book().write("language", "en");
            UpdateView(Paper.book().read("language"));
            // UpdateView("en");
        });

        frenchLayout.setOnClickListener(view -> {
            Paper.book().write("language", "fr");
            UpdateView(Paper.book().read("language"));
        });

        ukraineLayout.setOnClickListener(view -> {
            Paper.book().write("language", "uk");
            UpdateView(Paper.book().read("language"));
        });

        rusLayout.setOnClickListener(view -> {
            Paper.book().write("language", "ru");
            UpdateView(Paper.book().read("language"));
        });
    }

    private void UpdateView(String language) {
        Context context = LocaleHelper.setLocale(this, language);
        context.getResources();
        System.out.println(language);
        setUnderline();
        reStart();
        overridePendingTransition(0, 0);
    }

    public void reStart() {
        //Из-за нюансов с мультиязычностью нужно устанавливать дефолтные значения с новым переводом  в HomeActivity при смене языка.
        //Они устанавливаются в onCreate, но для того, чтобы дефолтные значения не устанавлилась тогда, когда не надо,
        //нужно оповещать активити о том, что приложение с новым языком перезапущено и тогда мы устанавливаем дефолтные значения.
        //В строке ниже мы устанавливаем значение в SharedPreferences о том, что приложение перезапущено. - new SaveLoadData(this).saveBoolean("restarting_state", true);
        //В HomeActivity в методе onCreate мы достаём это значение и проверяем: если приложение перезапущено (true), то мы устанавливаем дефолтные значения В НОВОМ ПЕРЕВОДЕ
        new SaveLoadData(this).saveBoolean("restarting_state", true);
        startActivity(new Intent(this, SettingsActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
    }

    private void setUnderline() {
        List<TextView> viewList = new ArrayList<>();
        viewList.add(findViewById(R.id.englishText));
        viewList.add(findViewById(R.id.frenchText));
        viewList.add(findViewById(R.id.ukrText));
        viewList.add(findViewById(R.id.rusText));

        for (TextView textView : viewList) {
            if (!textView.getTag().equals(Locale.getDefault().getLanguage())) {
                textView.setTypeface(textView.getTypeface(), Typeface.NORMAL);
                textView.setTextColor(ContextCompat.getColor(this, android.R.color.darker_gray));
            } else {
                textView.setTypeface(textView.getTypeface(), Typeface.BOLD);
                textView.setTextColor(ContextCompat.getColor(this, R.color.colorDark));
            }
        }
    }
}
