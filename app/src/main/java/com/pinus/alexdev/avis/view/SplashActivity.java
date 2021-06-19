package com.pinus.alexdev.avis.view;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.animation.AnimationUtils;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.pinus.alexdev.avis.Helper.LocaleHelper;
import com.pinus.alexdev.avis.R;
import com.pinus.alexdev.avis.view.home_activity.HomeActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.adorsys.android.securestoragelibrary.SecurePreferences;
import io.reactivex.disposables.CompositeDisposable;

public class SplashActivity extends AppCompatActivity {
    private static final String TAG = SplashActivity.class.getSimpleName();
    private CompositeDisposable disposable = new CompositeDisposable();
    Intent intent;

    @BindView(R.id.splash_layout)
    ConstraintLayout splash_layout;

    public static Context context;

    /*   @BindView(R.id.progress_bar)
       ProgressBar progressBar;*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);
        context = this;

        splash_layout.setAnimation(AnimationUtils.loadAnimation(this, R.anim.anim));

        Thread run1 = new Thread(() -> {
            try {
                Thread.sleep(2000);
                if (getPreference("email") == null && getPreference("pass") == null) {
                    intent = new Intent(this, LoginActivity.class);
                    //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                } else {
                    intent = new Intent(this, HomeActivity.class);
                }
                startActivity(intent);
                finish();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        run1.start();

    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase, "en"));
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    private String getPreference(String key) {
        // Log.v(key, SecurePreferences.getStringValue(key,getApplicationContext(), null));
        return SecurePreferences.getStringValue(key, getApplicationContext(), null);
    }

    public static Context getContext() {
        return context;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
