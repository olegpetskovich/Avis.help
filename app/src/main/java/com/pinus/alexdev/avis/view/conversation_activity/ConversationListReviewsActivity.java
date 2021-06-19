package com.pinus.alexdev.avis.view.conversation_activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.dagang.library.GradientButton;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.navigation.NavigationView;
import com.muddzdev.styleabletoast.StyleableToast;
import com.pinus.alexdev.avis.R;
import com.pinus.alexdev.avis.adapter.ConversationsAdapter;
import com.pinus.alexdev.avis.dto.request.TokenRequest;
import com.pinus.alexdev.avis.dto.response.ConversationResponse;
import com.pinus.alexdev.avis.dto.response.review_response.ChatRoomResponse;
import com.pinus.alexdev.avis.dto.response.review_response.ReviewResponse;
import com.pinus.alexdev.avis.network.ApiClient;
import com.pinus.alexdev.avis.network.apiServices.ConversationApiService;
import com.pinus.alexdev.avis.network.apiServices.NotificationApiService;
import com.pinus.alexdev.avis.utils.SaveLoadData;
import com.pinus.alexdev.avis.utils.Utils;
import com.pinus.alexdev.avis.view.BaseNavigationView;
import com.pinus.alexdev.avis.view.LoginActivity;
import com.pinus.alexdev.avis.view.billing_activity.BillingActivity;
import com.pinus.alexdev.avis.view.company_activity.CompanyActivity;
import com.pinus.alexdev.avis.view.cta_activity.CTAActivity;
import com.pinus.alexdev.avis.view.home_activity.HomeActivity;
import com.pinus.alexdev.avis.view.profile_activity.ProfileActivity;
import com.pinus.alexdev.avis.view.promo_code_activity.PromoCodesActivity;
import com.pinus.alexdev.avis.view.qr_manager_activity.QRManagerActivity;
import com.pinus.alexdev.avis.view.review_list_activity.ReviewsListActivity;
import com.pinus.alexdev.avis.view.settings_activity.SettingsActivity;
import com.pinus.alexdev.avis.view.team_activity.TeamActivity;
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
import static com.pinus.alexdev.avis.view.LoginActivity.ORGANIZATION_ID_KEY;
import static com.pinus.alexdev.avis.view.LoginActivity.USER_ID_KEY;
import static com.pinus.alexdev.avis.view.home_activity.HomeActivity.ANDROID_DEVICE_ID_KEY;

public class ConversationListReviewsActivity extends BaseNavigationView implements NavigationView.OnNavigationItemSelectedListener, SortChatDialog.SortChatDialogListener {
    @BindView(R.id.appbar_back)
    View appbar;

    @BindView(R.id.swipeRefresh)
    SwipeRefreshLayout swipeRefresh;

    @BindView(R.id.listView)
    ListView listView;

    @BindView(R.id.scanQrButton)
    ImageButton qrScanButton;

    @BindView(R.id.btnInviteToChat)
    GradientButton btnInviteToChat;

    @BindView(R.id.btnFilter)
    MaterialButton btnFilter;

    @BindView(R.id.btnSort)
    MaterialButton btnSort;

    private static final String TAG = ConversationListReviewsActivity.class.getSimpleName();
    private CompositeDisposable disposable = new CompositeDisposable();
    private ConversationApiService conversationsApiService;

    SortChatDialog sortChatDialog;

    //Поля выведены на уровень класса, чтобы методы сортировки имели доступ к ним (sortByName() и т.д.)
    private ArrayList<ReviewResponse> chatList;
    private ConversationsAdapter adapter;

