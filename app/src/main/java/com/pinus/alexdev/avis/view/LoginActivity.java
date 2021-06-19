package com.pinus.alexdev.avis.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;

import com.dagang.library.GradientButton;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.jakewharton.rxbinding3.view.RxView;
import com.muddzdev.styleabletoast.StyleableToast;
import com.pinus.alexdev.avis.Helper.LocaleHelper;
import com.pinus.alexdev.avis.R;
import com.pinus.alexdev.avis.dto.request.LoginRequest;
import com.pinus.alexdev.avis.network.ApiClient;
import com.pinus.alexdev.avis.network.apiServices.LoginApiService;
import com.pinus.alexdev.avis.utils.PrefUtils;
import com.pinus.alexdev.avis.utils.SaveLoadData;
import com.pinus.alexdev.avis.utils.Utils;
import com.pinus.alexdev.avis.view.home_activity.HomeActivity;
import com.pinus.alexdev.avis.view.profile_activity.ProfileActivity;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.adorsys.android.securestoragelibrary.SecurePreferences;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

import static com.pinus.alexdev.avis.utils.Utils.hideKeyboard;

public class LoginActivity extends AppCompatActivity {
    public static final String ORGANIZATION_ID_KEY = "ORGANIZATION_ID_KEY";
    public static final String USER_ID_KEY = "USER_ID_KEY";
    public static final String UI_PERMISSION_ID_KEY = "UI_PERMISSION_ID_KEY";

    public static final String EMAIL_KEY = "email";
    public static final String PASSWORD_KEY = "pass";

    @BindView(R.id.btnSignIn)
    GradientButton btnSignIn;

    @BindView(R.id.btnForgotPassword)
    MaterialButton btnForgotPassword;

    @BindView(R.id.loginInput)
    TextInputEditText loginInput;

    @BindView(R.id.passwordInput)
    AppCompatEditText passwordInput;

    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    @BindView(R.id.loginLogo)
    ImageView loginLogo;

//    @BindView(R.id.btnBottomSignUp)
//    GradientButton btnBottomSignUp;

    private LoginApiService loginApiService;

    private CompositeDisposable disposable = new CompositeDisposable();
    private static final String TAG = LoginActivity.class.getSimpleName();

    //Класс для удобной работы с SharedPreferences
    SaveLoadData saveLoadData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        saveLoadData = new SaveLoadData(this);

        loginApiService = ApiClient.getClient(getApplicationContext()).create(LoginApiService.class);

        disposable.add(RxView.clicks(btnSignIn.getButton())
                .subscribe(v -> {
                            hideKeyboard(LoginActivity.this);
                            if (Utils.isNetworkAvailable(this)) {

                                authentication(new LoginRequest(Objects.requireNonNull(loginInput.getText()).toString(), Objects.requireNonNull(passwordInput.getText()).toString()));
                            } else {
                                StyleableToast.makeText(getApplicationContext(), getString(R.string.internet_connection), Toast.LENGTH_LONG, R.style.mytoast).show();
                            }
                        }
                ));

        btnForgotPassword.setOnClickListener(v -> {
            if (!Objects.requireNonNull(loginInput.getText()).toString().isEmpty()) {
                disposable.add(loginApiService.resetPassword("en", loginInput.getText().toString())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(response -> StyleableToast.makeText(getApplicationContext(), getString(R.string.тew_password_sent), Toast.LENGTH_LONG, R.style.mytoast).show(),
                                   e -> StyleableToast.makeText(getApplicationContext(), getString(R.string.error_invalid_email), Toast.LENGTH_LONG, R.style.mytoast).show()));
            } else StyleableToast.makeText(getApplicationContext(), getString(R.string.email_field_must_be_filled), Toast.LENGTH_LONG, R.style.mytoast).show();
        });
    }

    private void authentication(LoginRequest loginRequest) {
        disposable.add(loginApiService.signIn(loginRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doAfterSuccess(x -> {
                    SecurePreferences.setValue(EMAIL_KEY, loginRequest.getEmail(), getApplicationContext());
                    SecurePreferences.setValue(PASSWORD_KEY, loginRequest.getPassword(), getApplicationContext());
                })
                .doOnSubscribe(s -> progressBar.setVisibility(View.VISIBLE))
                .doOnSuccess(l -> progressBar.setVisibility(View.INVISIBLE))
                .subscribe(user -> {
                            StyleableToast.makeText(getApplicationContext(), getString(R.string.authorized), Toast.LENGTH_LONG, R.style.mytoast).show();

                            String uiPermissionList = new Gson().toJson(user.getUiPermission());
                            saveLoadData.saveString(UI_PERMISSION_ID_KEY, uiPermissionList);

                            saveLoadData.saveInt(ORGANIZATION_ID_KEY, Integer.parseInt(user.getData().getOrganizationId())); //organizationId - id организации(компании)
                            saveLoadData.saveInt(USER_ID_KEY, user.getUserId()); //userId - id пользователя, который залогинен в данный момент, оно нужно для доступа к его данным на сервере
                            PrefUtils.storeApiKey(getApplicationContext(), user.getAccessToken());

                            Intent intent;
                            if (!user.getUiPermission().isStatistic()) {
                                intent = new Intent(LoginActivity.this, ProfileActivity.class);
                            } else {
                                intent = new Intent(LoginActivity.this, HomeActivity.class);
                            }
                            intent.putExtra("isAuth", true);
                            startActivity(intent);
                            finish();
                        },
                        e -> {
                            StyleableToast.makeText(getApplicationContext(), getString(R.string.wrongLogin), Toast.LENGTH_LONG, R.style.mytoast).show();
                            progressBar.setVisibility(View.INVISIBLE);
                            Log.e(TAG, "onError: " + e.getMessage());
                        }
                ));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposable.dispose();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase, "en"));
    }
}
