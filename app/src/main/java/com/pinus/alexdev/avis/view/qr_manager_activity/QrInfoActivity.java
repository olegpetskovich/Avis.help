package com.pinus.alexdev.avis.view.qr_manager_activity;

import android.animation.LayoutTransition;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatEditText;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.dagang.library.GradientButton;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.muddzdev.styleabletoast.StyleableToast;
import com.pinus.alexdev.avis.R;
import com.pinus.alexdev.avis.adapter.QrSpinnerListAdapter;
import com.pinus.alexdev.avis.dto.request.CtaUpdateRequest;
import com.pinus.alexdev.avis.dto.request.OpinionUpdateRequest;
import com.pinus.alexdev.avis.dto.request.cta_request.OptionsRequest;
import com.pinus.alexdev.avis.dto.response.BranchesResponse;
import com.pinus.alexdev.avis.dto.response.qr_manager_list_response.cta_response.OptionsResponse;
import com.pinus.alexdev.avis.model.AnswerModel;
import com.pinus.alexdev.avis.model.BranchModel;
import com.pinus.alexdev.avis.model.CtaModel;
import com.pinus.alexdev.avis.model.LanguagesModel;
import com.pinus.alexdev.avis.model.OpinionModel;
import com.pinus.alexdev.avis.network.ApiClient;
import com.pinus.alexdev.avis.network.apiServices.BranchApiService;
import com.pinus.alexdev.avis.network.apiServices.QrManagerApiService;
import com.pinus.alexdev.avis.utils.SaveLoadData;
import com.pinus.alexdev.avis.view.BaseToolbarBack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static com.pinus.alexdev.avis.view.BranchFilterActivity.BRANCH_FILTER_KEY;
import static com.pinus.alexdev.avis.view.LoginActivity.ORGANIZATION_ID_KEY;
import static com.pinus.alexdev.avis.view.qr_manager_activity.AddOrEditAnswerActivity.ADD_ANSWER;
import static com.pinus.alexdev.avis.view.qr_manager_activity.AddOrEditAnswerActivity.ANSWER_KEY;
import static com.pinus.alexdev.avis.view.qr_manager_activity.AddOrEditAnswerActivity.EDIT_ANSWER;

public class QrInfoActivity extends BaseToolbarBack implements QrSpinnerListAdapter.AnswerOrCategoryDataListener {
    private static final String TAG = QrInfoActivity.class.getSimpleName();

    public static final int CTA_QR_TYPE = 0;
    public static final int OPINION_QR_TYPE = 1;

    private final String EN_QUESTION_KEY = "enQ";
    private final String EN_SUCCESS_KEY = "enS";

    private final String FR_QUESTION_KEY = "frQ";
    private final String FR_SUCCESS_KEY = "frS";

    private final String UA_QUESTION_KEY = "uaQ";
    private final String UA_SUCCESS_KEY = "uaS";

    private final String RU_QUESTION_KEY = "ruQ";
    private final String RU_SUCCESS_KEY = "ruS";

    @BindView(R.id.appbar_back)
    View appbar;
    @BindView(R.id.backLayout)
    LinearLayout backTo;

    @BindView(R.id.scanQrButton)
    ImageButton qrScanButton;

    @BindView(R.id.tvQrType)
    TextView tvQrType;

    @BindView(R.id.tvQrCategory)
    TextView tvQrCategory;

    @BindView(R.id.tvBranch)
    TextView tvBranch;

    @BindView(R.id.qrNameText)
    AppCompatEditText qrNameText;

    @BindView(R.id.enterQuestionInput)
    AppCompatEditText enterQuestionInput;

    @BindView(R.id.successMessageInput)
    AppCompatEditText successMessageInput;

    @BindView(R.id.qrCategoryCard)
    MaterialCardView qrCategoryCard;

    @BindView(R.id.btnDelete)
    MaterialButton btnDelete;

    @BindView(R.id.btnSaveQR)
    GradientButton btnSaveQr;

    @BindView(R.id.cardEn)
    MaterialCardView cardEn;
    @BindView(R.id.btnEn)
    MaterialButton btnEn;

    @BindView(R.id.cardFr)
    MaterialCardView cardFr;
    @BindView(R.id.btnFr)
    MaterialButton btnFr;

