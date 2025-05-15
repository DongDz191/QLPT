package com.example.duan1.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.duan1.model.Account;

public class AccountViewModel extends ViewModel {
    private final MutableLiveData<Account> account = new MutableLiveData<>();
    public void setUser(Account account){
        this.account.setValue(account);
    }
    public LiveData getRoom(){
        return this.account;
    }
}