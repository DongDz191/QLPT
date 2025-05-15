package com.example.duan1.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
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

import com.example.duan1.Interface.IClickItemInvoice;
import com.example.duan1.R;
import com.example.duan1.ServiceDetailActivity;
import com.example.duan1.adapter.InvoiceAdapter;
import com.example.duan1.api.ApiClient;
import com.example.duan1.api.ApiService;
import com.example.duan1.databinding.DialogCreateInvoiceBinding;
import com.example.duan1.databinding.DialogInvoiceDetailBinding;
import com.example.duan1.databinding.FragmentInvoiceBinding;
import com.example.duan1.model.Account;
import com.example.duan1.model.Contract;
import com.example.duan1.model.Invoice;
import com.example.duan1.model.RoomType;
import com.example.duan1.model.SessionAccount;
import com.example.duan1.viewmodel.InvoiceViewModel;
import com.example.duan1.viewmodel.TotalViewModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InvoiceFragment extends Fragment {

    FragmentInvoiceBinding binding;
    DialogCreateInvoiceBinding binding2;
    Account account;
    RoomType roomType;
    Contract contract;
    Invoice invoice;
    List<Invoice> invoiceList = new ArrayList<>();
    ApiService apiService;
    InvoiceAdapter adapter;
    Integer totalService = 0;
    InvoiceViewModel model;
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    private static final String TAG = "InvoiceFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView started");
        binding = FragmentInvoiceBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated started");
        apiService = ApiClient.getApiService();
        binding.rvInvoice.setLayoutManager(new LinearLayoutManager(getContext()));
        SessionAccount sessionAccount = new SessionAccount(getContext());

        // Invoice ViewModel
        model = new ViewModelProvider(getActivity()).get(InvoiceViewModel.class);
        model.getInvoice().observe(getViewLifecycleOwner(), o -> {
            invoice = (Invoice) o;
            Log.d(TAG, "Observed invoice change: " + (invoice != null ? invoice.toString() : "null"));
            handleObserve();
            binding.btnCreateInvoice.setOnClickListener(view1 -> handleCreateInvoice());
        });

        account = sessionAccount.fetchAccount();
        Log.d(TAG, "Account fetched from SessionAccount: " + (account != null ? account.toString() : "null"));
        if (account == null) {
            Log.w(TAG, "Account is null, fragment may not function correctly!");
        }
    }

    private void handleObserve() {
        if (invoice == null) {
            Log.w(TAG, "Invoice is null, cannot proceed with handleObserve");
            Toast.makeText(getContext(), "Không tìm thấy thông tin hóa đơn!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Fetch Contract from API
        Log.d(TAG, "Fetching contract with idContract: " + invoice.getIdContract());
        apiService.getContractById(invoice.getIdContract()).enqueue(new Callback<Contract>() {
            @Override
            public void onResponse(Call<Contract> call, Response<Contract> response) {
                if (response.isSuccessful() && response.body() != null) {
                    contract = response.body();
                    Log.d(TAG, "Contract fetched successfully: " + contract.toString());

                    // Fetch RoomType from API
                    Log.d(TAG, "Fetching RoomType for idContract: " + invoice.getIdContract());
                    apiService.getRTByIdContract(invoice.getIdContract()).enqueue(new Callback<RoomType>() {
                        @Override
                        public void onResponse(Call<RoomType> call, Response<RoomType> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                roomType = response.body();
                                Log.d(TAG, "RoomType fetched successfully: " + roomType.toString());

                                // Fetch Invoice list from API
                                Log.d(TAG, "Fetching invoice list for idContract: " + contract.getIdContract());
                                apiService.getInvoiceByIdContract(contract.getIdContract()).enqueue(new Callback<List<Invoice>>() {
                                    @Override
                                    public void onResponse(Call<List<Invoice>> call, Response<List<Invoice>> response) {
                                        if (response.isSuccessful() && response.body() != null) {
                                            invoiceList.clear();
                                            invoiceList.addAll(response.body());
                                            Log.d(TAG, "Invoice list fetched successfully: " + invoiceList.size() + " invoices");
                                            setupAdapter();
                                        } else {
                                            Log.e(TAG, "Lỗi khi lấy danh sách hóa đơn: HTTP Code: " + response.code() + ", Message: " + response.message());
                                            if (response.errorBody() != null) {
                                                try {
                                                    Log.e(TAG, "Chi tiết lỗi từ errorBody: " + response.errorBody().string());
                                                } catch (Exception e) {
                                                    Log.e(TAG, "Lỗi khi đọc errorBody: " + e.getMessage(), e);
                                                }
                                            }
                                            Log.d(TAG, "Raw response: " + response.raw().toString());
                                            Toast.makeText(getContext(), "Lỗi khi lấy danh sách hóa đơn: " + response.code(), Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<List<Invoice>> call, Throwable t) {
                                        Log.e(TAG, "Lỗi kết nối API khi lấy danh sách hóa đơn: " + t.getMessage(), t);
                                        Toast.makeText(getContext(), "Lỗi kết nối API: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } else {
                                Log.e(TAG, "Lỗi khi lấy RoomType: HTTP Code: " + response.code() + ", Message: " + response.message());
                                if (response.errorBody() != null) {
                                    try {
                                        Log.e(TAG, "Chi tiết lỗi từ errorBody: " + response.errorBody().string());
                                    } catch (Exception e) {
                                        Log.e(TAG, "Lỗi khi đọc errorBody: " + e.getMessage(), e);
                                    }
                                }
                                Log.d(TAG, "Raw response: " + response.raw().toString());
                                Toast.makeText(getContext(), "Lỗi khi lấy RoomType: " + response.code(), Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<RoomType> call, Throwable t) {
                            Log.e(TAG, "Lỗi kết nối API khi lấy RoomType: " + t.getMessage(), t);
                            Toast.makeText(getContext(), "Lỗi kết nối API: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Log.e(TAG, "Lỗi khi lấy hợp đồng: HTTP Code: " + response.code() + ", Message: " + response.message());
                    if (response.errorBody() != null) {
                        try {
                            Log.e(TAG, "Chi tiết lỗi từ errorBody: " + response.errorBody().string());
                        } catch (Exception e) {
                            Log.e(TAG, "Lỗi khi đọc errorBody: " + e.getMessage(), e);
                        }
                    }
                    Log.d(TAG, "Raw response: " + response.raw().toString());
                    Toast.makeText(getContext(), "Lỗi khi lấy hợp đồng: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Contract> call, Throwable t) {
                Log.e(TAG, "Lỗi kết nối API khi lấy hợp đồng: " + t.getMessage(), t);
                Toast.makeText(getContext(), "Lỗi kết nối API: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupAdapter() {
        Log.d(TAG, "Setting up InvoiceAdapter with " + invoiceList.size() + " invoices");
        adapter = new InvoiceAdapter(getContext(), invoiceList, new IClickItemInvoice() {
            @Override
            public void onClickItem(Invoice invoice, int position) {
                Log.d(TAG, "Invoice item clicked at position " + position + ": " + invoice.toString());
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.CustomAlertDialog);
                DialogInvoiceDetailBinding bindingDialog = DialogInvoiceDetailBinding.inflate(getLayoutInflater());
                builder.setView(bindingDialog.getRoot());
                bindingDialog.setInvoice(invoice);
                AlertDialog dialog = builder.create();
                bindingDialog.tvDeltais.setOnClickListener(view -> {
                    Log.d(TAG, "Navigating to ServiceDetailActivity for invoice: " + invoice.toString());
                    Intent intent = new Intent(getContext(), ServiceDetailActivity.class);
                    intent.putExtra("invoice", invoice);
                    startActivity(intent);
                });
                bindingDialog.btnClose.setOnClickListener(view -> {
                    Log.d(TAG, "Closing invoice detail dialog");
                    dialog.dismiss();
                });
                dialog.show();
            }

            @Override
            public void onClickPay(Invoice invoice, int position) {
                Log.d(TAG, "Pay button clicked for invoice at position " + position + ": " + invoice.toString());
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.CustomAlertDialog);
                builder.setTitle("Thanh toán");
                builder.setMessage("Bạn có chắc chắn muốn thanh toán?");
                builder.setPositiveButton("Xác nhận", (dialogInterface, i) -> {
                    Log.d(TAG, "Confirming payment for invoice: " + invoice.toString());
                    invoice.setStatus(1);
                    apiService.updateInvoice(invoice.getIdInvoice(), invoice).enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            if (response.isSuccessful()) {
                                Log.d(TAG, "Invoice updated successfully (status set to 1)");
                                Toast.makeText(getContext(), "Thanh toán thành công!", Toast.LENGTH_SHORT).show();
                                // Create a new temporary invoice
                                Invoice invoiceTemp = new Invoice(invoice.getNewWaterIndex(), invoice.getNewPowerIndicator(), 2, contract.getIdContract(), account.getUsername());
                                Log.d(TAG, "Creating new temporary invoice: " + invoiceTemp.toString());
                                apiService.insertInvoice(invoiceTemp).enqueue(new Callback<Void>() {
                                    @Override
                                    public void onResponse(Call<Void> call, Response<Void> response) {
                                        if (response.isSuccessful()) {
                                            Log.d(TAG, "Temporary invoice created successfully");
                                            // Fetch the new temporary invoice
                                            Log.d(TAG, "Fetching new temporary invoice for idContract: " + contract.getIdContract());
                                            apiService.getInvoiceNow(contract.getIdContract()).enqueue(new Callback<Invoice>() {
                                                @Override
                                                public void onResponse(Call<Invoice> call, Response<Invoice> response) {
                                                    if (response.isSuccessful() && response.body() != null) {
                                                        Invoice newInvoice = response.body();
                                                        Log.d(TAG, "New temporary invoice fetched: " + newInvoice.toString());
                                                        model.setInvoice(newInvoice);
                                                        // Update the list after payment
                                                        int currentPosition = position;
                                                        if (currentPosition != RecyclerView.NO_POSITION) {
                                                            invoiceList.set(currentPosition, invoice);
                                                            adapter.notifyItemChanged(currentPosition);
                                                            Log.d(TAG, "Invoice list updated at position " + currentPosition);
                                                        }
                                                    } else {
                                                        Log.e(TAG, "Lỗi khi lấy hóa đơn mới: HTTP Code: " + response.code() + ", Message: " + response.message());
                                                        if (response.errorBody() != null) {
                                                            try {
                                                                Log.e(TAG, "Chi tiết lỗi từ errorBody: " + response.errorBody().string());
                                                            } catch (Exception e) {
                                                                Log.e(TAG, "Lỗi khi đọc errorBody: " + e.getMessage(), e);
                                                            }
                                                        }
                                                        Log.d(TAG, "Raw response: " + response.raw().toString());
                                                        Toast.makeText(getContext(), "Lỗi khi lấy hóa đơn mới: " + response.code(), Toast.LENGTH_SHORT).show();
                                                    }
                                                }

                                                @Override
                                                public void onFailure(Call<Invoice> call, Throwable t) {
                                                    Log.e(TAG, "Lỗi kết nối API khi lấy hóa đơn mới: " + t.getMessage(), t);
                                                    Toast.makeText(getContext(), "Lỗi kết nối API: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        } else {
                                            Log.e(TAG, "Lỗi khi tạo hóa đơn tạm: HTTP Code: " + response.code() + ", Message: " + response.message());
                                            if (response.errorBody() != null) {
                                                try {
                                                    Log.e(TAG, "Chi tiết lỗi từ errorBody: " + response.errorBody().string());
                                                } catch (Exception e) {
                                                    Log.e(TAG, "Lỗi khi đọc errorBody: " + e.getMessage(), e);
                                                }
                                            }
                                            Log.d(TAG, "Raw response: " + response.raw().toString());
                                            Toast.makeText(getContext(), "Lỗi khi tạo hóa đơn tạm: " + response.code(), Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<Void> call, Throwable t) {
                                        Log.e(TAG, "Lỗi kết nối API khi tạo hóa đơn tạm: " + t.getMessage(), t);
                                        Toast.makeText(getContext(), "Lỗi kết nối API: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } else {
                                Log.e(TAG, "Lỗi khi thanh toán: HTTP Code: " + response.code() + ", Message: " + response.message());
                                if (response.errorBody() != null) {
                                    try {
                                        Log.e(TAG, "Chi tiết lỗi từ errorBody: " + response.errorBody().string());
                                    } catch (Exception e) {
                                        Log.e(TAG, "Lỗi khi đọc errorBody: " + e.getMessage(), e);
                                    }
                                }
                                Log.d(TAG, "Raw response: " + response.raw().toString());
                                Toast.makeText(getContext(), "Lỗi khi thanh toán: " + response.code(), Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            Log.e(TAG, "Lỗi kết nối API khi thanh toán: " + t.getMessage(), t);
                            Toast.makeText(getContext(), "Lỗi kết nối API: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                    dialogInterface.cancel();
                });
                builder.setNegativeButton("Hủy", (dialogInterface, i) -> {
                    Log.d(TAG, "Payment cancelled by user");
                    dialogInterface.cancel();
                });
                builder.create().show();
            }
        });

        binding.rvInvoice.setAdapter(adapter);
    }

    @SuppressLint("DefaultLocale")
    private void handleCreateInvoice() {
        Log.d(TAG, "Starting handleCreateInvoice");
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.CustomAlertDialog);
        binding2 = DialogCreateInvoiceBinding.inflate(getLayoutInflater());
        builder.setView(binding2.getRoot());
        AlertDialog dialog = builder.create();
        dialog.show();

        // Event EditText Change for Electric
        binding2.edElectric.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Log.d(TAG, "Electric EditText changed: " + charSequence.toString());
                if (charSequence.toString().isEmpty()) {
                    Log.d(TAG, "Electric input is empty, setting electric to null");
                    binding2.setElectric(null);
                    return;
                }
                invoice.setNewPowerIndicator(Integer.parseInt(charSequence.toString()));
                int number = invoice.getNewPowerIndicator() - invoice.getOldPowerIndicator();
                int price = number * roomType.getElectronicFee();
                binding2.setElectric(price);
                binding2.setNumberElectric(number);
                binding2.setPriceElectric(roomType.getElectronicFee());
                Log.d(TAG, "Updated electric: number=" + number + ", price=" + price + ", electronicFee=" + roomType.getElectronicFee());
                total();
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        // Event EditText Change for Water
        binding2.edWater.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Log.d(TAG, "Water EditText changed: " + charSequence.toString());
                if (charSequence.toString().isEmpty()) {
                    Log.d(TAG, "Water input is empty, setting water to null");
                    binding2.setWater(null);
                    return;
                }
                invoice.setNewWaterIndex(Integer.parseInt(charSequence.toString()));
                int number = invoice.getNewWaterIndex() - invoice.getOldWaterIndex();
                int price = number * roomType.getWaterFee();
                binding2.setWater(price);
                binding2.setPriceWater(roomType.getWaterFee());
                binding2.setNumberWater(number);
                Log.d(TAG, "Updated water: number=" + number + ", price=" + price + ", waterFee=" + roomType.getWaterFee());
                total();
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        // Event EditText Change for Other Fees
        binding2.edFeeOther.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Log.d(TAG, "Other fees EditText changed: " + charSequence.toString());
                total();
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        // Total service ViewModel
        new ViewModelProvider(getActivity()).get(TotalViewModel.class).getTotal().observe(getViewLifecycleOwner(), o -> {
            totalService = (Integer) o;
            binding2.tvPriceService.setText(String.format("%,d", o));
            Log.d(TAG, "Total service updated: " + totalService);
            total();
        });

        binding2.btnCancel.setOnClickListener(view -> {
            Log.d(TAG, "Create invoice dialog cancelled");
            dialog.cancel();
        });

        binding2.btnConfirm.setOnClickListener(view -> {
            Log.d(TAG, "Confirm button clicked for creating invoice");
            if (binding2.getWater() == null || binding2.getElectric() == null) {
                Log.w(TAG, "Validation failed: Water or Electric is null");
                Toast.makeText(getContext(), "Bạn vui lòng không bỏ trống!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (binding2.getElectric() < 0 || binding2.getWater() < 0) {
                Log.w(TAG, "Validation failed: Electric or Water is negative");
                Toast.makeText(getContext(), "Bạn nhập chỉ số điện nước không chính xác!", Toast.LENGTH_SHORT).show();
                return;
            }
            invoice.setStatus(0);
            invoice.setDate(format.format(new Date()));
            invoice.setUsername(account.getUsername());
            invoice.setTotal(binding2.getTotal());
            invoice.setOtherFeesDescribe(binding2.edNameFeeOther.getText().toString());
            String feeOther = binding2.edFeeOther.getText().toString();
            if (!feeOther.isEmpty()) {
                invoice.setOtherFees(Integer.parseInt(feeOther));
            }
            Log.d(TAG, "Updated invoice before API update: " + invoice.toString());

            // Update current invoice via API
            Log.d(TAG, "Updating invoice with idInvoice: " + invoice.getIdInvoice());
            apiService.updateInvoice(invoice.getIdInvoice(), invoice).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        Log.d(TAG, "Invoice updated successfully");
                        // Create a new temporary invoice
                        Invoice invoiceTemp = new Invoice(invoice.getNewWaterIndex(), invoice.getNewPowerIndicator(),  2, contract.getIdContract(), account.getUsername());
                        Log.d(TAG, "Creating new temporary invoice: " + invoiceTemp.toString());
                        apiService.insertInvoice(invoiceTemp).enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(Call<Void> call, Response<Void> response) {
                                if (response.isSuccessful()) {
                                    Log.d(TAG, "Temporary invoice created successfully");
                                    // Fetch the new temporary invoice
                                    Log.d(TAG, "Fetching new temporary invoice for idContract: " + contract.getIdContract());
                                    apiService.getInvoiceNow(contract.getIdContract()).enqueue(new Callback<Invoice>() {
                                        @Override
                                        public void onResponse(Call<Invoice> call, Response<Invoice> response) {
                                            if (response.isSuccessful() && response.body() != null) {
                                                Invoice newInvoice = response.body();
                                                Log.d(TAG, "New temporary invoice fetched: " + newInvoice.toString());
                                                model.setInvoice(newInvoice);
                                                dialog.cancel();
                                                Toast.makeText(getContext(), "Tạo hóa đơn thành công!", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Log.e(TAG, "Lỗi khi lấy hóa đơn mới: HTTP Code: " + response.code() + ", Message: " + response.message());
                                                if (response.errorBody() != null) {
                                                    try {
                                                        Log.e(TAG, "Chi tiết lỗi từ errorBody: " + response.errorBody().string());
                                                    } catch (Exception e) {
                                                        Log.e(TAG, "Lỗi khi đọc errorBody: " + e.getMessage(), e);
                                                    }
                                                }
                                                Log.d(TAG, "Raw response: " + response.raw().toString());
                                                Toast.makeText(getContext(), "Lỗi khi lấy hóa đơn mới: " + response.code(), Toast.LENGTH_SHORT).show();
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<Invoice> call, Throwable t) {
                                            Log.e(TAG, "Lỗi kết nối API khi lấy hóa đơn mới: " + t.getMessage(), t);
                                            Toast.makeText(getContext(), "Lỗi kết nối API: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                } else {
                                    Log.e(TAG, "Lỗi khi tạo hóa đơn tạm: HTTP Code: " + response.code() + ", Message: " + response.message());
                                    if (response.errorBody() != null) {
                                        try {
                                            Log.e(TAG, "Chi tiết lỗi từ errorBody: " + response.errorBody().string());
                                        } catch (Exception e) {
                                            Log.e(TAG, "Lỗi khi đọc errorBody: " + e.getMessage(), e);
                                        }
                                    }
                                    Log.d(TAG, "Raw response: " + response.raw().toString());
                                    Toast.makeText(getContext(), "Lỗi khi tạo hóa đơn tạm: " + response.code(), Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<Void> call, Throwable t) {
                                Log.e(TAG, "Lỗi kết nối API khi tạo hóa đơn tạm: " + t.getMessage(), t);
                                Toast.makeText(getContext(), "Lỗi kết nối API: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        Log.e(TAG, "Lỗi khi cập nhật hóa đơn: HTTP Code: " + response.code() + ", Message: " + response.message());
                        if (response.errorBody() != null) {
                            try {
                                Log.e(TAG, "Chi tiết lỗi từ errorBody: " + response.errorBody().string());
                            } catch (Exception e) {
                                Log.e(TAG, "Lỗi khi đọc errorBody: " + e.getMessage(), e);
                            }
                        }
                        Log.d(TAG, "Raw response: " + response.raw().toString());
                        Toast.makeText(getContext(), "Lỗi khi cập nhật hóa đơn: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Log.e(TAG, "Lỗi kết nối API khi cập nhật hóa đơn: " + t.getMessage(), t);
                    Toast.makeText(getContext(), "Lỗi kết nối API: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void total() {
        int total = 0;
        total += totalService;
        String feeOther = binding2.edFeeOther.getText().toString();
        if (!feeOther.isEmpty()) {
            total += Integer.parseInt(feeOther);
        }
        if (binding2.getElectric() != null) {
            total += binding2.getElectric();
        }
        if (binding2.getWater() != null) {
            total += binding2.getWater();
        }
        binding2.setTotal(total);
        Log.d(TAG, "Calculated total: " + total);
    }
}