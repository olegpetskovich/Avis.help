package com.pinus.alexdev.avis.view.conversation_activity;

import android.animation.LayoutTransition;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dagang.library.GradientButton;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.jakewharton.rxbinding3.view.RxView;
import com.muddzdev.styleabletoast.StyleableToast;
import com.pinus.alexdev.avis.R;
import com.pinus.alexdev.avis.adapter.ArraySpinnerAdapter;
import com.pinus.alexdev.avis.adapter.ChatAdapter;
import com.pinus.alexdev.avis.adapter.InMessageAdapter;
import com.pinus.alexdev.avis.dto.Conversation;
import com.pinus.alexdev.avis.dto.response.ConversationResponse;
import com.pinus.alexdev.avis.dto.response.promo_code_response.PromoCodeResponse;
import com.pinus.alexdev.avis.enums.MessageType;
import com.pinus.alexdev.avis.enums.SenderChatEnums;
import com.pinus.alexdev.avis.model.ChatLocal;
import com.pinus.alexdev.avis.model.ChatMessage;
import com.pinus.alexdev.avis.network.ApiClient;
import com.pinus.alexdev.avis.network.apiServices.ConversationApiService;
import com.pinus.alexdev.avis.network.apiServices.PromoCodeApiService;
import com.pinus.alexdev.avis.network.apiServices.UserApiService;
import com.pinus.alexdev.avis.utils.SaveLoadData;
import com.pinus.alexdev.avis.utils.Utils;
import com.pinus.alexdev.avis.view.BaseToolbarBack;
import com.pinus.alexdev.avis.view.review_list_activity.InfoReviewActivity;

import java.util.ArrayList;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.CompletableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompClient;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static com.pinus.alexdev.avis.utils.UserDataPref.getUserSummaryInfo;
import static com.pinus.alexdev.avis.view.LoginActivity.ORGANIZATION_ID_KEY;

public class ConversationActivity extends BaseToolbarBack {

    private StompClient mStompClient;
    String TAG = ConversationActivity.class.getSimpleName();

    private ConversationApiService conversationApiService;
    private UserApiService userApiService;

    @BindView(R.id.appbar_back)
    View appbar;

    @BindView(R.id.layoutConversation)
    MaterialCardView linearLayout;

    @BindView(R.id.sendButton)
    MaterialButton sendButton;

    @BindView(R.id.messageText)
    TextInputEditText messageText;

    @BindView(R.id.messagesList)
    RecyclerView messagesList;

    @BindView(R.id.addPopupButton)
    MaterialButton addPopupButton;

    @BindView(R.id.backLayout)
    LinearLayout backTo;

    @BindView(R.id.goToBackReview)
    GradientButton goToBackReview;

    @BindView(R.id.layoutWithPromoList)
    RelativeLayout layoutPromoList;

    public InMessageAdapter inMessageAdapter;

    private String username = null;
    // long reviewId;
    long promoId = 0;
    public ArrayList<ChatLocal> chatLocals = new ArrayList<>();

    MessageType messageType = MessageType.CHAT;

    private PromoCodeApiService promoCodeApiService;
    ConversationResponse conversationInfo;

    private ChatAdapter mAdapter;

    //Класс для удобной работы с SharedPreferences
    SaveLoadData saveLoadData;

    @Override
    @SuppressLint("CheckResult")

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);
        ButterKnife.bind(this);

        if (!Utils.isNetworkAvailable(this))
            StyleableToast.makeText(getApplicationContext(), getString(R.string.internet_connection), Toast.LENGTH_LONG, R.style.mytoast).show();

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Roboto-RobotoRegular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        userApiService = ApiClient.getClient(getApplicationContext()).create(UserApiService.class);
        promoCodeApiService = ApiClient.getClient(getApplicationContext()).create(PromoCodeApiService.class);
        conversationApiService = ApiClient.getClient(getApplicationContext()).create(ConversationApiService.class);

        saveLoadData = new SaveLoadData(this);
        //new LongOperationGet(mStompClient, username).execute("");
        mStompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, "wss://qr-ticket-stage.eu-west-3.elasticbeanstalk.com/ws/websocket");
        disposable.add(RxView.clicks(addPopupButton).subscribe(t -> {
            listCollapseExpand();
            getPromoCode();
        }));

        username = getUserSummaryInfo(getApplicationContext()).getFirstName();
        connectStomp(username);

        //   revieId = getIntent().getExtras().getLong("idConversation");
        conversationInfo = (ConversationResponse) getIntent().getSerializableExtra("chatInfo");
        if (conversationInfo != null) {
            if (conversationInfo.getInvitationPhoneNumber() != null) {
                goToBackReview.setVisibility(View.GONE);

                setButtonBackClick(
                        appbar.findViewById(R.id.appBarTitle),
                        getString(R.string.chat_with) + " " + conversationInfo.getInvitationPhoneNumber(),
                        appbar.findViewById(R.id.logoImageBack),
                        backTo,
                        appbar.findViewById(R.id.toolbarPreviosActivityTitle),
                        "",
                        null
                );
            } else {
                setButtonBackClick(
                        appbar.findViewById(R.id.appBarTitle),
                        "Id: " + conversationInfo.getReviewId(),
                        appbar.findViewById(R.id.logoImageBack),
                        backTo,
                        appbar.findViewById(R.id.toolbarPreviosActivityTitle),
                        "",
                        null
                );
            }

            if (conversationInfo.getChatRoom() == null) {
                conversationApiService
                        .addChatByReviewId(conversationInfo.getReviewId())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(conversationResponse -> {
                            ConversationResponse response = conversationResponse;

                        }, e -> Log.e(TAG, "onError: " + e.getMessage()));
            } else {

            }
            getConversationHistory();
            setChatViewed();
        }


        //Если чат только начат, то приглашение в чат отправляется автоматически
