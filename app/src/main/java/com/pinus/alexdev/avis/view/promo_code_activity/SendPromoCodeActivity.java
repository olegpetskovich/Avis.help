package com.pinus.alexdev.avis.view.promo_code_activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dagang.library.GradientButton;
import com.google.gson.Gson;
import com.hbb20.CountryCodePicker;
import com.muddzdev.styleabletoast.StyleableToast;
import com.pinus.alexdev.avis.Helper.LocaleHelper;
import com.pinus.alexdev.avis.R;
import com.pinus.alexdev.avis.dto.request.PromoSmsRequest;
import com.pinus.alexdev.avis.dto.response.promo_code_response.PromoCodeResponse;
import com.pinus.alexdev.avis.network.ApiClient;
import com.pinus.alexdev.avis.network.apiServices.PromoCodeApiService;
import com.pinus.alexdev.avis.utils.FontManager;
import com.pinus.alexdev.avis.utils.SaveLoadData;
import com.pinus.alexdev.avis.utils.Utils;
import com.pinus.alexdev.avis.view.BaseToolbarBack;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

import static com.pinus.alexdev.avis.view.LoginActivity.ORGANIZATION_ID_KEY;

public class SendPromoCodeActivity extends BaseToolbarBack {
    private static final String TAG = SendPromoCodeActivity.class.getSimpleName();
    private CompositeDisposable disposable = new CompositeDisposable();

    @BindView(R.id.appbar_back)
    View appbar;

    @BindView(R.id.backLayout)
    LinearLayout backTo;

    @BindView(R.id.promoCodeTitle)
    TextView promoCodeTitle;

    @BindView(R.id.promoDescription)
    TextView promoDescription;

    @BindView(R.id.iconText)
    TextView iconText;

//    @BindView(R.id.descriptionTitle)
//    TextView descriptionTitle;

    @BindView(R.id.dueDateTitle)
    TextView dueDateTitle;

    @BindView(R.id.deletePromoButton)
    ImageView deletePromoButton;

    @BindView(R.id.btnSendPhoneNumber)
    GradientButton btnSendPhoneNumber;

    CountryCodePicker codePhoneNumber;
    EditText enterPhoneNumber;

    PromoCodeApiService promoApiService;

    //Класс для удобной работы с SharedPreferences
    SaveLoadData saveLoadData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_promo);
        ButterKnife.bind(this);

        promoApiService = ApiClient.getClient(this).create(PromoCodeApiService.class);

        if (!Utils.isNetworkAvailable(this))
            StyleableToast.makeText(getApplicationContext(), getString(R.string.internet_connection), Toast.LENGTH_LONG, R.style.mytoast).show();

        setButtonBackClick(
                appbar.findViewById(R.id.appBarTitle),
                getResources().getString(R.string.sendPromoCodeText),
                appbar.findViewById(R.id.logoImageBack),
                backTo,
                appbar.findViewById(R.id.toolbarPreviosActivityTitle),
                "",
                null
        );

        saveLoadData = new SaveLoadData(this);

        Gson gson = new Gson();
        PromoCodeResponse promoCodeResponse = gson.fromJson(getIntent().getStringExtra("promoCodeJson"), PromoCodeResponse.class);
        promoCodeTitle.setText(promoCodeResponse.getName());

        Typeface iconFont = FontManager.getTypeface(getApplicationContext(), FontManager.FONTAWESOME);
        FontManager.markAsIconContainer(iconText, iconFont);
        iconText.setText(promoCodeResponse.getIconId());
        promoDescription.setText(promoCodeResponse.getDescription());

