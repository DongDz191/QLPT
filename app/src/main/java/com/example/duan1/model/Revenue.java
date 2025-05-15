package com.example.duan1.model;

public class Revenue {

    private int idContract;
    private int paidAmount;

    private int unpaidAmount;

    public int getIdContract() {
        return idContract;
    }

    public void setIdContract(int idContract) {
        this.idContract = idContract;
    }

    public int getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(int paidAmount) {
        this.paidAmount = paidAmount;
    }

    public int getUnpaidAmount() {
        return unpaidAmount;
    }

    public void setUnpaidAmount(int unpaidAmount) {
        this.unpaidAmount = unpaidAmount;
    }
}
