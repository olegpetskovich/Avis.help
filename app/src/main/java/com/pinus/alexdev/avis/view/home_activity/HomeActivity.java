package com.pinus.alexdev.avis.view.home_activity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.CornerPathEffect;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.jakewharton.rxbinding3.view.RxView;
import com.muddzdev.styleabletoast.StyleableToast;
import com.pinus.alexdev.avis.Helper.LocaleHelper;
import com.pinus.alexdev.avis.R;
import com.pinus.alexdev.avis.callback.ICallback;
import com.pinus.alexdev.avis.dto.request.TokenRequest;
import com.pinus.alexdev.avis.dto.response.NPSResponse;
import com.pinus.alexdev.avis.dto.response.branch_statistic_response.BranchStatisticResponse;
import com.pinus.alexdev.avis.dto.response.branch_statistic_response.ReviewHourStatsItem;
import com.pinus.alexdev.avis.dto.response.user_summary_response.UserSummaryResponse;
import com.pinus.alexdev.avis.enums.PromoTypes;
import com.pinus.alexdev.avis.enums.TypesEnum;
import com.pinus.alexdev.avis.network.ApiClient;
import com.pinus.alexdev.avis.network.apiServices.BranchApiService;
import com.pinus.alexdev.avis.network.apiServices.LoginApiService;
import com.pinus.alexdev.avis.network.apiServices.NotificationApiService;
import com.pinus.alexdev.avis.network.apiServices.UserApiService;
import com.pinus.alexdev.avis.utils.App;
import com.pinus.alexdev.avis.utils.SaveLoadData;
import com.pinus.alexdev.avis.utils.Utils;
import com.pinus.alexdev.avis.view.BaseNavigationView;
import com.pinus.alexdev.avis.view.BranchChartListActivity;
import com.pinus.alexdev.avis.view.LoginActivity;
import com.pinus.alexdev.avis.view.billing_activity.BillingActivity;
import com.pinus.alexdev.avis.view.company_activity.CompanyActivity;
import com.pinus.alexdev.avis.view.conversation_activity.ConversationListReviewsActivity;
import com.pinus.alexdev.avis.view.cta_activity.CTAActivity;
import com.pinus.alexdev.avis.view.profile_activity.ProfileActivity;
import com.pinus.alexdev.avis.view.promo_code_activity.PromoCodesActivity;
import com.pinus.alexdev.avis.view.qr_manager_activity.QRManagerActivity;
import com.pinus.alexdev.avis.view.review_list_activity.ReviewsListActivity;
import com.pinus.alexdev.avis.view.settings_activity.NotificationsActivity;
import com.pinus.alexdev.avis.view.settings_activity.SettingsActivity;
import com.pinus.alexdev.avis.view.team_activity.TeamActivity;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.adorsys.android.securestoragelibrary.SecurePreferences;
import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.LineChartView;

import static com.pinus.alexdev.avis.enums.PromoTypes.OVERALL;
import static com.pinus.alexdev.avis.enums.RangeStatisticEnum.DAY;
import static com.pinus.alexdev.avis.enums.RangeStatisticEnum.MONTH;
import static com.pinus.alexdev.avis.enums.RangeStatisticEnum.WEEK;
import static com.pinus.alexdev.avis.view.LoginActivity.ORGANIZATION_ID_KEY;
import static com.pinus.alexdev.avis.view.LoginActivity.USER_ID_KEY;

public class HomeActivity extends BaseNavigationView implements NavigationView.OnNavigationItemSelectedListener, ICallback {
    private static final String TAG = HomeActivity.class.getSimpleName();

    public static final String UNIQUE_ANDROID_ID_KEY = "UNIQUE_ANDROID_ID_KEY";
    public static final String ANDROID_DEVICE_ID_KEY = "ANDROID_DEVICE_ID_KEY";

    private final int ALL_BRANCHES_STATISTIC_ID = -1;

    private String timeRange = "";

    final int BRANCH_SPINNER = 0;
    final int CHART_SPINNER = 1;

    @BindView(R.id.btnChooseBranch)
    MaterialButton btnChooseBranch;

    @BindView(R.id.btnChooseChart)
    MaterialButton btnChooseChart;

    @BindView(R.id.appbar_back)
    View appbar;

    @BindView(R.id.btnStartDate)
    MaterialButton btnStartDate;

    @BindView(R.id.btnEndDate)
    MaterialButton btnEndDate;

    @BindView(R.id.todayButton)
    MaterialButton todayButton;