//        ApiClient.getClient(getApplicationContext()).create(ReviewsApiService.class)
//                .getReviewsWithConversation()
//                .retry()
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(reviewResponses -> {
////                            for (ConversationResponse response : reviewResponses) {
////                                if (conversationInfo.getReviewId() == response.getRev()) {
////                                    conversationInfo = response;
////                                    return;
////                                }
////                            }
//                            //НЕ УДАЛЯТЬ
////                            if (!conversationInfo.isChat()) {
////                                setInviteLink();
////                                sendEchoViaStomp();
////                                messageType = MessageType.CHAT;
////                            }
//                        },
//                        e -> Log.e(TAG, "onError: " + e.getContent())
//                );

        disposable.add(RxView.clicks(sendButton).subscribe(t -> {
            sendEchoViaStomp();
            messageType = MessageType.CHAT;
        }));

        goToBackReview.getButton().setOnClickListener(v -> {
            Intent intentInfo = new Intent(this, InfoReviewActivity.class);
            intentInfo.putExtra("reviewId", conversationInfo.getReviewId());
            startActivity(intentInfo);
        });

        sendButton.setClickable(false);
        sendButton.setEnabled(false);
        messageText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!Objects.requireNonNull(messageText.getText()).toString().isEmpty()) {
                    sendButton.setIconTint(ContextCompat.getColorStateList(ConversationActivity.this, R.color.colorPrimary));
                    sendButton.setClickable(true);
                    sendButton.setEnabled(true);
                } else {
                    sendButton.setIconTint(ContextCompat.getColorStateList(ConversationActivity.this, android.R.color.darker_gray));
                    sendButton.setClickable(false);
                    sendButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void getConversationHistory() {
        disposable.add(conversationApiService.getConversationHistory(conversationInfo.getId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        this::setChatHistory,
                        e -> Log.e(TAG, "onError: " + e.getMessage())
                ));
    }

    private void setChatViewed() {
        disposable.add(conversationApiService.setChatViewed(conversationInfo.getId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(e -> System.out.print("Viewed"),
                        e -> {
                            Log.e(TAG, "onError:" + e.getMessage());
                            StyleableToast.makeText(getApplicationContext(), getString(R.string.error), Toast.LENGTH_LONG, R.style.mytoast).show();
                        }));
    }


    @SuppressLint("CheckResult")
    private void setInviteLink() {
        messageType = MessageType.CHATINVITE;
        messageText.setText(null);
        messageText.setText(getString(R.string.inviteChat) + " " + conversationInfo.getBranchName() + ": \n" + conversationInfo.getChatUrl());
//        listCollapseExpand();
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
                            Toast.makeText(ConversationActivity.this, "Stomp connection error", Toast.LENGTH_LONG).show();
                            break;
                        case CLOSED:
                            Toast.makeText(ConversationActivity.this, "Stomp connection closed", Toast.LENGTH_LONG).show();
                            resetSubscriptions();
                            break;
                        case FAILED_SERVER_HEARTBEAT:
                            Toast.makeText(ConversationActivity.this, "Stomp failed server heartbeat", Toast.LENGTH_LONG).show();
                            break;
                    }
                }, e -> Log.e(TAG, "onError: " + e.getMessage()));

        disposable.add(dispLifecycle);

        // Receive greetings
        Log.v(TAG, "/channel/" + adminUsername);
        Disposable dispTopic = mStompClient.topic("/channel/" + adminUsername)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(topicMessage -> {
                    Log.v(TAG, topicMessage.getPayload());
                    SenderChatEnums chatEnums = SenderChatEnums.CLIENT;
                    ChatMessage chat = new Gson().fromJson(topicMessage.getPayload(), ChatMessage.class);

                    if (chat.getMessageType() == MessageType.PROMOCODE)
                        chatEnums = SenderChatEnums.YOUR;

                    ChatLocal msgLocal = new ChatLocal(chat.getContent(),
                            chatEnums);
                    if (msgLocal.getMessage() != null) {
                        ConversationActivity.this.addItem(msgLocal);
                    }
                }, throwable -> Log.e(TAG, "Error on subscribe topic", throwable));

        disposable.add(dispTopic);
        mStompClient.connect();
    }

    public void sendEchoViaStomp() {
        if (!mStompClient.isConnected()) return;

        ChatMessage myObj = new ChatMessage(messageType, username, (long) conversationInfo.getReviewId());

        if (messageType != MessageType.PROMOCODE)
            myObj.setContent(messageText.getText().toString());
        else {
            myObj.setMessageType(messageType);
            myObj.setContent(messageText.getText().toString() + ": %PromoCodeLink%");
            myObj.setPromoId(promoId);
            messageType = MessageType.CHAT;
        }

        String json = new Gson().toJson(myObj);
        disposable.add(mStompClient.send("/app/chat/" + conversationInfo.getReviewId() + "/sendMessage", json)
                .compose(applySchedulers())
                .subscribe(() -> {
                    Log.d(TAG, "STOMP echo send successfully");
                    if (myObj.getMessageType() != MessageType.PROMOCODE) {
                        addItem(new ChatLocal(myObj.getContent(), SenderChatEnums.YOUR));
                        Log.d(TAG, json);
                    }
                    messageText.setText(null);
                }, throwable -> {
                    Log.e(TAG, "Error send STOMP echo", throwable);
                    Toast.makeText(ConversationActivity.this, throwable.getMessage(), Toast.LENGTH_LONG).show();
                }));
    }

    protected CompletableTransformer applySchedulers() {
        return upstream -> upstream
                .unsubscribeOn(Schedulers.newThread())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
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

    private void getPromoCode() {
        disposable.add(promoCodeApiService.getPromoCodes(saveLoadData.loadInt(ORGANIZATION_ID_KEY))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        this::setPromoCodeContent,
                        e -> Log.e(TAG, "onError: " + e.getMessage())
                ));

    }

    private void setPromoCodeContent(ArrayList<PromoCodeResponse> promoCodeContent) {
        ListView promoCodeList = findViewById(R.id.promoList);

        promoCodeList.setAdapter(new ArraySpinnerAdapter(this, R.layout.promo_item_message, promoCodeContent));
//        addPopupButton.setOnClickListener(v -> listCollapseExpand());

        promoCodeList.setOnItemClickListener((parent, view, position, id) -> {
            messageType = MessageType.PROMOCODE;
            PromoCodeResponse item = (PromoCodeResponse) promoCodeList.getAdapter().getItem(position);
            promoId = Long.parseLong(item.getId());
            messageText.setText(messageText.getText().toString() + " " + item.getName());
            listCollapseExpand();
        });
    }

    private void listCollapseExpand() {
        linearLayout
                .getLayoutTransition()
                .enableTransitionType(LayoutTransition.CHANGING);

        ViewGroup.LayoutParams params = layoutPromoList.getLayoutParams();
        params.height = params.height == 0 ? ViewGroup.LayoutParams.WRAP_CONTENT : 0;
        layoutPromoList.setLayoutParams(params);
    }

    private void setChatHistory(ArrayList<Conversation> conversationList) {
        for (Conversation item : conversationList) {
            if (item.getSender().equals(username)) {
                chatLocals.add(new ChatLocal(item.getContent(), SenderChatEnums.YOUR));
            } else {
                chatLocals.add(new ChatLocal(item.getContent(), SenderChatEnums.CLIENT));
            }
        }

        mAdapter = new ChatAdapter(chatLocals);
        mAdapter.setHasStableIds(true);
        messagesList.setAdapter(mAdapter);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);

        mLayoutManager.setOrientation(RecyclerView.VERTICAL);
        mLayoutManager.setStackFromEnd(true);
        //  mLayoutManager.setSmoothScrollbarEnabled(true);
        //  mLayoutManager.setReverseLayout(true);

        messagesList.setLayoutManager(mLayoutManager);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        setChatViewed();
        disconnectStomp();
        resetSubscriptions();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    private void addItem(ChatLocal echoModel) {
        chatLocals.add(echoModel);
        mAdapter.notifyDataSetChanged();
        messagesList.smoothScrollToPosition(chatLocals.size() - 1);
    }

    @Override
    protected void onPause() {
        chatLocals.clear();
//        mAdapter.notifyDataSetChanged();

        messagesList.removeAllViews();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
       /* if(null != inMessageAdapter)
        inMessageAdapter.clear();*/
        if (conversationInfo != null) getConversationHistory();
    }
}
