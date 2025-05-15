package com.example.duan1.adapter;

import android.view.View;

import androidx.annotation.NonNull;

import com.example.duan1.Interface.IClickItemContract;
import com.example.duan1.model.Contract;

import java.util.List;

public class Contract2Adapter extends ContractAdapter{
    public Contract2Adapter(List<Contract> list, IClickItemContract listener) {
        super(list, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHoler holder, int position) {
        super.onBindViewHolder(holder, position);
        holder.binding.btnCancel.setVisibility(View.INVISIBLE);
        holder.binding.btnExtension.setVisibility(View.INVISIBLE);
    }
}
