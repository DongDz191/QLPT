package com.example.duan1.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.duan1.Interface.IClickItemService;
import com.example.duan1.databinding.ItemAddServiceBinding;
import com.example.duan1.model.Service;

import java.util.List;

public class ServiceRoomAdapter extends RecyclerView.Adapter<ServiceRoomAdapter.MyViewHolder>{
    Context context;
    List<Service> list;
    IClickItemService onClickItemService;

    public ServiceRoomAdapter(Context context, List<Service> list, IClickItemService listener) {
        this.context = context;
        this.list = list;
        this.onClickItemService = listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(ItemAddServiceBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Service service = list.get(position);
        holder.binding.tvName.setText(service.getName());
        holder.binding.tvPrice.setText(String.format("%,d", service.getPrice()));
        holder.binding.btnAdd.setOnClickListener(view -> onClickItemService.onClickAdd(service,position));
        Glide.with(context).load(Uri.parse(service.getImage())).into(holder.binding.img);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        ItemAddServiceBinding binding ;
        public MyViewHolder(@NonNull ItemAddServiceBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
