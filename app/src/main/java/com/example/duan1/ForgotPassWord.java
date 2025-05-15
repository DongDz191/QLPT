package com.example.duan1;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.duan1.api.ApiClient;
import com.example.duan1.api.ApiService;
import com.example.duan1.model.Users;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPassWord extends AppCompatActivity {
    TextView username;
    EditText pass, repass;
    Button confom;
    private Users mUsers;
    ApiService apiService;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_pass_word);

        username = findViewById(R.id.password_reset_text);
        pass = findViewById(R.id.password_reset);
        repass = findViewById(R.id.respassword);
        confom = findViewById(R.id.btn_confom);
        apiService = ApiClient.getApiService();

        Intent intent = getIntent();
        // Giả định rằng Intent truyền username từ activity trước
        String userName = intent.getStringExtra("username");
        if (userName != null) {
            username.setText(userName);
        } else {
            Toast.makeText(this, "Không tìm thấy tên người dùng!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        confom.setOnClickListener(v -> {
            String password = pass.getText().toString();
            String respassword = repass.getText().toString();
            if (password.equals(respassword)) {
                // Gọi API để cập nhật mật khẩu
                apiService.updatePassword(userName, password).enqueue(new Callback<Boolean>() {
                    @Override
                    public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                        if (response.isSuccessful() && response.body() != null && response.body()) {
                            Intent intent = new Intent(getApplicationContext(), RegistrationActivity.class);
                            Toast.makeText(ForgotPassWord.this, "Password updated", Toast.LENGTH_SHORT).show();
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(ForgotPassWord.this, "Password not updated: " + response.code(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Boolean> call, Throwable t) {
                        Toast.makeText(ForgotPassWord.this, "Lỗi kết nối API: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(ForgotPassWord.this, "Password not matching", Toast.LENGTH_SHORT).show();
            }
        });
    }
}