package com.pinus.alexdev.avis.view;

import android.content.Context;
import android.content.Intent;
import com.google.android.material.navigation.NavigationView;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;

import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jakewharton.rxbinding3.view.RxView;
import com.pinus.alexdev.avis.Helper.LocaleHelper;
import com.pinus.alexdev.avis.dto.response.user_summary_response.UserSummaryResponse;
import com.pinus.alexdev.avis.utils.BageCountSubscriber;
import com.pinus.alexdev.avis.view.home_activity.HomeActivity;

import io.reactivex.disposables.CompositeDisposable;

public class BaseToolbarBack extends AppCompatActivity {
    private static final String TAG = BaseToolbarBack.class.getSimpleName();
    protected ActionBarDrawerToggle mToogle;

    protected NavigationView navigationView;
    protected BageCountSubscriber bageCountSubscriber = new BageCountSubscriber();
    protected UserSummaryResponse userSummaryResponse;
    protected CompositeDisposable disposable = new CompositeDisposable();

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposable.dispose();
        bageCountSubscriber = null;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    protected void startScan(ImageButton imageButton) {
        disposable.add(RxView.clicks(imageButton).subscribe((t) ->
                startActivity(new Intent(this, CustomScannerActivity.class))
        ));
    }

    protected void setButtonBackClick(TextView titleView,
                                      String stringRes,
                                      ImageView logoView,
                                      LinearLayout backTo,
                                      TextView titleBack,
                                      String stringResBack,
                                      Class<?> intentForStart) {
        titleView.setText(stringRes);
        RxView.clicks(logoView).subscribe(t -> {
            Intent intent = new Intent(this, HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        titleBack.setText(stringResBack);
        disposable.add(RxView.clicks(backTo).subscribe(t -> {
//            startActivity(new Intent(this, intentForStart));
            finish();
        }));
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase, "en"));
    }
}
