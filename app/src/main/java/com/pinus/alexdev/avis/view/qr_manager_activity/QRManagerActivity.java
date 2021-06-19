package com.pinus.alexdev.avis.view.qr_manager_activity;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.dagang.library.GradientButton;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.muddzdev.styleabletoast.StyleableToast;
import com.pinus.alexdev.avis.Helper.QrManagerRecyclerItemTouchHelper;
import com.pinus.alexdev.avis.Helper.RecyclerItemTouchHelperListener;
import com.pinus.alexdev.avis.R;
import com.pinus.alexdev.avis.adapter.QrManagerRVAdapter;
import com.pinus.alexdev.avis.dto.request.TokenRequest;
import com.pinus.alexdev.avis.dto.response.qr_manager_list_response.QrManagerListResponse;
import com.pinus.alexdev.avis.dto.response.qr_manager_list_response.cta_response.CTAResponse;
import com.pinus.alexdev.avis.dto.response.qr_manager_list_response.opinion_response.OpinionResponse;
import com.pinus.alexdev.avis.model.BranchModel;
import com.pinus.alexdev.avis.model.CtaModel;
import com.pinus.alexdev.avis.model.OpinionModel;
import com.pinus.alexdev.avis.model.QrModel;
import com.pinus.alexdev.avis.network.ApiClient;
import com.pinus.alexdev.avis.network.apiServices.NotificationApiService;
import com.pinus.alexdev.avis.network.apiServices.QrManagerApiService;
import com.pinus.alexdev.avis.utils.SaveLoadData;
import com.pinus.alexdev.avis.view.BaseNavigationView;
import com.pinus.alexdev.avis.view.BranchFilterActivity;
import com.pinus.alexdev.avis.view.LoginActivity;
import com.pinus.alexdev.avis.view.cta_activity.CTAActivity;
import com.pinus.alexdev.avis.view.review_list_activity.ReviewsListActivity;
import com.pinus.alexdev.avis.view.settings_activity.SettingsActivity;
import com.pinus.alexdev.avis.view.team_activity.TeamActivity;
import com.pinus.alexdev.avis.view.billing_activity.BillingActivity;
import com.pinus.alexdev.avis.view.company_activity.CompanyActivity;
import com.pinus.alexdev.avis.view.conversation_activity.ConversationListReviewsActivity;
import com.pinus.alexdev.avis.view.home_activity.HomeActivity;
import com.pinus.alexdev.avis.view.profile_activity.ProfileActivity;
import com.pinus.alexdev.avis.view.promo_code_activity.PromoCodesActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.adorsys.android.securestoragelibrary.SecurePreferences;
import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

import static com.pinus.alexdev.avis.utils.UserDataPref.getUserSummaryInfo;
import static com.pinus.alexdev.avis.view.BranchFilterActivity.BRANCH_FILTER_KEY;
import static com.pinus.alexdev.avis.view.LoginActivity.ORGANIZATION_ID_KEY;
import static com.pinus.alexdev.avis.view.LoginActivity.USER_ID_KEY;
import static com.pinus.alexdev.avis.view.home_activity.HomeActivity.ANDROID_DEVICE_ID_KEY;

public class QRManagerActivity extends BaseNavigationView implements NavigationView.OnNavigationItemSelectedListener, RecyclerItemTouchHelperListener {
    private CompositeDisposable disposable = new CompositeDisposable();
    private static final String TAG = QRManagerActivity.class.getSimpleName();

    @BindView(R.id.appbar_back)
    View appbar;

    @BindView(R.id.btnFilter)
    MaterialButton btnFilter;

    @BindView(R.id.btnAddQRCode)
    GradientButton btnAddQRCode;

    @BindView(R.id.scanQrButton)
    ImageButton qrScanButton;

    @BindView(R.id.includedQrTypeLay)
    LinearLayout includedQrTypeLay;

    @BindView(R.id.btnOpinion)
    MaterialButton btnOpinion;

    @BindView(R.id.btnLandingPage)
    MaterialButton btnLandingPage;

    @BindView(R.id.btnSurvey)
    MaterialButton btnSurvey;

    @BindView(R.id.btnCallToAction)
    MaterialButton btnCallToAction;

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    //Класс для удобной работы с SharedPreferences
    private SaveLoadData saveLoadData;

    private QrManagerApiService qrManagerApiService;
    private QrManagerRVAdapter qrManagerRVAdapter;
    private ArrayList<QrModel> qrList;

