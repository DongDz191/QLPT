package com.example.duan1.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(
foreignKeys = {@ForeignKey(entity = Invoice.class, childColumns = "idInvoice", parentColumns = "idInvoice", onDelete = ForeignKey.CASCADE),
@ForeignKey(entity = Service.class,childColumns = "idService", parentColumns = "idService", onDelete = ForeignKey.CASCADE)})
public class ServiceDetail {
    @PrimaryKey(autoGenerate = true)
    private int idServiceDetail;
    @NonNull
    private int idInvoice;
    @NonNull
    private int idService;
    private String date;
    private int number;

    public ServiceDetail() {
    }

    public ServiceDetail(int idInvoice, int idService, String date, int number) {
        this.idInvoice = idInvoice;
        this.idService = idService;
        this.date = date;
        this.number = number;
    }

    public int getIdServiceDetail() {
        return idServiceDetail;
    }

    public void setIdServiceDetail(int idServiceDetail) {
        this.idServiceDetail = idServiceDetail;
    }

    public int getIdInvoice() {
        return idInvoice;
    }

    public void setIdInvoice(int idInvoice) {
        this.idInvoice = idInvoice;
    }

    public int getIdService() {
        return idService;
    }

    public void setIdService(int idService) {
        this.idService = idService;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }
}
