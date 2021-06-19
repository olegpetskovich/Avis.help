package com.pinus.alexdev.avis.callback;

import com.pinus.alexdev.avis.model.BranchModel;

import java.util.ArrayList;

public interface IBranchFilterListener {
    //С новой API фильтрацию бранчей нужно будет реализовать с помощью ID бранча, который будет храниться в данных каждого респонса ревью, а не с помощью имени бранча, как это сделано сейчас и это баговый вариант
    void sendDataToActivity(ArrayList<BranchModel> branchesName);
}
