package com.example.duan1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.duan1.adapter.ViewPagerAdapter;
import com.example.duan1.api.ApiClient;
import com.example.duan1.api.ApiService;
import com.example.duan1.databinding.ActivityRoomAvalibleBinding;
import com.example.duan1.model.Account;
import com.example.duan1.model.Contract;
import com.example.duan1.model.Invoice;
import com.example.duan1.model.Room;
import com.example.duan1.model.SessionAccount;
import com.example.duan1.viewmodel.AccountViewModel;
import com.example.duan1.viewmodel.ContractViewModel;
import com.example.duan1.viewmodel.InvoiceViewModel;
import com.google.android.material.tabs.TabLayoutMediator;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RoomAvalibleActivity extends AppCompatActivity {
    final String arrTitle[] = new String[]{"Dịch vụ", "Thành viên", "Hóa đơn", "Hợp đồng"};
    public ActivityRoomAvalibleBinding binding;
    Account account;
    Room room;
    Contract contract;
    ApiService apiService;
    private static final String TAG = "RoomAvalibleActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate started");
        binding = ActivityRoomAvalibleBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        apiService = ApiClient.getApiService();

        // Log the entire Intent extras for debugging
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            Log.d(TAG, "Intent extras: " + extras.toString());
            for (String key : extras.keySet()) {
                Log.d(TAG, "Extra key: " + key + ", value: " + extras.get(key));
            }
        } else {
            Log.w(TAG, "Intent extras are null!");
        }

        // Try to get roomCode directly
        String roomCode = getIntent().getStringExtra("roomCode");

        // If roomCode is null, try to get it from the Room object
        if (roomCode == null || roomCode.isEmpty()) {
            Room roomFromIntent = (Room) getIntent().getSerializableExtra("room");
            if (roomFromIntent != null) {
                roomCode = roomFromIntent.getRoomCode();
                Log.d(TAG, "Retrieved roomCode from Room object: " + roomCode);
            }
        }

        // Try to get account from Intent
        account = (Account) getIntent().getSerializableExtra("account");

        // If account is null, try to fetch it from SessionAccount
        if (account == null) {
            SessionAccount sessionAccount = new SessionAccount(this);
            account = sessionAccount.fetchAccount();
            Log.d(TAG, "Retrieved account from SessionAccount: " + (account != null ? account.toString() : "null"));
        }

        Log.d(TAG, "Retrieved roomCode: " + roomCode);
        Log.d(TAG, "Retrieved account: " + (account != null ? account.toString() : "null"));

        // Check if roomCode and account are valid
        if (roomCode == null || roomCode.isEmpty() || account == null) {
            Log.e(TAG, "Room code or account is missing! roomCode: " + roomCode + ", account: " + account);
            Toast.makeText(this, "Không tìm thấy thông tin phòng hoặc tài khoản!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Fetch Room from API using getRoomByRoomCode
        Log.d(TAG, "Fetching room with roomCode: " + roomCode);
        apiService.getRoomByRoomCode(roomCode).enqueue(new Callback<Room>() {
            @Override
            public void onResponse(Call<Room> call, Response<Room> response) {
                if (response.isSuccessful() && response.body() != null) {
                    room = response.body();
                    Log.d(TAG, "Room fetched successfully: " + room.toString());

                    // Fetch Contract from API
                    fetchContract(room.getRoomCode());
                } else {
                    Log.e(TAG, "Lỗi khi lấy thông tin phòng: " + response.code());
                    if (response.errorBody() != null) {
                        try {
                            Log.e(TAG, "Chi tiết lỗi: " + response.errorBody().string());
                        } catch (Exception e) {
                            Log.e(TAG, "Lỗi khi đọc errorBody: " + e.getMessage());
                        }
                    }
                    Toast.makeText(RoomAvalibleActivity.this, "Lỗi khi lấy thông tin phòng: " + response.code(), Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<Room> call, Throwable t) {
                Log.e(TAG, "Lỗi kết nối API khi lấy thông tin phòng: " + t.getMessage());
                Toast.makeText(RoomAvalibleActivity.this, "Lỗi kết nối API: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        binding.imgBack.setOnClickListener(view -> finish());
    }

    private void fetchContract(String roomCode) {
        Log.d(TAG, "Fetching contract for roomCode: " + roomCode);
        apiService.getContractByRoomCode(roomCode).enqueue(new Callback<Contract>() {
            @Override
            public void onResponse(Call<Contract> call, Response<Contract> response) {
                if (response.isSuccessful() && response.body() != null) {
                    contract = response.body();
                    Log.d(TAG, "Contract fetched successfully: " + contract.toString());

                    // Fetch Invoice from API
                    fetchInvoice(contract.getIdContract());
                } else {
                    Log.e(TAG, "Lỗi khi lấy hợp đồng: HTTP Code: " + response.code() + ", Message: " + response.message());
                    if (response.errorBody() != null) {
                        try {
                            Log.e(TAG, "Chi tiết lỗi từ errorBody: " + response.errorBody().string());
                        } catch (Exception e) {
                            Log.e(TAG, "Lỗi khi đọc errorBody: " + e.getMessage());
                        }
                    }
                    Toast.makeText(RoomAvalibleActivity.this, "Lỗi khi lấy hợp đồng: " + response.code(), Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<Contract> call, Throwable t) {
                Log.e(TAG, "Lỗi kết nối API khi lấy hợp đồng: " + t.getMessage());
                Toast.makeText(RoomAvalibleActivity.this, "Lỗi kết nối API: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void fetchInvoice(int idContract) {
        Log.d(TAG, "Fetching invoice for idContract: " + idContract);
        apiService.getInvoiceNow(idContract).enqueue(new Callback<Invoice>() {
            @Override
            public void onResponse(Call<Invoice> call, Response<Invoice> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Invoice invoice = response.body();
                    Log.d(TAG, "Invoice fetched successfully: " + invoice.toString());

                    // Set ViewModel
                    InvoiceViewModel invoiceViewModel = new ViewModelProvider(RoomAvalibleActivity.this).get(InvoiceViewModel.class);
                    invoiceViewModel.setInvoice(invoice);
                    ContractViewModel contractViewModel = new ViewModelProvider(RoomAvalibleActivity.this).get(ContractViewModel.class);
                    contractViewModel.setContract(contract);

                    // Set ViewPager và TabLayout
                    setupViewPager();
                } else {
                    // Enhanced logging for error details
                    Log.e(TAG, "Lỗi khi lấy hóa đơn: HTTP Code: " + response.code() + ", Message: " + response.message());
                    if (response.errorBody() != null) {
                        try {
                            String errorBody = response.errorBody().string();
                            Log.e(TAG, "Chi tiết lỗi từ errorBody: " + errorBody);
                        } catch (Exception e) {
                            Log.e(TAG, "Lỗi khi đọc errorBody: " + e.getMessage(), e);
                        }
                    }
                    Log.d(TAG, "Raw response: " + response.raw().toString());
                    Toast.makeText(RoomAvalibleActivity.this, "Lỗi khi lấy hóa đơn: " + response.code(), Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<Invoice> call, Throwable t) {
                Log.e(TAG, "Lỗi kết nối API khi lấy hóa đơn: " + t.getMessage(), t);
                Toast.makeText(RoomAvalibleActivity.this, "Lỗi kết nối API: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void setupViewPager() {
        Log.d(TAG, "Setting up ViewPager and TabLayout");
        ViewPagerAdapter pagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), getLifecycle());
        binding.viewPager.setAdapter(pagerAdapter);
        TabLayoutMediator mediator = new TabLayoutMediator(binding.tabLayout, binding.viewPager, (tab, position) -> {
            tab.setText(arrTitle[position]);
        });
        mediator.attach();
    }
}