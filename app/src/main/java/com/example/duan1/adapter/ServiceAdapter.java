package com.example.duan1.adapter;

import static com.example.duan1.base.BaseCheckValid.checkEmptyString;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.duan1.R;
import com.example.duan1.api.ApiClient;
import com.example.duan1.api.ApiService;
import com.example.duan1.databinding.ItemServiceBinding;
import com.example.duan1.model.Service;

import java.util.List;

import gun0912.tedimagepicker.builder.TedImagePicker;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ServiceAdapter extends RecyclerView.Adapter<ServiceAdapter.MyViewHolder> {
    Context context;
    List<Service> list;
    ImageView imageViewEdit;
    String strimage = "";
    ApiService apiService;

    public ServiceAdapter(Context context, List<Service> list) {
        this.context = context;
        this.list = list;
        this.apiService = ApiClient.getApiService();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(ItemServiceBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Service service = list.get(position);
        try {
            holder.binding.imageService.setImageURI(Uri.parse(service.getImage().trim()));
        } catch (Exception e) {
            Toast.makeText(context, "False", Toast.LENGTH_SHORT).show();
        }
        holder.binding.tvService.setText(service.getName());
        holder.binding.priceService.setText(String.valueOf(service.getPrice()) + " VND");
        Glide.with(context).load(Uri.parse(service.getImage())).into(holder.binding.imageService);

        holder.binding.imvDelete.setOnClickListener(v -> {
            int currentPosition = holder.getAdapterPosition(); // Lấy vị trí hiện tại
            if (currentPosition != RecyclerView.NO_POSITION) {
                Service currentService = list.get(currentPosition);
                apiService.deleteService(currentService.getIdService()).enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            list.remove(currentPosition);
                            notifyItemRemoved(currentPosition);
                            notifyItemRangeChanged(currentPosition, getItemCount());
                        } else {
                            Toast.makeText(context, "Lỗi khi xóa dịch vụ: " + response.code(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(context, "Lỗi kết nối API: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        holder.binding.imgEdit.setOnClickListener(v -> {
            int currentPosition = holder.getAdapterPosition(); // Lấy vị trí hiện tại
            if (currentPosition != RecyclerView.NO_POSITION) {
                Service currentService = list.get(currentPosition);
                showDialogEdit(currentService, currentPosition);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ItemServiceBinding binding;

        public MyViewHolder(ItemServiceBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public void showDialogEdit(Service service, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater layoutInflater = ((Activity) context).getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.dialog_edit_service, null);
        builder.setView(view);

        EditText edtEditDV = view.findViewById(R.id.edtEditDv);
        EditText edtPriceDV = view.findViewById(R.id.edtEditPricedv);
        imageViewEdit = view.findViewById(R.id.setImageEdit);

        imageViewEdit.setOnClickListener(v -> addImage());
        edtEditDV.setText(service.getName());
        edtPriceDV.setText(String.valueOf(service.getPrice()));
        imageViewEdit.setImageURI(Uri.parse(service.getImage()));

        builder.setNegativeButton("Edit", (dialog, which) -> {
            String editDV = edtEditDV.getText().toString().trim();
            String priceText = edtPriceDV.getText().toString().trim();
            String strImage = strimage;

            if (!checkEmptyString(editDV, priceText)) {
                Toast.makeText(context, "Vui lòng không để trống tên hoặc giá dịch vụ", Toast.LENGTH_SHORT).show();
                return;
            }

            int editPrice;
            try {
                editPrice = Integer.parseInt(priceText);
            } catch (NumberFormatException e) {
                Toast.makeText(context, "Giá dịch vụ không hợp lệ", Toast.LENGTH_SHORT).show();
                return;
            }

            service.setName(editDV);
            service.setPrice(editPrice);
            if (checkEmptyString(strImage)) {
                service.setImage(strImage);
            }

            // Gọi API để cập nhật dịch vụ
            apiService.updateService(service.getIdService(), service).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        int currentPosition = position;
                        if (currentPosition != RecyclerView.NO_POSITION) {
                            list.set(currentPosition, service);
                            notifyItemChanged(currentPosition);
                        }
                    } else {
                        Toast.makeText(context, "Lỗi khi cập nhật dịch vụ: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(context, "Lỗi kết nối API: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        builder.setPositiveButton("Exit", (dialog, which) -> dialog.dismiss());
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void addImage() {
        TedImagePicker.with(context).start(uri -> {
            strimage = uri.toString();
            Glide.with(context).load(uri).into(imageViewEdit);
        });
    }
}