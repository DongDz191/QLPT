package com.example.duan1.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class TotalViewModel extends ViewModel {
    private final MutableLiveData<Integer> total = new MutableLiveData<>();
    public void setTotal(Integer total){
        this.total.setValue(total);
    }
    public LiveData getTotal(){
        return this.total;
    }
}
