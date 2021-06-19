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
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatEditText;
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
import com.pinus.alexdev.avis.dto.request.opinion_request.Opinion;
import com.pinus.alexdev.avis.dto.request.opinion_request.OpinionRequest;
import com.pinus.alexdev.avis.dto.response.BranchesItem;
import com.pinus.alexdev.avis.model.BranchModel;
import com.pinus.alexdev.avis.model.LanguagesModel;
import com.pinus.alexdev.avis.network.ApiClient;
import com.pinus.alexdev.avis.network.apiServices.QrManagerApiService;
import com.pinus.alexdev.avis.utils.SaveLoadData;
import com.pinus.alexdev.avis.view.BaseToolbarBack;
import com.pinus.alexdev.avis.view.BranchFilterActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

import static com.pinus.alexdev.avis.view.BranchFilterActivity.BRANCH_FILTER_KEY;
import static com.pinus.alexdev.avis.view.LoginActivity.ORGANIZATION_ID_KEY;
import static com.pinus.alexdev.avis.view.qr_manager_activity.AddOrEditAnswerActivity.ADD_ANSWER;
import static com.pinus.alexdev.avis.view.qr_manager_activity.AddOrEditAnswerActivity.ANSWER_KEY;
import static com.pinus.alexdev.avis.view.qr_manager_activity.AddOrEditAnswerActivity.EDIT_ANSWER;
import static com.pinus.alexdev.avis.view.qr_manager_activity.AddOrEditCategoryActivity.ADD_CATEGORY;
import static com.pinus.alexdev.avis.view.qr_manager_activity.AddOrEditCategoryActivity.CATEGORY_KEY;
import static com.pinus.alexdev.avis.view.qr_manager_activity.AddOrEditCategoryActivity.EDIT_CATEGORY;

public class OpinionQRActivity extends BaseToolbarBack implements QrSpinnerListAdapter.AnswerOrCategoryDataListener {
    private static final String TAG = OpinionQRActivity.class.getSimpleName();
    private CompositeDisposable disposable = new CompositeDisposable();

    private final int MAX_INPUT_QUESTION_LENGTH = 600;
    private final int MAX_INPUT_SUCCESS_MESSAGE_LENGTH = 50;

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

    @BindView(R.id.cardOpenSpinner)
    MaterialCardView cardOpenSpinner;

    @BindView(R.id.spinnerText)
    TextView spinnerText;

    @BindView(R.id.layoutOpinionSpinner)
    MaterialCardView layoutOpinionSpinner;

    @BindView(R.id.layoutWithCategoriesList)
    RelativeLayout layoutWithCategoriesList;

    @BindView(R.id.categoriesListView)
    ListView categoriesListView;

    @BindView(R.id.enterQRNameInput)
    AppCompatEditText enterQRNameInput;

    @BindView(R.id.enterQuestionInput)
    AppCompatEditText enterQuestionInput;

    @BindView(R.id.successMessageInput)
    AppCompatEditText successMessageInput;

    @BindView(R.id.tvCountCharactersQuestion)
    TextView tvCountCharactersQuestion;

    @BindView(R.id.tvCountCharactersSuccessMessage)
    TextView tvCountCharactersSuccessMessage;

    @BindView(R.id.scanQrButton)
    ImageButton qrScanButton;

    @BindView(R.id.btnSaveQR)
    GradientButton btnSaveQR;

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

    @BindView(R.id.btnChooseBranch)
    MaterialButton btnChooseBranch;

    private ArrayList<BranchModel> branches;
    private String selectedCategory;
    private ArrayList<String> categories;
    private QrSpinnerListAdapter adapter;

    private QrManagerApiService qrManagerApiService;

    private LanguagesModel languagesModel;