    private ArrayList<BranchModel> branchesID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_manager);
        ButterKnife.bind(this);
        startScan(qrScanButton);
        saveLoadData = new SaveLoadData(this);

        userSummaryResponse = getUserSummaryInfo(getApplicationContext());
        navigationView = findViewById(R.id.nvViewQR);
        setNavigationDrawerHeaderContent();
        setOnNavigationView();

        setTitleAndLogoClick(appbar.findViewById(R.id.appBarTitle), R.string.menuQRManager, appbar.findViewById(R.id.logoImageApp));

        btnFilter.setOnClickListener(v -> {
            BRANCH_FILTER_KEY = "QR_FILTER";
            startActivity(new Intent(this, BranchFilterActivity.class));
        });

        btnAddQRCode.getButton().setOnClickListener(view -> setLayoutVisibility());

        btnOpinion.setOnClickListener(v -> {
            Intent intent = new Intent(this, OpinionQRActivity.class);
            startActivity(intent);

            setLayoutVisibility();
        });

        btnLandingPage.setOnClickListener(v -> {
            Intent intent = new Intent(this, LandingPageQRActivity.class);
            startActivity(intent);

            setLayoutVisibility();
        });

        btnSurvey.setOnClickListener(v -> {
            Intent intent = new Intent(this, SurveyQRActivity.class);
            startActivity(intent);

            setLayoutVisibility();
        });

        btnCallToAction.setOnClickListener(v -> {
            Intent intent = new Intent(this, CallToActionQRActivity.class);
            startActivity(intent);

            setLayoutVisibility();
        });

        qrManagerApiService = ApiClient.getClient(getApplicationContext()).create(QrManagerApiService.class);

        getQrList();
    }

    private void setLayoutVisibility() {
//        TransitionManager.beginDelayedTransition(includedQrTypeLay);
        if (includedQrTypeLay.getVisibility() == View.VISIBLE) {
            includedQrTypeLay.setVisibility(View.GONE);
        } else {
            includedQrTypeLay.setVisibility(View.VISIBLE);
        }
    }

    @SuppressLint("CheckResult")
    private void getQrList() {
        progressBar.setVisibility(View.VISIBLE);
        disposable.add(qrManagerApiService.getOrganizationQrList(saveLoadData.loadInt(ORGANIZATION_ID_KEY))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        qrList1 -> {
                            setQrManagerContent(qrList1);
                            progressBar.setVisibility(View.GONE);
                        },
                        e -> Log.e(TAG, "onError: " + e.getMessage())
                ));
    }

    private void setQrManagerContent(QrManagerListResponse qrList) {
        ArrayList<QrModel> qrModels = new ArrayList<>();
        for (CTAResponse response : qrList.getCta()) {
            CtaModel model = new CtaModel();
            model.setBranchId(response.getBranchId());
            model.setDefaultLocale(response.getDefaultLocale());
            model.setName(response.getName());
            model.setId(response.getId());
            model.setMessage(response.getMessage());
            model.setOptions(response.getOptions());
            model.setQuestion(response.getQuestion());
            model.setQrCode(response.getQrCode());
            qrModels.add(model);
        }

        for (OpinionResponse response : qrList.getOpinion()) {
            OpinionModel model = new OpinionModel();
            model.setBranchId(response.getBranchId());
            model.setDefaultLocale(response.getDefaultLocale());
            model.setName(response.getName());
            model.setId(response.getId());
            model.setMessage(response.getMessage());
            model.setAskNps(response.isAskNps());
            model.setQuestion(response.getQuestion());
            model.setQrCode(response.getQrCode());
            model.setCategory(response.getCategory());
            qrModels.add(model);
        }

        if (saveLoadData.loadString(BRANCH_FILTER_KEY) != null && !saveLoadData.loadString(BRANCH_FILTER_KEY).isEmpty()) {
            branchesID = new Gson().fromJson(saveLoadData.loadString(BRANCH_FILTER_KEY), new TypeToken<List<BranchModel>>() {
            }.getType());
            ArrayList<QrModel> filteredQrs = new ArrayList<>();
            for (BranchModel branchModel : branchesID) {
                for (int i = 0; i < qrModels.size(); i++) {
                    if (qrModels.get(i) instanceof CtaModel) {
                        CtaModel model = (CtaModel) qrModels.get(i);
                        if (model.getBranchId() == branchModel.getBranchID())
                            filteredQrs.add(model);
                    } else {
                        OpinionModel model = (OpinionModel) qrModels.get(i);
                        if (model.getBranchId() == branchModel.getBranchID())
                            filteredQrs.add(model);
                    }
                }
            }
            qrModels.clear();
            qrModels = filteredQrs;
            saveLoadData.saveString(BRANCH_FILTER_KEY, "");
        }

        this.qrList = qrModels;

        if (qrModels.size() != 0) {
            btnFilter.setVisibility(View.VISIBLE);
        }
//        else {
//            btnFilter.setVisibility(View.GONE);
//        }

        qrManagerRVAdapter = new QrManagerRVAdapter(this, qrModels);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(qrManagerRVAdapter);

        ItemTouchHelper.SimpleCallback itemTouchHelperCallBack = new QrManagerRecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(itemTouchHelperCallBack).attachToRecyclerView(recyclerView);
        qrManagerRVAdapter.notifyDataSetChanged();
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof QrManagerRVAdapter.MyViewHolder) {

            QrModel deletedItem = qrList.get(viewHolder.getAdapterPosition());
            int deleteIndex = viewHolder.getAdapterPosition();

            qrManagerRVAdapter.removeItem(deletedItem, deleteIndex, qrManagerApiService);
        }
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
    protected void onResume() {
        super.onResume();
        getQrList();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mToogle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
