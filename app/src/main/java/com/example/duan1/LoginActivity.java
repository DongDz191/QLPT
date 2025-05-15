package com.example.duan1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.duan1.api.ApiClient;
import com.example.duan1.api.ApiService;
import com.example.duan1.databinding.ActivityLoginBinding;
import com.example.duan1.model.Account;
import com.example.duan1.model.SessionAccount;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    ActivityLoginBinding binding;
    ApiService apiService;
    SessionAccount sessionManage;

    private static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        apiService = ApiClient.getApiService();

        sessionManage = new SessionAccount(this);
        Account account2 = sessionManage.fetchAccount();
        binding.edUserName.setText(account2.getUsername());
        binding.edPassword.setText(account2.getPassword());

        binding.btnLogin.setOnClickListener(v -> {
            String username = binding.edUserName.getText().toString().trim();
            String password = binding.edPassword.getText().toString().trim();

            if (checkValidate(username, password)) {
                apiService.checkLogin(username, password).enqueue(new Callback<Account>() {
                    @Override
                    public void onResponse(Call<Account> call, Response<Account> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            Account account = response.body();
                            sessionManage.saveAccount(account);
                            Log.d(TAG, "Đăng nhập thành công");
                            Intent intent = new Intent(LoginActivity.this, MenuMainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Log.e(TAG, "Thông tin tài khoản hoặc mật khẩu không chính xác");
                        }
                    }

                    @Override
                    public void onFailure(Call<Account> call, Throwable t) {
                        Log.e(TAG, "Lỗi kết nối API: " + t.getMessage());
                    }
                });
            } else {
                Log.e(TAG, "Vui lòng không để trống!");
            }
        });

//        binding.tvQuenMatKhau.setOnClickListener(v -> {
//            Intent intent = new Intent(getApplicationContext(), ForgotPasswordActivity.class);
//            startActivity(intent);
//        });
    }

    private boolean checkValidate(String username, String password) {
        return !(username.isEmpty() || password.isEmpty());
    }
}