package com.pinus.alexdev.avis.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.jakewharton.rxbinding3.view.RxView;
import com.pinus.alexdev.avis.Helper.LocaleHelper;
import com.pinus.alexdev.avis.R;
import com.pinus.alexdev.avis.dto.response.NewCtaResponse;
import com.pinus.alexdev.avis.dto.response.login_response.UiPermissionsResponse;
import com.pinus.alexdev.avis.dto.response.user_summary_response.UserSummaryResponse;
import com.pinus.alexdev.avis.network.ApiClient;
import com.pinus.alexdev.avis.network.apiServices.BranchApiService;
import com.pinus.alexdev.avis.network.apiServices.CTAService;
import com.pinus.alexdev.avis.utils.BageCountSubscriber;
import com.pinus.alexdev.avis.utils.SaveLoadData;
import com.pinus.alexdev.avis.view.conversation_activity.ConversationActivity;
import com.pinus.alexdev.avis.view.home_activity.HomeActivity;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompClient;

import static com.pinus.alexdev.avis.utils.UserDataPref.getUserSummaryInfo;
import static com.pinus.alexdev.avis.view.LoginActivity.ORGANIZATION_ID_KEY;
import static com.pinus.alexdev.avis.view.LoginActivity.UI_PERMISSION_ID_KEY;

public class BaseNavigationView extends AppCompatActivity {
    private static final String TAG = BaseNavigationView.class.getSimpleName();
    protected ActionBarDrawerToggle mToogle;
    private StompClient mStompClient;

    protected NavigationView navigationView = null;
    protected BageCountSubscriber bageCountSubscriber = new BageCountSubscriber();
    protected UserSummaryResponse userSummaryResponse;
    protected CompositeDisposable disposable = new CompositeDisposable();

    private CTAService ctaService;
    private String username = null;

    //Класс для удобной работы с SharedPreferences
    SaveLoadData saveLoadData;
    UiPermissionsResponse uiPermission;

    protected void setOnNavigationView() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawerLayout = findViewById(R.id.drawer);
        saveLoadData = new SaveLoadData(this);

        updateBadge();

        bageCountSubscriber.setBadge(userSummaryResponse.getFirstName(), this);

        mToogle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };

        ctaService = ApiClient.getClient(getApplicationContext()).create(CTAService.class);
        mStompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, "wss://qr-ticket-stage.eu-west-3.elasticbeanstalk.com/ws/websocket");

        username = getUserSummaryInfo(getApplicationContext()).getFirstName();

        connectStomp(username);
