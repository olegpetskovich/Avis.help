package com.pinus.alexdev.avis.model;

import java.util.Objects;

public class BranchModel {
    private String branchName;
    private boolean isClicked;
    private int itemPosition;
    private long branchID;

    //Метод equals и hashCode переопределены, чтобы метод remove класса ArrayList удалял объект, сранивая не по адресу, а по данным (В нашем случае по branchId)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BranchModel)) return false;
        BranchModel model = (BranchModel) o;
        return branchID == model.branchID;
    }

    @Override
    public int hashCode() {
        return Objects.hash(branchID);
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public boolean isClicked() {
        return isClicked;
    }

    public void setClicked(boolean clicked) {
        isClicked = clicked;
    }

    public int getItemPosition() {
        return itemPosition;
    }

    public void setItemPosition(int itemPosition) {
        this.itemPosition = itemPosition;
    }

    public long getBranchID() {
        return branchID;
    }

    public void setBranchID(long branchID) {
        this.branchID = branchID;
    }
}
