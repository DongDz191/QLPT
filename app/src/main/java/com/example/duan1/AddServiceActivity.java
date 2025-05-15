package com.example.duan1;

import androidx.appcompat.app.AppCompatActivity;

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
import com.example.duan1.model.Service;

import gun0912.tedimagepicker.builder.TedImagePicker;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddServiceActivity extends AppCompatActivity {
    ImageView imageViewBack;
    EditText edtDv, edtPrice;
    String strimage;
    Button btnXn, btnExit;
    ImageView imageViewAdd;
    private RequestManager requestManager;
    ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_service);

        apiService = ApiClient.getApiService();
        requestManager = Glide.with(this);
        imageViewBack = findViewById(R.id.imgBack);
        imageViewBack.setOnClickListener(v -> {
            Intent intent = new Intent(AddServiceActivity.this, ServiceManagerActivity.class);
            startActivity(intent);
        });

        btnXn = findViewById(R.id.btnXn);
        edtDv = findViewById(R.id.edtAddv);
        edtPrice = findViewById(R.id.edtPricedv);
        imageViewAdd = findViewById(R.id.setImage);
        btnExit = findViewById(R.id.btnExit);
        btnExit.setOnClickListener(v -> finish());

        btnXn.setOnClickListener(v -> addService());
        imageViewAdd.setOnClickListener(v -> addImage());
    }

    public void addService() {
        if (valiDate() > 0) {
            String strService = edtDv.getText().toString().trim();
            int strPrice = Integer.parseInt(edtPrice.getText().toString().trim());
            String strImage = strimage != null ? strimage : "";

            Service service = new Service(strService, strPrice, strImage);
            apiService.insertService(service).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(AddServiceActivity.this, "Thêm dịch vụ thành công", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(AddServiceActivity.this, "Lỗi khi thêm dịch vụ: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(AddServiceActivity.this, "Lỗi kết nối API: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public void addImage() {
        TedImagePicker.with(AddServiceActivity.this).start(uri -> {
            requestManager.load(uri).into(imageViewAdd);
            strimage = uri.toString();
        });
    }

    private int valiDate() {
        int check = 1;
        if (edtDv.getText().length() == 0 || edtPrice.getText().length() == 0) {
            Toast.makeText(AddServiceActivity.this, "Không được để trống", Toast.LENGTH_SHORT).show();
            check = -1;
        }
        return check;
    }
}