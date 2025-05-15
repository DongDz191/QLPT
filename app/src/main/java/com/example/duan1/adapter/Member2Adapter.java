package com.example.duan1.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.duan1.databinding.ItemMemberContractBinding;
import com.example.duan1.model.Member;

import java.util.List;

public class Member2Adapter extends RecyclerView.Adapter<Member2Adapter.MyViewHolder>{
    Context context;
    List<Member> list;

    public Member2Adapter(Context context, List<Member> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(ItemMemberContractBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.binding.imgDelete.setVisibility(View.INVISIBLE);
        holder.binding.imgEdit.setVisibility(View.INVISIBLE);
        Member member = list.get(position);
        holder.binding.setMember(member);
        holder.binding.executePendingBindings();
        Glide.with(context).load(Uri.parse(member.getImage())).into(holder.binding.imgAvatar);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
    class MyViewHolder extends RecyclerView.ViewHolder{
        ItemMemberContractBinding binding;
        public MyViewHolder( ItemMemberContractBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
