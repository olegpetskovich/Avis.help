package com.pinus.alexdev.avis.view.cta_activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.muddzdev.styleabletoast.StyleableToast;
import com.pinus.alexdev.avis.Helper.LocaleHelper;
import com.pinus.alexdev.avis.R;
import com.pinus.alexdev.avis.adapter.CTARVAdapter;
import com.pinus.alexdev.avis.dto.request.TokenRequest;
import com.pinus.alexdev.avis.dto.response.qr_manager_list_response.cta_response.CTAResponse;
import com.pinus.alexdev.avis.dto.response.qr_manager_list_response.cta_response.OptionsResponse;
import com.pinus.alexdev.avis.model.AnswerModel;
import com.pinus.alexdev.avis.model.BranchModel;
import com.pinus.alexdev.avis.model.CTA_QRModel;
import com.pinus.alexdev.avis.network.ApiClient;
import com.pinus.alexdev.avis.network.apiServices.NotificationApiService;
import com.pinus.alexdev.avis.network.apiServices.QrManagerApiService;
import com.pinus.alexdev.avis.utils.SaveLoadData;
import com.pinus.alexdev.avis.view.BaseNavigationView;
import com.pinus.alexdev.avis.view.BranchFilterActivity;
import com.pinus.alexdev.avis.view.LoginActivity;
import com.pinus.alexdev.avis.view.billing_activity.BillingActivity;
import com.pinus.alexdev.avis.view.company_activity.CompanyActivity;
import com.pinus.alexdev.avis.view.conversation_activity.ConversationListReviewsActivity;
import com.pinus.alexdev.avis.view.home_activity.HomeActivity;
import com.pinus.alexdev.avis.view.profile_activity.ProfileActivity;
import com.pinus.alexdev.avis.view.promo_code_activity.PromoCodesActivity;
import com.pinus.alexdev.avis.view.qr_manager_activity.QRManagerActivity;
import com.pinus.alexdev.avis.view.review_list_activity.ReviewsListActivity;
import com.pinus.alexdev.avis.view.settings_activity.SettingsActivity;
import com.pinus.alexdev.avis.view.team_activity.TeamActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.adorsys.android.securestoragelibrary.SecurePreferences;
import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static com.pinus.alexdev.avis.utils.UserDataPref.getUserSummaryInfo;
import static com.pinus.alexdev.avis.view.BranchFilterActivity.BRANCH_FILTER_KEY;
import static com.pinus.alexdev.avis.view.LoginActivity.ORGANIZATION_ID_KEY;
import static com.pinus.alexdev.avis.view.LoginActivity.USER_ID_KEY;
import static com.pinus.alexdev.avis.view.home_activity.HomeActivity.ANDROID_DEVICE_ID_KEY;

public class CTAActivity extends BaseNavigationView implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = CTAActivity.class.getSimpleName();

    @BindView(R.id.appbar_back)
    View appbar;

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    @BindView(R.id.btnFilter)
    MaterialButton btnFilter;

    CTARVAdapter ctarvAdapter;

    //Класс для удобной работы с SharedPreferences
    SaveLoadData saveLoadData;

    private QrManagerApiService qrManagerApiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cta);
        ButterKnife.bind(this);
        setTitleAndLogoClick(appbar.findViewById(R.id.appBarTitle), R.string.menuCTA, appbar.findViewById(R.id.logoImageApp));

        saveLoadData = new SaveLoadData(this);

        userSummaryResponse = getUserSummaryInfo(getApplicationContext());

//        ArrayList<CTA_QRModel> models = getCTAQrData();

        qrManagerApiService = ApiClient.getClient(getApplicationContext()).create(QrManagerApiService.class);
        getCTAsList();

        btnFilter.setOnClickListener(v -> {
            BRANCH_FILTER_KEY = "CTA_ACTIVITY_FILTER";
            startActivity(new Intent(this, BranchFilterActivity.class));
        });

        navigationView = findViewById(R.id.nvViewCTA);
        setNavigationDrawerHeaderContent();
        setOnNavigationView();
    }

    private void getCTAsList() {
        qrManagerApiService.getCTAs(saveLoadData.loadInt(ORGANIZATION_ID_KEY))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(listResponse -> {
                    ArrayList<CTA_QRModel> models = new ArrayList<>();
                    for (CTAResponse response : listResponse) {
                        CTA_QRModel model = new CTA_QRModel();
                        model.setQRName(response.getName());
                        model.setBranchId(response.getBranchId());

                        List<AnswerModel> answerModels = new ArrayList<>();
                        for (OptionsResponse optionResponse : response.getOptions()) {
                            AnswerModel answerModel = new AnswerModel();
                            answerModel.setAnswerName(optionResponse.getValue().getEn());
                            answerModel.setQuestionText(response.getQuestion().getEn());
                            answerModel.setClickedCount(String.valueOf(optionResponse.getHits()));
                            answerModel.setAverageReactionTime(String.valueOf(optionResponse.getAverageReactionTime()));
                            answerModels.add(answerModel);
                        }
                        model.setAnswerModels(answerModels);
                        models.add(model);
                    }

                    if (saveLoadData.loadString(BRANCH_FILTER_KEY) != null && !saveLoadData.loadString(BRANCH_FILTER_KEY).isEmpty()) {
                        ArrayList<BranchModel> branchesID = new Gson().fromJson(saveLoadData.loadString(BRANCH_FILTER_KEY), new TypeToken<List<BranchModel>>() {
                        }.getType());

                        ArrayList<CTA_QRModel> filteredQrs = new ArrayList<>();
                        for (BranchModel branchModel : branchesID) {
                            for (int i = 0; i < models.size(); i++) {
                                CTA_QRModel model = models.get(i);
                                if (model.getBranchId() == branchModel.getBranchID())
                                    filteredQrs.add(model);
                            }
                        }

                        models = filteredQrs;
                        saveLoadData.saveString(BRANCH_FILTER_KEY, "");
                    }

                    recyclerView.setLayoutManager(new LinearLayoutManager(this));
                    ctarvAdapter = new CTARVAdapter(this, models);
                    recyclerView.setAdapter(ctarvAdapter);
                }, e -> Log.e(TAG, "onError: " + e.getMessage()));
    }

    private ArrayList<CTA_QRModel> getCTAQrData() {
        ArrayList<CTA_QRModel> models = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            CTA_QRModel model = new CTA_QRModel();
            model.setQRName("QR Name " + i);

            List<AnswerModel> answerModels = new ArrayList<>();
            AnswerModel answerModel_1 = new AnswerModel();
            answerModel_1.setAnswerName("Answer 1");
            answerModel_1.setQuestionText("Question1");
            answerModel_1.setClickedCount("7");
            answerModel_1.setAverageReactionTime("25");

            AnswerModel answerModel_2 = new AnswerModel();
            answerModel_2.setAnswerName("Answer 2");
            answerModel_2.setQuestionText("Question2");
            answerModel_2.setClickedCount("4");
            answerModel_2.setAverageReactionTime("35");

            AnswerModel answerModel_3 = new AnswerModel();
            answerModel_3.setAnswerName("Answer 3");
            answerModel_3.setQuestionText("Question3");
            answerModel_3.setClickedCount("15");
            answerModel_3.setAverageReactionTime("57");

            answerModels.add(answerModel_1);
            answerModels.add(answerModel_2);
            answerModels.add(answerModel_3);

            model.setAnswerModels(answerModels);
            models.add(model);
        }
        return models;
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
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mToogle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getCTAsList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
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
