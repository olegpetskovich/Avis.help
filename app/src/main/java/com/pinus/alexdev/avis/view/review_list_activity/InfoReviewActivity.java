package com.pinus.alexdev.avis.view.review_list_activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatEditText;
import androidx.core.app.ActivityOptionsCompat;

import com.dagang.library.GradientButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.zxing.integration.android.IntentIntegrator;
import com.jakewharton.rxbinding3.view.RxView;
import com.muddzdev.styleabletoast.StyleableToast;
import com.pinus.alexdev.avis.R;
import com.pinus.alexdev.avis.dto.response.ConversationResponse;
import com.pinus.alexdev.avis.dto.response.review_response.ReviewResponse;
import com.pinus.alexdev.avis.network.ApiClient;
import com.pinus.alexdev.avis.network.apiServices.ReviewsApiService;
import com.pinus.alexdev.avis.utils.Utils;
import com.pinus.alexdev.avis.view.BaseToolbarBack;
import com.pinus.alexdev.avis.view.CustomScannerActivity;
import com.pinus.alexdev.avis.view.conversation_activity.ConversationActivity;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static com.pinus.alexdev.avis.utils.Utils.checkField;

public class InfoReviewActivity extends BaseToolbarBack {
    @BindView(R.id.appbar_back)
    View appbar;

    private static final String TAG = InfoReviewActivity.class.getSimpleName();

    private ReviewsApiService reviewApiService;

    @BindView(R.id.scrollView)
    ScrollView scrollView;

    @BindView(R.id.reviewCreatedTitle)
    TextView txtCreatedAt;
    @BindView(R.id.reviewViewedTitle)
    TextView txtViewedAt;

    @BindView(R.id.tvBranch)
    TextView tvBranch;
    @BindView(R.id.tvQrCategory)
    TextView tvQrCategory;
    @BindView(R.id.tvImpression)
    TextView tvImpression;

    @BindView(R.id.tvComment)
    TextView tvComment;

    @BindView(R.id.tvPhone)
    TextView tvPhone;

    @BindView(R.id.btnGoToConversation)
    GradientButton btnGoToConversation;

    @BindView(R.id.phoneView1)
    View phoneView1;

    @BindView(R.id.phoneView2)
    View phoneView2;

    @BindView(R.id.reviewImg)
    ImageView reviewImg;

    @BindView(R.id.commentText)
    AppCompatEditText commentText;
    @BindView(R.id.btnSaveComment)
    GradientButton btnSaveComment;

    @BindView(R.id.backLayout)
    LinearLayout backTo;


    @BindView(R.id.phoneTitle)
    TextView phoneTitle;
    @BindView(R.id.scanQrButton)
    ImageButton qrScanButton;

