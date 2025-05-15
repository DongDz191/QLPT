package com.example.duan1.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.duan1.Interface.IClickItemContract;
import com.example.duan1.databinding.ItemContractBinding;
import com.example.duan1.model.Contract;

import java.util.List;

public class ContractAdapter extends RecyclerView.Adapter<ContractAdapter.ViewHoler> {
    List<Contract> list;
    IClickItemContract listener;
    public ContractAdapter(List<Contract> list, IClickItemContract listener) {
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHoler onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHoler(ItemContractBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHoler holder, int position) {
        Contract contract =  list.get(position);
        holder.binding.setContract(contract);
        holder.itemView.setOnClickListener(view -> listener.onClickItem(contract,position));
        holder.binding.btnExtension.setOnClickListener(view -> listener.onClickExtend(contract,position));
        holder.binding.btnCancel.setOnClickListener(view -> listener.onClickCancel(contract,position));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHoler extends RecyclerView.ViewHolder{
      ItemContractBinding binding;
      public ViewHoler(@NonNull ItemContractBinding binding) {
          super(binding.getRoot());
          this.binding = binding;
      }
  }
}
