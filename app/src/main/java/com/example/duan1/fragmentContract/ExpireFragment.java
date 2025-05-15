package com.example.duan1.fragmentContract;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.duan1.Interface.IClickItemContract;
import com.example.duan1.ShowMemberContract;
import com.example.duan1.adapter.Contract2Adapter;
import com.example.duan1.api.ApiClient;
import com.example.duan1.api.ApiService;
import com.example.duan1.databinding.FragmentExpireBinding;
import com.example.duan1.model.Contract;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ExpireFragment extends Fragment {
    FragmentExpireBinding binding;
    ApiService apiService;
    Contract2Adapter adapter;
    List<Contract> list = new ArrayList<>();
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    String date = format.format(new Date());

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentExpireBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        apiService = ApiClient.getApiService();

        adapter = new Contract2Adapter(list, new IClickItemContract() {
            @Override
            public void onClickItem(Contract contract, int position) {
                Intent intent = new Intent(getActivity(), ShowMemberContract.class);
                intent.putExtra("contract", contract);
                startActivity(intent);
            }

            @Override
            public void onClickExtend(Contract contract, int position) {}

            @Override
            public void onClickCancel(Contract contract, int position) {}
        });

        binding.rv.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rv.setAdapter(adapter);
        loadRecycleview();

        binding.edSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                loadRecycleview();
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        binding.edDate.setOnClickListener(view1 -> {
            new DatePickerDialog(getContext(), (datePicker, i, i1, i2) -> {
                Calendar calendar = Calendar.getInstance();
                calendar.set(i, i1, i2);
                binding.edDate.setText(format.format(calendar.getTime()));
                loadRecycleview(); // Reload data when date changes
            }, 2022, 10, 11).show();
        });
    }

    private void loadRecycleview() {
        String filterDate = binding.edDate.getText().toString();
        String keyword = binding.edSearch.getText().toString();

        Call<List<Contract>> call;
        if (filterDate.isEmpty()) {
            call = apiService.getSearchExpire("%" + keyword + "%");
        } else {
            call = apiService.getSearchFilterExpire(filterDate, "%" + keyword + "%");
        }

        call.enqueue(new Callback<List<Contract>>() {
            @Override
            public void onResponse(Call<List<Contract>> call, Response<List<Contract>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    list.clear();
                    list.addAll(response.body());
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getContext(), "Lỗi khi lấy danh sách hợp đồng: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Contract>> call, Throwable t) {
                Toast.makeText(getContext(), "Lỗi kết nối API: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}