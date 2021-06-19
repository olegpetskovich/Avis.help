package com.pinus.alexdev.avis.view.settings_activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.iid.FirebaseInstanceId;
import com.pinus.alexdev.avis.R;
import com.pinus.alexdev.avis.dto.request.TokenRequest;
import com.pinus.alexdev.avis.network.ApiClient;
import com.pinus.alexdev.avis.network.apiServices.NotificationApiService;
import com.pinus.alexdev.avis.utils.SaveLoadData;
import com.pinus.alexdev.avis.view.BaseToolbarBack;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static com.pinus.alexdev.avis.view.LoginActivity.USER_ID_KEY;
import static com.pinus.alexdev.avis.view.home_activity.HomeActivity.ANDROID_DEVICE_ID_KEY;

public class NotificationsActivity extends BaseToolbarBack {
    private final String TAG = NotificationsActivity.class.getSimpleName();

    public static final String REVIEW_NOTIFICATION_KEY = "reviewSwitchStatus";
    public static final String CHAT_NOTIFICATION_KEY = "chatSwitchStatus";
    public static final String CTA_NOTIFICATION_KEY = "ctaSwitchStatus";
    public static final String SURVEY_NOTIFICATION_KEY = "surveySwitchStatus";

    @BindView(R.id.appbar_back)
    View appbar;

    @BindView(R.id.backLayout)
    LinearLayout backTo;

    @BindView(R.id.scanQrButton)
    ImageButton qrScanButton;

    @BindView(R.id.reviewSwitch)
    SwitchCompat reviewSwitch;

    @BindView(R.id.chatsSwitch)
    SwitchCompat chatsSwitch;

    @BindView(R.id.ctaSwitch)
    SwitchCompat ctaSwitch;

    @BindView(R.id.surveySwitch)
    SwitchCompat surveySwitch;

    NotificationApiService notificationApiService;

    //Класс для удобной работы с SharedPreferences
    private SaveLoadData saveLoadData;

    String token = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);
        ButterKnife.bind(this);
        startScan(qrScanButton);
        setButtonBackClick(
                appbar.findViewById(R.id.appBarTitle),
                getResources().getString(R.string.notificationsTitle),
                appbar.findViewById(R.id.logoImageBack),
                backTo,
                appbar.findViewById(R.id.toolbarPreviosActivityTitle),
                "",
                null
        );

        notificationApiService = ApiClient.getClient(getApplicationContext()).create(NotificationApiService.class);

        saveLoadData = new SaveLoadData(this);
        setFirstTimeDefaultChecked();

        setSwitchViewContent(reviewSwitch, REVIEW_NOTIFICATION_KEY);
        setSwitchViewContent(chatsSwitch, CHAT_NOTIFICATION_KEY);
        setSwitchViewContent(ctaSwitch, CTA_NOTIFICATION_KEY);

        //Пока что это View не реализовано, поэтому оно отключено
//        setSwitchViewContent(surveySwitch, SURVEY_NOTIFICATION_KEY);
        surveySwitch.setEnabled(false);
        surveySwitch.setTrackTintList(ContextCompat.getColorStateList(this, R.color.accent));
    }

    private void setFirstTimeDefaultChecked() {
        //При первом запуске приложения все уведомления по дефолту будут включены.
        //Точно такой же код находится в классе FCMService, чтобы уведомления включались сразу в двух случаях:
        // • Когда пришло первое уведомление и все уведомления сразу устанавливаются на включенные, то есть на true (В классе FCMService)
        // • Когда пользователь открыл это активити и при открытии вызывается этот метод, в котором все уведомления сразу устанавливаются на включенные, то есть на true
        if (saveLoadData.isApplicationFirstRun("firstRunREVIEW", true)) {
            saveLoadData.saveBoolean(NotificationsActivity.REVIEW_NOTIFICATION_KEY, true);
            saveLoadData.saveBoolean("firstRunREVIEW", false);
        }

        if (saveLoadData.isApplicationFirstRun("firstRunCHAT", true)) {
            saveLoadData.saveBoolean(NotificationsActivity.CHAT_NOTIFICATION_KEY, true);
            saveLoadData.saveBoolean("firstRunCHAT", false);
        }

        if (saveLoadData.isApplicationFirstRun("firstRunCTA", true)) {
            saveLoadData.saveBoolean(NotificationsActivity.CTA_NOTIFICATION_KEY, true);
            saveLoadData.saveBoolean("firstRunCTA", false);
        }

//        if (saveLoadData.isApplicationFirstRun("firstRunSURVEY", true)) {
//            saveLoadData.saveBoolean(NotificationsActivity.SURVEY_NOTIFICATION_KEY, true);
//            saveLoadData.saveBoolean("firstRunSURVEY", false);
//        }
    }

    private void setSwitchViewContent(SwitchCompat switchView, String NOTIFICATION_KEY) {
        switchView.setChecked(saveLoadData.loadBoolean(NOTIFICATION_KEY));
        boolean reviewSwitchState = switchView.isChecked();
        if (reviewSwitchState)
            switchView.setTrackTintList(ContextCompat.getColorStateList(this, R.color.colorPrimary));
        else
            switchView.setTrackTintList(ContextCompat.getColorStateList(this, R.color.accent));

        switchView.setOnCheckedChangeListener((buttonView, isChecked) -> {
            FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(task -> {
                if (!task.isSuccessful()) {
                    Log.w(TAG, "getInstanceId failed", task.getException());
                    return;
                }
                // Get new Instance ID token
                token = task.getResult().getToken();
                Log.v("TokenTAG", "TokenLogIn: " + token);
                notificationApiService = ApiClient.getClient(getApplicationContext()).create(NotificationApiService.class);
            });

            if (isChecked) {
                saveLoadData.saveBoolean(NOTIFICATION_KEY, true);
                switchView.setTrackTintList(ContextCompat.getColorStateList(this, R.color.colorPrimary));
            } else {
                saveLoadData.saveBoolean(NOTIFICATION_KEY, false);
                switchView.setTrackTintList(ContextCompat.getColorStateList(this, R.color.accent));
            }

            boolean ctaState = saveLoadData.loadBoolean(NotificationsActivity.CTA_NOTIFICATION_KEY);
            boolean chatState = saveLoadData.loadBoolean(NotificationsActivity.CHAT_NOTIFICATION_KEY);
            boolean reviewState = saveLoadData.loadBoolean(NotificationsActivity.REVIEW_NOTIFICATION_KEY);
            boolean surveyState = saveLoadData.loadBoolean(NotificationsActivity.SURVEY_NOTIFICATION_KEY); // - это функция ещё не реализована в проекте, но я отправляю её, чтобы заполнить DTO. В boolean переменной значение по умолчанию всё равно устанавливается на false.

            disposable.add(notificationApiService.updateToken(saveLoadData.loadLong(ANDROID_DEVICE_ID_KEY), new TokenRequest(ctaState, chatState, reviewState, surveyState, 7), saveLoadData.loadInt(USER_ID_KEY))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(tokenResponse -> {

                    }, e -> Log.e(TAG, "onError: " + e.getMessage())));
        });
    }
}