    ReviewResponse reviewResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_review);
        ButterKnife.bind(this);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        if (!Utils.isNetworkAvailable(this))
            StyleableToast.makeText(getApplicationContext(), getString(R.string.internet_connection), Toast.LENGTH_LONG, R.style.mytoast).show();

        disposable.add(RxView.clicks(qrScanButton).subscribe((t) ->
                new IntentIntegrator(this).setOrientationLocked(false).setCaptureActivity(CustomScannerActivity.class).initiateScan()
        ));

        reviewApiService = ApiClient.getClient(getApplicationContext()).create(ReviewsApiService.class);
        reviewApiService
                .getReviewInfo(getIntent().getIntExtra("reviewId", -1))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(revResp -> {
                    reviewResponse = revResp;

                    setButtonBackClick(
                            appbar.findViewById(R.id.appBarTitle),
                            "Id: " + reviewResponse.getReview().getId(),
                            appbar.findViewById(R.id.logoImageBack),
                            backTo,
                            appbar.findViewById(R.id.toolbarPreviosActivityTitle),
                            "",
                            null);

                    disposable.add(reviewApiService.setViewed(reviewResponse.getReview().getId(), true)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(response -> StyleableToast.makeText(getApplicationContext(), "Viewed", Toast.LENGTH_LONG, R.style.mytoast).show(),
                                    e -> Log.e(TAG, "onError:" + e.getMessage())));

                    disposable.add(RxView.clicks(backTo).subscribe(t -> finish()));

                    disposable.add(RxView.clicks(btnSaveComment.getButton()).subscribe(t -> sentComment()));

                    reviewImg.setOnClickListener(v -> {
                        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, reviewImg, "MyAnimation");
                        Intent intent = new Intent(this, ImageActivity.class);
                        intent.putExtra("reviewImg", reviewResponse.getReview().getImageUrl());
                        startActivity(intent, options.toBundle());
                    });

                    setContent();

                }, e -> Log.e(TAG, e.getMessage()));

    }

    private void sentComment() {
        if (checkField(commentText))
            disposable.add(reviewApiService.addComment(reviewResponse.getReview().getId(), commentText.getText().toString())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(t -> {
                                hideSoftKeyboard(InfoReviewActivity.this, commentText);
                                commentText.clearFocus();
                                StyleableToast.makeText(getApplicationContext(), getString(R.string.saveCommentToast), Toast.LENGTH_LONG, R.style.mytoast).show();
                            },
                            e -> Log.e(TAG, "onError: " + e.getMessage())));
        else
            StyleableToast.makeText(getApplicationContext(), getString(R.string.you_cant_save_empty_field), Toast.LENGTH_LONG, R.style.mytoast).show();
    }

    private void setContent() {
//        Log.v(TAG, "idReviwItem: " + reviewResponse.getReview().getId());
//        Log.v(TAG, "reviewParceable" + reviewResponse.getExecutionStatus());

        txtCreatedAt.setText(getString(R.string.createdReviewTitle) + " " + convertDateHour(reviewResponse.getReview().getDateCreated()));
        txtViewedAt.setText(getString(R.string.viwedReviwedTitle) + " " + convertDateHour(reviewResponse.getReview().getDateViewed()));
        tvBranch.setText(reviewResponse.getBranchName());
        tvQrCategory.setText(reviewResponse.getOpinionCategoty());
        tvImpression.setText(String.valueOf(reviewResponse.getReview().getImpression()));

        tvPhone.setText(reviewResponse.getReview().getPhone());
        //Код предназначен для того, чтобы подчеркнуть номер снизу как ссылку
        tvPhone.setPaintFlags(tvPhone.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        SpannableString content = new SpannableString(tvPhone.getText());
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        tvPhone.setText(content);

        tvComment.setText(reviewResponse.getReview().getComment());
        commentText.setText(reviewResponse.getReview().getManagerComment());

        //Вызов этого метода позволяет проскроллить ScrollView в самый верх layout(a).
        //Сделано это потому, что без вызова этого метода TextInputEditText будет фокусировать внимание на себе
        //и layout будет скроллиться до него, а не запускаться с самого верха, как это надо.
        scrollView.smoothScrollTo(0, 0);

        if (reviewResponse.getReview().isAnonymous()) {
            btnGoToConversation.setVisibility(View.GONE);
            tvPhone.setVisibility(View.GONE);
            phoneTitle.setVisibility(View.GONE);
            phoneView1.setVisibility(View.GONE);
            phoneView2.setVisibility(View.GONE);
        }

        Picasso.get().load(reviewResponse.getReview().getImageUrl()).placeholder(R.drawable.ic_image).error(R.drawable.ic_image).into(reviewImg);

        disposable.add(RxView.clicks(btnGoToConversation.getButton()).subscribe(
                t -> {
                    ConversationResponse response = new ConversationResponse();
                    response.setBranchName(reviewResponse.getBranchName());
                    response.setChatRoom(reviewResponse.getReview().getChatRoom());
                    response.setChatUrl(reviewResponse.getReview().getChatUrl());
                    response.setReviewId(reviewResponse.getReview().getId());

                    if (reviewResponse.getReview().getChatRoom() != null)
                    response.setId(reviewResponse.getReview().getChatRoom().getId());

                    Intent intent = new Intent(this, ConversationActivity.class);
                    intent.putExtra("chatInfo", response);
                    startActivity(intent);
                }
        ));
    }

    @Override
    protected void onDestroy() {
        disposable.dispose();
        super.onDestroy();
    }

    private String convertDateHour(String data_value) {
        if (data_value != null && !data_value.isEmpty()) {
            SimpleDateFormat sd1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            Date dt = null;
            try {
                dt = sd1.parse(data_value);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            SimpleDateFormat sd2 = new SimpleDateFormat("dd/MM/yy");

            return sd2.format(dt);
        }
        return "";
    }

    public static void hideSoftKeyboard(Activity activity, View view) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
    }
}
