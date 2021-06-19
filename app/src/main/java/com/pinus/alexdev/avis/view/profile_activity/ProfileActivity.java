package com.pinus.alexdev.avis.view.profile_activity;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.dagang.library.GradientButton;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.navigation.NavigationView;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.jakewharton.rxbinding3.view.RxView;
import com.muddzdev.styleabletoast.StyleableToast;
import com.pinus.alexdev.avis.R;
import com.pinus.alexdev.avis.dto.request.TokenRequest;
import com.pinus.alexdev.avis.dto.request.UserRequest;
import com.pinus.alexdev.avis.dto.response.user_summary_response.UserSummaryResponse;
import com.pinus.alexdev.avis.network.ApiClient;
import com.pinus.alexdev.avis.network.apiServices.NotificationApiService;
import com.pinus.alexdev.avis.network.apiServices.UserApiService;
import com.pinus.alexdev.avis.utils.SaveLoadData;
import com.pinus.alexdev.avis.utils.Utils;
import com.pinus.alexdev.avis.view.BaseNavigationView;
import com.pinus.alexdev.avis.view.PhotoCropActivity;
import com.pinus.alexdev.avis.view.billing_activity.BillingActivity;
import com.pinus.alexdev.avis.view.company_activity.CompanyActivity;
import com.pinus.alexdev.avis.view.conversation_activity.ConversationListReviewsActivity;
import com.pinus.alexdev.avis.view.cta_activity.CTAActivity;
import com.pinus.alexdev.avis.view.home_activity.HomeActivity;
import com.pinus.alexdev.avis.view.LoginActivity;
import com.pinus.alexdev.avis.view.promo_code_activity.PromoCodesActivity;
import com.pinus.alexdev.avis.view.qr_manager_activity.QRManagerActivity;
import com.pinus.alexdev.avis.view.review_list_activity.ReviewsListActivity;
import com.pinus.alexdev.avis.view.settings_activity.SettingsActivity;
import com.pinus.alexdev.avis.view.team_activity.TeamActivity;
import com.squareup.picasso.Picasso;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.adorsys.android.securestoragelibrary.SecurePreferences;
import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import static com.pinus.alexdev.avis.utils.UserDataPref.getUserSummaryInfo;
import static com.pinus.alexdev.avis.utils.UserDataPref.userRequest;
import static com.pinus.alexdev.avis.view.LoginActivity.USER_ID_KEY;
import static com.pinus.alexdev.avis.view.PhotoCropActivity.INTENT_ACTIVITY_NAME_KEY;
import static com.pinus.alexdev.avis.view.PhotoCropActivity.INTENT_URI_KEY;
import static com.pinus.alexdev.avis.view.home_activity.HomeActivity.ANDROID_DEVICE_ID_KEY;

public class ProfileActivity extends BaseNavigationView implements NavigationView.OnNavigationItemSelectedListener {
    @BindView(R.id.appbar_back)
    View appbar;

    CircleImageView profileImage;

    @BindView(R.id.logoutButton)
    Button logoutButton;

    @BindView(R.id.emailText)
    EditText emailText;

    @BindView(R.id.btnChangePassword)
    MaterialButton btnChangePassword;

    @BindView(R.id.nameText)
    EditText nameText;

    @BindView(R.id.surnameText)
    EditText surnameText;

    @BindView(R.id.telNumberText)
    EditText telNumberText;

    @BindView(R.id.saveButton)
    GradientButton saveButton;

    @BindView(R.id.avatarImageProfile)
    CircleImageView avatarImage;

    @BindView(R.id.scanQrButton)
    ImageButton qrScanButton;

    private static final String TAG = ProfileActivity.class.getSimpleName();

    private UserApiService userApiService;

    private static final int GALLERY_REQUEST = 5;
    File file;

    public static Bitmap updatedPhotoBitmap;

    //Поле вынесено на уровень класса, чтобы при смене email(a) текст в хэдере менялся сразу, а не после перезапуска активити
    TextView tvHeaderEmail;

