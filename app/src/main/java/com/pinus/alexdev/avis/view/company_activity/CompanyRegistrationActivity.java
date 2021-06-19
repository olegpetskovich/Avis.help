package com.pinus.alexdev.avis.view.company_activity;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.dagang.library.GradientButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.jakewharton.rxbinding3.view.RxView;
import com.muddzdev.styleabletoast.StyleableToast;
import com.pinus.alexdev.avis.R;
import com.pinus.alexdev.avis.dto.request.OrganizationRequest;
import com.pinus.alexdev.avis.dto.request.TokenRequest;
import com.pinus.alexdev.avis.network.ApiClient;
import com.pinus.alexdev.avis.network.ApiClientMultipart;
import com.pinus.alexdev.avis.network.apiServices.BranchApiService;
import com.pinus.alexdev.avis.network.apiServices.NotificationApiService;
import com.pinus.alexdev.avis.utils.SaveLoadData;
import com.pinus.alexdev.avis.view.BaseNavigationView;
import com.pinus.alexdev.avis.view.billing_activity.BillingActivity;
import com.pinus.alexdev.avis.view.conversation_activity.ConversationListReviewsActivity;
import com.pinus.alexdev.avis.view.cta_activity.CTAActivity;
import com.pinus.alexdev.avis.view.home_activity.HomeActivity;
import com.pinus.alexdev.avis.view.LoginActivity;
import com.pinus.alexdev.avis.view.promo_code_activity.PromoCodesActivity;
import com.pinus.alexdev.avis.view.qr_manager_activity.QRManagerActivity;
import com.pinus.alexdev.avis.view.review_list_activity.ReviewsListActivity;
import com.pinus.alexdev.avis.view.settings_activity.SettingsActivity;
import com.pinus.alexdev.avis.view.profile_activity.ProfileActivity;
import com.pinus.alexdev.avis.view.team_activity.TeamActivity;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.adorsys.android.securestoragelibrary.SecurePreferences;
import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static com.pinus.alexdev.avis.utils.UserDataPref.getUserSummaryInfo;
import static com.pinus.alexdev.avis.view.LoginActivity.USER_ID_KEY;
import static com.pinus.alexdev.avis.view.home_activity.HomeActivity.ANDROID_DEVICE_ID_KEY;

public class CompanyRegistrationActivity extends BaseNavigationView implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = CompanyRegistrationActivity.class.getSimpleName();

    @BindView(R.id.appbar)
    View appbar;

    @BindView(R.id.avatarImage)
    CircleImageView avatarImage;

    @BindView(R.id.companyName)
    TextInputEditText companyName;

    @BindView(R.id.btnSaveOrganizationName)
    GradientButton btnSaveOrganizationName;

    @BindView(R.id.scanQrButton)
    ImageButton qrScanButton;

    File file;

    //Класс для удобной работы с SharedPreferences
    SaveLoadData saveLoadData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_registration);
        ButterKnife.bind(this);
        startScan(qrScanButton);
        saveLoadData = new SaveLoadData(this);

        setTitleAndLogoClick(appbar.findViewById(R.id.appBarTitle), R.string.tvCompanyRegistration, appbar.findViewById(R.id.logoImageApp));

        disposable.add(RxView.clicks(btnSaveOrganizationName.getButton()).subscribe(t -> {
            if (Objects.requireNonNull(companyName.getText()).toString().isEmpty())
                StyleableToast.makeText(getApplicationContext(), getString(R.string.toast_field_can_not_be_empty), Toast.LENGTH_SHORT, R.style.mytoast).show();
            else addOrganization(Objects.requireNonNull(companyName.getText()).toString());
        }));

        userSummaryResponse = getUserSummaryInfo(getApplicationContext());

        navigationView = findViewById(R.id.nvViewCompanyRegistration);
        setNavigationDrawerHeaderContent();
        setOnNavigationView();
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

    private void addOrganization(String organizationName) {
        disposable.add(ApiClientMultipart.getClient(getApplicationContext()).create(BranchApiService.class)
                .addOrganization(new OrganizationRequest(organizationName))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                            Log.v(TAG, "Image success");
                            StyleableToast.makeText(getApplicationContext(), getString(R.string.organization_is_added), Toast.LENGTH_SHORT, R.style.mytoast).show();
                        },
                        e -> {
                            Log.e(TAG, "onError:" + e.getMessage());
                            StyleableToast.makeText(getApplicationContext(), getString(R.string.error), Toast.LENGTH_SHORT, R.style.mytoast).show();
                        }));
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
                startActivity(new Intent(this, SettingsActivity.class));
                finish();
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
                        .updateToken(saveLoadData.loadLong(ANDROID_DEVICE_ID_KEY), new TokenRequest(false, false, false, false, 0), saveLoadData.loadInt(USER_ID_KEY))
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