    @BindView(R.id.weekButton)
    MaterialButton weekButton;

    @BindView(R.id.monthButton)
    MaterialButton monthButton;

    @BindView(R.id.scanQrButton)
    ImageButton qrScanButton;

    /*@BindView(R.id.searchButton)
    ImageButton searchButton;*/

    @BindView(R.id.totalConversationCount)
    TextView totalConversation;

    @BindView(R.id.totalReviewCount)
    TextView totalReview;

    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    @BindView(R.id.ratingBar)
    ProgressBar ratingBar;

    @BindView(R.id.ratingCountTitle)
    TextView ratingTitle;

    @BindView(R.id.tvNPS)
    TextView tvNPS;

    @BindView(R.id.tvPromoters)
    TextView tvPromoters;

    @BindView(R.id.tvPassives)
    TextView tvNeutral;

    @BindView(R.id.tvDetractors)
    TextView tvDetractors;

    @BindView(R.id.tvChart)
    TextView tvChart;

    private int mYear, mMonth, mDay;

    private BranchApiService branchApiService;
    private NotificationApiService notificationApiService;

    String startDate = "";
    String endDate = "";

    long branchId = ALL_BRANCHES_STATISTIC_ID;

    private String category = "";
    private long opinionId = -1;

    TypesEnum type = TypesEnum.OVERALL; //Дефолтное значение, чтобы избежать ошибки NPE
    PromoTypes qrType = OVERALL; //Дефолтное значение, чтобы избежать ошибки NPE

    DatePickerDialog datePickerDialog;

    private LoginApiService loginApiService;

    ArrayList axisValues;
    ArrayList yAxisValues;

    //Класс для удобной работы с SharedPreferences
    SaveLoadData saveLoadData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
        startScan(qrScanButton);
        if (!Utils.isNetworkAvailable(this))
            StyleableToast.makeText(getApplicationContext(), getString(R.string.internet_connection), Toast.LENGTH_SHORT, R.style.mytoast).show();

        saveLoadData = new SaveLoadData(this);
        if (saveLoadData.loadBoolean("restarting_state")) {
            saveLoadData.saveString("chart_data_name", getString(OVERALL.getValue()));
            saveLoadData.saveString("branch_data_name", getString(TypesEnum.OVERALL.getValue()));
            saveLoadData.saveLong("branch_data_id", 0);

            //Это значение мы получаем из LanguagesActivity, там, в методе reStart(), оно устанавливается (там же и объяснение принципа работы)
            saveLoadData.saveBoolean("restarting_state", false);
        }

        //Создаём уникальный id для девайса и сохраняем его для дальнейшего использования
        if (saveLoadData.loadString(UNIQUE_ANDROID_ID_KEY) == null || saveLoadData.loadString("UNIQUE_ANDROID_ID_KEY").isEmpty()) {
            String uniqueID = UUID.randomUUID().toString();
            saveLoadData.saveString(UNIQUE_ANDROID_ID_KEY, uniqueID);
        }

        axisValues = new ArrayList();
        yAxisValues = new ArrayList();

        loginApiService = ApiClient.getClient(getApplicationContext()).create(LoginApiService.class);
        branchApiService = ApiClient.getClient(getApplicationContext()).create(BranchApiService.class);
        notificationApiService = ApiClient.getClient(getApplicationContext()).create(NotificationApiService.class);

        //Осуществляется первый запрос для изначального отображения данных
        timeRange = WEEK.toString();
        setIntervalDate(WEEK.getValue());
        getStatsByRange(branchId, category, saveLoadData.loadInt(ORGANIZATION_ID_KEY), timeRange, BRANCH_SPINNER);

        setButtonFilter();

        ((TextView) appbar.findViewById(R.id.appBarTitle)).setText(getResources().getString(R.string.homeTitle));

        disposable.add(RxView.clicks(btnStartDate)
                .subscribe(t -> {
                            setButtonStyle(todayButton, R.color.colorWhite, R.color.colorDark);
                            setButtonStyle(weekButton, R.color.colorWhite, R.color.colorDark);
                            setButtonStyle(monthButton, R.color.colorWhite, R.color.colorDark);

                            setButtonStyle(btnStartDate, R.color.colorPrimary, android.R.color.white);
                            setButtonStyle(btnEndDate, R.color.colorPrimary, android.R.color.white);

                            setStartDate();

                            timeRange = "";
                        },
                        e -> Log.e(TAG, "onError: " + e.getMessage())));
        disposable.add(RxView.clicks(btnEndDate)
                .subscribe(t -> {
                            setButtonStyle(todayButton, R.color.colorWhite, R.color.colorDark);
                            setButtonStyle(weekButton, R.color.colorWhite, R.color.colorDark);
                            setButtonStyle(monthButton, R.color.colorWhite, R.color.colorDark);

                            setButtonStyle(btnStartDate, R.color.colorPrimary, android.R.color.white);
                            setButtonStyle(btnEndDate, R.color.colorPrimary, android.R.color.white);

                            setEndDate();

                            timeRange = "";
                        },
                        e -> Log.e(TAG, "onError: " + e.getMessage())));

