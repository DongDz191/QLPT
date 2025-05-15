package com.example.duan1.Interface;


import com.example.duan1.model.Contract;

public interface IClickItemContract {
    void onClickItem(Contract contract, int i);
    void onClickExtend(Contract contract, int i);
    void onClickCancel(Contract contract, int i);
}
