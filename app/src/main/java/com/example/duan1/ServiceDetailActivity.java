package com.example.duan1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.Toast;

import com.example.duan1.Interface.IClickItemServiceDetail;
import com.example.duan1.adapter.ServiceDetailAdapter;
import com.example.duan1.api.ApiClient;
import com.example.duan1.api.ApiService;
import com.example.duan1.databinding.ActivityServiceDetailBinding;
import com.example.duan1.model.Invoice;
import com.example.duan1.model.Service;
import com.example.duan1.model.ServiceDetail;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ServiceDetailActivity extends AppCompatActivity {
    ActivityServiceDetailBinding binding;
    Invoice invoice;
    ApiService apiService;
    List<ServiceDetail> listServiceDetail;
    ServiceDetailAdapter detailAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityServiceDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        apiService = ApiClient.getApiService();
        invoice = (Invoice) getIntent().getSerializableExtra("invoice");
        if (invoice == null) {
            Toast.makeText(this, "Không tìm thấy hóa đơn!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        listServiceDetail = new ArrayList<>();
        binding.rvService.setLayoutManager(new LinearLayoutManager(this));

        detailAdapter = new ServiceDetailAdapter(this, listServiceDetail, new IClickItemServiceDetail() {
            @Override
            public void onClickDelete(ServiceDetail serviceDetail, int i) {
                handleItemDelete(serviceDetail, i);
            }
        });
        binding.rvService.setAdapter(detailAdapter);

        // Lấy danh sách ServiceDetail từ API
        loadServiceDetails();

        binding.imgBack.setOnClickListener(view -> finish());
    }

    private void loadServiceDetails() {
        apiService.getServiceDetailByIdInvoice(invoice.getIdInvoice()).enqueue(new Callback<List<ServiceDetail>>() {
            @Override
            public void onResponse(Call<List<ServiceDetail>> call, Response<List<ServiceDetail>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listServiceDetail.clear();
                    listServiceDetail.addAll(response.body());
                    detailAdapter.notifyDataSetChanged();
                    handleTotal();
                } else {
                    Toast.makeText(ServiceDetailActivity.this, "Lỗi khi lấy danh sách chi tiết dịch vụ: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<ServiceDetail>> call, Throwable t) {
                Toast.makeText(ServiceDetailActivity.this, "Lỗi kết nối API: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleItemDelete(ServiceDetail serviceDetail, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomAlertDialog);
        builder.setTitle("Xóa");
        builder.setMessage("Bạn có chắc chắn muốn xóa?");
        builder.setNegativeButton("OK", (dialogInterface, i) -> {
            apiService.deleteServiceDetail(serviceDetail.getIdServiceDetail()).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        listServiceDetail.remove(position);
                        detailAdapter.notifyItemRemoved(position);
                        detailAdapter.notifyItemRangeChanged(position, detailAdapter.getItemCount());
                        handleTotal();
                    } else {
                        Toast.makeText(ServiceDetailActivity.this, "Lỗi khi xóa chi tiết dịch vụ: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(ServiceDetailActivity.this, "Lỗi kết nối API: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
            dialogInterface.dismiss();
        });
        builder.setPositiveButton("Hủy", (dialogInterface, i) -> dialogInterface.dismiss());
        builder.create().show();
    }

    private void handleTotal() {
        final int[] total = {0};
        for (ServiceDetail o : listServiceDetail) {
            // Gọi API để lấy Service
            apiService.getServiceById(o.getIdService()).enqueue(new Callback<Service>() {
                @Override
                public void onResponse(Call<Service> call, Response<Service> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Service service = response.body();
                        total[0] += o.getNumber() * service.getPrice();
                        binding.tvTotal.setText("Tổng tiền: " + String.format("%,d", total[0]));
                    }
                }

                @Override
                public void onFailure(Call<Service> call, Throwable t) {
                    Toast.makeText(ServiceDetailActivity.this, "Lỗi kết nối API: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}