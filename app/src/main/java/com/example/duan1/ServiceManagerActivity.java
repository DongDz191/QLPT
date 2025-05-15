package com.example.duan1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.duan1.adapter.ServiceAdapter;
import com.example.duan1.api.ApiClient;
import com.example.duan1.api.ApiService;
import com.example.duan1.databinding.ActivityServiceManagerBinding;
import com.example.duan1.model.Service;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ServiceManagerActivity extends AppCompatActivity {
    ActivityServiceManagerBinding binding;
    List<Service> serviceList;
    ServiceAdapter serviceAdapter;
    ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityServiceManagerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        apiService = ApiClient.getApiService();
        serviceList = new ArrayList<>();
        loaddata();

        binding.imgBack.setOnClickListener(view -> finish());
        binding.floatAddService.setOnClickListener(v -> {
            Intent intent = new Intent(ServiceManagerActivity.this, AddServiceActivity.class);
            startActivity(intent);
        });

        binding.rvService.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    binding.floatAddService.hide();
                } else {
                    binding.floatAddService.show();
                }
                super.onScrolled(recyclerView, dx, dy);
            }
        });
    }

    private void loaddata() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        binding.rvService.setLayoutManager(linearLayoutManager);
        serviceAdapter = new ServiceAdapter(this, serviceList);
        binding.rvService.setAdapter(serviceAdapter);

        // Gọi API để lấy danh sách Service
        apiService.getAllServices().enqueue(new Callback<List<Service>>() {
            @Override
            public void onResponse(Call<List<Service>> call, Response<List<Service>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    serviceList.clear();
                    serviceList.addAll(response.body());
                    serviceAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(ServiceManagerActivity.this, "Lỗi khi lấy danh sách dịch vụ: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Service>> call, Throwable t) {
                Toast.makeText(ServiceManagerActivity.this, "Lỗi kết nối API: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loaddata(); // Tải lại dữ liệu từ API
    }
}