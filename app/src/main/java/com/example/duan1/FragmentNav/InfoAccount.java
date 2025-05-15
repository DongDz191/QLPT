package com.example.duan1.FragmentNav;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.duan1.R;
import com.example.duan1.databinding.FragmentHomeFragmnetBinding;
import com.example.duan1.databinding.FragmentInfoAccountBinding;
import com.example.duan1.model.Account;
import com.example.duan1.model.SessionAccount;



public class InfoAccount extends Fragment {
    FragmentInfoAccountBinding binding;
    Account account;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentInfoAccountBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SessionAccount sessionManage = new SessionAccount(getContext());
        account = sessionManage.fetchAccount();
        binding.imgAvatar.setImageURI(Uri.parse(account.getImage()));
        binding.tvPoss.setText(account.getTitle());
        binding.tvName.setText(account.getName());
        binding.tvUser.setText(account.getUsername());
        binding.tvPass.setText(account.getPassword());
        binding.tvPhone.setText(account.getPhone());

    }
}