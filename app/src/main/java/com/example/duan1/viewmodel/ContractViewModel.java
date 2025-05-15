package com.example.duan1.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.duan1.model.Contract;

public class ContractViewModel extends ViewModel {
    private final MutableLiveData<Contract> contract = new MutableLiveData<>();
    public void setContract(Contract contract){
        this.contract.setValue(contract);
    }
    public LiveData getContract(){
        return this.contract;
    }
}
