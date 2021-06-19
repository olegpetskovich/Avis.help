package com.pinus.alexdev.avis.view.profile_activity;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.dagang.library.GradientButton;
import com.google.android.material.textfield.TextInputEditText;
import com.muddzdev.styleabletoast.StyleableToast;
import com.pinus.alexdev.avis.Helper.LocaleHelper;
import com.pinus.alexdev.avis.R;
import com.pinus.alexdev.avis.dto.request.ChangePasswordRequest;
import com.pinus.alexdev.avis.dto.request.UserRequest;
import com.pinus.alexdev.avis.network.ApiClient;
import com.pinus.alexdev.avis.network.apiServices.UserApiService;
import com.pinus.alexdev.avis.utils.SaveLoadData;
import com.pinus.alexdev.avis.view.BaseToolbarBack;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.adorsys.android.securestoragelibrary.SecurePreferences;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static com.pinus.alexdev.avis.utils.UserDataPref.userRequest;
import static com.pinus.alexdev.avis.view.LoginActivity.PASSWORD_KEY;
import static com.pinus.alexdev.avis.view.LoginActivity.USER_ID_KEY;

public class ChangePasswordActivity extends BaseToolbarBack {
    private static final String TAG = ChangePasswordActivity.class.getSimpleName();

    @BindView(R.id.appbar_back)
    View appbar;
    @BindView(R.id.backLayout)
    LinearLayout backTo;

    @BindView(R.id.btnSaveChangedPassword)
    GradientButton btnSaveChangedPassword;

    @BindView(R.id.scanQrButton)
    ImageButton qrScanButton;

    @BindView(R.id.yourPasswordInput)
    TextInputEditText yourPasswordInput;

    @BindView(R.id.newPasswordInput)
    TextInputEditText newPasswordInput;

    @BindView(R.id.confirmInput)
    TextInputEditText confirmInput;

    private UserApiService userApiService;

    //Класс для удобной работы с SharedPreferences
    SaveLoadData saveLoadData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        ButterKnife.bind(this);
        startScan(qrScanButton);
        setButtonBackClick(
                appbar.findViewById(R.id.appBarTitle),
                getResources().getString(R.string.changePassword),
                appbar.findViewById(R.id.logoImageBack),
                backTo,
                appbar.findViewById(R.id.toolbarPreviosActivityTitle),
                "",
                null
        );

        saveLoadData = new SaveLoadData(this);
        userApiService = ApiClient.getClient(getApplicationContext()).create(UserApiService.class);

        btnSaveChangedPassword.getButton().setOnClickListener(v -> {
            String myOldPassword = SecurePreferences.getStringValue(PASSWORD_KEY, this, "");
            String myEnteredNowPassword = yourPasswordInput.getText().toString();

            String newPassword = newPasswordInput.getText().toString();
            String confirmedPassword = confirmInput.getText().toString();

            if (myOldPassword.equals(myEnteredNowPassword) && newPassword.equals(confirmedPassword)) {
                if (newPassword.length() < 6 || newPassword.length() > 20) {
                    StyleableToast.makeText(this, getString(R.string.password_must_contain_from_six_to_twenty), Toast.LENGTH_LONG, R.style.mytoast).show();
                    return;
                }
                disposable.add(userApiService.updatePassword(new ChangePasswordRequest(newPassword, myEnteredNowPassword), saveLoadData.loadInt(USER_ID_KEY))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(t -> {
                                    SecurePreferences.setValue(PASSWORD_KEY, newPassword, this);
                                    yourPasswordInput.setText(null);
                                    newPasswordInput.setText(null);
                                    confirmInput.setText(null);
                                    StyleableToast.makeText(this, getString(R.string.password_changed_successfully), Toast.LENGTH_LONG, R.style.mytoast).show();
                                },
                                e -> {
                                    Log.e(TAG, "onError: " + e.getMessage());
                                    StyleableToast.makeText(this, getString(R.string.passwords_do_not_match), Toast.LENGTH_LONG, R.style.mytoast).show();
                                }
                        ));

            } else StyleableToast.makeText(this, getString(R.string.passwords_do_not_match), Toast.LENGTH_LONG, R.style.mytoast).show();

        });
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase, "en"));
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
}