    //Класс для удобной работы с SharedPreferences
    SaveLoadData saveLoadData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);
        startScan(qrScanButton);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setTitleAndLogoClick(appbar.findViewById(R.id.appBarTitle), R.string.profileTitle, appbar.findViewById(R.id.logoImageApp));

        saveLoadData = new SaveLoadData(this);

        if (!Utils.isNetworkAvailable(this))
            StyleableToast.makeText(getApplicationContext(), getString(R.string.internet_connection), Toast.LENGTH_LONG, R.style.mytoast).show();

        userSummaryResponse = getUserSummaryInfo(getApplicationContext());

        navigationView = findViewById(R.id.nvViewProfile);
        setNavigationDrawerHeaderContent();
        setOnNavigationView();

        userApiService = ApiClient.getClient(getApplicationContext()).create(UserApiService.class);

//        setContent(getUserSummaryInfo(getApplicationContext()));

        setLogoutButton();

        disposable.add(RxView.clicks(btnChangePassword).subscribe(unit -> startActivity(new Intent(this, ChangePasswordActivity.class))));

        disposable.add(RxView.clicks(saveButton.getButton()).subscribe(t -> saveProfile()));
        disposable.add(RxView.clicks(avatarImage).subscribe(t -> {
                    final RxPermissions rxPermissions = new RxPermissions(ProfileActivity.this);
                    disposable.add(rxPermissions
                            .request(Manifest.permission.READ_EXTERNAL_STORAGE)
                            .subscribe(granted -> {
                                if (granted) { // Always true pre-M
                                    ProfileActivity.this.getIntentImage();
                                } else {
                                    StyleableToast.makeText(ProfileActivity.this.getApplicationContext(), "You don't have permission", Toast.LENGTH_LONG, R.style.mytoast).show();
                                }
                            }));
                }
        ));

        if (updatedPhotoBitmap != null) uploadImage(updatedPhotoBitmap);
        else setUserContent();
    }

    private void setUserContent() {
        disposable.add(userApiService
                .getUserById(saveLoadData.loadInt(USER_ID_KEY))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::setContent,
                        new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable e) throws Exception {
                                Log.e(TAG, "onError: " + e.getMessage());
                                StyleableToast.makeText(ProfileActivity.this.getApplicationContext(), ProfileActivity.this.getString(R.string.error), Toast.LENGTH_LONG, R.style.mytoast).show();
                            }
                        }));
    }

    private void setLogoutButton() {
        disposable.add(RxView.clicks(logoutButton)
                .subscribe(v ->
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
                                        })));
    }

    private void setNavigationDrawerHeaderContent() {
        View navigationHeader = navigationView.inflateHeaderView(R.layout.top_item_nav_draw);
        navigationHeader.setOnClickListener(v -> {
            DrawerLayout drawerLayout = findViewById(R.id.drawer);
            drawerLayout.closeDrawer(GravityCompat.START);
        });

        profileImage = navigationHeader.findViewById(R.id.profileImage);
        tvHeaderEmail = navigationHeader.findViewById(R.id.tvEmail);
        if (userSummaryResponse != null) {
            if (userSummaryResponse.getAvatarUrl() != null && !userSummaryResponse.getAvatarUrl().isEmpty()) {
                Picasso.get().load(userSummaryResponse.getAvatarUrl()).placeholder(R.drawable.company_logo).error(R.drawable.company_logo).into(profileImage);
            } else profileImage.setImageResource(R.drawable.company_logo);
            tvHeaderEmail.setText(userSummaryResponse.getEmail());
        } else {
            profileImage.setImageResource(R.drawable.company_logo);
            tvHeaderEmail.setText("");
        }
    }

    private void getIntentImage() {
        startActivityForResult(new Intent(Intent.ACTION_PICK)
                .setType("image/*"), GALLERY_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch (requestCode) {
            case GALLERY_REQUEST:
                if (resultCode == RESULT_OK) {
                    Intent intent = new Intent(this, PhotoCropActivity.class);
                    intent.putExtra(INTENT_URI_KEY, imageReturnedIntent.getData().toString());
                    intent.putExtra(INTENT_ACTIVITY_NAME_KEY, "ProfileActivity");
                    startActivity(intent);
                    finish();
                }
        }
    }

    private void uploadImage(Bitmap bitmap) {
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, 200, 200, false);

        //Код для конвертации Bitmap в byteArray
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        resizedBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        resizedBitmap.recycle();

        RequestBody fbody = RequestBody.create(MediaType.parse("image/png"), byteArray);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", "fileName", fbody);

        Log.v("ContentTag", fbody.contentType().toString());
        try {
            Log.v("ContentTag", String.valueOf(fbody.contentLength()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        disposable.add(userApiService.updateAvatar(fbody, body, saveLoadData.loadInt(USER_ID_KEY))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(t -> {
                            Log.v(TAG, "Image success");
                            StyleableToast.makeText(getApplicationContext(), getString(R.string.photoUpload), Toast.LENGTH_LONG, R.style.mytoast).show();
                            userRequest(this); //Этот метод вызывается для того, чтобы в при повторном запуске этого активити, в переменную userSummaryResponse(которая в onCreate()) присваивались обновлённые данные.
                            setUserContent();
                            profileImage.setImageBitmap(bitmap);
                            updatedPhotoBitmap = null;
                        },
                        e -> {
                            Log.e(TAG, "onError: " + e.getMessage());
                            StyleableToast.makeText(getApplicationContext(), getString(R.string.error), Toast.LENGTH_LONG, R.style.mytoast).show();
                        }));
    }

    private void setContent(UserSummaryResponse userSummaryResponse) {
        telNumberText.setText(userSummaryResponse.getPhoneNumber());
        emailText.setText(userSummaryResponse.getEmail());

//        currentPassText.setText(getPreference("pass"));

        nameText.setText(userSummaryResponse.getFirstName());
        surnameText.setText(userSummaryResponse.getLastName());

        Picasso.get().load(userSummaryResponse.getAvatarUrl()).placeholder(R.drawable.avatar).error(R.drawable.avatar).into(avatarImage);
    }

    private String getPreference(String key) {
        // Log.v(key, SecurePreferences.getStringValue(key,getApplicationContext(), null));
        return SecurePreferences.getStringValue(key, getApplicationContext(), null);
    }

    private void saveProfile() {
        //Устанавливаем обновлённый email в хэдер NavigationView
        tvHeaderEmail.setText(emailText.getText().toString());

        disposable.add(userApiService.updateUser(new UserRequest(
                nameText.getText().toString(),
                surnameText.getText().toString(),
                telNumberText.getText().toString(),
                emailText.getText().toString(),
                null), saveLoadData.loadInt(USER_ID_KEY))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doAfterSuccess(t -> {
                    StyleableToast.makeText(getApplicationContext(), getString(R.string.profileUpdated), Toast.LENGTH_LONG, R.style.mytoast).show();
                    userRequest(getApplicationContext());
                }).subscribe(t -> {
                        },
                        e -> {
                            Log.e(TAG, "onError: " + e.getMessage());
                            StyleableToast.makeText(getApplicationContext(), getString(R.string.invalidPromo), Toast.LENGTH_LONG, R.style.mytoast).show();
                        }
                ));
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.home:
                startActivity(new Intent(ProfileActivity.this, HomeActivity.class));
                finish();
                break;
            case R.id.review:
                startActivity(new Intent(ProfileActivity.this, ReviewsListActivity.class));
                finish();
                break;
            case R.id.company:
                startActivity(new Intent(ProfileActivity.this, CompanyActivity.class));
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
                startActivity(new Intent(ProfileActivity.this, PromoCodesActivity.class));
                finish();
                break;
            case R.id.settings:
                startActivity(new Intent(ProfileActivity.this, SettingsActivity.class));
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
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }
}