//        openNewCTA();

        drawerLayout.addDrawerListener(mToogle);
        navigationView.bringToFront();

        uiPermission = new Gson().fromJson(saveLoadData.loadString(UI_PERMISSION_ID_KEY), UiPermissionsResponse.class);
        setUiVisibilityByPermissionsValue(navigationView.getMenu());

        navigationView.setNavigationItemSelectedListener((NavigationView.OnNavigationItemSelectedListener) this);
        mToogle.syncState();

        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    private void setUiVisibilityByPermissionsValue(Menu menu) {
        MenuItem homeItem = menu.findItem(R.id.home);
        MenuItem reviewItem = menu.findItem(R.id.review);
        MenuItem conversationsItem = menu.findItem(R.id.conversations);
        MenuItem ctaItem = menu.findItem(R.id.cta);
        MenuItem companyItem = menu.findItem(R.id.company);
        MenuItem promoCodesItem = menu.findItem(R.id.promoCodes);
        MenuItem billingItem = menu.findItem(R.id.billing);
        MenuItem qrManagerItem = menu.findItem(R.id.qrManager);

        boolean statistic = uiPermission.isStatistic();
        boolean review = uiPermission.isReview();
        boolean chats = uiPermission.isChats();
        boolean company = uiPermission.isCompany();
        boolean promo = uiPermission.isPromoCodes();
        boolean cta = uiPermission.isCta();
        boolean billing = uiPermission.isStatistic();
        boolean scanner = uiPermission.isScanner();

        if (!statistic) homeItem.setVisible(false);
        else homeItem.setVisible(true);

        if (!review) reviewItem.setVisible(false);
        else reviewItem.setVisible(true);

        if (!chats) conversationsItem.setVisible(false);
        else conversationsItem.setVisible(true);

        if (!company) companyItem.setVisible(false);
        else companyItem.setVisible(true);

        if (!promo) promoCodesItem.setVisible(false);
        else promoCodesItem.setVisible(true);

        if (!cta) ctaItem.setVisible(false);
        else ctaItem.setVisible(true);

        if (!billing) billingItem.setVisible(false);
        else billingItem.setVisible(true);
    }

    public void connectStomp(String adminUsername) {

        mStompClient.withClientHeartbeat(25000).withServerHeartbeat(1000);

        //  resetSubscriptions();

        Disposable dispLifecycle = mStompClient.lifecycle()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(lifecycleEvent -> {
                    switch (lifecycleEvent.getType()) {
                        case OPENED:
                            Log.v(TAG, "Stomp connection opened");
                            break;
                        case ERROR:
                            Log.e(TAG, "Stomp connection error", lifecycleEvent.getException());
                            Toast.makeText(this, "Stomp connection error", Toast.LENGTH_LONG).show();
                            break;
                        case CLOSED:
                            Toast.makeText(this, "Stomp connection closed", Toast.LENGTH_LONG).show();
                            resetSubscriptions();
                            break;
                        case FAILED_SERVER_HEARTBEAT:
                            Toast.makeText(this, "Stomp failed server heartbeat", Toast.LENGTH_LONG).show();
                            break;
                    }
                });

        disposable.add(dispLifecycle);

        // Receive greetings
        Log.v(TAG, "/channel/" + adminUsername);
        Disposable dispTopic = mStompClient.topic("/channel/cta/" + adminUsername)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(stompMessage -> {
                    NewCtaResponse ctaResponse = new Gson().fromJson(stompMessage.getPayload(), NewCtaResponse.class);
                    if (!ctaResponse.isDone()) {
                        Log.v("MyLog", stompMessage.getPayload());
//                        setCTAIntent(ctaResponse);
                        Intent intent = new Intent(this, CTADialogActivity.class);
                        intent.putExtra("ctaName", ctaResponse.getName());
                        intent.putExtra("ctaAnswer", ctaResponse.getAnswer());
                        intent.putExtra("ctaID", ctaResponse.getId());
                        startActivity(intent);
                    }
                }, throwable -> Log.e(TAG, "Error on subscribe topic", throwable));

        disposable.add(dispTopic);
        mStompClient.connect();
    }

    private void setCTAIntent(NewCtaResponse ctaResponse) {
        Intent intent = new Intent(this, CTADialogActivity.class);
        intent.putExtra("ctaName", ctaResponse.getName());
        intent.putExtra("ctaAnswer", ctaResponse.getAnswer());
        intent.putExtra("ctaID", ctaResponse.getId());
        startActivity(intent);
    }

    public void disconnectStomp() {
        mStompClient.disconnect();
    }

    private void resetSubscriptions() {
        if (disposable != null) {
            disposable.dispose();
        }
        disposable = new CompositeDisposable();
    }

    protected void openNewCTA() {
        disposable.add(ctaService
                .getNewCtaList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(newCtaResponses -> {
                    if (newCtaResponses.size() > 0) {
                        for (NewCtaResponse newCtaResponse : newCtaResponses) {
                            setCTAIntent(newCtaResponse);
                        }
                    }
                }, e -> Log.e(TAG, "onError: " + e.getMessage())));
    }

    @SuppressLint("CheckResult")
    protected void updateBadge() {
        //                    Log.v(TAG, "New auth response: " + response.getNewAuthReviewCount() + " : " + TAG);
        //
        disposable.add(ApiClient.getClient(getApplicationContext()).create(BranchApiService.class).getStatistic(new SaveLoadData(this).loadInt(ORGANIZATION_ID_KEY))
                .subscribeOn(Schedulers.io())
                .repeatWhen(objectFlowable -> objectFlowable.delay(3, TimeUnit.SECONDS))
                .observeOn(AndroidSchedulers.mainThread())
                .filter(f -> f != null && bageCountSubscriber != null)
                .subscribe(this::setMenuCounter, e -> Log.e(TAG, "onError: " + e.getMessage())));
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disconnectStomp();
        resetSubscriptions();
        disposable.dispose();
        bageCountSubscriber = null;
        navigationView = null;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    protected void startScan(ImageButton imageButton) {
        disposable.add(RxView.clicks(imageButton).subscribe((t) -> {
                    if (!uiPermission.isScanner()) {
                        Toast.makeText(this, R.string.you_havent_access, Toast.LENGTH_SHORT).show();
                        return;
                    }

                    startActivity(new Intent(this, CustomScannerActivity.class));
                }
        ));
    }

    protected void setTitleAndLogoClick(TextView titleView, int stringRes, ImageView logoView) {
        titleView.setText(stringRes);
        disposable.add(RxView.clicks(logoView).subscribe(t -> {
            Intent intent = new Intent(this, HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }));
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase, "en"));
    }

    private void setMenuCounter(int messagesCount) {
//        TextView tvAuthReview = (TextView) navigationView.getMenu().findItem(R.id.review).getActionView();
        TextView tvConversations = (TextView) navigationView.getMenu().findItem(R.id.conversations).getActionView();

        //Объединяем количество авторизированных ревью с количеством анонимных,
        //потому что теперь по дизайну они объеденины в один раздел
//        boolean review = setUnreadNotificationItem(authReviewCount + anonReviewCount, tvAuthReview);
        boolean conversation = setUnreadNotificationItem(messagesCount, tvConversations);

//        if (review || conversation)
        if (conversation)
            Objects.requireNonNull(this.getSupportActionBar()).setHomeAsUpIndicator(R.drawable.ic_menu_notif);
        else
            Objects.requireNonNull(this.getSupportActionBar()).setHomeAsUpIndicator(R.drawable.ic_menu);
    }

    private boolean setUnreadNotificationItem(int count, TextView view) {
        if (count > 0) {
            view.setVisibility(View.VISIBLE);
            view.setText(String.valueOf(count));
            view.setBackgroundResource(R.drawable.circle_text_view);
            view.setTextColor(getResources().getColor(android.R.color.white));
            view.setTextSize(12);
            view.setGravity(Gravity.CENTER);
            view.setBackgroundResource(R.drawable.circle_text_view);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.CENTER_VERTICAL;
            view.setLayoutParams(params);
            return true;
        } else {
            view.setVisibility(View.GONE);
            return false;
        }
    }
}
