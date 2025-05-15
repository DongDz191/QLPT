package com.example.duan1.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.duan1.model.Invoice;

public class InvoiceViewModel extends ViewModel {
    private final MutableLiveData<Invoice> invoice = new MutableLiveData<>();
    public void setInvoice(Invoice invoice){
        this.invoice.setValue(invoice);
    }
    public LiveData getInvoice(){
        return this.invoice;
    }
}
