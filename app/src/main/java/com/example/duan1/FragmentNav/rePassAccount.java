package com.example.duan1.FragmentNav;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.duan1.api.ApiClient;
import com.example.duan1.api.ApiService;
import com.example.duan1.databinding.FragmentRePassAccountBinding;
import com.example.duan1.model.Account;
import com.example.duan1.model.SessionAccount;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class rePassAccount extends Fragment {
    FragmentRePassAccountBinding binding;
    Account account;
    ApiService apiService;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentRePassAccountBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        apiService = ApiClient.getApiService();
        SessionAccount sessionManage = new SessionAccount(getContext());
        account = sessionManage.fetchAccount();

        binding.btnXN.setOnClickListener(v -> {
            if (validate() > 0) {
                if (check()) {
                    Toast.makeText(getContext(), "Đổi mật khẩu thành công", Toast.LENGTH_SHORT).show();
                    binding.edtReNewPass.setText("");
                    binding.edtOldPass.setText("");
                    binding.edtNewPass.setText("");
                } else {
                    Toast.makeText(getContext(), "Lỗi", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public int validate() {
        int check = 1;
        if (binding.edtNewPass.getText().length() == 0 || binding.edtOldPass.getText().length() == 0 || binding.edtReNewPass.getText().length() == 0) {
            Toast.makeText(getContext(), "Không được để trống", Toast.LENGTH_SHORT).show();
            check = -1;
        } else if (!binding.edtOldPass.getText().toString().equals(account.getPassword())) {
            Toast.makeText(getContext(), "Sai mật khẩu", Toast.LENGTH_SHORT).show();
            check = -1;
        } else {
            if (!binding.edtNewPass.getText().toString().equals(binding.edtReNewPass.getText().toString())) {
                Toast.makeText(getContext(), "Mật khẩu lặp lại không đúng", Toast.LENGTH_SHORT).show();
                check = -1;
            }
        }
        return check;
    }

    public boolean check() {
        String newPassword = binding.edtNewPass.getText().toString();
        apiService.updatePassword(account.getUsername(), newPassword).enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (response.isSuccessful() && response.body() != null && response.body()) {
                    account.setPassword(newPassword);
                    new SessionAccount(getContext()).saveAccount(account); // Update session with new password
                } else {
                    Toast.makeText(getContext(), "Lỗi khi cập nhật mật khẩu: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                Toast.makeText(getContext(), "Lỗi kết nối API: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        return true;
    }
}