package com.pinus.alexdev.avis.view.review_list_activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.navigation.NavigationView;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.LiveData;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PageKeyedDataSource;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.muddzdev.styleabletoast.StyleableToast;
import com.pinus.alexdev.avis.Helper.LocaleHelper;
import com.pinus.alexdev.avis.R;
import com.pinus.alexdev.avis.adapter.ConversationsAdapter;
import com.pinus.alexdev.avis.adapter.paged_review_list.PLAdapter;
import com.pinus.alexdev.avis.adapter.paged_review_list.SourceFactory;
import com.pinus.alexdev.avis.dto.request.TokenRequest;
import com.pinus.alexdev.avis.dto.response.review_response.ReviewResponse;
import com.pinus.alexdev.avis.network.ApiClient;
import com.pinus.alexdev.avis.network.apiServices.NotificationApiService;
import com.pinus.alexdev.avis.network.apiServices.ReviewsApiService;
import com.pinus.alexdev.avis.utils.SaveLoadData;
import com.pinus.alexdev.avis.utils.Utils;
import com.pinus.alexdev.avis.view.BaseNavigationView;
import com.pinus.alexdev.avis.view.LoginActivity;
import com.pinus.alexdev.avis.view.cta_activity.CTAActivity;
import com.pinus.alexdev.avis.view.settings_activity.SettingsActivity;
import com.pinus.alexdev.avis.view.team_activity.TeamActivity;
import com.pinus.alexdev.avis.view.billing_activity.BillingActivity;
import com.pinus.alexdev.avis.view.company_activity.CompanyActivity;
import com.pinus.alexdev.avis.view.conversation_activity.ConversationListReviewsActivity;
import com.pinus.alexdev.avis.view.home_activity.HomeActivity;
import com.pinus.alexdev.avis.view.profile_activity.ProfileActivity;
import com.pinus.alexdev.avis.view.promo_code_activity.PromoCodesActivity;
import com.pinus.alexdev.avis.view.qr_manager_activity.QRManagerActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.adorsys.android.securestoragelibrary.SecurePreferences;
import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

import static com.pinus.alexdev.avis.utils.UserDataPref.getUserSummaryInfo;
import static com.pinus.alexdev.avis.view.LoginActivity.USER_ID_KEY;
import static com.pinus.alexdev.avis.view.home_activity.HomeActivity.ANDROID_DEVICE_ID_KEY;
import static com.pinus.alexdev.avis.view.review_list_activity.SortReviewDialog.SORT_BY_AUTHORIZATION;
import static com.pinus.alexdev.avis.view.review_list_activity.SortReviewDialog.SORT_BY_DATE;
import static com.pinus.alexdev.avis.view.review_list_activity.SortReviewDialog.SORT_BY_NUMBER;
import static com.pinus.alexdev.avis.view.review_list_activity.SortReviewDialog.SORT_BY_RATING;

public class ReviewsListActivity extends BaseNavigationView implements NavigationView.OnNavigationItemSelectedListener, SortReviewDialog.SortReviewDialogListener {
    @BindView(R.id.appbar_back)
    View appbar;

    @BindView(R.id.swipeRefresh)
    SwipeRefreshLayout swipeRefresh;

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    @BindView(R.id.scanQrButton)
    ImageButton qrScanButton;

    @BindView(R.id.btnFilter)
    MaterialButton btnFilter;

    @BindView(R.id.btnSort)
    MaterialButton btnSort;

    SortReviewDialog sortReviewDialog;

    //Поля, которые нужны для сортировки данных в списке, они буду использоваться в будущем, пока что их значение будет пустым
    private String orderBy = "";
    private String sortBy = "";

    private LiveData<PagedList<ReviewResponse>> itemPagedList;
    private LiveData<PageKeyedDataSource<Integer, ReviewResponse>> liveDataSource;
    private SourceFactory dataSourceFactory;

    //Поля выведены на уровень класса, чтобы методы сортировки имели доступ к ним (sortByName() и т.д.)
    private ArrayList<ReviewResponse> reviewList;
    private ConversationsAdapter adapter;

    private static final String TAG = ReviewsListActivity.class.getSimpleName();
    private CompositeDisposable disposable = new CompositeDisposable();
    private ReviewsApiService reviewsApiService;

