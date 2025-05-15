package com.example.duan1;

import static com.example.duan1.base.BaseCheckValid.checkEmptyString;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.example.duan1.api.ApiClient;
import com.example.duan1.api.ApiService;
import com.example.duan1.model.Account;
import com.example.duan1.model.SessionAccount;

import gun0912.tedimagepicker.builder.TedImagePicker;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegistrationActivity extends AppCompatActivity {
    EditText edntk, edpass, edsdt, edname;
    Button btndk;
    ImageView imganh;
    String taikhoan, ten, matkhau, sdt;
    String pathImage = "";
    ApiService apiService;
    RequestManager requestManager;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        apiService = ApiClient.getApiService();
        edntk = findViewById(R.id.dktk);
        edpass = findViewById(R.id.dkmk);
        edname = findViewById(R.id.dkname);
        edsdt = findViewById(R.id.dksdt);
        btndk = findViewById(R.id.btn_dkk);
        requestManager = Glide.with(this);
        imganh = findViewById(R.id.img_anh);

        btndk.setOnClickListener(v -> {
            Account account = new Account();
            account.setUsername(edntk.getText().toString());
            account.setName(edname.getText().toString());
            account.setPassword(edpass.getText().toString());
            account.setPhone(edsdt.getText().toString());
            account.setImage(pathImage);

            ten = edname.getText().toString().trim();
            matkhau = edpass.getText().toString().trim();
            sdt = edsdt.getText().toString().trim();
            taikhoan = edntk.getText().toString().trim();

            if (!checkEmptyString(ten, matkhau, sdt, taikhoan, pathImage)) {
                Toast.makeText(RegistrationActivity.this, "Không được bỏ trống", Toast.LENGTH_SHORT).show();
            } else {
                // Gọi API để đăng ký tài khoản
                apiService.insertAccount(account).enqueue(new Callback<Long>() {
                    @Override
                    public void onResponse(Call<Long> call, Response<Long> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            Toast.makeText(RegistrationActivity.this, "Đăng ký thành công", Toast.LENGTH_SHORT).show();
                            SessionAccount sessionManage = new SessionAccount(RegistrationActivity.this);
                            sessionManage.saveAccount(account);
                            startActivity(new Intent(RegistrationActivity.this, LoginActivity.class));
                            finish();
                        } else {
                            Toast.makeText(RegistrationActivity.this, "Tên tài khoản đã tồn tại: " + response.code(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Long> call, Throwable t) {
                        Toast.makeText(RegistrationActivity.this, "Lỗi kết nối API: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        imganh.setOnClickListener(v -> {
            TedImagePicker.with(RegistrationActivity.this).start(uri -> {
                requestManager.load(uri).into(imganh);
                pathImage = uri.toString();
            });
        });
    }
}