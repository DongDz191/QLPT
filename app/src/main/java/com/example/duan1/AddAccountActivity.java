package com.example.duan1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import gun0912.tedimagepicker.builder.TedImagePicker;

public class AddAccountActivity extends AppCompatActivity {
    EditText edtUser, edtPass, edtName, edtPhone;
    Button btnXN, btnExit;
    String strimage;
    public RequestManager requestManager;
    ApiService apiService;
    ImageView btnBack;
    ImageView editImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestManager = Glide.with(this);
        setContentView(R.layout.activity_add_account);

        apiService = ApiClient.getApiService();

        btnBack = findViewById(R.id.imgBack);
        btnExit = findViewById(R.id.btnExit);
        btnXN = findViewById(R.id.btnXn);
        edtUser = findViewById(R.id.edtuser);
        edtPass = findViewById(R.id.edtPass);
        editImage = findViewById(R.id.editImage);
        edtName = findViewById(R.id.edtName);
        edtPhone = findViewById(R.id.edtPhone);

        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(AddAccountActivity.this, AccountManagerActivity.class);
            startActivity(intent);
        });

        btnExit.setOnClickListener(v -> finish());

        btnXN.setOnClickListener(v -> addAccount());

        editImage.setOnClickListener(v -> addImage());
    }

    private void addImage() {
        TedImagePicker.with(AddAccountActivity.this).start(uri -> {
            requestManager.load(uri).into(editImage);
            Log.i("TAG", "Uri: " + uri.toString());
            strimage = uri.toString();
        });
    }

    private void addAccount() {
        if (valiDate() > 0) {
            String user = edtUser.getText().toString().trim();
            String pass = edtPass.getText().toString().trim();
            String phone = edtPhone.getText().toString().trim();
            String name = edtName.getText().toString().trim();
            String image = strimage;

            Account account = new Account(user, pass, name, phone, "manager", image);
            apiService.insertAccount(account).enqueue(new Callback<Long>() {
                @Override
                public void onResponse(Call<Long> call, Response<Long> response) {
                    if (response.isSuccessful() && response.body() != null && response.body() > 0) {
                        Toast.makeText(AddAccountActivity.this, "Thêm tài khoản thành công", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(AddAccountActivity.this, "Thêm tài khoản thất bại: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Long> call, Throwable t) {
                    Toast.makeText(AddAccountActivity.this, "Lỗi kết nối API: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private int valiDate() {
        int check = 1;
        if (edtUser.getText().length() == 0 || edtPass.getText().length() == 0 || edtPhone.getText().length() == 0 || edtName.getText().length() == 0) {
            Toast.makeText(AddAccountActivity.this, "Không được để trống", Toast.LENGTH_SHORT).show();
            check = -1;
        }
        return check;
    }
}