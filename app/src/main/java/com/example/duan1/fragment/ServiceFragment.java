package com.example.duan1.fragment;

import android.app.AlertDialog;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.duan1.Interface.IClickItemService;
import com.example.duan1.Interface.IClickItemServiceDetail;
import com.example.duan1.R;
import com.example.duan1.adapter.ServiceDetailAdapter;
import com.example.duan1.adapter.ServiceRoomAdapter;
import com.example.duan1.api.ApiClient;
import com.example.duan1.api.ApiService;
import com.example.duan1.databinding.DialogNumberServiceDetailBinding;
import com.example.duan1.databinding.FragmentServiceBinding;
import com.example.duan1.model.Invoice;
import com.example.duan1.model.Service;
import com.example.duan1.model.ServiceDetail;
import com.example.duan1.viewmodel.InvoiceViewModel;
import com.example.duan1.viewmodel.TotalViewModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ServiceFragment extends Fragment {
    FragmentServiceBinding binding;
    List<ServiceDetail> listServiceDetail = new ArrayList<>();
    Invoice invoice;
    ApiService apiService;
    List<Service> listService = new ArrayList<>();
    ServiceRoomAdapter serviceAdapter;
    ServiceDetailAdapter detailAdapter;
    TotalViewModel model2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentServiceBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        apiService = ApiClient.getApiService();
        listService = new ArrayList<>();
        binding.rvService.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.rvDetail.setLayoutManager(new LinearLayoutManager(getContext()));

        // Total ViewModel
        model2 = new ViewModelProvider(getActivity()).get(TotalViewModel.class);

        // Invoice ViewModel
        new ViewModelProvider(getActivity()).get(InvoiceViewModel.class).getInvoice().observe(getViewLifecycleOwner(), o -> {
            invoice = (Invoice) o;
            handleObserve();
        });

        // Fetch all services from API
        apiService.getAllServices().enqueue(new Callback<List<Service>>() {
            @Override
            public void onResponse(Call<List<Service>> call, Response<List<Service>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listService.clear();
                    listService.addAll(response.body());
                    Log.i("servi", "onViewCreated: " + listService.size());
                    if (serviceAdapter != null) {
                        serviceAdapter.notifyDataSetChanged();
                    }
                } else {
                    Toast.makeText(getContext(), "Lỗi khi lấy danh sách dịch vụ: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Service>> call, Throwable t) {
                Toast.makeText(getContext(), "Lỗi kết nối API: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleObserve() {
        if (invoice == null) return;

        // Fetch ServiceDetails from API
        apiService.getServiceDetailByIdInvoice(invoice.getIdInvoice()).enqueue(new Callback<List<ServiceDetail>>() {
            @Override
            public void onResponse(Call<List<ServiceDetail>> call, Response<List<ServiceDetail>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listServiceDetail.clear();
                    listServiceDetail.addAll(response.body());
                    Log.d("Detail", "handleObserve: " + listServiceDetail.size());
                    setupDetailAdapter();
                    handleTotal();
                } else {
                    Toast.makeText(getContext(), "Lỗi khi lấy danh sách chi tiết dịch vụ: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<ServiceDetail>> call, Throwable t) {
                Toast.makeText(getContext(), "Lỗi kết nối API: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // Initialize adapter service
        serviceAdapter = new ServiceRoomAdapter(getContext(), listService, new IClickItemService() {
            @Override
            public void onClickAdd(Service service, int i) {
                handleItemAddService(service, i);
            }
        });
        binding.rvService.setAdapter(serviceAdapter);

        // Search service
        binding.edSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                apiService.searchServices("%" + charSequence + "%").enqueue(new Callback<List<Service>>() {
                    @Override
                    public void onResponse(Call<List<Service>> call, Response<List<Service>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            listService.clear();
                            listService.addAll(response.body());
                            serviceAdapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(getContext(), "Lỗi khi tìm kiếm dịch vụ: " + response.code(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Service>> call, Throwable t) {
                        Toast.makeText(getContext(), "Lỗi kết nối API: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });
    }

    private void setupDetailAdapter() {
        detailAdapter = new ServiceDetailAdapter(getContext(), listServiceDetail, new IClickItemServiceDetail() {
            @Override
            public void onClickDelete(ServiceDetail serviceDetail, int i) {
                handleItemDelete(serviceDetail, i);
            }
        });
        binding.rvDetail.setAdapter(detailAdapter);
    }

    private void handleTotal() {
        final int[] total = {0};
        for (ServiceDetail o : listServiceDetail) {
            apiService.getServiceById(o.getIdService()).enqueue(new Callback<Service>() {
                @Override
                public void onResponse(Call<Service> call, Response<Service> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Service service = response.body();
                        total[0] += o.getNumber() * service.getPrice();
                        model2.setTotal(total[0]);
                        binding.tvTotal.setText("Tổng tiền: " + String.format("%,d", total[0]));
                    }
                }

                @Override
                public void onFailure(Call<Service> call, Throwable t) {
                    Toast.makeText(getContext(), "Lỗi kết nối API: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void handleItemDelete(ServiceDetail serviceDetail, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.CustomAlertDialog);
        builder.setTitle("Xóa");
        builder.setMessage("Bạn có chắc chắn muốn xóa?");
        builder.setNegativeButton("OK", (dialogInterface, i) -> {
            apiService.deleteServiceDetail(serviceDetail.getIdServiceDetail()).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        int currentPosition = position;
                        if (currentPosition != RecyclerView.NO_POSITION) {
                            listServiceDetail.remove(currentPosition);
                            detailAdapter.notifyItemRemoved(currentPosition);
                            detailAdapter.notifyItemRangeChanged(currentPosition, detailAdapter.getItemCount());
                            handleTotal();
                        }
                    } else {
                        Toast.makeText(getContext(), "Lỗi khi xóa chi tiết dịch vụ: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(getContext(), "Lỗi kết nối API: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
            dialogInterface.dismiss();
        });
        builder.setPositiveButton("Hủy", (dialogInterface, i) -> dialogInterface.dismiss());
        builder.create().show();
    }

    private void handleItemAddService(Service service, int i) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.CustomAlertDialog);
        DialogNumberServiceDetailBinding binding2 = DialogNumberServiceDetailBinding.inflate(getLayoutInflater());
        builder.setView(binding2.getRoot());
        AlertDialog dialog = builder.create();
        dialog.show();
        binding2.setService(service);
        binding2.img.setImageURI(Uri.parse(service.getImage()));
        binding2.imgNext.setOnClickListener(view -> {
            String s = binding2.edNumber.getText().toString();
            if (!s.isEmpty()) {
                int number = Integer.parseInt(s) + 1;
                binding2.edNumber.setText(number + "");
            }
        });
        binding2.imgPrevious.setOnClickListener(view -> {
            String s = binding2.edNumber.getText().toString();
            if (!s.isEmpty()) {
                int number = Integer.parseInt(s);
                if (number > 0) {
                    number--;
                }
                binding2.edNumber.setText(number + "");
            }
        });
        binding2.btnAdd.setOnClickListener(view -> {
            try {
                String stringNumber = binding2.edNumber.getText().toString();
                if (!stringNumber.isEmpty()) {
                    int number = Integer.parseInt(stringNumber);
                    if (number != 0) {
                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                        String date = format.format(new Date());
                        ServiceDetail detail = new ServiceDetail(invoice.getIdInvoice(), service.getIdService(), date, number);
                        apiService.insertServiceDetail(detail).enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(Call<Void> call, Response<Void> response) {
                                if (response.isSuccessful()) {
                                    listServiceDetail.add(0, detail);
                                    detailAdapter.notifyDataSetChanged();
                                    dialog.dismiss();
                                    handleTotal();
                                } else {
                                    Toast.makeText(getContext(), "Lỗi khi thêm chi tiết dịch vụ: " + response.code(), Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<Void> call, Throwable t) {
                                Toast.makeText(getContext(), "Lỗi kết nối API: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        Toast.makeText(getContext(), "Số lượng không hợp lệ!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "Số lượng trống!", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Toast.makeText(getContext(), "Thêm thất bại!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}