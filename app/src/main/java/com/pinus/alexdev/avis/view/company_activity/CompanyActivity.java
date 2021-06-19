package com.pinus.alexdev.avis.view.company_activity;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.dagang.library.GradientButton;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.jakewharton.rxbinding3.view.RxView;
import com.muddzdev.styleabletoast.StyleableToast;
import com.pinus.alexdev.avis.R;
import com.pinus.alexdev.avis.adapter.BranchAdapter;
import com.pinus.alexdev.avis.dto.request.OrganizationRequest;
import com.pinus.alexdev.avis.dto.request.TokenRequest;
import com.pinus.alexdev.avis.dto.response.BranchesResponse;
import com.pinus.alexdev.avis.network.ApiClient;
import com.pinus.alexdev.avis.network.ApiClientMultipart;
import com.pinus.alexdev.avis.network.apiServices.BranchApiService;
import com.pinus.alexdev.avis.network.apiServices.NotificationApiService;
import com.pinus.alexdev.avis.utils.SaveLoadData;
import com.pinus.alexdev.avis.utils.Utils;
import com.pinus.alexdev.avis.view.BaseNavigationView;
import com.pinus.alexdev.avis.view.PhotoCropActivity;
import com.pinus.alexdev.avis.view.conversation_activity.ConversationListReviewsActivity;
import com.pinus.alexdev.avis.view.cta_activity.CTAActivity;
import com.pinus.alexdev.avis.view.home_activity.HomeActivity;
import com.pinus.alexdev.avis.view.LoginActivity;
import com.pinus.alexdev.avis.view.promo_code_activity.PromoCodesActivity;
import com.pinus.alexdev.avis.view.qr_manager_activity.QRManagerActivity;
import com.pinus.alexdev.avis.view.review_list_activity.ReviewsListActivity;
import com.pinus.alexdev.avis.view.settings_activity.SettingsActivity;
import com.pinus.alexdev.avis.view.team_activity.TeamActivity;
import com.pinus.alexdev.avis.view.billing_activity.BillingActivity;
import com.pinus.alexdev.avis.view.profile_activity.ProfileActivity;
import com.squareup.picasso.Picasso;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.adorsys.android.securestoragelibrary.SecurePreferences;
import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import static com.pinus.alexdev.avis.utils.UserDataPref.getUserSummaryInfo;
import static com.pinus.alexdev.avis.utils.UserDataPref.userRequest;
import static com.pinus.alexdev.avis.view.LoginActivity.ORGANIZATION_ID_KEY;
import static com.pinus.alexdev.avis.view.LoginActivity.USER_ID_KEY;
import static com.pinus.alexdev.avis.view.PhotoCropActivity.INTENT_ACTIVITY_NAME_KEY;
import static com.pinus.alexdev.avis.view.PhotoCropActivity.INTENT_URI_KEY;
import static com.pinus.alexdev.avis.view.company_activity.AddBranchActivity.BRANCH_FOR_CREATING;
import static com.pinus.alexdev.avis.view.home_activity.HomeActivity.ANDROID_DEVICE_ID_KEY;

public class CompanyActivity extends BaseNavigationView implements NavigationView.OnNavigationItemSelectedListener, BranchAdapter.BranchListListener, DeleteBranchDialog.DeleteBranchDialogListener {
    private CompositeDisposable disposable = new CompositeDisposable();
    private static final String TAG = CompanyActivity.class.getSimpleName();

    @BindView(R.id.appbar_back)
    View appbar;

    @BindView(R.id.companyName)
    TextInputEditText companyNameTxt;

    @BindView(R.id.branchListView)
    ListView branchListView;

    @BindView(R.id.scanQrButton)
    ImageButton qrScanButton;

    @BindView(R.id.avatarImage)
    CircleImageView avatarImage;

    @BindView(R.id.btnSaveOrganizationName)
    GradientButton btnSaveOrganizationName;

    @BindView(R.id.btnAddBranch)
    GradientButton btnAddBranch;

    @BindView(R.id.btnDeleteCompany)
    MaterialButton btnDeleteCompany;

    ArrayList<BranchesResponse> branchesResponseArrayList = new ArrayList<>();
    BranchApiService branchApiService;
    BranchAdapter branchAdapter;

    public static Bitmap updatedPhotoBitmap;