        setBranchSpinnerBtn();
        setChartSpinnerBtn();

        totalReview.setOnClickListener(v -> {
            startActivity(new Intent(this, ReviewsListActivity.class));
            finish();
        });

        totalConversation.setOnClickListener(v -> {
            startActivity(new Intent(this, ConversationListReviewsActivity.class));
            finish();
        });

        userRequest();
    }

    private void setNavigationDrawerHeaderContent() {
        View navigationHeader = navigationView.inflateHeaderView(R.layout.top_item_nav_draw);
        navigationHeader.setOnClickListener(v -> {
            startActivity(new Intent(HomeActivity.this, ProfileActivity.class));
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

    private void firebaseInitializingSendToken() {
        FirebaseApp.initializeApp(this);

        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {

                // Get new Instance ID token
                String token = task.getResult().getToken();
                addDevice(token);
            } else {
                Log.w(TAG, "getInstanceId failed", task.getException());

                String token = "A token does not exist for this device because the phone does not support google services. You need a new phone.";
                addDevice(token);
            }
        });
    }

    private void addDevice(String token) {
        boolean ctaState = saveLoadData.loadBoolean(NotificationsActivity.CTA_NOTIFICATION_KEY);
        boolean chatState = saveLoadData.loadBoolean(NotificationsActivity.CHAT_NOTIFICATION_KEY);
        boolean reviewState = saveLoadData.loadBoolean(NotificationsActivity.REVIEW_NOTIFICATION_KEY);
        boolean surveyState = saveLoadData.loadBoolean(NotificationsActivity.SURVEY_NOTIFICATION_KEY);

        SharedPreferences prefs = getSharedPreferences("com.mycompany.myAppName", MODE_PRIVATE);
        if (prefs.getBoolean("firstrun", true)) {
            disposable.add(notificationApiService.addToken(new TokenRequest(saveLoadData.loadString(UNIQUE_ANDROID_ID_KEY), "ANDROID", 7, ctaState, chatState, true, surveyState, token), saveLoadData.loadInt(USER_ID_KEY))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(tokenResponse -> {
                        long deviceId = tokenResponse.getId();
                        saveLoadData.saveLong(ANDROID_DEVICE_ID_KEY, deviceId);
                    }, e -> {
                        Log.e(TAG, "onError: " + e.getMessage());
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }));
            prefs.edit().putBoolean("firstrun", false).apply();
        } else {
            long deviceID = saveLoadData.loadLong(ANDROID_DEVICE_ID_KEY);
            disposable.add(notificationApiService.updateToken(deviceID, new TokenRequest(saveLoadData.loadString(UNIQUE_ANDROID_ID_KEY), "ANDROID", 7, ctaState, chatState, true, surveyState, token), saveLoadData.loadInt(USER_ID_KEY))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(tokenResponse -> {

                    }, e -> Log.e(TAG, "onError: " + e.getMessage())));
        }
    }

    private void setButtonFilter() {
        disposable.add(RxView.clicks(todayButton).subscribe(t -> {
            setButtonStyle(btnStartDate, android.R.color.white, R.color.colorPrimaryDark);
            setButtonStyle(btnEndDate, android.R.color.white, R.color.colorPrimaryDark);

            setButtonStyle(todayButton, R.color.colorRedNPS, R.color.colorWhite);
            setButtonStyle(weekButton, R.color.colorWhite, R.color.colorDark);
            setButtonStyle(monthButton, R.color.colorWhite, R.color.colorDark);

            timeRange = DAY.toString();
            setIntervalDate(DAY.getValue());
        }));
        disposable.add(RxView.clicks(weekButton).subscribe(t -> {
            setButtonStyle(btnStartDate, android.R.color.white, R.color.colorPrimaryDark);
            setButtonStyle(btnEndDate, android.R.color.white, R.color.colorPrimaryDark);

            setButtonStyle(todayButton, R.color.colorWhite, R.color.colorDark);
            setButtonStyle(weekButton, R.color.colorRedNPS, R.color.colorWhite);
            setButtonStyle(monthButton, R.color.colorWhite, R.color.colorDark);

            timeRange = WEEK.toString();
            setIntervalDate(WEEK.getValue());
        }));
        disposable.add(RxView.clicks(monthButton).subscribe(t -> {
            setButtonStyle(btnStartDate, android.R.color.white, R.color.colorPrimaryDark);
            setButtonStyle(btnEndDate, android.R.color.white, R.color.colorPrimaryDark);

            setButtonStyle(todayButton, R.color.colorWhite, R.color.colorDark);
            setButtonStyle(weekButton, R.color.colorWhite, R.color.colorDark);
            setButtonStyle(monthButton, R.color.colorRedNPS, R.color.colorWhite);

            timeRange = MONTH.toString();
            setIntervalDate(MONTH.getValue());
        }));
    }

    private void setButtonStyle(MaterialButton buttonStyle, int backgroundColor, int textColor) {
        buttonStyle.setBackgroundColor(ContextCompat.getColor(this, backgroundColor));
        buttonStyle.setTextColor(ContextCompat.getColor(this, textColor));
    }

    private void getStatsByRange(long branchId, String category, int organizationId, String range, int spinnerType) {
        //Эта проверка на то, нужно ли получать нам данные обо всех бранчах или же только для конкретного.
        //Если branchId совпадает с полем ALL_BRANCHES_STATISTIC_ID (а его мы присваиваем в branchId если в списке бранчей был выбран айтем "Все филиалы"),
        //то мы делаем запрос на получение данных по всем бранчам, иначе - только по конкретному бранчу, который мы определяем по его branchId
        if (branchId == ALL_BRANCHES_STATISTIC_ID) {
            //Эта проверка нужна для того, чтобы определять, когда нужно получать общую статистику для всего, а когда нужно получать статистику для графика по выбранной категории
            String ctgr;
            if (spinnerType == CHART_SPINNER) ctgr = category;
            else ctgr = null;

            disposable.add(branchApiService.getAllOrganizationStatistic(organizationId, ctgr, "", range, "")
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(response -> {
                                setContent(response, spinnerType);
                                setDataChart(response.getReview().getReviews());
                            },
                            e -> Log.e(TAG, "onError: " + e.getMessage())
                    ));
        } else if (opinionId != -1) {
            disposable.add(branchApiService.getBranchOpinionStatistic(branchId, organizationId, opinionId, "", range, "")
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(response -> {
                                setContent(response, spinnerType);
                                setDataChart(response.getReview().getReviews());
                            },
                            e -> Log.e(TAG, "onError: " + e.getMessage())
                    ));
        } else {

            disposable.add(branchApiService.getBranchStatistic(branchId, organizationId, category, "", range, "")
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(response -> {
                                setContent(response, spinnerType);
                                setDataChart(response.getReview().getReviews());
                            },
                            e -> Log.e(TAG, "onError: " + e.getMessage())
                    ));
        }
    }

    private void getStatsByPeriod(long branchId, String category, String from, int organizationId, String to, int spinnerType) {
        //Эта проверка на то, нужно ли получать нам данные обо всех бранчах или же только для конкретного.
        //Если branchId совпадает с полем ALL_BRANCHES_STATISTIC_ID (а его мы присваиваем в branchId если в списке бранчей был выбран айтем "Все филиалы"),
        //то мы делаем запрос на получение данных по всем бранчам, иначе - только по конкретному бранчу, который мы определяем по его branchId
        if (branchId == ALL_BRANCHES_STATISTIC_ID) {
            //Эта проверка нужна для того, чтобы определять, когда нужно получать общую статистику для всего, а когда нужно получать статистику для графика по выбранной категории
            String ctgr;
            if (spinnerType == CHART_SPINNER) ctgr = category;
            else ctgr = null;

            disposable.add(branchApiService.getAllOrganizationStatistic(organizationId, ctgr, from, "", to)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(response -> {
                                setContent(response, spinnerType);
                                setDataChart(response.getReview().getReviews());
                            },
                            e -> Log.e(TAG, "onError: " + e.getMessage())
                    ));
        } else if (opinionId != -1) {
            disposable.add(branchApiService.getBranchOpinionStatistic(branchId, organizationId, opinionId, from, "", to)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(response -> {
                                setContent(response, spinnerType);
                                setDataChart(response.getReview().getReviews());
                            },
                            e -> Log.e(TAG, "onError: " + e.getMessage())
                    ));
        } else
            disposable.add(branchApiService.getBranchStatistic(branchId, organizationId, category, from, "", to)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(response -> {
                                setContent(response, spinnerType);
                                setDataChart(response.getReview().getReviews());
                            },
                            e -> Log.e(TAG, "onError: " + e.getMessage())
                    ));
    }

    private void setContent(BranchStatisticResponse statsResponse, int spinnerType) {
        ratingBar.setFocusable(false);
        if (spinnerType == BRANCH_SPINNER) {
            setNPSContent(statsResponse.getNps());
            totalReview.setText(statsResponse.getReview().getNumberOfReviews());
            totalConversation.setText(statsResponse.getNumberOfBranchChats());
        }

        ratingTitle.setText(statsResponse.getReview().getAverageImpression());
        ratingBar.setProgress((int) (Float.valueOf(statsResponse.getReview().getAverageImpression()) * 10));
    }

    private void setNPSContent(NPSResponse npsResponse) {
        int nps;
        int promoters;
        int neutral;
        int detractors;

        if (npsResponse == null) {
            tvNPS.setText(App.getContext().getString(R.string.nps_no_data));
            tvNPS.setBackground(ContextCompat.getDrawable(this, R.drawable.tv_nps_big_circle_yellow));
            tvNPS.setTextSize(17);
            promoters = 0;
            neutral = 0;
            detractors = 0;
        } else {
            nps = (int) Float.parseFloat(npsResponse.getNps());

            if (nps > 70 && nps <= 100) {
                tvNPS.setBackground(ContextCompat.getDrawable(this, R.drawable.tv_nps_big_circle_green));
                tvNPS.setTextColor(ContextCompat.getColor(this, R.color.colorDarkerGreenNPS));
            } else if (nps > 30 && nps <= 70) {
                tvNPS.setBackground(ContextCompat.getDrawable(this, R.drawable.tv_nps_big_circle_green));
                tvNPS.setTextColor(ContextCompat.getColor(this, R.color.colorDarkerGreenNPS));
            } else if (nps < 31 && nps >= 0) {
                tvNPS.setBackground(ContextCompat.getDrawable(this, R.drawable.tv_nps_big_circle_yellow));
                tvNPS.setTextColor(ContextCompat.getColor(this, R.color.colorYellowNPS));
            } else if (nps < 0 && nps >= -100) {
                tvNPS.setBackground(ContextCompat.getDrawable(this, R.drawable.tv_nps_big_circle_red));
                tvNPS.setTextColor(ContextCompat.getColor(this, R.color.colorRedNPS));
            }

            tvNPS.setText(String.format(Locale.getDefault(), "%d", nps));
            tvNPS.setTextSize(30);

            promoters = (int) Float.parseFloat(npsResponse.getPromoters());
            neutral = (int) Float.parseFloat(npsResponse.getNeutral());
            detractors = (int) Float.parseFloat(npsResponse.getDetractor());
        }

        tvPromoters.setText(String.format(Locale.getDefault(), "%d", promoters) + "%");

        tvNeutral.setText(String.format(Locale.getDefault(), "%d", neutral) + "%");

        tvDetractors.setText(String.format(Locale.getDefault(), "%d", detractors) + "%");
    }

    private void setIntervalDate(int range) {
        Calendar c = Calendar.getInstance(TimeZone.getDefault()); // this takes current date
        c.set(Calendar.HOUR_OF_DAY, 23);
        c.set(Calendar.MINUTE, 59);
        c.set(Calendar.SECOND, 00);

        String ed = +c.get(Calendar.DAY_OF_MONTH) + "/" + (c.get(Calendar.MONTH) + 1) + "/" + c.get(Calendar.YEAR);

        endDate = getFormattedDate(c);
        btnEndDate.setText(ed);

        c.add(Calendar.DATE, range);
        c.set(Calendar.MINUTE, 0);

        c.set(Calendar.HOUR_OF_DAY, 0);

        startDate = getFormattedDate(c);

        Log.v("DateInfo", "ZonedDate: " + startDate);

        String sd = +c.get(Calendar.DAY_OF_MONTH) + "/" + (c.get(Calendar.MONTH) + 1) + "/" + c.get(Calendar.YEAR);
        btnStartDate.setText(sd);

        getStatsByRange(branchId, category, saveLoadData.loadInt(ORGANIZATION_ID_KEY), timeRange, BRANCH_SPINNER);

        Log.v(TAG, "DATACLick: " + startDate + " : " + endDate);
    }

    public void setStartDate() {
        // Get Current Date
        final Calendar calendar = Calendar.getInstance();
        mYear = calendar.get(Calendar.YEAR);
        mMonth = calendar.get(Calendar.MONTH);
        mDay = calendar.get(Calendar.DAY_OF_MONTH);

        datePickerDialog = new DatePickerDialog(this, R.style.DialogTheme,
                (view, year, monthOfYear, dayOfMonth) -> {
                    btnStartDate.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);

                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    calendar.set(Calendar.MONTH, monthOfYear);
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.HOUR_OF_DAY, 0);
                    calendar.set(Calendar.MINUTE, 0);
                    calendar.set(Calendar.SECOND, 0);

                    startDate = getFormattedDate(calendar);
                    Log.v("DateInfo", "ZonedDate: " + startDate);

                    getStatsByPeriod(branchId, category, startDate, saveLoadData.loadInt(ORGANIZATION_ID_KEY), endDate, BRANCH_SPINNER);

                    Log.v(TAG, "time: " + startDate);
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
        //Устанавливаем цвет текста для кнопок
        datePickerDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(this, R.color.accent));
        datePickerDialog.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(this, R.color.accent));
    }

    public void setEndDate() {
        //Get Current Date
        final Calendar calendar = Calendar.getInstance();
        mYear = calendar.get(Calendar.YEAR);
        mMonth = calendar.get(Calendar.MONTH);
        mDay = calendar.get(Calendar.DAY_OF_MONTH);

        datePickerDialog = new DatePickerDialog(this, R.style.DialogTheme,
                (view, year, monthOfYear, dayOfMonth) -> {
                    btnEndDate.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);

                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    calendar.set(Calendar.MONTH, monthOfYear);
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.HOUR, 23);
                    calendar.set(Calendar.MINUTE, 59);
                    calendar.set(Calendar.SECOND, 00);

                    endDate = getFormattedDate(calendar);
                    Log.v("DateInfo", "ZonedDate: " + endDate);

                    getStatsByPeriod(branchId, category, startDate, saveLoadData.loadInt(ORGANIZATION_ID_KEY), endDate, BRANCH_SPINNER);

                }, mYear, mMonth, mDay);
        datePickerDialog.show();
        //Устанавливаем цвет текста для кнопок
        datePickerDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(this, R.color.accent));
        datePickerDialog.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(this, R.color.accent));
    }

    @NotNull
    private String getFormattedDate(Calendar c) {
        //Получаем дату в с TimeZone
        Date date = new Date(c.getTimeInMillis());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
        sdf.setTimeZone(TimeZone.getDefault());
        String text = sdf.format(date);
        //Добавляем двуеточие перед последними двумя числами, потому что сервер принимает дату именно в таком формате
        StringBuilder builder = new StringBuilder(text);
        builder.insert(text.length() - 2, ":");
        return builder.toString();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.home:
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
                notificationApiService
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
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    private void setDataChart(List<ReviewHourStatsItem> reviewHourStatsItems) {
        LineChartView chartView = findViewById(R.id.chart_view);
        chartView.setZoomEnabled(false);
        chartView.setScrollEnabled(false);

        if (reviewHourStatsItems == null || reviewHourStatsItems.size() == 0) {
            tvChart.setText(getString(R.string.tvChart));
            chartView.setLineChartData(getEmptyChartData());
            return;
        }

        for (ReviewHourStatsItem reviewHourStatsItem : reviewHourStatsItems) {
            int count = Integer.parseInt(reviewHourStatsItem.getCount());
            if (count == 0) {
                tvChart.setText(getString(R.string.tvChart));
            } else {
                tvChart.setText("");
                break;
            }
        }

        ArrayList<String> axisData = new ArrayList();
        ArrayList<Float> yAxisData = new ArrayList();

        ArrayList xAxisValues = new ArrayList();
        ArrayList yAxisValues = new ArrayList();

        for (ReviewHourStatsItem reviewHourStatsItem : reviewHourStatsItems)
            axisData.add(dateStringParseAndFormat(reviewHourStatsItem.getDate()));
        for (ReviewHourStatsItem reviewHourStatsItem : reviewHourStatsItems)
            yAxisData.add(Float.valueOf(reviewHourStatsItem.getImpression()));

        for (int i = 0; i < axisData.size(); i++) {
            xAxisValues.add(new AxisValue(i).setLabel(axisData.get(i)));
        }

        for (int i = 0; i < yAxisData.size(); i++) {
            yAxisValues.add(new PointValue(i, yAxisData.get(i)));
        }

        Line line = new Line(yAxisValues);
        line.setColor(ContextCompat.getColor(this, R.color.colorPrimary));
        line.setStrokeWidth(2);
        line.setHasPoints(false);
        line.setPathEffect(new CornerPathEffect(100));

        ArrayList lines = new ArrayList();
        lines.add(line);

        LineChartData data = new LineChartData();
        data.setLines(lines);

        Axis xAxis = new Axis();
        xAxis.setValues(xAxisValues);
        data.setAxisXBottom(xAxis);

        Axis yAxis = new Axis();
        data.setAxisYLeft(yAxis);

        chartView.setLineChartData(data);

        //Код предназначенный для того, чтобы верхняя и боковая(справа) части графика не обрезались
        Viewport viewport = new Viewport(chartView.getMaximumViewport());
        viewport.top = 5.0f;
        viewport.right = viewport.right + 1;
        chartView.setMaximumViewport(viewport);
        chartView.setCurrentViewport(viewport);
    }

    /* Метод нужен для того, чтобы задать пустые данные графику в том случае, когда приходящие данные с сервера равны null.
    Если его не очистить, то, после прихода пустого ответа от запроса на сервер, покажеться текст "Нет данных за выбранный период"
    и метод завершит своё выполнение, но на самом графике останутся показываться данные из прошлого запроса. Поэтому нужно очищать график. - O.P.Code */
    private LineChartData getEmptyChartData() {
        ArrayList xAxisValues = new ArrayList();
        ArrayList yAxisValues = new ArrayList();

        xAxisValues.add(new AxisValue(0).setLabel("0"));
        yAxisValues.add(new PointValue(0, 0));

        LineChartData data = new LineChartData();

        Axis xAxis = new Axis();
        xAxis.setValues(xAxisValues);
        data.setAxisXBottom(xAxis);

        Axis yAxis = new Axis();
        data.setAxisYLeft(yAxis);

        return data;
    }

    private String dateStringParseAndFormat(String dateString) {
        SimpleDateFormat sd1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        Date dt = null;
        try {
            dt = sd1.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        SimpleDateFormat sd2 = new SimpleDateFormat("dd.MM");

        String date = sd2.format(dt);
        return date;
    }

    @Override
    public void callback(UserSummaryResponse o) {
        this.userSummaryResponse = o;
    }

    public void userRequest() {
        UserApiService userApiService = ApiClient.getClient(getApplicationContext()).create(UserApiService.class);
        new CompositeDisposable().add(userApiService.getUserById(saveLoadData.loadInt(USER_ID_KEY))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe((x) -> {
                    findViewById(R.id.drawer).setVisibility(View.INVISIBLE);
                    findViewById(R.id.progress_barM).setVisibility(View.VISIBLE);
                })
                .doOnSuccess((x) -> {/* TODO hide Loading Spinner*/
                    findViewById(R.id.progress_barM).setVisibility(View.INVISIBLE);
                    findViewById(R.id.drawer).setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.INVISIBLE);
                })
                .onErrorResumeNext(userApiService.getUserById(saveLoadData.loadInt(USER_ID_KEY)))
                .subscribe(
                        response -> {
                            SharedPreferences.Editor editor = HomeActivity.this.getApplicationContext().getSharedPreferences("USEROBJECT", Context.MODE_PRIVATE).edit();
                            editor.putString("meUserObject", new Gson().toJson(response));
                            editor.apply();
                            Log.v(TAG, "USER_ID: " + response.getId());
                            userSummaryResponse = response;

                            //NavigationView инициализируется тут и данные в его хэдер также устанавливаются тут, потому что нам нужно в хэдер помещать данные из UserSummaryResponse.
                            //А UserSummaryResponse приходит в отдельном потоке, поэтому чтобы данные установились, их нужно устанавливать только после того, как они придут с сервера.
                            navigationView = findViewById(R.id.nvViewHome);
                            setNavigationDrawerHeaderContent();
                            setOnNavigationView();

                            HomeActivity.this.firebaseInitializingSendToken();

                            HomeActivity.this.setButtonStyle(todayButton, R.color.colorWhite, R.color.colorDark);
                            HomeActivity.this.setButtonStyle(weekButton, R.color.colorRedNPS, R.color.colorWhite);
                            HomeActivity.this.setButtonStyle(monthButton, R.color.colorWhite, R.color.colorDark);

                            //Изначально timeRange задаётся в onCreate, поэтому ошибки NPE быть не должно, но эта проверка нужна для того, чтобы понимать,
                            //какой именно запрос осуществлять, за период, выбранный по датам, или же из предоставленных периодов (ДЕНЬ, НЕДЕЛЯ, МЕСЯЦ)
                            if (timeRange != null && !timeRange.isEmpty())
                                getStatsByRange(branchId, category, saveLoadData.loadInt(ORGANIZATION_ID_KEY), timeRange, BRANCH_SPINNER);
                        },
                        e -> {
                            progressBar.setVisibility(View.INVISIBLE);
                            Log.e(TAG, "onError: " + e.getMessage());
                        }
                ));
    }

    private void setBranchSpinnerBtn() {
        Intent intent = new Intent(this, BranchChartListActivity.class);
        intent.putExtra("list_type", BranchChartListActivity.BRANCH_LIST);
        btnChooseBranch.setOnClickListener(view -> startActivity(intent));
    }

    private void setChartSpinnerBtn() {
        Intent intent = new Intent(this, BranchChartListActivity.class);
        intent.putExtra("list_type", BranchChartListActivity.CHART_LIST);
        btnChooseChart.setOnClickListener(view -> startActivity(intent));
    }

    @Override
    protected void onResume() {
        super.onResume();
        String chartType = saveLoadData.loadString("chart_data_name");
        long opinionId = saveLoadData.loadLong("chart_data_opinion_id");
        int spinnerType = -1;

        String branchName = saveLoadData.loadString("branch_data_name");
        long branchId = saveLoadData.loadLong("branch_data_id");

        if (chartType != null && !chartType.equals("") && opinionId == -1) {
            btnChooseChart.setText(chartType);
            category = chartType;
            this.opinionId = opinionId;
            spinnerType = CHART_SPINNER;
            if (chartType.equals(getString(OVERALL.getValue())))
                this.branchId = ALL_BRANCHES_STATISTIC_ID;
        } else if (chartType != null && !chartType.equals("")) {
            btnChooseChart.setText(chartType);
            this.opinionId = opinionId;
            spinnerType = CHART_SPINNER;

            if (chartType.equals(getString(OVERALL.getValue())))
                this.branchId = ALL_BRANCHES_STATISTIC_ID;
        } else btnChooseChart.setText(getString(R.string.choose_category));

        if (branchName != null && !branchName.equals("")) {
            btnChooseBranch.setText(branchName);
            String value = getString(TypesEnum.OVERALL.getValue());
            //Если выбран пункт "Все филиалы", то присваиваем глобальной переменной branchId значение ALL_BRANCHES_STATISTIC_ID. Иначе - присвываем глобальной переменной branchId айдишник выбранного бранча
            //Это значение потом (В методах getStatsByPeriod() и getStatsByRange()) поможет определить, запрашивать статистику по всем филиалам или же по конкретному
            if (branchName.equals(value)) this.branchId = ALL_BRANCHES_STATISTIC_ID;
            else this.branchId = branchId;

            spinnerType = BRANCH_SPINNER;
        }

        if (!timeRange.equals("") || !startDate.equals("") && !endDate.equals("")) {
            if (timeRange.equals(""))
                getStatsByPeriod(this.branchId, category, startDate, saveLoadData.loadInt(ORGANIZATION_ID_KEY), endDate, spinnerType);
            else
                getStatsByRange(this.branchId, category, saveLoadData.loadInt(ORGANIZATION_ID_KEY), timeRange, spinnerType);
        }
    }

    /*Этот метод используется для того, чтобы получить нужный enum для запроса на сервер.
    Не используется он внутри самого файла PromoTypes,
    потому что при использовании мультиязычности нельзя использовать контекст всего приложения ( App.getContext() (как было раньше в методе файла PromoTypes)),
    потому что при переводе(при перезапуске) оно в памяти оставляет ресурсы прошлого перевода.
    Этот метод(fromString(String text)) используется в этом классе и получает строковые ресурсы с помощью контекста активити.
    Только при использовании контекста активити всё работает как нужно. */
    public PromoTypes fromString(String text) {
        for (PromoTypes b : PromoTypes.values()) {
            String promoType = getString(b.getValue());
            if (promoType.equalsIgnoreCase(text)) {
                return b;
            }
        }
        return null;
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