    @BindView(R.id.cardUa)
    MaterialCardView cardUa;
    @BindView(R.id.btnUa)
    MaterialButton btnUa;

    @BindView(R.id.cardRu)
    MaterialCardView cardRu;
    @BindView(R.id.btnRu)
    MaterialButton btnRu;

    @BindView(R.id.cardOpenSpinner)
    MaterialCardView cardOpenSpinner;

    @BindView(R.id.spinnerText)
    TextView spinnerText;

    @BindView(R.id.spinnerArrowImg)
    ImageView spinnerArrowImg;

    @BindView(R.id.answerLayout)
    ConstraintLayout answerLayout;

    @BindView(R.id.layoutCtaSpinner)
    MaterialCardView layoutCtaSpinner;

    @BindView(R.id.layoutWithAnswersList)
    RelativeLayout layoutWithAnswersList;

    @BindView(R.id.answersList)
    ListView answersListView;

    LanguagesModel languagesModel;

    private QrManagerApiService qrManagerApiService;
    private BranchApiService branchApiService;

    private Intent intent;

    private ArrayList<String> answers;
    private QrSpinnerListAdapter adapter;

    private ArrayList<AnswerModel> answersList = new ArrayList<>();

    private CtaModel ctaModel;
    private OpinionModel opinionModel;

    private int branchId;

