package com.example.duan1.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.duan1.Interface.IClickItemServiceDetail;
import com.example.duan1.api.ApiClient;
import com.example.duan1.api.ApiService;
import com.example.duan1.databinding.ItemServiceDetailBinding;
import com.example.duan1.model.Service;
import com.example.duan1.model.ServiceDetail;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ServiceDetailAdapter extends RecyclerView.Adapter<ServiceDetailAdapter.MyViewHolder> {
    Context context;
    List<ServiceDetail> list;
    ApiService apiService;
    IClickItemServiceDetail iClickItemServiceDetail;

    public ServiceDetailAdapter(Context context, List<ServiceDetail> list, IClickItemServiceDetail listener) {
        this.context = context;
        this.list = list;
        this.apiService = ApiClient.getApiService();
        this.iClickItemServiceDetail = listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(ItemServiceDetailBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ServiceDetail detail = list.get(position);
        // Gọi API để lấy thông tin Service
        apiService.getServiceById(detail.getIdService()).enqueue(new Callback<Service>() {
            @Override
            public void onResponse(Call<Service> call, Response<Service> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Service service = response.body();
                    holder.binding.setServiceDetail(detail);
                    holder.binding.setService(service);
                    holder.binding.img.setImageURI(Uri.parse(service.getImage()));
                } else {
                    Toast.makeText(context, "Lỗi khi lấy thông tin dịch vụ: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Service> call, Throwable t) {
                Toast.makeText(context, "Lỗi kết nối API: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        holder.binding.imgDelete.setOnClickListener(view -> {
            int currentPosition = holder.getAdapterPosition();
            if (currentPosition != RecyclerView.NO_POSITION) {
                ServiceDetail currentDetail = list.get(currentPosition);
                iClickItemServiceDetail.onClickDelete(currentDetail, currentPosition);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        ItemServiceDetailBinding binding;

        public MyViewHolder(@NonNull ItemServiceDetailBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}