    //Класс для удобной работы с SharedPreferences
    SaveLoadData saveLoadData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opinion_qr);
        ButterKnife.bind(this);
        startScan(qrScanButton);
        setButtonBackClick(
                appbar.findViewById(R.id.appBarTitle),
                getResources().getString(R.string.opinionTitle),
                appbar.findViewById(R.id.logoImageBack),
                backTo,
                appbar.findViewById(R.id.toolbarPreviosActivityTitle),
                "",
                null
        );

        String[] categories = {"General", "Service", "Cleanliness", "Add QR Category"};
        ArrayList<BranchesItem> branchesResponseList = new ArrayList<>();

        for (String category : categories) {
            BranchesItem branchesItem = new BranchesItem();
            branchesItem.setName(category);
            branchesResponseList.add(branchesItem);
        }

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

        saveLoadData = new SaveLoadData(this);

        qrManagerApiService = ApiClient.getClient(getApplicationContext()).create(QrManagerApiService.class);
        btnSaveQR.getButton().setOnClickListener(v -> {
            //Проверка на наличие имени
            String qrName = enterQRNameInput.getText().toString();
            if (qrName.isEmpty()) {
                StyleableToast.makeText(getApplicationContext(), getString(R.string.you_didnt_write_name_for_qr), Toast.LENGTH_SHORT, R.style.mytoast).show();
                return;
            }

            //Запаковываем список бранчей для requestDto
            long[] branchIds;
            if (branches == null || branches.isEmpty()) {
                StyleableToast.makeText(getApplicationContext(), getString(R.string.you_didnt_select_branch), Toast.LENGTH_SHORT, R.style.mytoast).show();
                return;
            } else {
                branchIds = new long[branches.size()];
                for (int i = 0; i < branchIds.length; i++)
                    branchIds[i] = branches.get(i).getBranchID();
            }

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

            disposable.add(qrManagerApiService.addOpinionQr(new OpinionRequest(branchIds, new Opinion(true, selectedCategory, "en", messagesMap, qrName, questionsMap)), saveLoadData.loadInt(ORGANIZATION_ID_KEY))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(s -> {
                        StyleableToast.makeText(getApplicationContext(), getString(R.string.opinion_is_added), Toast.LENGTH_SHORT, R.style.mytoast).show();
                        finish();
                    }, e -> Log.e(TAG, "onError: " + e.getMessage())));
        });


        btnChooseBranch.setOnClickListener(v -> {
            BRANCH_FILTER_KEY = "OPINION_CHOOSE_BRANCH";
            startActivity(new Intent(this, BranchFilterActivity.class));
        });

        setInputCounter(enterQuestionInput, MAX_INPUT_QUESTION_LENGTH, tvCountCharactersQuestion);
        setInputCounter(successMessageInput, MAX_INPUT_SUCCESS_MESSAGE_LENGTH, tvCountCharactersSuccessMessage);

        setSpinnerSelectAnswerContent();

        languagesModel = new LanguagesModel();
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

    private void setInputCounter(AppCompatEditText inputText, int max_input_length, TextView tvCountCharacters) {
        inputText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int cnt = max_input_length - inputText.getText().length();
                tvCountCharacters.setText(String.valueOf(cnt));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void setCardStroke(MaterialCardView cardView, int backgroundColor) {
        cardView.setStrokeColor(ContextCompat.getColor(this, backgroundColor));
    }

    private void setButtonStyle(MaterialButton button, int backgroundColor, int textColor) {
        button.setBackgroundColor(ContextCompat.getColor(this, backgroundColor));
        button.setTextColor(ContextCompat.getColor(this, textColor));
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

    private void setSpinnerSelectAnswerContent() {
        categories = new ArrayList<>();
        categories.add(getString(R.string.addCategory));

        adapter = new QrSpinnerListAdapter(this, R.layout.item_answer, categories);
        adapter.isCategorySpinner = true;
        adapter.setAnswerOrCategoryDataListener(this);

        categoriesListView.setAdapter(adapter);
        categoriesListView.setOnItemClickListener((parent, view, position, id) -> {
            if (position == 0) {

            } else {
                selectedCategory = (String) parent.getAdapter().getItem(position);
                spinnerText.setText(selectedCategory);
            }

            String name = (String) parent.getAdapter().getItem(position);
            Log.v("AnswerName", "Answer name is: " + name);

            listCollapseExpand();
        });

        cardOpenSpinner.setOnClickListener(v -> listCollapseExpand());

//        cardOpenSpinner.setOnClickListener(v -> {
//            new Thread(() -> h.postDelayed(() -> spinnerSelectAnswer.performClick(), 100)).start(); // - метод performClick() нужен для того, чтобы открывать список самостоятельно, но это нужно делать в отдельном потоке с небольшой задержкой
//        });
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

    private void listCollapseExpand() {
        layoutOpinionSpinner
                .getLayoutTransition()
                .enableTransitionType(LayoutTransition.CHANGING);

        ViewGroup.LayoutParams params = layoutWithCategoriesList.getLayoutParams();
        params.height = params.height == 0 ? getTotalHeightOfListView(categoriesListView) : 0;
        getTotalHeightOfListView(categoriesListView);
        layoutWithCategoriesList.setLayoutParams(params);
        categoriesListView.requestLayout();
    }

    @Override
    protected void onResume() {
        super.onResume();

        //Чтобы qr был создан для всех бранчей, нужно передавать список всех айдишников бранчей на вход в request dto
        if (saveLoadData.loadString(BRANCH_FILTER_KEY) != null && !saveLoadData.loadString(BRANCH_FILTER_KEY).isEmpty()) {
            branches = new Gson().fromJson(saveLoadData.loadString(BRANCH_FILTER_KEY), new TypeToken<List<BranchModel>>() {
            }.getType());

            btnChooseBranch.setText("");
            for (BranchModel branch : branches) {
                if (branches.size() > 1)
                    btnChooseBranch.setText(btnChooseBranch.getText() + branch.getBranchName() + " ");
                else btnChooseBranch.setText(branch.getBranchName());
            }
            saveLoadData.saveString(BRANCH_FILTER_KEY, "");
        }
        if (saveLoadData.loadInt("add_or_edit_category") == ADD_CATEGORY) {
            if (saveLoadData.loadString(CATEGORY_KEY) != null) {
                categories.add(saveLoadData.loadString(CATEGORY_KEY));
                adapter.notifyDataSetChanged();
                saveLoadData.saveString(CATEGORY_KEY, null);
                StyleableToast.makeText(getApplicationContext(), getString(R.string.category_added), Toast.LENGTH_SHORT, R.style.mytoast).show();
            }
        } else if (saveLoadData.loadInt("add_or_edit_category") == EDIT_CATEGORY) {
            if (saveLoadData.loadString(CATEGORY_KEY) != null) {
                categories.set(saveLoadData.loadInt("category_position"), saveLoadData.loadString(CATEGORY_KEY));
                adapter.notifyDataSetChanged();
                saveLoadData.saveString(CATEGORY_KEY, null);
                StyleableToast.makeText(getApplicationContext(), getString(R.string.category_edited), Toast.LENGTH_SHORT, R.style.mytoast).show();
            }

            if (saveLoadData.loadBoolean("isForDelete")) {
                categories.remove(saveLoadData.loadInt("category_position"));
                adapter.notifyDataSetChanged();
                saveLoadData.saveBoolean("isForDelete", false);
                StyleableToast.makeText(getApplicationContext(), getString(R.string.category_deleted), Toast.LENGTH_SHORT, R.style.mytoast).show();
            }
        }
    }

    @Override
    public void listCollapsed() {
        listCollapseExpand();
    }
}