    //Класс для удобной работы с SharedPreferences
    SaveLoadData saveLoadData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversations_list_reviews);
        ButterKnife.bind(this);
        startScan(qrScanButton);

        saveLoadData = new SaveLoadData(this);

        btnInviteToChat.setVisibility(View.VISIBLE);
        btnInviteToChat.getButton().setOnClickListener(v -> startActivity(new Intent(this, InviteToChatActivity.class)));

        if (!Utils.isNetworkAvailable(this))
            StyleableToast.makeText(getApplicationContext(), getString(R.string.internet_connection), Toast.LENGTH_LONG, R.style.mytoast).show();

        userSummaryResponse = getUserSummaryInfo(this);

        navigationView = findViewById(R.id.nvViewAuthReviewList);
        setNavigationDrawerHeaderContent();
        setOnNavigationView();

        setTitleAndLogoClick(appbar.findViewById(R.id.appBarTitle), R.string.conversationtitle, appbar.findViewById(R.id.logoImageApp));
        conversationsApiService = ApiClient.getClient(getApplicationContext()).create(ConversationApiService.class);

        retrieveData();

        btnFilter.setOnClickListener(v -> {

        });

        btnSort.setOnClickListener(v -> {
            sortChatDialog = new SortChatDialog();
            sortChatDialog.show(getSupportFragmentManager(), "sort chat dialog");
        });

        swipeRefresh.setOnRefreshListener(this::retrieveData);

        bageCountSubscriber.getModelChanges()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(t ->
                                retrieveData(),
                        e -> Log.e(TAG, "onError: " + e.getMessage()));
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

        disposable.add(conversationsApiService.getOrganizationConversationsList(saveLoadData.loadInt(ORGANIZATION_ID_KEY))
                .retry()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        conversationList -> {
                            setConversationList(conversationList);
                            swipeRefresh.setRefreshing(false);
                        },
                        e -> Log.e(TAG, "onError: " + e.getMessage())
                ));
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

    private void setConversationList(ArrayList<ConversationResponse> conversationList) {
//        //Цикл отбирает все пришедшие Review, чтобы при передаче их в адаптер были только авторизированные отзывы.
//        //Так должно быть в случае со списком чатов, потому что пользователь не может начать диалог в неавторизованном отзыве.

//        Collections.reverse(authorizedReviews);
//
//        Collections.sort(authorizedReviews, (data1, data2) -> Long.compare(data2.getDateCreated(), data1.getDateCreated()));
//        Collections.sort(authorizedReviews, (data1, data2) -> Boolean.compare(data1.isViewed(), data2.isViewed()));
//
//        ArrayList<ReviewResponse> list = getFilteredBranchesList(authorizedReviews);
//        authorizedReviews = list;
//        //Этим логом выводится количество ревью для выбранных бранчей.
//        //Из-за того, что сейчас фильтрация осуществляется по имени, возникает баг - повторное добавление ревью из-за того, что имена некоторых бранчей одинаковое.
//        //Когда фильтрация будет проходить по ID-шнику бранча, а не по его имени, то такой проблемы не будет.
//        Log.v("TagReviewCount", "Count of reviews is: " + list.size());
//
//        this.chatList = authorizedReviews;
        adapter = new ConversationsAdapter(this, conversationList);
//
//        // Attach the adapter to a ListView
        listView.setAdapter(adapter);
        listView.setOnItemClickListener((parent, view, position, id) -> {
            Intent intentInfo = new Intent(ConversationListReviewsActivity.this, ConversationActivity.class);
            ConversationResponse conversationResponse = (ConversationResponse) listView.getAdapter().getItem(position);
            //Создаём объект для ChatRoomResponse, потому что в ConversationActivity просиходит проверка этого поля на null.
            //Если это поле равно null, то создаётся чат. Но дело в том, что в этом активити отображается список уже созданных чатов.
            //А поле ChatRoomResponse нужно для того, чтобы определять, создан ли чат или нет. Если не создан, то создаём новый чат.
            //Так вот, создавая объект для этого поля, устанавливается как бы заглушка, чтобы при проверке в ConversationActivity, не создавался чат.
            if (conversationResponse.getChatRoom() == null) conversationResponse.setChatRoom(new ChatRoomResponse());
            intentInfo.putExtra("chatInfo", conversationResponse);
            startActivity(intentInfo);
        });
//    }
//
//    //С новой API фильтрацию бранчей нужно будет реализовать с помощью ID бранча, который будет храниться в данных каждого респонса ревью, а не с помощью имени бранча, как это сделано сейчас и это баговый вариант
//    private ArrayList<ReviewResponse> getFilteredBranchesList(ArrayList<ReviewResponse> authorizedReviews) {
//        String jsonListBranchesNames = saveLoadData.loadString(BRANCH_FILTER_KEY);
//
//        //ПОМЕНЯТЬ НА ФИЛЬТРАЦИЮ ПО ID
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
    }

    @Override
    public void sendChosenBtnData(int typeOfSort) {
//        switch (typeOfSort) {
//            case SORT_BY_NUMBER:
//                sortByNumber();
//                break;
//            case SORT_BY_DATE:
//                sortByDate();
//                break;
//            case SORT_BY_RATING:
//                sortByRating();
//                break;
//        }
        sortChatDialog.dismiss();
    }

//    private void sortByNumber() {
//        Collections.sort(chatList, (data1, data2) -> Long.compare(data2.getId(), data1.getId()));
//        adapter.notifyDataSetChanged();
//    }
//
//    private void sortByDate() {
//        Collections.sort(chatList, (data1, data2) -> Long.compare(data2.getDateCreated(), data1.getDateCreated()));
//        adapter.notifyDataSetChanged();
//    }
//
//    private void sortByRating() {
//        Collections.sort(chatList, (data1, data2) -> Long.compare(data2.getImpression(), data1.getImpression()));
//        adapter.notifyDataSetChanged();
//    }

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


  /*  private  void initializeCountDrawer(@IdRes int itemId, int count) {

        TextView view = (TextView) navigationView.getMenu().findItem(itemId).getActionView();
        ViewGroup.LayoutParams params = view.getLayoutParams();

        if (params != null) {
            params.width= R.dimen.activity_vertical_margin;
            params.height = R.dimen.activity_vertical_margin;
        } else
            params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(params);
        view.setBackgroundResource(R.drawable.circle_text_view);
        view.setGravity(Gravity.CENTER | Gravity.CENTER_VERTICAL);
        view.setTextColor(Color.WHITE);
        view.setText(count > 0 ? String.valueOf(count) : null);
        Log.v(TAG, "initializeCountDrawer" + count);

    }*/


}