    //Класс для удобной работы с SharedPreferences
    SaveLoadData saveLoadData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authorized_reviews);
        ButterKnife.bind(this);
        startScan(qrScanButton);

        saveLoadData = new SaveLoadData(this);

        if (!Utils.isNetworkAvailable(this))
            StyleableToast.makeText(getApplicationContext(), getString(R.string.internet_connection), Toast.LENGTH_LONG, R.style.mytoast).show();

        userSummaryResponse = getUserSummaryInfo(this);

        navigationView = findViewById(R.id.nvViewAuthReviewList);
        setNavigationDrawerHeaderContent();
        setOnNavigationView();

        setTitleAndLogoClick(appbar.findViewById(R.id.appBarTitle), R.string.menuReview, appbar.findViewById(R.id.logoImageApp));
        reviewsApiService = ApiClient.getClient(getApplicationContext()).create(ReviewsApiService.class);

        retrieveData();

        btnFilter.setOnClickListener(v -> {

        });

        btnSort.setOnClickListener(v -> {
            sortReviewDialog = new SortReviewDialog();
            sortReviewDialog.show(getSupportFragmentManager(), "sort reviewResponse dialog");
        });

        swipeRefresh.setOnRefreshListener(this::retrieveData);

      /*          bageCountSubscriber.getModelChanges()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(t->
                    retrieveData(),
                        e -> Log.e(TAG, "onError: " + e.getContent()));*/
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

    private void retrieveData() {
        swipeRefresh.setRefreshing(true);

        actionDataToViewModel(this, orderBy, sortBy);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        PLAdapter adapter = new PLAdapter(this);
        getPagedList().observe(this, pagedList -> {
            adapter.submitList(pagedList);
            swipeRefresh.setRefreshing(false);
        });

        recyclerView.setAdapter(adapter);
    }

    public void actionDataToViewModel(Context context, String orderBy, String sortBy) {
        dataSourceFactory = new SourceFactory(context, orderBy, sortBy);
        liveDataSource = dataSourceFactory.getLiveDataSource();

        PagedList.Config config = (new PagedList.Config.Builder())
                .setEnablePlaceholders(false)
                .setPageSize(3)
                .build();

        itemPagedList = new LivePagedListBuilder<Integer, ReviewResponse>(dataSourceFactory, config).build();
    }

    public LiveData<PagedList<ReviewResponse>> getPagedList() {
        return itemPagedList;
    }

    public void actionNewValuesToDataSource(Context context, String orderBy, String sortBy) {
        dataSourceFactory.setNewValuesToDataSource(context, orderBy, sortBy);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.home:
                startActivity(new Intent(this, HomeActivity.class));
                finish();
                break;
            case R.id.review:
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

    //СОРТИРОВАТЬ ДАННЫЕ МЕТОДОМ actionNewValuesToDataSource, КОГДА ВЫЯСНИТСЯ КАКИЕ ДАННЫЕ НА ВХОД НУЖНО ВВОДИТЬ ДЛЯ ПАРАМЕТРОВ orderBy и sortBy
    //КОД НЕ УДАЛЯТЬ
//    private void setRecyclerView(ArrayList<ReviewResponse> reviewList) {
//        Collections.reverse(reviewList);
//
//        Collections.sort(reviewList, (data1, data2) -> Long.compare(data2.getDateCreated(), data1.getDateCreated()));
//        Collections.sort(reviewList, (data1, data2) -> Boolean.compare(data1.isViewed(), data2.isViewed()));
//
//        ArrayList<ReviewResponse> list = getFilteredBranchesList(reviewList);
//        reviewList = list;
//        //Этим логом выводится количество ревью для выбранных бранчей.
//        //Из-за того, что сейчас фильтрация осуществляется по имени, возникает баг - повторное добавление ревью из-за того, что имена некоторых бранчей одинаковое.
//        //Когда фильтрация будет проходить по ID-шнику бранча, а не по его имени, то такой проблемы не будет.
//        Log.v("TagReviewCount", "Count of reviews is: " + list.size());
//
//
//        this.reviewList = reviewList;
//        adapter = new ConversationsAdapter(ReviewsListActivity.this, reviewList);
//        adapter.isChat = false;
//
//        // Attach the adapter to a ListView
//        recyclerView.setAdapter(adapter);
//        recyclerView.setOnItemClickListener((parent, view, position, id) -> {
//            Intent intentInfo = new Intent(ReviewsListActivity.this, InfoReviewActivity.class);
//            intentInfo.putExtra("revieInfo", (ReviewResponse) recyclerView.getAdapter().getItem(position));
//            startActivity(intentInfo);
//        });
//    }

    //С новой API фильтрацию бранчей нужно будет реализовать с помощью ID бранча, который будет храниться в данных каждого респонса ревью, а не с помощью имени бранча, как это сделано сейчас и это баговый вариант
    //КОД НЕ УДАЛЯТЬ
//    private ArrayList<ReviewResponse> getFilteredBranchesList(ArrayList<ReviewResponse> authorizedReviews) {
//        String jsonListBranchesNames = saveLoadData.loadString(BRANCH_FILTER_KEY);
//        ArrayList<String> filteredBranchesNames = new Gson().fromJson(jsonListBranchesNames, new TypeToken<List<String>>(){}.getType());
//
//        if (filteredBranchesNames == null)
//            return authorizedReviews;
//
//        ArrayList<ReviewResponse> filteredReviews = new ArrayList<>();
//        for (String selectedBranchName : filteredBranchesNames) {
//            if (selectedBranchName.equals(getString(OVERALL.getValue()))) // 0 здесь означает все бранчи без всяких сортировок
//                return authorizedReviews;
//            else {
//                for (int i = 0; i < authorizedReviews.size(); i++) {
//                    String branchName = authorizedReviews.get(i).getBranch();
//                    if (branchName.equals(selectedBranchName)) {
//                        filteredReviews.add(authorizedReviews.get(i));
//                    }
//                }
//            }
//        }
//        return filteredReviews;
//    }

    @Override
    public void sendChosenBtnData(int typeOfSort) {
        switch (typeOfSort) {
            case SORT_BY_NUMBER:
                sortByNumber();
                break;
            case SORT_BY_DATE:
                sortByDate();
                break;
            case SORT_BY_RATING:
                sortByRating();
                break;
            case SORT_BY_AUTHORIZATION:
                sortByAuthorization();
                break;
        }
        sortReviewDialog.dismiss();
    }

    //СОРТИРОВАТЬ ДАННЫЕ МЕТОДОМ actionNewValuesToDataSource, КОГДА ВЫЯСНИТСЯ КАКИЕ ДАННЫЕ НА ВХОД НУЖНО ВВОДИТЬ ДЛЯ ПАРАМЕТРОВ orderBy и sortBy
    private void sortByNumber() {
        actionNewValuesToDataSource(this, "", "id");
        //        adapter.notifyDataSetChanged();
    }

    //СОРТИРОВАТЬ ДАННЫЕ МЕТОДОМ actionNewValuesToDataSource, КОГДА ВЫЯСНИТСЯ КАКИЕ ДАННЫЕ НА ВХОД НУЖНО ВВОДИТЬ ДЛЯ ПАРАМЕТРОВ orderBy и sortBy
    private void sortByDate() {
        actionNewValuesToDataSource(this, "", "dateCreated");
//        adapter.notifyDataSetChanged();
    }

    //СОРТИРОВАТЬ ДАННЫЕ МЕТОДОМ actionNewValuesToDataSource, КОГДА ВЫЯСНИТСЯ КАКИЕ ДАННЫЕ НА ВХОД НУЖНО ВВОДИТЬ ДЛЯ ПАРАМЕТРОВ orderBy и sortBy
    private void sortByRating() {
        actionNewValuesToDataSource(this, "", "impression");
//        adapter.notifyDataSetChanged();
    }

    //СОРТИРОВАТЬ ДАННЫЕ МЕТОДОМ actionNewValuesToDataSource, КОГДА ВЫЯСНИТСЯ КАКИЕ ДАННЫЕ НА ВХОД НУЖНО ВВОДИТЬ ДЛЯ ПАРАМЕТРОВ orderBy и sortBy
    private void sortByAuthorization() {
        actionNewValuesToDataSource(this, "", "anonymous");
//        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        retrieveData();
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onRestart() {
        retrieveData();
        super.onRestart();
    }

    @Override
    protected void onDestroy() {
        disposable.dispose();
        super.onDestroy();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase, "en"));
    }
}
