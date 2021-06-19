package com.pinus.alexdev.avis.view.promo_code_activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.dagang.library.GradientButton;
import com.muddzdev.styleabletoast.StyleableToast;
import com.pinus.alexdev.avis.R;
import com.pinus.alexdev.avis.dto.response.StatusResponse;
import com.pinus.alexdev.avis.network.ApiClient;
import com.pinus.alexdev.avis.network.apiServices.PromoCodeApiService;
import com.pinus.alexdev.avis.utils.SaveLoadData;
import com.pinus.alexdev.avis.view.BaseToolbarBack;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static com.pinus.alexdev.avis.view.LoginActivity.ORGANIZATION_ID_KEY;

public class ApplyPromoCodeActivity extends BaseToolbarBack {
    private static final String TAG = ApplyPromoCodeActivity.class.getSimpleName();
    private CompositeDisposable disposable = new CompositeDisposable();

    @BindView(R.id.scanQrButton)
    ImageButton qrScanButton;

    @BindView(R.id.appbar_back)
    View appbar;

    @BindView(R.id.backLayout)
    LinearLayout backTo;

    @BindView(R.id.editText1)
    EditText editText1;

    @BindView(R.id.editText2)
    EditText editText2;

    @BindView(R.id.editText3)
    EditText editText3;

    @BindView(R.id.editText4)
    EditText editText4;

    @BindView(R.id.editText5)
    EditText editText5;

    @BindView(R.id.editText6)
    EditText editText6;

    @BindView(R.id.btnApplyPromoCodeID)
    GradientButton btnApplyPromoCodeID;

    PromoCodeApiService promoApiService;

    private final int CODE_ID_SIZE = 6;

    //Класс для удобной работы с SharedPreferences
    SaveLoadData saveLoadData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apply_promo_code);
        ButterKnife.bind(this);
        startScan(qrScanButton);
        setButtonBackClick(
                appbar.findViewById(R.id.appBarTitle),
                getResources().getString(R.string.applyPromoCodeSmall),
                appbar.findViewById(R.id.logoImageBack),
                backTo,
                appbar.findViewById(R.id.toolbarPreviosActivityTitle),
                "",
                null
        );

        promoApiService = ApiClient.getClient(this).create(PromoCodeApiService.class);
        saveLoadData = new SaveLoadData(this);

        //Этот метод нужен для автоматического перебрасывания курсора на следующий EditText
        setEditTextContent(editText1, editText2);
        setEditTextContent(editText2, editText3);
        setEditTextContent(editText3, editText4);
        setEditTextContent(editText4, editText5);
        setEditTextContent(editText5, editText6);

        //Этот метод нужен для автоматического перебрасывания курсора на прежний EditText после нажатия кнопки стереть (backspace)
        removeEditTextContent(editText6, editText5);
        removeEditTextContent(editText5, editText4);
        removeEditTextContent(editText4, editText3);
        removeEditTextContent(editText3, editText2);
        removeEditTextContent(editText2, editText1);

        btnApplyPromoCodeID.getButton().setOnClickListener(v -> {
            //В дальнейшем сделать проверку на правильность кода, а не только на количество, как сейчас

            //Собираем числа каждого EditText в один String
            String codeID = "";
            codeID += editText1.getText().toString();
            codeID += editText2.getText().toString();
            codeID += editText3.getText().toString();
            codeID += editText4.getText().toString();
            codeID += editText5.getText().toString();
            codeID += editText6.getText().toString();
            Log.v("MyTag", codeID);

            //Проверка на наличие больших букв (код должен состоять только из цифр и больших букв)
            boolean capitalFlag = false;
            char[] charArr = codeID.toCharArray();
            for (char c : charArr) {
                if (!Character.isLetter(c)) continue;
                if (Character.isUpperCase(c)) {
                    capitalFlag = true;
                } else {
                    capitalFlag = false;
                    break;
                }
            }

            if (codeID.length() != CODE_ID_SIZE || !capitalFlag) {
                StyleableToast.makeText(getApplicationContext(), getString(R.string.code_id_should_be_six_numbers), Toast.LENGTH_SHORT, R.style.mytoast).show();
            } else {
                promoApiService
                        .proceedPromoCode(codeID, saveLoadData.loadInt(ORGANIZATION_ID_KEY))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(statusResponse -> {
                            
                        }, e -> {
                            Log.e(TAG, "onError: " + e.getMessage());
                            StyleableToast.makeText(getApplicationContext(), getString(R.string.code_does_not_exist), Toast.LENGTH_SHORT, R.style.mytoast).show();
                        });
            }
        });
    }

    private void setEditTextContent(EditText firstEditText, EditText secondEditText) {
        firstEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (firstEditText.getText().toString().length() == 1) {
                    secondEditText.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void removeEditTextContent(EditText secondEditText, EditText firstEditText) {
        secondEditText.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_DEL) {
                firstEditText.requestFocus();
            }
            return false;
        });
    }
}
