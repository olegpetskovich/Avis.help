package com.pinus.alexdev.avis.view.settings_activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.navigation.NavigationView;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.jakewharton.rxbinding3.view.RxView;
import com.muddzdev.styleabletoast.StyleableToast;
import com.pinus.alexdev.avis.R;
import com.pinus.alexdev.avis.dto.request.TokenRequest;
import com.pinus.alexdev.avis.network.ApiClient;
import com.pinus.alexdev.avis.network.apiServices.NotificationApiService;
import com.pinus.alexdev.avis.utils.SaveLoadData;
import com.pinus.alexdev.avis.view.BaseNavigationView;
import com.pinus.alexdev.avis.view.LoginActivity;
import com.pinus.alexdev.avis.view.company_activity.CompanyActivity;
import com.pinus.alexdev.avis.view.cta_activity.CTAActivity;
import com.pinus.alexdev.avis.view.team_activity.TeamActivity;
import com.pinus.alexdev.avis.view.billing_activity.BillingActivity;
import com.pinus.alexdev.avis.view.conversation_activity.ConversationListReviewsActivity;
import com.pinus.alexdev.avis.view.home_activity.HomeActivity;
import com.pinus.alexdev.avis.view.profile_activity.ProfileActivity;
import com.pinus.alexdev.avis.view.promo_code_activity.PromoCodesActivity;
import com.pinus.alexdev.avis.view.qr_manager_activity.QRManagerActivity;
import com.pinus.alexdev.avis.view.review_list_activity.ReviewsListActivity;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.adorsys.android.securestoragelibrary.SecurePreferences;
import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static com.pinus.alexdev.avis.utils.UserDataPref.getUserSummaryInfo;
import static com.pinus.alexdev.avis.view.LoginActivity.USER_ID_KEY;
import static com.pinus.alexdev.avis.view.home_activity.HomeActivity.ANDROID_DEVICE_ID_KEY;

public class SettingsActivity extends BaseNavigationView implements NavigationView.OnNavigationItemSelectedListener {
    @BindView(R.id.appbar_back)
    View appbar;

    private static final String TAG = LanguagesActivity.class.getSimpleName();

    @BindView(R.id.btnLanguage)
    MaterialButton btnLanguage;

    @BindView(R.id.btnNotifications)
    MaterialButton btnNotifications;

    @BindView(R.id.btnTermsConditions)
    MaterialButton btnTermsConditions;

    //Класс для удобной работы с SharedPreferences
    SaveLoadData saveLoadData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);
        setTitleAndLogoClick(appbar.findViewById(R.id.appBarTitle), R.string.settingsTitle, appbar.findViewById(R.id.logoImageApp));

        saveLoadData = new SaveLoadData(this);

        userSummaryResponse = getUserSummaryInfo(getApplicationContext());

        navigationView = findViewById(R.id.nvViewSettings);
        setNavigationDrawerHeaderContent();
        setOnNavigationView();

        disposable.add(RxView.clicks(btnLanguage).subscribe(t -> startActivity(new Intent(SettingsActivity.this, LanguagesActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))));
        disposable.add(RxView.clicks(btnNotifications).subscribe(t -> startActivity(new Intent(SettingsActivity.this, NotificationsActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))));
        disposable.add(RxView.clicks(btnTermsConditions).subscribe(t -> startActivity(new Intent(SettingsActivity.this, TermsOfUseActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))));
    }

    private void setNavigationDrawerHeaderContent() {
        View navigationHeader = navigationView.inflateHeaderView(R.layout.top_item_nav_draw);
        navigationHeader.setOnClickListener(v -> {
            startActivity(new Intent(this, ProfileActivity.class));
            finish();
        });

        CircleImageView profileImage = navigationHeader.findViewById(R.id.profileImage);
        TextView tvEmail = navigationHeader.findViewById(R.id.tvEmail);
        if (userSummaryResponse != null) {
            if (userSummaryResponse.getAvatarUrl() != null && !userSummaryResponse.getAvatarUrl().isEmpty()) {
                Picasso.get().load(userSummaryResponse.getAvatarUrl()).placeholder(R.drawable.company_logo).error(R.drawable.company_logo).into(profileImage);
            } else profileImage.setImageResource(R.drawable.company_logo);
            tvEmail.setText(userSummaryResponse.getEmail());
        } else {
            profileImage.setImageResource(R.drawable.company_logo);
            tvEmail.setText("");
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.home:
                startActivity(new Intent(this, HomeActivity.class));
                finish();
                break;
            case R.id.review:
                startActivity(new Intent(this, ReviewsListActivity.class));
                finish();
                break;
            case R.id.company:
                startActivity(new Intent(this, CompanyActivity.class));
                finish();
                break;
            case R.id.conversations:
                Intent intent = new Intent(this, ConversationListReviewsActivity.class);
                intent.putExtra("reviewOrConversation", "conversation");
                startActivity(intent);
                finish();
                break;
            case R.id.cta:
                startActivity(new Intent(this, CTAActivity.class));
                finish();
                break;
            case R.id.qrManager:
                startActivity(new Intent(this, QRManagerActivity.class));
                finish();
                break;
            case R.id.promoCodes:
                startActivity(new Intent(this, PromoCodesActivity.class));
                finish();
                break;
            case R.id.settings:
                break;
            case R.id.billing:
                startActivity(new Intent(this, BillingActivity.class));
                finish();
                break;
            case R.id.team:
                startActivity(new Intent(this, TeamActivity.class));
                finish();
                break;
            case R.id.sign_out:
                ApiClient.getClient(getApplicationContext()).create(NotificationApiService.class)
                        .updateToken(saveLoadData.loadLong(ANDROID_DEVICE_ID_KEY), new TokenRequest(false, false, false, false, 1), saveLoadData.loadInt(USER_ID_KEY))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(t -> {
                                    SecurePreferences.clearAllValues(getApplicationContext());
                                    startActivity(new Intent(this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
                                    finish();
                                },
                                e -> {
                                    Log.e(TAG, "onError: " + e.getMessage());
                                    StyleableToast.makeText(getApplicationContext(), getString(R.string.error), Toast.LENGTH_LONG, R.style.mytoast).show();
                                });
                break;
        }
        DrawerLayout drawerLayout = findViewById(R.id.drawer);
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void setLogOutToken(String oldToken) {
        disposable.add(ApiClient.getClient(getApplicationContext())
                .create(NotificationApiService.class)
                .setLogout(oldToken) //Этот метод(запрос) ставит статус уведомлений на сервере на 0 - то есть отключает их.
                // Этот метод(запрос) и метод(запрос) setLogin нужно вызывать каждый раз при входе и выходе из аккаунта, чтобы включать и выключать уведомления.
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(t -> {
                            SecurePreferences.clearAllValues(getApplicationContext());
                            startActivity(new Intent(this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
                            //Очищаем текст email(a) из памяти, чтобы он не отобразился при входе в другой аккаунт
                            saveLoadData.saveString("loginText", "");
                            finish();
                        },
                        e -> {
                            Log.e(TAG, "onError: " + e.getMessage());
                            StyleableToast.makeText(getApplicationContext(), getString(R.string.error), Toast.LENGTH_LONG, R.style.mytoast).show();
                        }));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mToogle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }
}