//        descriptionTitle.setText(promoCodeResponse.getDescription());

        String date = String.valueOf(promoCodeResponse.getLifeTime());

        dueDateTitle.setText(getResources().getString(R.string.validPromoTitle) + ": " + date + " " + getResources().getString(R.string.daysTitle));

        deletePromoButton.setOnClickListener(view -> promoApiService
                .deletePromo(saveLoadData.loadInt(ORGANIZATION_ID_KEY), Integer.parseInt(promoCodeResponse.getId()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(t -> {
                            startActivity(new Intent(this, PromoCodesActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
                            StyleableToast.makeText(this, getString(R.string.promoDeleted), Toast.LENGTH_LONG, R.style.mytoast).show();
                        },
                        e -> {
                            StyleableToast.makeText(this, getString(R.string.error), Toast.LENGTH_LONG, R.style.mytoast).show();
                            Log.e(TAG, "onError: " + e.getMessage());
                        }
                ));

        codePhoneNumber = findViewById(R.id.codePhoneNumber);
        enterPhoneNumber = findViewById(R.id.enterPhoneNumber);
        codePhoneNumber.registerCarrierNumberEditText(enterPhoneNumber); //добавляем к спиннеру кодов страны поле ввода номера
        initializeCodeCountryContent();

        btnSendPhoneNumber.getButton().setOnClickListener(view -> {
                    if (!enterPhoneNumber.getText().toString().isEmpty()) {
                        String phoneNumber = codePhoneNumber.getFullNumber();
                        disposable.add(promoApiService.sendPromoCode("en", new PromoSmsRequest(phoneNumber), saveLoadData.loadInt(ORGANIZATION_ID_KEY), Long.parseLong(promoCodeResponse.getId()))
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(
                                        response -> {
                                            StyleableToast.makeText(SendPromoCodeActivity.this, SendPromoCodeActivity.this.getString(R.string.promoCodeSent), Toast.LENGTH_LONG, R.style.mytoast).show();
                                            codePhoneNumber.setFullNumber("");
                                        },
                                        e -> {
                                            StyleableToast.makeText(SendPromoCodeActivity.this, SendPromoCodeActivity.this.getString(R.string.error), Toast.LENGTH_LONG, R.style.mytoast).show();
                                            Log.e(TAG, "onError: " + e.getMessage());
                                        }
                                ));
                    } else {
                        StyleableToast.makeText(SendPromoCodeActivity.this, SendPromoCodeActivity.this.getString(R.string.toast_field_can_not_be_empty), Toast.LENGTH_LONG, R.style.mytoast).show();
                    }

                }
        );
    }

    private void initializeCodeCountryContent() {
        String language = Locale.getDefault().getLanguage();
        switch (language) {
            case "ru":
                codePhoneNumber.changeDefaultLanguage(CountryCodePicker.Language.RUSSIAN);
                codePhoneNumber.setCountryPreference("BY,RU,UA"); //НЕ ставить пробел позле запятой

                codePhoneNumber.setDefaultCountryUsingNameCode("UA");
                codePhoneNumber.resetToDefaultCountry();
                break;
            case "en":
                codePhoneNumber.changeDefaultLanguage(CountryCodePicker.Language.ENGLISH);
                codePhoneNumber.setCountryPreference("US,CA,AU,GB,IE,NZ"); //НЕ ставить пробел позле запятой

                codePhoneNumber.setDefaultCountryUsingNameCode("US"); //Установлено US вместо вызова Locale.getDefault().getLanguage(), потому что в списке языков нет сокращения en
                codePhoneNumber.resetToDefaultCountry();
                break;
            case "fr":
                codePhoneNumber.changeDefaultLanguage(CountryCodePicker.Language.FRENCH);
                codePhoneNumber.setCountryPreference("CA,FR"); //НЕ ставить пробел позле запятой

                codePhoneNumber.setDefaultCountryUsingNameCode(Locale.getDefault().getLanguage());
                codePhoneNumber.resetToDefaultCountry();
                break;
            case "uk":
                codePhoneNumber.changeDefaultLanguage(CountryCodePicker.Language.UKRAINIAN);
                codePhoneNumber.setCountryPreference("BY,RU,UA"); //НЕ ставить пробел позле запятой

                codePhoneNumber.setDefaultCountryUsingNameCode("UA");
                codePhoneNumber.resetToDefaultCountry();
                break;
        }
    }

    private String getDate(PromoCodeResponse promoCodeResponse) {
        String dt = DateFormat.getDateTimeInstance().format(new Date());  // Start date
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(sdf.parse(dt));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        c.add(Calendar.DATE, Integer.parseInt(promoCodeResponse.getLifeTime()));  // number of days to add
        dt = sdf.format(c.getTime());
        return dt;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase, "en"));
    }
}
