package com.pinus.alexdev.avis.view;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dagang.library.GradientButton;
import com.google.gson.Gson;
import com.muddzdev.styleabletoast.StyleableToast;
import com.pinus.alexdev.avis.R;
import com.pinus.alexdev.avis.adapter.BranchFilterRVAdapter;
import com.pinus.alexdev.avis.callback.IBranchFilterListener;
import com.pinus.alexdev.avis.dto.response.BranchesResponse;
import com.pinus.alexdev.avis.model.BranchModel;
import com.pinus.alexdev.avis.network.ApiClient;
import com.pinus.alexdev.avis.network.apiServices.BranchApiService;
import com.pinus.alexdev.avis.utils.SaveLoadData;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

import static com.pinus.alexdev.avis.view.LoginActivity.ORGANIZATION_ID_KEY;

public class BranchFilterActivity extends BaseToolbarBack implements IBranchFilterListener {
    private static final String TAG = BranchFilterActivity.class.getSimpleName();

    public static String BRANCH_FILTER_KEY; // - Значение этой переменной инициализируется перед запуском этого активити в том активити, которое его вызывает

    private CompositeDisposable disposable = new CompositeDisposable();

    @BindView(R.id.appbar_back)
    View appbar;

    @BindView(R.id.scanQrButton)
    ImageButton qrScanButton;

    @BindView(R.id.backLayout)
    LinearLayout backTo;

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    @BindView(R.id.btnSelect)
    GradientButton btnSelect;

    BranchApiService branchApiService;

    BranchFilterRVAdapter rvAdapter;

    ArrayList<BranchModel> branchesID;

    //Класс для удобной работы с SharedPreferences
    SaveLoadData saveLoadData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_branch_filter);
        ButterKnife.bind(this);
        startScan(qrScanButton);
        setButtonBackClick(
                appbar.findViewById(R.id.appBarTitle),
                getString(R.string.btnFilter),
                appbar.findViewById(R.id.logoImageBack),
                backTo,
                appbar.findViewById(R.id.toolbarPreviosActivityTitle),
                "",
                null
        );

        saveLoadData = new SaveLoadData(this);
        saveLoadData.saveString(BRANCH_FILTER_KEY, ""); //Очищаем список для следующего использования

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        branchApiService = ApiClient.getClient(getApplicationContext()).create(BranchApiService.class);

        disposable.add(branchApiService.getBranchList(saveLoadData.loadInt(ORGANIZATION_ID_KEY))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                            for (BranchesResponse resp : response) {
                                resp.setClicked(false);
                            }

                            BranchesResponse allBranchesModel = new BranchesResponse();
                            allBranchesModel.setClicked(false);
                            allBranchesModel.setName(getString(R.string.all_branches));
                            response.add(0, allBranchesModel);

                            rvAdapter = new BranchFilterRVAdapter(this, response);
                            rvAdapter.setBranchFilterListener(this);
                            recyclerView.setAdapter(rvAdapter);
                        },
                        e -> Log.e(TAG, "onError: " + e.getMessage())
                ));

        btnSelect.getButton().setOnClickListener(v -> {
            if (branchesID.size() != 0) {
                String jsonListBranchesID = new Gson().toJson(branchesID);
                saveLoadData.saveString(BRANCH_FILTER_KEY, jsonListBranchesID);
                finish();
            } else
                StyleableToast.makeText(getApplicationContext(), getString(R.string.selectPoint), Toast.LENGTH_LONG, R.style.mytoast).show();
        });
    }

    @Override
    public void sendDataToActivity(ArrayList<BranchModel> branchesID) {
        this.branchesID = branchesID;
    }
}
