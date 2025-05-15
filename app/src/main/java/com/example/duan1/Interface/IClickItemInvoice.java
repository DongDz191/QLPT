package com.example.duan1.Interface;


import com.example.duan1.model.Invoice;

public interface IClickItemInvoice {
    void onClickItem(Invoice invoice, int position);
    void onClickPay(Invoice invoice, int position);
}
