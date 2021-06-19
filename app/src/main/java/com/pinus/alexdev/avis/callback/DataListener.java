package com.pinus.alexdev.avis.callback;

import com.pinus.alexdev.avis.model.ChartItemModel;

public interface DataListener {
    void sendDataToActivity(ChartItemModel data, long id, int branchOrChart);
}