    private SaveLoadData saveLoadData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_info);
        ButterKnife.bind(this);
        startScan(qrScanButton);
        setButtonBackClick(
                appbar.findViewById(R.id.appBarTitle),
                "", //Здесь должно быть имя выбранного QR
                appbar.findViewById(R.id.logoImageBack),
                backTo,
                appbar.findViewById(R.id.toolbarPreviosActivityTitle),
                "",
                null
        );

        saveLoadData = new SaveLoadData(this);

        enterQuestionInput.setFocusable(false);
        enterQuestionInput.setOnClickListener(v -> {
            if (!enterQuestionInput.isFocusable()) {
                Toast.makeText(this, getString(R.string.selectLanguage), Toast.LENGTH_SHORT).show();
            }
        });

        successMessageInput.setFocusable(false);
        successMessageInput.setOnClickListener(v -> {
            if (!successMessageInput.isFocusable()) {
                Toast.makeText(this, getString(R.string.selectLanguage), Toast.LENGTH_SHORT).show();
            }
        });

        intent = getIntent();
        languagesModel = new LanguagesModel();
        if (intent.getIntExtra("qrType", -1) != -1) {
            branchApiService = ApiClient.getClient(getApplicationContext()).create(BranchApiService.class);

            qrManagerApiService = ApiClient.getClient(getApplicationContext()).create(QrManagerApiService.class);

            if (intent.getIntExtra("qrType", -1) == CTA_QR_TYPE) {
                qrCategoryCard.setVisibility(View.GONE);
                answerLayout.setVisibility(View.VISIBLE);

                ctaModel = new Gson().fromJson(intent.getStringExtra("qrManagerJson"), CtaModel.class);
                branchId = ctaModel.getBranchId();

                setSpinnerSelectAnswerContent(ctaModel.getOptions());

                tvQrType.setText(getString(R.string.ctaTitle));

                tvQrCategory.setText(ctaModel.getCategory());
                qrNameText.setText(ctaModel.getName());
                if (ctaModel.getQuestion() != null) {
                    languagesModel.setEnQ(ctaModel.getQuestion().getEn());
                    languagesModel.setFrQ(ctaModel.getQuestion().getFr());
                    languagesModel.setRuQ(ctaModel.getQuestion().getRu());
                    languagesModel.setUaQ(ctaModel.getQuestion().getUkr());

                    enterQuestionInput.setText(ctaModel.getQuestion().getEn());
                    setContentFullness(languagesModel);
                }
                if (ctaModel.getMessage() != null) {
                    languagesModel.setEnS(ctaModel.getMessage().getEn());
                    languagesModel.setFrS(ctaModel.getMessage().getFr());
                    languagesModel.setRuS(ctaModel.getMessage().getRu());
                    languagesModel.setUaS(ctaModel.getMessage().getUkr());

                    successMessageInput.setText(ctaModel.getMessage().getEn());
                    setContentFullness(languagesModel);
                }

                btnSaveQr.getButton().setOnClickListener(v -> {
                    //Запаковываем список messages для requestDto
                    Map<String, Object> messagesMap = new HashMap<>();
                    messagesMap.put("en", languagesModel.getEnS());
                    messagesMap.put("fr", languagesModel.getFrS());
                    messagesMap.put("ukr", languagesModel.getUaS());
                    messagesMap.put("ru", languagesModel.getRuS());

                    //Запаковываем список questions для requestDto
                    Map<String, Object> questionsMap = new HashMap<>();
                    questionsMap.put("en", languagesModel.getEnQ());
                    questionsMap.put("fr", languagesModel.getFrQ());
                    questionsMap.put("ukr", languagesModel.getUaQ());
                    questionsMap.put("ru", languagesModel.getRuQ());

                    //Запаковываем список ответов (answers) для requestDto
                    List<OptionsRequest> list = new ArrayList<>();
                    for (AnswerModel model : answersList) {
                        Map<String, String> map = new HashMap<>();
                        map.put("en", model.getAnswerName());
                        list.add(new OptionsRequest(model.getKey(), map));
                    }

                    if (answers.size() < 2) { //Проверяется, не меньше ли список answers размера 2, потому что по дефолту он состоит из размера 1, первый айтем в нём "Добавить ответ"
                        StyleableToast.makeText(getApplicationContext(), getString(R.string.you_didnt_add_answers), Toast.LENGTH_SHORT, R.style.mytoast).show();
                        return;
                    }

                    if (spinnerText.getText().toString().equals(getString(R.string.edit_answers_spinner))) {
                        StyleableToast.makeText(getApplicationContext(), getString(R.string.you_didnt_select_answers), Toast.LENGTH_SHORT, R.style.mytoast).show();
                        return;
                    }

                    qrManagerApiService
                            .updateCtaQr(ctaModel.getBranchId(), new CtaUpdateRequest("en", qrNameText.getText().toString(), list, messagesMap, questionsMap), ctaModel.getId(), saveLoadData.loadInt(ORGANIZATION_ID_KEY))
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(s -> {
                                StyleableToast.makeText(getApplicationContext(), getString(R.string.qr_updated), Toast.LENGTH_LONG, R.style.mytoast).show();
                                finish();
                            }, e -> Log.e("QrOnError", "OnError: " + e.getMessage()));
                });


                btnDelete.setOnClickListener(v ->
                        qrManagerApiService
                                .deleteQrCta(ctaModel.getBranchId(), ctaModel.getId(), saveLoadData.loadInt(ORGANIZATION_ID_KEY))
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(s -> {
                                    StyleableToast.makeText(getApplicationContext(), getString(R.string.qrDeleted), Toast.LENGTH_LONG, R.style.mytoast).show();
                                    finish();
                                }, e -> Log.e("QrOnError", "OnError: " + e.getMessage())));

            } else if (intent.getIntExtra("qrType", -1) == OPINION_QR_TYPE) {
                opinionModel = new Gson().fromJson(intent.getStringExtra("qrManagerJson"), OpinionModel.class);
                branchId = opinionModel.getBranchId();

                tvQrType.setText(getString(R.string.opinionTitle));
                tvQrCategory.setText(opinionModel.getCategory());
                qrNameText.setText(opinionModel.getName());
                if (opinionModel.getQuestion() != null) {
                    languagesModel.setEnQ(opinionModel.getQuestion().getEn());
                    languagesModel.setFrQ(opinionModel.getQuestion().getFr());
                    languagesModel.setRuQ(opinionModel.getQuestion().getRu());
                    languagesModel.setUaQ(opinionModel.getQuestion().getUkr());

                    enterQuestionInput.setText(opinionModel.getQuestion().getEn());
                    setContentFullness(languagesModel);
                }
                if (opinionModel.getMessage() != null) {
                    languagesModel.setEnS(opinionModel.getMessage().getEn());
                    languagesModel.setFrS(opinionModel.getMessage().getFr());
                    languagesModel.setRuS(opinionModel.getMessage().getRu());
                    languagesModel.setUaS(opinionModel.getMessage().getUkr());

                    successMessageInput.setText(opinionModel.getMessage().getEn());
                    setContentFullness(languagesModel);
                }

                btnSaveQr.getButton().setOnClickListener(v -> {
                    //Запаковываем список messages для requestDto
                    Map<String, Object> messagesMap = new HashMap<>();
                    messagesMap.put("en", languagesModel.getEnS());
                    messagesMap.put("fr", languagesModel.getFrS());
                    messagesMap.put("ukr", languagesModel.getUaS());
                    messagesMap.put("ru", languagesModel.getRuS());

                    //Запаковываем список questions для requestDto
                    Map<String, Object> questionsMap = new HashMap<>();
                    questionsMap.put("en", languagesModel.getEnQ());
                    questionsMap.put("fr", languagesModel.getFrQ());
                    questionsMap.put("ukr", languagesModel.getUaQ());
                    questionsMap.put("ru", languagesModel.getRuQ());

                    qrManagerApiService
                            .updateOpinionQr(opinionModel.getBranchId(), new OpinionUpdateRequest(true, opinionModel.getCategory(), "en", qrNameText.getText().toString(), messagesMap, questionsMap), opinionModel.getId(), saveLoadData.loadInt(ORGANIZATION_ID_KEY))
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(s -> {
                                StyleableToast.makeText(getApplicationContext(), getString(R.string.qr_updated), Toast.LENGTH_LONG, R.style.mytoast).show();
                                finish();
                            }, e -> Log.e("QrOnError", "OnError: " + e.getMessage()));
                });

                btnDelete.setOnClickListener(v ->
                        qrManagerApiService
                                .deleteQrOpinion(opinionModel.getBranchId(), opinionModel.getId(), saveLoadData.loadInt(ORGANIZATION_ID_KEY))
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(s -> {
                                    StyleableToast.makeText(getApplicationContext(), getString(R.string.qrDeleted), Toast.LENGTH_LONG, R.style.mytoast).show();
                                    finish();
                                }, e -> Log.e("QrOnError", "OnError: " + e.getMessage())));
            }

            branchApiService
                    .getBranchList(saveLoadData.loadInt(ORGANIZATION_ID_KEY))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(branchesResponses -> {
                        for (BranchesResponse response : branchesResponses) {
                            if (branchId == response.getId()) {
                                tvBranch.setText(response.getName());
                                return;
                            }
                        }
                    }, e -> Log.e("QrOnError", "OnError: " + e.getMessage()));

            enterQuestionInput.setFocusable(true);
            enterQuestionInput.setFocusableInTouchMode(true);

            successMessageInput.setFocusable(true);
            successMessageInput.setFocusableInTouchMode(true);

            languagesModel.setCurrentQ(EN_QUESTION_KEY);
            languagesModel.setCurrentS(EN_SUCCESS_KEY);
            inputTextHandler(languagesModel);

            setCardStroke(cardEn, R.color.colorPrimary);
            setCardStroke(cardFr, android.R.color.transparent);
            setCardStroke(cardUa, android.R.color.transparent);
            setCardStroke(cardRu, android.R.color.transparent);
        }

        btnEn.setOnClickListener(v -> {
            enterQuestionInput.setFocusable(true);
            enterQuestionInput.setFocusableInTouchMode(true);

            successMessageInput.setFocusable(true);
            successMessageInput.setFocusableInTouchMode(true);

            Log.v(TAG, "EN_Q: " + languagesModel.getEnQ() + ", " + "EN_S: " + languagesModel.getEnS());
            languagesModel.setCurrentQ(EN_QUESTION_KEY);
            languagesModel.setCurrentS(EN_SUCCESS_KEY);
            enterQuestionInput.setText(languagesModel.getEnQ());
            successMessageInput.setText(languagesModel.getEnS());
            inputTextHandler(languagesModel);

            setCardStroke(cardEn, R.color.colorPrimary);
            setCardStroke(cardFr, android.R.color.transparent);
            setCardStroke(cardUa, android.R.color.transparent);
            setCardStroke(cardRu, android.R.color.transparent);
        });
        btnFr.setOnClickListener(v -> {
            enterQuestionInput.setFocusable(true);
            enterQuestionInput.setFocusableInTouchMode(true);

            successMessageInput.setFocusable(true);
            successMessageInput.setFocusableInTouchMode(true);

            Log.v(TAG, "FR_Q: " + languagesModel.getFrQ() + ", " + "FR_S: " + languagesModel.getFrS());
            languagesModel.setCurrentQ(FR_QUESTION_KEY);
            languagesModel.setCurrentS(FR_SUCCESS_KEY);
            enterQuestionInput.setText(languagesModel.getFrQ());
            successMessageInput.setText(languagesModel.getFrS());
            inputTextHandler(languagesModel);

            setCardStroke(cardEn, android.R.color.transparent);
            setCardStroke(cardFr, R.color.colorPrimary);
            setCardStroke(cardUa, android.R.color.transparent);
            setCardStroke(cardRu, android.R.color.transparent);
        });
        btnUa.setOnClickListener(v -> {
            enterQuestionInput.setFocusable(true);
            enterQuestionInput.setFocusableInTouchMode(true);

            successMessageInput.setFocusable(true);
            successMessageInput.setFocusableInTouchMode(true);

            Log.v(TAG, "UA_Q: " + languagesModel.getUaQ() + ", " + "UA_S: " + languagesModel.getUaS());
            languagesModel.setCurrentQ(UA_QUESTION_KEY);
            languagesModel.setCurrentS(UA_SUCCESS_KEY);
            enterQuestionInput.setText(languagesModel.getUaQ());
            successMessageInput.setText(languagesModel.getUaS());
            inputTextHandler(languagesModel);

            setCardStroke(cardEn, android.R.color.transparent);
            setCardStroke(cardFr, android.R.color.transparent);
            setCardStroke(cardUa, R.color.colorPrimary);
            setCardStroke(cardRu, android.R.color.transparent);
        });
        btnRu.setOnClickListener(v -> {
            enterQuestionInput.setFocusable(true);
            enterQuestionInput.setFocusableInTouchMode(true);

            successMessageInput.setFocusable(true);
            successMessageInput.setFocusableInTouchMode(true);

            Log.v(TAG, "RU_Q: " + languagesModel.getRuQ() + ", " + "RU_S: " + languagesModel.getRuS());
            languagesModel.setCurrentQ(RU_QUESTION_KEY);
            languagesModel.setCurrentS(RU_SUCCESS_KEY);
            enterQuestionInput.setText(languagesModel.getRuQ());
            successMessageInput.setText(languagesModel.getRuS());
            inputTextHandler(languagesModel);

            setCardStroke(cardEn, android.R.color.transparent);
            setCardStroke(cardFr, android.R.color.transparent);
            setCardStroke(cardUa, android.R.color.transparent);
            setCardStroke(cardRu, R.color.colorPrimary);
        });
    }

    private void setCardStroke(MaterialCardView cardView, int backgroundColor) {
        cardView.setStrokeColor(ContextCompat.getColor(this, backgroundColor));
    }

    private void setButtonStyle(MaterialButton button, int backgroundColor, int textColor) {
        button.setBackgroundColor(ContextCompat.getColor(this, backgroundColor));
        button.setTextColor(ContextCompat.getColor(this, textColor));
    }

    private void setContentFullness(LanguagesModel languagesModel) {
        if ((languagesModel.getEnQ() != null && !languagesModel.getEnQ().isEmpty()) && (languagesModel.getEnS() != null && !languagesModel.getEnS().isEmpty())) {
            setButtonStyle(btnEn, R.color.colorPrimary, android.R.color.white);
        } else
            setButtonStyle(btnEn, android.R.color.transparent, R.color.colorPrimaryDark);
        if ((languagesModel.getFrQ() != null && !languagesModel.getFrQ().isEmpty()) && (languagesModel.getFrS() != null && !languagesModel.getFrS().isEmpty())) {
            setButtonStyle(btnFr, R.color.colorPrimary, android.R.color.white);
        } else
            setButtonStyle(btnFr, android.R.color.transparent, R.color.colorPrimaryDark);
        if ((languagesModel.getUaQ() != null && !languagesModel.getUaQ().isEmpty()) && (languagesModel.getUaS() != null && !languagesModel.getUaS().isEmpty())) {
            setButtonStyle(btnUa, R.color.colorPrimary, android.R.color.white);
        } else
            setButtonStyle(btnUa, android.R.color.transparent, R.color.colorPrimaryDark);
        if ((languagesModel.getRuQ() != null && !languagesModel.getRuQ().isEmpty()) && (languagesModel.getRuS() != null && !languagesModel.getRuS().isEmpty())) {
            setButtonStyle(btnRu, R.color.colorPrimary, android.R.color.white);
        } else
            setButtonStyle(btnRu, android.R.color.transparent, R.color.colorPrimaryDark);
    }

    private void inputTextHandler(LanguagesModel languagesModel) {
        enterQuestionInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.v(TAG, "Key question: " + languagesModel.getCurrentQ() + ", " + "Key success: " + languagesModel.getCurrentS());
                switch (languagesModel.getCurrentQ()) {
                    case EN_QUESTION_KEY:
                        languagesModel.setEnQ(s.toString());
                        if (!languagesModel.getEnQ().isEmpty() && !languagesModel.getEnS().isEmpty()) {
                            setButtonStyle(btnEn, R.color.colorPrimary, android.R.color.white);
                        } else
                            setButtonStyle(btnEn, android.R.color.transparent, R.color.colorPrimaryDark);
                        break;
                    case FR_QUESTION_KEY:
                        languagesModel.setFrQ(s.toString());
                        if (!languagesModel.getFrQ().isEmpty() && !languagesModel.getFrS().isEmpty()) {
                            setButtonStyle(btnFr, R.color.colorPrimary, android.R.color.white);
                        } else
                            setButtonStyle(btnFr, android.R.color.transparent, R.color.colorPrimaryDark);
                        break;
                    case UA_QUESTION_KEY:
                        languagesModel.setUaQ(s.toString());
                        if (!languagesModel.getUaQ().isEmpty() && !languagesModel.getUaS().isEmpty()) {
                            setButtonStyle(btnUa, R.color.colorPrimary, android.R.color.white);
                        } else
                            setButtonStyle(btnUa, android.R.color.transparent, R.color.colorPrimaryDark);
                        break;
                    case RU_QUESTION_KEY:
                        languagesModel.setRuQ(s.toString());
                        if (!languagesModel.getRuQ().isEmpty() && !languagesModel.getRuS().isEmpty()) {
                            setButtonStyle(btnRu, R.color.colorPrimary, android.R.color.white);
                        } else
                            setButtonStyle(btnRu, android.R.color.transparent, R.color.colorPrimaryDark);
                        break;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        successMessageInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                switch (languagesModel.getCurrentS()) {
                    case EN_SUCCESS_KEY:
                        languagesModel.setEnS(s.toString());
                        if (!languagesModel.getEnQ().isEmpty() && !languagesModel.getEnS().isEmpty()) {
                            setButtonStyle(btnEn, R.color.colorPrimary, android.R.color.white);
                        } else
                            setButtonStyle(btnEn, android.R.color.transparent, R.color.colorPrimaryDark);
                        break;
                    case FR_SUCCESS_KEY:
                        languagesModel.setFrS(s.toString());
                        if (!languagesModel.getFrQ().isEmpty() && !languagesModel.getFrS().isEmpty()) {
                            setButtonStyle(btnFr, R.color.colorPrimary, android.R.color.white);
                        } else
                            setButtonStyle(btnFr, android.R.color.transparent, R.color.colorPrimaryDark);
                        break;
                    case UA_SUCCESS_KEY:
                        languagesModel.setUaS(s.toString());
                        if (!languagesModel.getUaQ().isEmpty() && !languagesModel.getUaS().isEmpty()) {
                            setButtonStyle(btnUa, R.color.colorPrimary, android.R.color.white);
                        } else
                            setButtonStyle(btnUa, android.R.color.transparent, R.color.colorPrimaryDark);
                        break;
                    case RU_SUCCESS_KEY:
                        languagesModel.setRuS(s.toString());
                        if (!languagesModel.getRuQ().isEmpty() && !languagesModel.getRuS().isEmpty()) {
                            setButtonStyle(btnRu, R.color.colorPrimary, android.R.color.white);
                        } else
                            setButtonStyle(btnRu, android.R.color.transparent, R.color.colorPrimaryDark);
                        break;
                }
//                Log.v(TAG, "Typing success text: " + s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void setSpinnerSelectAnswerContent(List<OptionsResponse> options) {
        answers = new ArrayList<>();

        for (int i = 0; i < options.size(); i++)
            answers.add(options.get(i).getValue().getEn()); // Упаковываем spinner для редактирования
        answers.add(0, getString(R.string.addAnswer));

        adapter = new QrSpinnerListAdapter(this, R.layout.item_answer, answers);
        adapter.isCategorySpinner = false;
        adapter.setAnswerOrCategoryDataListener(this);

        answersListView.setAdapter(adapter);
        answersListView.setOnItemClickListener((parent, view, position, id) -> {
            if (position == 0) {

            } else {
                String answer = (String) parent.getAdapter().getItem(position);
                if (answersList.isEmpty()) spinnerText.setText("");

                boolean isAnswerUnique = true;
                for (AnswerModel answerText : answersList) {
                    if (answer.equals(answerText.getAnswerName())) {
                        isAnswerUnique = false; // - Проверка на выбор повторных айтемов
                        return;
                    } else {
                        isAnswerUnique = true;
                    }
                }

                if (isAnswerUnique) {
                    AnswerModel answerModel = new AnswerModel();
                    answerModel.setKey("key_" + answer);
                    answerModel.setAnswerName(answer);
                    answersList.add(answerModel);
                    spinnerText.setText(spinnerText.getText() + " " + answer);
                }
            }

            String name = (String) parent.getAdapter().getItem(position);
            Log.v("AnswerName", "Answer name is: " + name);

            listCollapseExpand();
        });

        cardOpenSpinner.setOnClickListener(v -> listCollapseExpand());

    }

    private void listCollapseExpand() {
        layoutCtaSpinner
                .getLayoutTransition()
                .enableTransitionType(LayoutTransition.CHANGING);

        ViewGroup.LayoutParams params = layoutWithAnswersList.getLayoutParams();
        params.height = params.height == 0 ? getTotalHeightOfListView(answersListView) : 0;
        getTotalHeightOfListView(answersListView);
        layoutWithAnswersList.setLayoutParams(params);
        answersListView.requestLayout();
    }

    private int getTotalHeightOfListView(ListView listView) {
        ListAdapter mAdapter = listView.getAdapter();
        int totalHeight = 0;

        for (int i = 0; i < mAdapter.getCount(); i++) {
            View mView = mAdapter.getView(i, null, listView);

            mView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

            totalHeight += mView.getMeasuredHeight();
            Log.w("HEIGHT" + i, String.valueOf(totalHeight));
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (mAdapter.getCount() - 1));

        //На случай, если нужно сразу установить параметры для ListView
//            listView.setLayoutParams(params);
//            listView.requestLayout();

        return params.height;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (intent.getIntExtra("qrType", -1) == CTA_QR_TYPE) {
            if (saveLoadData.loadInt("add_or_edit_answer") == ADD_ANSWER) {
                if (saveLoadData.loadString(ANSWER_KEY) != null) {
                    answers.add(saveLoadData.loadString(ANSWER_KEY));
                    adapter.notifyDataSetChanged();
                    saveLoadData.saveString(ANSWER_KEY, null);
                    StyleableToast.makeText(getApplicationContext(), getString(R.string.answer_added), Toast.LENGTH_SHORT, R.style.mytoast).show();
                }
            } else if (saveLoadData.loadInt("add_or_edit_answer") == EDIT_ANSWER) {
                if (saveLoadData.loadString(ANSWER_KEY) != null) {
                    answers.set(saveLoadData.loadInt("answer_position"), saveLoadData.loadString(ANSWER_KEY));
                    adapter.notifyDataSetChanged();
                    saveLoadData.saveString(ANSWER_KEY, null);
                    StyleableToast.makeText(getApplicationContext(), getString(R.string.answer_edited), Toast.LENGTH_SHORT, R.style.mytoast).show();
                }
                if (saveLoadData.loadBoolean("isForDelete")) {
                    int pos = saveLoadData.loadInt("answer_position");
                    answers.remove(pos);
                    adapter.notifyDataSetChanged();
                    saveLoadData.saveBoolean("isForDelete", false);
                    StyleableToast.makeText(getApplicationContext(), getString(R.string.answer_deleted), Toast.LENGTH_SHORT, R.style.mytoast).show();
                }
            }
        }
    }


    @Override
    public void listCollapsed() {
        listCollapseExpand();
    }
}
