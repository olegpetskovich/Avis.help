package com.pinus.alexdev.avis.view.promo_code_activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatEditText;

import com.dagang.library.GradientButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.jakewharton.rxbinding3.view.RxView;
import com.muddzdev.styleabletoast.StyleableToast;
import com.pinus.alexdev.avis.Helper.IconTextView;
import com.pinus.alexdev.avis.R;
import com.pinus.alexdev.avis.dto.request.PromoCodeRequest;
import com.pinus.alexdev.avis.network.ApiClient;
import com.pinus.alexdev.avis.network.apiServices.PromoCodeApiService;
import com.pinus.alexdev.avis.utils.FontManager;
import com.pinus.alexdev.avis.utils.SaveLoadData;
import com.pinus.alexdev.avis.view.BaseToolbarBack;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static com.pinus.alexdev.avis.utils.Utils.checkField;
import static com.pinus.alexdev.avis.view.LoginActivity.ORGANIZATION_ID_KEY;

public class CreatePromoCodeActivity extends BaseToolbarBack {
    private static final String TAG = CreatePromoCodeActivity.class.getSimpleName();

    private final int MAX_INPUT_LENGTH = 200;

    @BindView(R.id.appbar_back)
    View appbar;

    @BindView(R.id.scanQrButton)
    ImageButton qrScanButton;

    @BindView(R.id.backLayout)
    LinearLayout backTo;

    @BindView(R.id.addIcon)
    IconTextView addIconButton;

    @BindView(R.id.savePromoButton)
    GradientButton savePromoButton;

    @BindView(R.id.promoNameText)
    TextInputEditText promoNameText;

    @BindView(R.id.descriptionInput)
    AppCompatEditText descriptionInput;

    @BindView(R.id.tvCountCharacters)
    TextView tvCountCharacters;

    @BindView(R.id.promoValidText)
    TextInputEditText promoValidText;

    @BindView(R.id.iconCard)
    MaterialCardView iconCard;

    @BindView(R.id.progress)
    ProgressBar progress;

    private PromoCodeApiService promoCodeApiService;

    String resultIcon = "gift";

    //Класс для удобной работы с SharedPreferences
    SaveLoadData saveLoadData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_promo_code);
        ButterKnife.bind(this);
        startScan(qrScanButton);
        setButtonBackClick(
                appbar.findViewById(R.id.appBarTitle),
                getString(R.string.createPromoTitle),
                appbar.findViewById(R.id.logoImageBack),
                backTo,
                appbar.findViewById(R.id.toolbarPreviosActivityTitle),
                "",
                null
        );

        promoCodeApiService = ApiClient.getClient(getApplicationContext()).create(PromoCodeApiService.class);

        saveLoadData = new SaveLoadData(this);

        Typeface iconFont = FontManager.getTypeface(getApplicationContext(), FontManager.FONTAWESOME);
        FontManager.markAsIconContainer(addIconButton, iconFont);
        addIconButton.setText(getString(R.string._23));

        iconCard.setOnClickListener(v -> {
            progress.setVisibility(View.VISIBLE);
            startActivityForResult(new Intent(this, IconsActivity.class), 1);
        });

        descriptionInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int cnt = MAX_INPUT_LENGTH - descriptionInput.getText().length();
                tvCountCharacters.setText(String.valueOf(cnt));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        disposable.add(RxView.clicks(savePromoButton.getButton()).subscribe(
                v -> {
                    if (checkField(promoNameText) && checkField(descriptionInput) && checkField(promoValidText) && Integer.valueOf(promoValidText.getText().toString()) != 0) {
                        savePromoCode(
                                new PromoCodeRequest(
                                        resultIcon,
                                        1,
                                        Integer.valueOf(promoValidText.getText().toString()),
                                        promoNameText.getText().toString(),
                                        descriptionInput.getText().toString())
                        );
                    } else
                        StyleableToast.makeText(getApplicationContext(), getString(R.string.fillAllFields), Toast.LENGTH_LONG, R.style.mytoast).show();

                }));
    }

    @Override
    protected void onPause() {
        super.onPause();
        progress.setVisibility(View.GONE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                resultIcon = data.getStringExtra("resultIconId");
                addIconButton.setText(resultIcon);
            }
//            findViewById(R.id.progress_barM).setVisibility(View.INVISIBLE);
//            findViewById(R.id.drawer).setVisibility(View.VISIBLE);
        }
    }

    private void savePromoCode(PromoCodeRequest promoCodeRequest) {
        disposable.add(promoCodeApiService.addPromoCode(saveLoadData.loadInt(ORGANIZATION_ID_KEY), promoCodeRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        t -> {
                            StyleableToast.makeText(getApplicationContext(), getString(R.string.promoAdedd), Toast.LENGTH_LONG, R.style.mytoast).show();
                            startActivity(new Intent(this, PromoCodesActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
                        },
                        e -> {
                            StyleableToast.makeText(getApplicationContext(), getString(R.string.error), Toast.LENGTH_LONG, R.style.mytoast).show();
                            Log.e(TAG, "onError: " + e.getMessage());
                        }
                ));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