    private static final int ORGANIZATION_PHOTO_REQUEST = 6;
    private static final int BRANCH_PHOTO_FOR_UPDATE_REQUEST = 8;

    private DeleteBranchDialog deleteBranchDialog;

    //Класс для удобной работы с SharedPreferences
    SaveLoadData saveLoadData;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company);
        ButterKnife.bind(this);
        startScan(qrScanButton);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        // Setting on Touch Listener for handling the touch inside ScrollView
        setTitleAndLogoClick(appbar.findViewById(R.id.appBarTitle), R.string.companyTitle, appbar.findViewById(R.id.logoImageApp));

        saveLoadData = new SaveLoadData(this);

        if (!Utils.isNetworkAvailable(this))
            StyleableToast.makeText(getApplicationContext(), getString(R.string.internet_connection), Toast.LENGTH_SHORT, R.style.mytoast).show();

        branchApiService = ApiClient.getClient(getApplicationContext()).create(BranchApiService.class);

        if (updatedPhotoBitmap != null) uploadImage(updatedPhotoBitmap);
        else getOrganization();
        getBranchList();

        userSummaryResponse = getUserSummaryInfo(getApplicationContext());
        navigationView = findViewById(R.id.nvViewCompany);
        setNavigationDrawerHeaderContent();
        setOnNavigationView();

        btnDeleteCompany.setOnClickListener(v -> {
//            ДОДЕЛАТЬ УДАЛЕНИЕ БРАНЧА. ЧТОБЫ МОЖНО БЫЛО УДАЛЯТЬ БРАНЧ В BranchAdapter, В НЁМ ДОЛЖЕН БЫТЬ FragmentManager, ЗНАЧИТ ЕГО НУЖНА ПЕРЕДАТЬ В АДАПТЕР.
//            ТАКЖЕ НАЛАДИТЬ УДАЛЕНИЕ В AddBranchActivity
        });

        disposable.add(RxView.clicks(avatarImage).subscribe(t -> prepareAndUploadPhoto(ORGANIZATION_PHOTO_REQUEST)));

        disposable.add(RxView.clicks(btnSaveOrganizationName.getButton()).subscribe(t -> {
            if (Objects.requireNonNull(companyNameTxt.getText()).toString().isEmpty())
                StyleableToast.makeText(getApplicationContext(), getString(R.string.toast_field_can_not_be_empty), Toast.LENGTH_SHORT, R.style.mytoast).show();
            else saveOrganizationName();
        }));

        disposable.add(RxView.clicks(btnAddBranch.getButton()).subscribe(t -> {
            Intent intent = new Intent(this, AddBranchActivity.class);
            intent.putExtra("checkBranch", BRANCH_FOR_CREATING);
            startActivity(intent);
        }));
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

    private void getOrganization() {
        disposable.add(branchApiService.getOrganization(saveLoadData.loadInt(ORGANIZATION_ID_KEY))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(t -> {
                    Picasso.get().load(t.getLogoUrl()).placeholder(R.drawable.company_logo).error(R.drawable.company_logo).into(avatarImage);
                    companyNameTxt.setText(t.getName());
                    if (Objects.requireNonNull(companyNameTxt.getText()).toString().isEmpty()) {
                        CompanyActivity.this.startActivity(new Intent(CompanyActivity.this, CompanyRegistrationActivity.class));
                        CompanyActivity.this.finish();
                    }
                },
                        e -> {
                            Log.e(TAG, "onError:" + e.getMessage());
                            StyleableToast.makeText(getApplicationContext(), getString(R.string.error), Toast.LENGTH_SHORT, R.style.mytoast).show();
                        }));
    }

    private void prepareAndUploadPhoto(int photoRequestType) {
        disposable.add(new RxPermissions(this)
                .request(Manifest.permission.READ_EXTERNAL_STORAGE)
                .subscribe(granted -> {
                    // granted - always true in pre-M
                    if (granted) getIntentImage(photoRequestType);
                    else
                        StyleableToast.makeText(getApplicationContext(), "You don't have permission", Toast.LENGTH_SHORT, R.style.mytoast).show();
                }));
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

        disposable.add(branchApiService.updateOrganizationPhoto(fbody, body, saveLoadData.loadInt(ORGANIZATION_ID_KEY))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(t -> {
                            Log.v(TAG, "Image success");
                            StyleableToast.makeText(getApplicationContext(), getString(R.string.photoUpload), Toast.LENGTH_LONG, R.style.mytoast).show();
                            userRequest(this); //Этот метод вызывается для того, чтобы в при повторном запуске этого активити, в переменную userSummaryResponse(которая в onCreate()) присваивались обновлённые данные.
                            getOrganization();
                            updatedPhotoBitmap = null;
                        },
                        e -> {
                            Log.e(TAG, "onError: " + e.getMessage());
                            StyleableToast.makeText(getApplicationContext(), getString(R.string.error), Toast.LENGTH_LONG, R.style.mytoast).show();
                        }));
    }


    private void saveOrganizationName() {
        disposable.add(ApiClientMultipart.getClient(getApplicationContext()).create(BranchApiService.class)
                .updateOrganizationName(new OrganizationRequest(companyNameTxt.getText().toString()), saveLoadData.loadInt(ORGANIZATION_ID_KEY))
                .doAfterSuccess(t -> getOrganization())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(t -> {
                            Log.v(TAG, "Image success");
                            StyleableToast.makeText(getApplicationContext(), getString(R.string.profileUpdated), Toast.LENGTH_SHORT, R.style.mytoast).show();
                        },
                        e -> {
                            Log.e(TAG, "onError:" + e.getMessage());
                            StyleableToast.makeText(getApplicationContext(), getString(R.string.error), Toast.LENGTH_SHORT, R.style.mytoast).show();
                        }));
    }

    private void getIntentImage(int photoRequestType) {
        startActivityForResult(new Intent(Intent.ACTION_PICK)
                .setType("image/*"), photoRequestType);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case ORGANIZATION_PHOTO_REQUEST:
                    Intent intent = new Intent(this, PhotoCropActivity.class);
                    intent.putExtra(INTENT_URI_KEY, imageReturnedIntent.getData().toString());
                    intent.putExtra(INTENT_ACTIVITY_NAME_KEY, "CompanyActivity");
                    startActivity(intent);
                    finish();
                    break;
            }
        }
    }

    private void getBranchList() {
        disposable.add(branchApiService.getBranchList(saveLoadData.loadInt(ORGANIZATION_ID_KEY))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::setBranchContent,
                        e -> {
                            Log.e(TAG, "onError:" + e.getMessage());
                            StyleableToast.makeText(getApplicationContext(), getString(R.string.invalidPromo), Toast.LENGTH_SHORT, R.style.mytoast).show();
                        }));

    }

    private void setBranchContent(ArrayList<BranchesResponse> branchContent) {
        branchesResponseArrayList = branchContent;
        Collections.reverse(branchesResponseArrayList);

        branchAdapter = new BranchAdapter(CompanyActivity.this, branchesResponseArrayList, new RxPermissions(this));
        branchAdapter.setBranchListListener(this);
        if (branchContent.size() > 1) branchListView.setScrollbarFadingEnabled(false);
        branchListView.setAdapter(branchAdapter);
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

    @Override
    protected void onResume() {
        super.onResume();
//        getBranchList();
    }

    @Override
    protected void onDestroy() {
        disposable.dispose();
        super.onDestroy();
    }

    int branchPosition;
    @Override
    public void openDeleteBranchDialog(int position) {
        branchPosition = position;
        deleteBranchDialog = new DeleteBranchDialog();
        deleteBranchDialog.show(getSupportFragmentManager(), "delete branch dialog");
    }

    @Override
    public void deleteBranch(int branchId) {
        branchApiService
                .deleteBranch(branchId, new SaveLoadData(this).loadInt(ORGANIZATION_ID_KEY))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> {
                    StyleableToast.makeText(getApplicationContext(), getString(R.string.branch_deleted), Toast.LENGTH_LONG, R.style.mytoast).show();
                    deleteBranchDialog.dismiss(); // - закрытие диалога после удаления компании
//                    startActivity(new Intent(this, CompanyActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    branchAdapter.remove(branchPosition);
                    branchAdapter.notifyDataSetChanged();
                }, e -> Log.e(TAG, "onError:" + e.getMessage()));
    }

    @Override
    public void dismissDialog() {
        deleteBranchDialog.dismiss();
    }
}
