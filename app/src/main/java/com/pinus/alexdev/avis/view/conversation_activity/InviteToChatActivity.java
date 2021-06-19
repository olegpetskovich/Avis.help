package com.pinus.alexdev.avis.view.conversation_activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.dagang.library.GradientButton;
import com.hbb20.CountryCodePicker;
import com.muddzdev.styleabletoast.StyleableToast;
import com.pinus.alexdev.avis.R;
import com.pinus.alexdev.avis.dto.request.ChatByNumberRequest;
import com.pinus.alexdev.avis.dto.response.BranchesResponse;
import com.pinus.alexdev.avis.dto.response.ConversationResponse;
import com.pinus.alexdev.avis.network.ApiClient;
import com.pinus.alexdev.avis.network.apiServices.ConversationApiService;
import com.pinus.alexdev.avis.utils.SaveLoadData;
import com.pinus.alexdev.avis.view.BaseToolbarBack;

import java.util.ArrayList;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static com.pinus.alexdev.avis.view.LoginActivity.ORGANIZATION_ID_KEY;

public class InviteToChatActivity extends BaseToolbarBack {
    private static final String TAG = InviteToChatActivity.class.getSimpleName();

    @BindView(R.id.appbar_back)
    View appbar;

    @BindView(R.id.scanQrButton)
    ImageButton qrScanButton;

    @BindView(R.id.backLayout)
    LinearLayout backTo;

    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    @BindView(R.id.btnInvite)
    GradientButton btnInvite;

    CountryCodePicker codePhoneNumber;
    EditText enterPhoneNumber;

    ConversationApiService conversationApiService;

    //Класс для удобной работы с SharedPreferences
    SaveLoadData saveLoadData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_to_chat);
        ButterKnife.bind(this);
        saveLoadData = new SaveLoadData(this);

        conversationApiService = ApiClient.getClient(getApplicationContext()).create(ConversationApiService.class);

        setButtonBackClick(
                appbar.findViewById(R.id.appBarTitle),
                getResources().getString(R.string.inviteTitle),
                appbar.findViewById(R.id.logoImageBack),
                backTo,
                appbar.findViewById(R.id.toolbarPreviosActivityTitle),
                "",
                null
        );

        codePhoneNumber = findViewById(R.id.codePhoneNumber);
        enterPhoneNumber = findViewById(R.id.enterPhoneNumber);
        codePhoneNumber.registerCarrierNumberEditText(enterPhoneNumber); //добавляем к спиннеру кодов страны поле ввода номера
        initializeCodeCountryContent();

        btnInvite.getButton().setOnClickListener(view -> {
            progressBar.setVisibility(View.VISIBLE);

            conversationApiService.getBranchList(saveLoadData.loadInt(ORGANIZATION_ID_KEY))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(branchesResponses -> {
                        int branchId = branchesResponses.get(0).getId();
                        String phoneNumber = codePhoneNumber.getFullNumber();
                        conversationApiService
                                .addChatByNumber(saveLoadData.loadInt(ORGANIZATION_ID_KEY), new ChatByNumberRequest("en", phoneNumber), branchId)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(conversationResponse -> {
                                    progressBar.setVisibility(View.GONE);
                                    StyleableToast.makeText(getApplicationContext(), getString(R.string.invitation_completed), Toast.LENGTH_LONG, R.style.mytoast).show();
                                    enterPhoneNumber.setText("");
                                    }, e -> Log.e(TAG, e.getMessage()));
                    }, e -> Log.e(TAG, e.getMessage()));
        });
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
}
