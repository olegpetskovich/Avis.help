package com.pinus.alexdev.avis.view;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.dagang.library.GradientButton;
import com.google.gson.Gson;
import com.pinus.alexdev.avis.Helper.LocaleHelper;
import com.pinus.alexdev.avis.R;
import com.pinus.alexdev.avis.dto.response.NewCtaResponse;
import com.pinus.alexdev.avis.network.ApiClient;
import com.pinus.alexdev.avis.network.apiServices.CTAService;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompClient;

import static com.pinus.alexdev.avis.utils.UserDataPref.getUserSummaryInfo;
import static ua.naiksoftware.stomp.dto.LifecycleEvent.Type.CLOSED;
import static ua.naiksoftware.stomp.dto.LifecycleEvent.Type.ERROR;
import static ua.naiksoftware.stomp.dto.LifecycleEvent.Type.FAILED_SERVER_HEARTBEAT;
import static ua.naiksoftware.stomp.dto.LifecycleEvent.Type.OPENED;

public class CTADialogActivity extends Activity {
    private static final String TAG = CTADialogActivity.class.getSimpleName();

    @BindView(R.id.tvQR)
    TextView tvQR;

    @BindView(R.id.tvTask)
    TextView tvTask;

    @BindView(R.id.btnDone)
    GradientButton btnDone;

    private CTAService ctaService;
    private Intent intent;

    private String ctaName;
    private String ctaAnswer;
    private long ctaID;

    private CompositeDisposable disposable = new CompositeDisposable();

    private StompClient mStompClient;
    private String username = null;
    private long id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ctadialog);
        ButterKnife.bind(this);
        //Вызов этого метода отключает возможность закрытия диалога просто касанием по пустому месту
        setFinishOnTouchOutside(false);

        ctaService = ApiClient.getClient(getApplicationContext()).create(CTAService.class);

        intent = getIntent();
        ctaName = intent.getStringExtra("ctaName");
        ctaAnswer = intent.getStringExtra("ctaAnswer");
        ctaID = intent.getLongExtra("ctaID", -1);

        //Это присваивание нужно для того, чтоб дальше, при закрытии активити,
        // можно было проверить id открытого СТА и закрыть именно его,
        // потому что в противно случае закрываются все активити в стеке, даже если надо закрывать одно
        id = ctaID;

        tvQR.setText(ctaName + ":");
        tvTask.setText(ctaAnswer);

        btnDone.getButton().setOnClickListener(v ->
                disposable.add(ctaService.completeCta(ctaID)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(completeCtaResponse -> {
//                            if (completeCtaResponse. != null)
//                            finish();
                        })));

        mStompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, "wss://qr-ticket-stage.eu-west-3.elasticbeanstalk.com/ws/websocket");

        username = getUserSummaryInfo(getApplicationContext()).getFirstName();
        connectStomp(username);
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
                    if (ctaResponse.isDone() && id == ctaResponse.getId()) {
                        finish();
                        id = 0;
                    }
                }, throwable -> Log.e(TAG, "Error on subscribe topic", throwable));

        disposable.add(dispTopic);
        mStompClient.connect();
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

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase, "en"));
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onBackPressed() {
        //Этот переопределённый метод с пустой реализацией позволяет запретить отключать диалог нажатием кнопки назад
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disconnectStomp();
        resetSubscriptions();
        disposable.dispose();
    }
}
