package com.pinus.alexdev.avis.view;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.dagang.library.GradientButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.muddzdev.styleabletoast.StyleableToast;
import com.pinus.alexdev.avis.R;
import com.pinus.alexdev.avis.adapter.RVBranchAdapter;
import com.pinus.alexdev.avis.adapter.RVChartAdapter;
import com.pinus.alexdev.avis.callback.DataListener;
import com.pinus.alexdev.avis.dto.response.BranchesItem;
import com.pinus.alexdev.avis.dto.response.BranchesResponse;
import com.pinus.alexdev.avis.dto.response.qr_manager_list_response.QrManagerListResponse;
import com.pinus.alexdev.avis.dto.response.qr_manager_list_response.opinion_response.OpinionResponse;
import com.pinus.alexdev.avis.enums.TypesEnum;
import com.pinus.alexdev.avis.model.ChartItemModel;
import com.pinus.alexdev.avis.network.ApiClient;
import com.pinus.alexdev.avis.network.apiServices.BranchApiService;
import com.pinus.alexdev.avis.network.apiServices.QrManagerApiService;
import com.pinus.alexdev.avis.utils.SaveLoadData;
import com.pinus.alexdev.avis.view.home_activity.HomeActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import lecho.lib.hellocharts.view.Chart;

import static com.pinus.alexdev.avis.enums.PromoTypes.OVERALL;
import static com.pinus.alexdev.avis.view.LoginActivity.ORGANIZATION_ID_KEY;
import static com.pinus.alexdev.avis.view.LoginActivity.USER_ID_KEY;

public class BranchChartListActivity extends BaseToolbarBack implements DataListener {
    private static final String TAG = BranchChartListActivity.class.getSimpleName();

    public static final int BRANCH_LIST = 0;
    public static final int BRANCH = 0;

    public static final int CHART_LIST = 1;
    public static final int CHART = 1;

    private int branchOrChart;

    @BindView(R.id.btnSelect)
    GradientButton btnSelect;

    @BindView(R.id.scanQrButton)
    ImageButton qrScanButton;

    @BindView(R.id.backLayout)
    LinearLayout btnBack;

    @BindView(R.id.logoImageBack)
    ImageView logoImageBack;

    @BindView(R.id.appBarTitle)
    TextView appBarTitle;

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    private BranchApiService branchApiService;

    ChartItemModel data;
    long id;

    //Класс для удобной работы с SharedPreferences
    SaveLoadData saveLoadData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_branch_list);
        ButterKnife.bind(this);
        startScan(qrScanButton);

        branchApiService = ApiClient.getClient(getApplicationContext()).create(BranchApiService.class);
        saveLoadData = new SaveLoadData(this);

        btnBack.setOnClickListener(view -> finish());
        logoImageBack.setOnClickListener(view -> finish());

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        if (getIntent().getIntExtra("list_type", -1) == BRANCH_LIST) {
            appBarTitle.setText(getResources().getString(R.string.selectYourBranchTitle));

            disposable.add(branchApiService.getBranchList(saveLoadData.loadInt(ORGANIZATION_ID_KEY))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(response -> {
                                progressBar.setVisibility(View.INVISIBLE);
                                BranchesResponse branch = new BranchesResponse();
                                branch.setName(getString(TypesEnum.OVERALL.getValue()));
                                response.add(0, branch);

                                RVBranchAdapter rVAdapter = new RVBranchAdapter(getApplicationContext(), response);
                                rVAdapter.setDataListener(BranchChartListActivity.this);
                                recyclerView.setAdapter(rVAdapter);
                            },
                            e -> {
                                progressBar.setVisibility(View.INVISIBLE);
                                Log.e(TAG, "onError: " + e.getMessage());
                            }
                    ));
        } else {
            appBarTitle.setText(getResources().getString(R.string.selectChartType));

            disposable.add(branchApiService.getCategoryList(saveLoadData.loadInt(ORGANIZATION_ID_KEY))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(response ->
                                    ApiClient.getClient(getApplicationContext()).create(QrManagerApiService.class)
                                            .getOrganizationQrList(saveLoadData.loadInt(ORGANIZATION_ID_KEY))
                                            .subscribeOn(Schedulers.io())
                                            .observeOn(AndroidSchedulers.mainThread())
                                            .subscribe(qrManagerListResponse -> {
                                                ArrayList<ChartItemModel> chartItemModels = new ArrayList<>();

                                                ChartItemModel categoriesModel = new ChartItemModel();
                                                categoriesModel.setName(getString(R.string.categories));
                                                categoriesModel.setOpinionId(-1);
                                                chartItemModels.add(categoriesModel);

                                                for (String category : response) {
                                                    if (category == null) continue;

                                                    ChartItemModel model = new ChartItemModel();
                                                    model.setName(category);
                                                    model.setOpinionId(-1);
                                                    chartItemModels.add(model);
                                                }

                                                ChartItemModel opinionModel = new ChartItemModel();
                                                opinionModel.setName(getString(R.string.qrs));
                                                opinionModel.setOpinionId(-1);
                                                chartItemModels.add(opinionModel);

                                                for (OpinionResponse opinionResponse : qrManagerListResponse.getOpinion()) {
                                                    ChartItemModel model = new ChartItemModel();
                                                    model.setName(opinionResponse.getName());
                                                    model.setOpinionId(opinionResponse.getId());
                                                    chartItemModels.add(model);
                                                }

                                                ChartItemModel overallRatingModel = new ChartItemModel();
                                                overallRatingModel.setName(getString(OVERALL.getValue()));
                                                overallRatingModel.setOpinionId(-1);
                                                chartItemModels.add(0, overallRatingModel);

                                                progressBar.setVisibility(View.INVISIBLE);

                                                RVChartAdapter rVAdapter = new RVChartAdapter(this, chartItemModels);
                                                rVAdapter.setDataListener(this);
                                                recyclerView.setAdapter(rVAdapter);
                                            }, e -> {
                                                progressBar.setVisibility(View.INVISIBLE);
                                                Log.e(TAG, "onError: " + e.getMessage());
                                            }),
                            e -> {
                                progressBar.setVisibility(View.INVISIBLE);
                                Log.e(TAG, "onError: " + e.getMessage());
                            }
                    ));
        }

        btnSelect.getButton().setOnClickListener(view -> {
            if (data == null || data.equals("")) {
                StyleableToast.makeText(getApplicationContext(), getString(R.string.selectPoint), Toast.LENGTH_LONG, R.style.mytoast).show();
            } else {
                if (branchOrChart == BRANCH) {
                    saveLoadData.saveString("branch_data_name", data.getName());
                    saveLoadData.saveLong("branch_data_id", id);

//                    //очищаем данные, чтобы не использовались прошлые выбранные данные, которые в данный момент использовать не надо
//                    saveLoadData.saveString("chart_data_name", "");
//                    saveLoadData.saveLong("chart_data_opinion_id", -1);
                } else {
                    saveLoadData.saveString("chart_data_name", data.getName());
                    saveLoadData.saveLong("chart_data_opinion_id", data.getOpinionId());

//                    //очищаем данные, чтобы не использовались прошлые выбранные данные, которые в данный момент использовать не надо
//                    saveLoadData.saveString("branch_data_name", "");
//                    saveLoadData.saveLong("branch_data_id", 0);
                }
                finish();
            }
        });
    }

    @Override
    public void sendDataToActivity(ChartItemModel data, long id, int branchOrChart) {
        this.data = data;
        this.id = id;
        this.branchOrChart = branchOrChart;
    }
}
