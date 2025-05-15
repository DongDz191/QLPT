package com.example.duan1.FragmentNav;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.example.duan1.AccountManagerActivity;
import com.example.duan1.ContractManageActivity;
import com.example.duan1.RevenueActivity;
import com.example.duan1.RoomManageActivity;
import com.example.duan1.ServiceManagerActivity;
import com.example.duan1.databinding.FragmentHomeFragmnetBinding;
import com.example.duan1.model.Account;
import com.example.duan1.model.SessionAccount;

public class HomeFragmnet extends Fragment {
    FragmentHomeFragmnetBinding binding;
    SessionAccount sessionAccount;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        this.binding = FragmentHomeFragmnetBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.contractManage.setOnClickListener(v -> startActivity(new Intent(view.getContext(), ContractManageActivity.class)));
        binding.roomManage.setOnClickListener(v -> startActivity(new Intent(view.getContext(), RoomManageActivity.class)));
        binding.seviceManage.setOnClickListener(v -> startActivity(new Intent(view.getContext(), ServiceManagerActivity.class)));
        binding.createAccount.setOnClickListener(v -> startActivity(new Intent(view.getContext(), AccountManagerActivity.class)));
        binding.quanLyDoanhThu.setOnClickListener(v -> startActivity(new Intent(view.getContext(), RevenueActivity.class)));
        sessionAccount = new SessionAccount(getContext());
        Account account = sessionAccount.fetchAccount();
//        binding.tvName.setText(account.getName().trim());
//        binding.tvPos.setText(account.getTitle().trim());
//        Glide.with(getContext()).load(Uri.parse(account.getImage())).into(binding.ImageAvatar);
      
    }
}