package com.example.duan1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.duan1.adapter.AccountAdapter;
import com.example.duan1.api.ApiClient;
import com.example.duan1.api.ApiService;
import com.example.duan1.databinding.ActivityAccountManagerBinding;
import com.example.duan1.model.Account;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AccountManagerActivity extends AppCompatActivity {
    ActivityAccountManagerBinding binding;
    List<Account> accountList;
    AccountAdapter accountAdapter;
    ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_manager);
        binding = ActivityAccountManagerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Khởi tạo ApiService
        apiService = ApiClient.getApiService();

        binding.imgBack.setOnClickListener(view -> finish());

        // Khởi tạo accountList và accountAdapter
        accountList = new ArrayList<>();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        binding.rvAccount.setLayoutManager(linearLayoutManager);
        accountAdapter = new AccountAdapter(this, accountList);
        binding.rvAccount.setAdapter(accountAdapter);

        // Load dữ liệu từ API
        loadDataFromApi();

        binding.floatAddAccount.setOnClickListener(v -> {
            Intent intent = new Intent(AccountManagerActivity.this, AddAccountActivity.class);
            startActivity(intent);
        });

        binding.rvAccount.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    binding.floatAddAccount.hide();
                } else {
                    binding.floatAddAccount.show();
                }
                super.onScrolled(recyclerView, dx, dy);
            }
        });
    }

    private void loadDataFromApi() {
        apiService.getAllAccounts().enqueue(new Callback<List<Account>>() {
            @Override
            public void onResponse(Call<List<Account>> call, Response<List<Account>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    accountList.clear();
                    accountList.addAll(response.body());
                    accountAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(AccountManagerActivity.this, "Lỗi tải danh sách tài khoản: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Account>> call, Throwable t) {
                Toast.makeText(AccountManagerActivity.this, "Lỗi kết nối API: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadDataFromApi();
    }
}